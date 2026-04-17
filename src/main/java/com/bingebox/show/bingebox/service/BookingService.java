package com.bingebox.show.bingebox.service;

import com.bingebox.show.bingebox.dto.BookingDTO;
import com.bingebox.show.bingebox.dto.BookingSeatDTO;
import com.bingebox.show.bingebox.model.*;
import com.bingebox.show.bingebox.repository.BookingRepository;
import com.bingebox.show.bingebox.repository.ShowRepository;
import com.bingebox.show.bingebox.repository.ShowSeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookingService implements BookingServiceInterface {

    private final BookingRepository bookingRepository;
    private final ShowRepository showRepository;
    private final ShowSeatRepository showSeatRepository;

    @Cacheable(value = "bookings", key = "'reference:' + #bookingReference")
    @Transactional(readOnly = true)
    public Booking getBookingByReference(String bookingReference) {
        log.info("Fetching booking by reference: {}", bookingReference);
        return bookingRepository.findByBookingReference(bookingReference)
                .orElseThrow(() -> new RuntimeException("Booking not found with reference: " + bookingReference));
    }

    @Cacheable(value = "bookings", key = "'customer:' + #email")
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByCustomerEmail(String email) {
        log.info("Fetching bookings for customer email: {}", email);
        return bookingRepository.findByCustomerEmail(email);
    }

    @Cacheable(value = "bookings", key = "'show:' + #showId")
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByShow(Long showId) {
        log.info("Fetching bookings for show id: {}", showId);
        return bookingRepository.findByShowId(showId);
    }

    @CacheEvict(value = "bookings", allEntries = true)
    public Booking createBooking(BookingRequest bookingRequest) {
        log.info("Creating booking for show: {}, seats: {}", bookingRequest.getShowId(), bookingRequest.getSeatNumbers());
        
        // Validate show exists and is active
        Show show = showRepository.findByIdAndIsActiveTrue(bookingRequest.getShowId())
                .orElseThrow(() -> new RuntimeException("Show not found with id: " + bookingRequest.getShowId()));

        // Check if seats are available
        List<ShowSeat> showSeats = showSeatRepository.findByShowIdAndSeatNumberIn(
                bookingRequest.getShowId(), bookingRequest.getSeatNumbers());
        
        if (showSeats.size() != bookingRequest.getSeatNumbers().size()) {
            throw new RuntimeException("Some seats not found");
        }

        // Check seat availability
        List<ShowSeat> unavailableSeats = showSeats.stream()
                .filter(seat -> seat.getStatus() != ShowSeat.SeatStatus.AVAILABLE)
                .collect(Collectors.toList());
        
        if (!unavailableSeats.isEmpty()) {
            throw new RuntimeException("Seats not available: " + 
                    unavailableSeats.stream()
                            .map(ShowSeat::getSeatNumber)
                            .collect(Collectors.joining(", ")));
        }

        // Lock seats temporarily
        showSeats.forEach(seat -> seat.setStatus(ShowSeat.SeatStatus.LOCKED));
        showSeatRepository.saveAll(showSeats);

        // Calculate pricing with discounts
        PricingCalculation pricing = calculatePricing(showSeats, show);

        // Create booking
        Booking booking = Booking.builder()
                .bookingReference(generateBookingReference())
                .movie(show.getMovie())
                .theatre(show.getTheatre())
                .show(show)
                .customerName(bookingRequest.getCustomerName())
                .customerEmail(bookingRequest.getCustomerEmail())
                .customerPhone(bookingRequest.getCustomerPhone())
                .totalAmount(pricing.getTotalAmount())
                .discountAmount(pricing.getDiscountAmount())
                .finalAmount(pricing.getFinalAmount())
                .status(Booking.BookingStatus.PENDING)
                .paymentStatus(Booking.PaymentStatus.PENDING)
                .bookingDate(LocalDateTime.now())
                .specialRequests(bookingRequest.getSpecialRequests())
                .build();

        booking = bookingRepository.save(booking);

        // Create booking seats
        List<BookingSeat> bookingSeats = showSeats.stream()
                .map(showSeat -> BookingSeat.builder()
                        .booking(booking)
                        .showSeat(showSeat)
                        .seatNumber(showSeat.getSeatNumber())
                        .rowNumber(showSeat.getRowNumber())
                        .seatPrice(showSeat.getPrice())
                        .discountAmount(calculateSeatDiscount(showSeat.getPrice(), showSeats.indexOf(showSeat), show))
                        .finalPrice(calculateSeatFinalPrice(showSeat.getPrice(), showSeats.indexOf(showSeat), show))
                        .build())
                .collect(Collectors.toList());

        booking.setBookingSeats(bookingSeats);
        bookingRepository.save(booking);

        log.info("Booking created successfully with reference: {}", booking.getBookingReference());
        return booking;
    }

    @CacheEvict(value = "bookings", allEntries = true)
    public Booking confirmBooking(String bookingReference) {
        log.info("Confirming booking: {}", bookingReference);
        
        Booking booking = getBookingByReference(bookingReference);
        
        if (booking.getStatus() != Booking.BookingStatus.PENDING) {
            throw new RuntimeException("Booking cannot be confirmed in current status: " + booking.getStatus());
        }

        // Confirm seat bookings
        booking.getBookingSeats().forEach(bookingSeat -> {
            ShowSeat showSeat = bookingSeat.getShowSeat();
            showSeat.setStatus(ShowSeat.SeatStatus.BOOKED);
            showSeatRepository.save(showSeat);
        });

        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        booking.setPaymentStatus(Booking.PaymentStatus.PAID);
        
        return bookingRepository.save(booking);
    }

    @CacheEvict(value = "bookings", allEntries = true)
    public Booking cancelBooking(String bookingReference) {
        log.info("Cancelling booking: {}", bookingReference);
        
        Booking booking = getBookingByReference(bookingReference);
        
        if (booking.getStatus() == Booking.BookingStatus.CANCELLED) {
            throw new RuntimeException("Booking is already cancelled");
        }

        // Release seats
        booking.getBookingSeats().forEach(bookingSeat -> {
            ShowSeat showSeat = bookingSeat.getShowSeat();
            showSeat.setStatus(ShowSeat.SeatStatus.AVAILABLE);
            showSeatRepository.save(showSeat);
        });

        booking.setStatus(Booking.BookingStatus.CANCELLED);
        booking.setPaymentStatus(Booking.PaymentStatus.REFUNDED);
        
        return bookingRepository.save(booking);
    }

    private PricingCalculation calculatePricing(List<ShowSeat> showSeats, Show show) {
        BigDecimal totalAmount = showSeats.stream()
                .map(ShowSeat::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDiscount = BigDecimal.ZERO;

        // Apply afternoon show discount (20%)
        if (show.isAfternoonShow()) {
            BigDecimal afternoonDiscount = totalAmount.multiply(BigDecimal.valueOf(0.20));
            totalDiscount = totalDiscount.add(afternoonDiscount);
            log.info("Applied 20% afternoon show discount: {}", afternoonDiscount);
        }

        // Apply third ticket discount (50% on third ticket)
        if (showSeats.size() >= 3) {
            BigDecimal thirdTicketPrice = showSeats.get(2).getPrice();
            BigDecimal thirdTicketDiscount = thirdTicketPrice.multiply(BigDecimal.valueOf(0.50));
            totalDiscount = totalDiscount.add(thirdTicketDiscount);
            log.info("Applied 50% discount on third ticket: {}", thirdTicketDiscount);
        }

        BigDecimal finalAmount = totalAmount.subtract(totalDiscount);

        return PricingCalculation.builder()
                .totalAmount(totalAmount)
                .discountAmount(totalDiscount)
                .finalAmount(finalAmount)
                .build();
    }

    private BigDecimal calculateSeatDiscount(BigDecimal seatPrice, int seatIndex, Show show) {
        BigDecimal discount = BigDecimal.ZERO;

        // Apply afternoon show discount (20%)
        if (show.isAfternoonShow()) {
            discount = discount.add(seatPrice.multiply(BigDecimal.valueOf(0.20)));
        }

        // Apply third ticket discount (50% on third ticket)
        if (seatIndex == 2) { // Third ticket (0-indexed)
            discount = discount.add(seatPrice.multiply(BigDecimal.valueOf(0.50)));
        }

        return discount;
    }

    private BigDecimal calculateSeatFinalPrice(BigDecimal seatPrice, int seatIndex, Show show) {
        BigDecimal discount = calculateSeatDiscount(seatPrice, seatIndex, show);
        return seatPrice.subtract(discount).setScale(2, RoundingMode.HALF_UP);
    }

    private String generateBookingReference() {
        return "BB" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    @Cacheable(value = "bookings", key = "'movie:' + #movieId")
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByMovie(Long movieId) {
        log.info("Fetching bookings for movie id: {}", movieId);
        return bookingRepository.findByMovieId(movieId);
    }

    @Cacheable(value = "bookings", key = "'theatre:' + #theatreId")
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByTheatre(Long theatreId) {
        log.info("Fetching bookings for theatre id: {}", theatreId);
        return bookingRepository.findByTheatreId(theatreId);
    }

    @Cacheable(value = "bookings", key = "'status:' + #status")
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByStatus(Booking.BookingStatus status) {
        log.info("Fetching bookings for status: {}", status);
        return bookingRepository.findByStatus(status);
    }

    @Cacheable(value = "bookings", key = "'search:' + #searchTerm")
    @Transactional(readOnly = true)
    public List<Booking> searchBookings(String searchTerm) {
        log.info("Searching bookings with term: {}", searchTerm);
        return bookingRepository.searchBookings(searchTerm);
    }

    @Override
    public BookingDTO convertToDTO(Booking booking) {
        if (booking == null) return null;

        List<BookingSeatDTO> bookingSeatDTOs = booking.getBookingSeats() != null ?
                booking.getBookingSeats().stream()
                        .map(this::convertBookingSeatToDTO)
                        .collect(Collectors.toList()) : null;

        return BookingDTO.builder()
                .id(booking.getId())
                .bookingReference(booking.getBookingReference())
                .movieId(booking.getMovie() != null ? booking.getMovie().getId() : null)
                .movieTitle(booking.getMovie() != null ? booking.getMovie().getTitle() : null)
                .theatreId(booking.getTheatre() != null ? booking.getTheatre().getId() : null)
                .theatreName(booking.getTheatre() != null ? booking.getTheatre().getName() : null)
                .showId(booking.getShow() != null ? booking.getShow().getId() : null)
                .showDate(booking.getShow() != null ? booking.getShow().getShowDate() : null)
                .customerName(booking.getCustomerName())
                .customerEmail(booking.getCustomerEmail())
                .customerPhone(booking.getCustomerPhone())
                .bookingSeats(bookingSeatDTOs)
                .totalAmount(booking.getTotalAmount())
                .discountAmount(booking.getDiscountAmount())
                .finalAmount(booking.getFinalAmount())
                .status(booking.getStatus())
                .paymentStatus(booking.getPaymentStatus())
                .bookingDate(booking.getBookingDate())
                .specialRequests(booking.getSpecialRequests())
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .build();
    }

    private BookingSeatDTO convertBookingSeatToDTO(com.bingebox.show.bingebox.model.BookingSeat bookingSeat) {
        if (bookingSeat == null) return null;

        return BookingSeatDTO.builder()
                .id(bookingSeat.getId())
                .seatNumber(bookingSeat.getSeatNumber())
                .rowNumber(bookingSeat.getRowNumber())
                .seatType(bookingSeat.getShowSeat() != null ? bookingSeat.getShowSeat().getSeatType() : null)
                .seatPrice(bookingSeat.getSeatPrice())
                .discountAmount(bookingSeat.getDiscountAmount())
                .finalPrice(bookingSeat.getFinalPrice())
                .build();
    }

    @Override
    public List<BookingDTO> convertToDTOList(List<Booking> bookings) {
        return bookings.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @lombok.Data
    @lombok.Builder
    public static class BookingRequest implements BookingServiceInterface.BookingRequest {
        private Long showId;
        private List<String> seatNumbers;
        private String customerName;
        private String customerEmail;
        private String customerPhone;
        private String specialRequests;
    }

    @lombok.Data
    @lombok.Builder
    private static class PricingCalculation {
        private BigDecimal totalAmount;
        private BigDecimal discountAmount;
        private BigDecimal finalAmount;
    }
}
