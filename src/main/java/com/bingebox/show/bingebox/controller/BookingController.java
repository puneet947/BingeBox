package com.bingebox.show.bingebox.controller;

import com.bingebox.show.bingebox.dto.BookingDTO;
import com.bingebox.show.bingebox.dto.BookingRequestDTO;
import com.bingebox.show.bingebox.model.Booking;
import com.bingebox.show.bingebox.service.BookingService;
import com.bingebox.show.bingebox.service.BookingServiceInterface;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @GetMapping("/reference/{bookingReference}")
    public ResponseEntity<BookingDTO> getBookingByReference(@PathVariable String bookingReference) {
        log.info("GET /api/v1/bookings/reference/{} - Fetching booking by reference", bookingReference);
        try {
            Booking booking = bookingService.getBookingByReference(bookingReference);
            return ResponseEntity.ok(bookingService.convertToDTO(booking));
        } catch (RuntimeException e) {
            log.error("Error fetching booking {}: {}", bookingReference, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/customer/{email}")
    public ResponseEntity<List<BookingDTO>> getBookingsByCustomerEmail(@PathVariable String email) {
        log.info("GET /api/v1/bookings/customer/{} - Fetching bookings for customer", email);
        List<Booking> bookings = bookingService.getBookingsByCustomerEmail(email);
        return ResponseEntity.ok(bookingService.convertToDTOList(bookings));
    }

    @GetMapping("/show/{showId}")
    public ResponseEntity<List<BookingDTO>> getBookingsByShow(@PathVariable Long showId) {
        log.info("GET /api/v1/bookings/show/{} - Fetching bookings for show", showId);
        List<Booking> bookings = bookingService.getBookingsByShow(showId);
        return ResponseEntity.ok(bookingService.convertToDTOList(bookings));
    }

    @PostMapping
    public ResponseEntity<BookingDTO> createBooking(@Valid @RequestBody BookingRequestDTO bookingRequestDTO) {
        log.info("POST /api/v1/bookings - Creating booking for show: {}", bookingRequestDTO.getShowId());
        try {
            BookingService.BookingRequest bookingRequest = BookingService.BookingRequest.builder()
                    .showId(bookingRequestDTO.getShowId())
                    .seatNumbers(bookingRequestDTO.getSeatNumbers())
                    .customerName(bookingRequestDTO.getCustomerName())
                    .customerEmail(bookingRequestDTO.getCustomerEmail())
                    .customerPhone(bookingRequestDTO.getCustomerPhone())
                    .specialRequests(bookingRequestDTO.getSpecialRequests())
                    .build();
            
            Booking booking = bookingService.createBooking(bookingRequest);
            return ResponseEntity.ok(bookingService.convertToDTO(booking));
        } catch (RuntimeException e) {
            log.error("Error creating booking: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{bookingReference}/confirm")
    public ResponseEntity<BookingDTO> confirmBooking(@PathVariable String bookingReference) {
        log.info("PUT /api/v1/bookings/{}/confirm - Confirming booking", bookingReference);
        try {
            Booking booking = bookingService.confirmBooking(bookingReference);
            return ResponseEntity.ok(bookingService.convertToDTO(booking));
        } catch (RuntimeException e) {
            log.error("Error confirming booking {}: {}", bookingReference, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{bookingReference}/cancel")
    public ResponseEntity<BookingDTO> cancelBooking(@PathVariable String bookingReference) {
        log.info("PUT /api/v1/bookings/{}/cancel - Cancelling booking", bookingReference);
        try {
            Booking booking = bookingService.cancelBooking(bookingReference);
            return ResponseEntity.ok(bookingService.convertToDTO(booking));
        } catch (RuntimeException e) {
            log.error("Error cancelling booking {}: {}", bookingReference, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<BookingDTO>> createBulkBookings(@Valid @RequestBody List<BookingRequestDTO> bookingRequestDTOs) {
        log.info("POST /api/v1/bookings/bulk - Creating {} bulk bookings", bookingRequestDTOs.size());
        try {
            List<BookingService.BookingRequest> bookingRequests = bookingRequestDTOs.stream()
                    .map(dto -> BookingService.BookingRequest.builder()
                            .showId(dto.getShowId())
                            .seatNumbers(dto.getSeatNumbers())
                            .customerName(dto.getCustomerName())
                            .customerEmail(dto.getCustomerEmail())
                            .customerPhone(dto.getCustomerPhone())
                            .specialRequests(dto.getSpecialRequests())
                            .build())
                    .collect(Collectors.toList());
            
            List<Booking> bookings = bookingRequests.stream()
                    .map(bookingService::createBooking)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(bookingService.convertToDTOList(bookings));
        } catch (RuntimeException e) {
            log.error("Error creating bulk bookings: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/bulk/cancel")
    public ResponseEntity<List<BookingDTO>> cancelBulkBookings(@RequestBody List<String> bookingReferences) {
        log.info("PUT /api/v1/bookings/bulk/cancel - Cancelling {} bookings", bookingReferences.size());
        try {
            List<Booking> bookings = bookingReferences.stream()
                    .map(bookingService::cancelBooking)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(bookingService.convertToDTOList(bookings));
        } catch (RuntimeException e) {
            log.error("Error cancelling bulk bookings: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
