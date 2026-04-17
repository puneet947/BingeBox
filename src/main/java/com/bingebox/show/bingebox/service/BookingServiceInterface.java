package com.bingebox.show.bingebox.service;

import com.bingebox.show.bingebox.dto.BookingDTO;
import com.bingebox.show.bingebox.model.Booking;

import java.util.List;

public interface BookingServiceInterface {
    
    // Basic CRUD operations
    Booking getBookingByReference(String bookingReference);
    List<Booking> getBookingsByCustomerEmail(String email);
    List<Booking> getBookingsByShow(Long showId);
    Booking createBooking(BookingRequest bookingRequest);
    Booking confirmBooking(String bookingReference);
    Booking cancelBooking(String bookingReference);
    
    // Bulk operations
    List<Booking> createBulkBookings(List<BookingRequest> bookingRequests);
    List<Booking> cancelBulkBookings(List<String> bookingReferences);
    
    // Advanced operations
    List<Booking> getBookingsByMovie(Long movieId);
    List<Booking> getBookingsByTheatre(Long theatreId);
    List<Booking> getBookingsByStatus(Booking.BookingStatus status);
    List<Booking> searchBookings(String searchTerm);
    
    // DTO operations
    BookingDTO convertToDTO(Booking booking);
    List<BookingDTO> convertToDTOList(List<Booking> bookings);
    
    // Inner request class
    @lombok.Data
    @lombok.Builder
    class BookingRequest {
        private Long showId;
        private List<String> seatNumbers;
        private String customerName;
        private String customerEmail;
        private String customerPhone;
        private String specialRequests;
    }
}
