package com.bingebox.show.bingebox.repository;

import com.bingebox.show.bingebox.model.ShowSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShowSeatRepository extends JpaRepository<ShowSeat, Long> {
    
    List<ShowSeat> findByShowIdAndStatus(Long showId, ShowSeat.SeatStatus status);
    
    List<ShowSeat> findByShowId(Long showId);
    
    List<ShowSeat> findByShowIdAndSeatNumberIn(Long showId, List<String> seatNumbers);
    
    Optional<ShowSeat> findByShowIdAndSeatNumber(Long showId, String seatNumber);
    
    @Query("SELECT ss FROM ShowSeat ss WHERE ss.show.id = :showId AND ss.status = 'AVAILABLE' ORDER BY ss.rowNumber, ss.seatNumber")
    List<ShowSeat> findAvailableSeatsByShow(@Param("showId") Long showId);
    
    @Query("SELECT ss FROM ShowSeat ss WHERE ss.show.id = :showId AND ss.status != 'MAINTENANCE' ORDER BY ss.rowNumber, ss.seatNumber")
    List<ShowSeat> findActiveSeatsByShow(@Param("showId") Long showId);
    
    @Query("SELECT COUNT(ss) FROM ShowSeat ss WHERE ss.show.id = :showId AND ss.status = 'BOOKED'")
    Long countBookedSeatsByShow(@Param("showId") Long showId);
    
    @Query("SELECT COUNT(ss) FROM ShowSeat ss WHERE ss.show.id = :showId AND ss.status = 'AVAILABLE'")
    Long countAvailableSeatsByShow(@Param("showId") Long showId);
    
    @Query("SELECT ss FROM ShowSeat ss WHERE ss.show.id = :showId AND ss.rowNumber = :rowNumber ORDER BY ss.seatNumber")
    List<ShowSeat> findSeatsByRow(@Param("showId") Long showId, @Param("rowNumber") String rowNumber);
    
    boolean existsByShowIdAndSeatNumber(Long showId, String seatNumber);
}
