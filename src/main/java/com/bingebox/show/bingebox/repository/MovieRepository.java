package com.bingebox.show.bingebox.repository;

import com.bingebox.show.bingebox.model.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    
    List<Movie> findByIsActiveTrue();
    
    List<Movie> findByIsActiveTrueAndCitiesId(Long cityId);
    
    List<Movie> findByIsActiveTrueAndLanguage(String language);
    
    List<Movie> findByIsActiveTrueAndGenre(Movie.Genre genre);
    
    List<Movie> findByIsActiveTrueAndCitiesIdAndLanguage(Long cityId, String language);
    
    List<Movie> findByIsActiveTrueAndCitiesIdAndGenre(Long cityId, Movie.Genre genre);
    
    List<Movie> findByIsActiveTrueAndLanguageAndGenre(String language, Movie.Genre genre);
    
    List<Movie> findByIsActiveTrueAndCitiesIdAndLanguageAndGenre(Long cityId, String language, Movie.Genre genre);
    
    @Query("SELECT m FROM Movie m JOIN m.cities c WHERE c.id = :cityId AND m.isActive = true")
    List<Movie> findMoviesByCity(@Param("cityId") Long cityId);
    
    @Query("SELECT m FROM Movie m WHERE m.isActive = true AND " +
           "(LOWER(m.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(m.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Movie> searchMovies(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT m FROM Movie m JOIN m.cities c WHERE c.id = :cityId AND m.isActive = true AND " +
           "(LOWER(m.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(m.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Movie> searchMoviesByCity(@Param("cityId") Long cityId, @Param("searchTerm") String searchTerm);
    
    Page<Movie> findByIsActiveTrue(Pageable pageable);
    
    Optional<Movie> findByIdAndIsActiveTrue(Long id);
}
