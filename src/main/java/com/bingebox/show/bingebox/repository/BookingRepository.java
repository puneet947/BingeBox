package com.bingebox.show.bingebox.repository;

import com.bingebox.show.bingebox.model.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    Optional<Booking> findByBookingReference(String bookingReference);
    
    List<Booking> findByCustomerEmail(String customerEmail);
    
    List<Booking> findByCustomerEmailAndStatus(String customerEmail, Booking.BookingStatus status);
    
    List<Booking> findByMovieId(Long movieId);
    
    List<Booking> findByTheatreId(Long theatreId);
    
    List<Booking> findByShowId(Long showId);
    
    List<Booking> findByBookingDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT b FROM Booking b WHERE b.customerEmail = :email AND b.bookingDate >= :fromDate")
    List<Booking> findRecentBookingsByEmail(@Param("email") String email, @Param("fromDate") LocalDateTime fromDate);
    
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.show.id = :showId AND b.status = 'CONFIRMED'")
    Long countConfirmedBookingsByShow(@Param("showId") Long showId);
    
    @Query("SELECT b FROM Booking b WHERE b.customerName LIKE %:searchTerm% OR b.customerEmail LIKE %:searchTerm% OR b.bookingReference LIKE %:searchTerm%")
    List<Booking> searchBookings(@Param("searchTerm") String searchTerm);
    
    Page<Booking> findByStatus(Booking.BookingStatus status, Pageable pageable);
    
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.bookingDate >= :startDate AND b.bookingDate <= :endDate AND b.status = 'CONFIRMED'")
    Long countBookingsInPeriod(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT SUM(b.finalAmount) FROM Booking b WHERE b.bookingDate >= :startDate AND b.bookingDate <= :endDate AND b.status = 'CONFIRMED'")
    Double totalRevenueInPeriod(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
