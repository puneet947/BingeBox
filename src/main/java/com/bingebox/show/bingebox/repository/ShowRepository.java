package com.bingebox.show.bingebox.repository;

import com.bingebox.show.bingebox.model.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShowRepository extends JpaRepository<Show, Long> {
    
    List<Show> findByIsActiveTrue();
    
    List<Show> findByMovieIdAndIsActiveTrue(Long movieId);
    
    List<Show> findByTheatreIdAndIsActiveTrue(Long theatreId);
    
    List<Show> findByMovieIdAndTheatreIdAndIsActiveTrue(Long movieId, Long theatreId);
    
    List<Show> findByShowDateBetweenAndIsActiveTrue(LocalDateTime startDate, LocalDateTime endDate);
    
    List<Show> findByMovieIdAndShowDateBetweenAndIsActiveTrue(Long movieId, LocalDateTime startDate, LocalDateTime endDate);
    
    Optional<Show> findByIdAndIsActiveTrue(Long id);
    
    @Query("SELECT s FROM Show s WHERE s.movie.id = :movieId AND s.theatre.city.id = :cityId AND s.isActive = true")
    List<Show> findShowsByMovieAndCity(@Param("movieId") Long movieId, @Param("cityId") Long cityId);
    
    @Query("SELECT s FROM Show s WHERE s.movie.id = :movieId AND s.theatre.city.id = :cityId AND " +
           "DATE(s.showDate) = DATE(:showDate) AND s.isActive = true")
    List<Show> findShowsByMovieCityAndDate(@Param("movieId") Long movieId, @Param("cityId") Long cityId, @Param("showDate") LocalDateTime showDate);
    
    @Query("SELECT s FROM Show s WHERE s.isActive = true AND s.showDate >= :currentDate ORDER BY s.showDate ASC")
    List<Show> findUpcomingShows(@Param("currentDate") LocalDateTime currentDate);
    
    @Query("SELECT s FROM Show s WHERE s.isActive = true AND s.showDate >= :currentDate AND s.showDate <= :endDate ORDER BY s.showDate ASC")
    List<Show> findShowsInDateRange(@Param("currentDate") LocalDateTime currentDate, @Param("endDate") LocalDateTime endDate);
}
