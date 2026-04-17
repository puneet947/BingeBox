package com.bingebox.show.bingebox.service;

import com.bingebox.show.bingebox.dto.MovieDTO;
import com.bingebox.show.bingebox.model.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface MovieServiceInterface {
    
    // Basic CRUD operations
    List<Movie> getAllActiveMovies();
    Page<Movie> getAllActiveMovies(Pageable pageable);
    Optional<Movie> getMovieById(Long id);
    Movie createMovie(Movie movie);
    Movie updateMovie(Long id, Movie movieDetails);
    void deleteMovie(Long id);
    
    // City-based operations
    List<Movie> getMoviesByCity(Long cityId);
    List<Movie> getMoviesByCityAndLanguage(Long cityId, String language);
    List<Movie> getMoviesByCityAndGenre(Long cityId, Movie.Genre genre);
    List<Movie> getMoviesByCityLanguageAndGenre(Long cityId, String language, Movie.Genre genre);
    Movie addCityToMovie(Long movieId, Long cityId);
    Movie removeCityFromMovie(Long movieId, Long cityId);
    
    // Language and genre operations
    List<Movie> getMoviesByLanguage(String language);
    List<Movie> getMoviesByGenre(Movie.Genre genre);
    List<Movie> getMoviesByLanguageAndGenre(String language, Movie.Genre genre);
    
    // Search operations
    List<Movie> searchMovies(String searchTerm);
    List<Movie> searchMoviesByCity(Long cityId, String searchTerm);
    
    // DTO operations
    MovieDTO convertToDTO(Movie movie);
    Movie convertToEntity(MovieDTO movieDTO);
    List<MovieDTO> convertToDTOList(List<Movie> movies);
}
