package com.bingebox.show.bingebox.service;

import com.bingebox.show.bingebox.dto.MovieDTO;
import com.bingebox.show.bingebox.model.City;
import com.bingebox.show.bingebox.model.Movie;
import com.bingebox.show.bingebox.repository.CityRepository;
import com.bingebox.show.bingebox.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MovieService implements MovieServiceInterface {

    private final MovieRepository movieRepository;
    private final CityRepository cityRepository;

    @Cacheable(value = "movies", key = "'all'")
    @Transactional(readOnly = true)
    public List<Movie> getAllActiveMovies() {
        log.info("Fetching all active movies from database");
        return movieRepository.findByIsActiveTrue();
    }

    @Cacheable(value = "movies", key = "'page:' + #pageable.pageNumber + ':' + #pageable.pageSize")
    @Transactional(readOnly = true)
    public Page<Movie> getAllActiveMovies(Pageable pageable) {
        log.info("Fetching active movies page {} from database", pageable.getPageNumber());
        return movieRepository.findByIsActiveTrue(pageable);
    }

    @Cacheable(value = "movies", key = "'id:' + #id")
    @Transactional(readOnly = true)
    public Optional<Movie> getMovieById(Long id) {
        log.info("Fetching movie by id: {}", id);
        return movieRepository.findByIdAndIsActiveTrue(id);
    }

    @Cacheable(value = "movies", key = "'city:' + #cityId")
    @Transactional(readOnly = true)
    public List<Movie> getMoviesByCity(Long cityId) {
        log.info("Fetching movies for city id: {}", cityId);
        return movieRepository.findByIsActiveTrueAndCitiesId(cityId);
    }

    @Cacheable(value = "movies", key = "'city:' + #cityId + ':language:' + #language")
    @Transactional(readOnly = true)
    public List<Movie> getMoviesByCityAndLanguage(Long cityId, String language) {
        log.info("Fetching movies for city id: {} and language: {}", cityId, language);
        return movieRepository.findByIsActiveTrueAndCitiesIdAndLanguage(cityId, language);
    }

    @Cacheable(value = "movies", key = "'city:' + #cityId + ':genre:' + #genre")
    @Transactional(readOnly = true)
    public List<Movie> getMoviesByCityAndGenre(Long cityId, Movie.Genre genre) {
        log.info("Fetching movies for city id: {} and genre: {}", cityId, genre);
        return movieRepository.findByIsActiveTrueAndCitiesIdAndGenre(cityId, genre);
    }

    @Cacheable(value = "movies", key = "'city:' + #cityId + ':language:' + #language + ':genre:' + #genre")
    @Transactional(readOnly = true)
    public List<Movie> getMoviesByCityLanguageAndGenre(Long cityId, String language, Movie.Genre genre) {
        log.info("Fetching movies for city id: {}, language: {}, genre: {}", cityId, language, genre);
        return movieRepository.findByIsActiveTrueAndCitiesIdAndLanguageAndGenre(cityId, language, genre);
    }

    @Cacheable(value = "movies", key = "'language:' + #language")
    @Transactional(readOnly = true)
    public List<Movie> getMoviesByLanguage(String language) {
        log.info("Fetching movies for language: {}", language);
        return movieRepository.findByIsActiveTrueAndLanguage(language);
    }

    @Cacheable(value = "movies", key = "'genre:' + #genre")
    @Transactional(readOnly = true)
    public List<Movie> getMoviesByGenre(Movie.Genre genre) {
        log.info("Fetching movies for genre: {}", genre);
        return movieRepository.findByIsActiveTrueAndGenre(genre);
    }

    @Cacheable(value = "movies", key = "'language:' + #language + ':genre:' + #genre")
    @Transactional(readOnly = true)
    public List<Movie> getMoviesByLanguageAndGenre(String language, Movie.Genre genre) {
        log.info("Fetching movies for language: {} and genre: {}", language, genre);
        return movieRepository.findByIsActiveTrueAndLanguageAndGenre(language, genre);
    }

    @Cacheable(value = "movies", key = "'search:' + #searchTerm")
    @Transactional(readOnly = true)
    public List<Movie> searchMovies(String searchTerm) {
        log.info("Searching movies with term: {}", searchTerm);
        return movieRepository.searchMovies(searchTerm);
    }

    @Cacheable(value = "movies", key = "'search:' + #cityId + ':' + #searchTerm")
    @Transactional(readOnly = true)
    public List<Movie> searchMoviesByCity(Long cityId, String searchTerm) {
        log.info("Searching movies in city {} with term: {}", cityId, searchTerm);
        return movieRepository.searchMoviesByCity(cityId, searchTerm);
    }

    @CacheEvict(value = "movies", allEntries = true)
    public Movie createMovie(Movie movie) {
        log.info("Creating new movie: {}", movie.getTitle());
        movie.setIsActive(true);
        return movieRepository.save(movie);
    }

    @CacheEvict(value = "movies", allEntries = true)
    public Movie updateMovie(Long id, Movie movieDetails) {
        log.info("Updating movie with id: {}", id);
        return movieRepository.findByIdAndIsActiveTrue(id)
                .map(movie -> {
                    movie.setTitle(movieDetails.getTitle());
                    movie.setDescription(movieDetails.getDescription());
                    movie.setDurationMinutes(movieDetails.getDurationMinutes());
                    movie.setLanguage(movieDetails.getLanguage());
                    movie.setGenre(movieDetails.getGenre());
                    movie.setReleaseDate(movieDetails.getReleaseDate());
                    movie.setPosterUrl(movieDetails.getPosterUrl());
                    movie.setTrailerUrl(movieDetails.getTrailerUrl());
                    movie.setRating(movieDetails.getRating());
                    movie.setCities(movieDetails.getCities());
                    return movieRepository.save(movie);
                })
                .orElseThrow(() -> new RuntimeException("Movie not found with id: " + id));
    }

    @CacheEvict(value = "movies", allEntries = true)
    public void deleteMovie(Long id) {
        log.info("Soft deleting movie with id: {}", id);
        movieRepository.findByIdAndIsActiveTrue(id)
                .ifPresent(movie -> {
                    movie.setIsActive(false);
                    movieRepository.save(movie);
                });
    }

    @CacheEvict(value = "movies", allEntries = true)
    public Movie addCityToMovie(Long movieId, Long cityId) {
        log.info("Adding city {} to movie {}", cityId, movieId);
        Movie movie = movieRepository.findByIdAndIsActiveTrue(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found with id: " + movieId));
        
        City city = cityRepository.findByIdAndIsActiveTrue(cityId)
                .orElseThrow(() -> new RuntimeException("City not found with id: " + cityId));
        
        movie.getCities().add(city);
        return movieRepository.save(movie);
    }

    @CacheEvict(value = "movies", allEntries = true)
    public Movie removeCityFromMovie(Long movieId, Long cityId) {
        log.info("Removing city {} from movie {}", cityId, movieId);
        Movie movie = movieRepository.findByIdAndIsActiveTrue(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found with id: " + movieId));
        
        movie.getCities().removeIf(city -> city.getId().equals(cityId));
        return movieRepository.save(movie);
    }

    @Override
    public MovieDTO convertToDTO(Movie movie) {
        if (movie == null) return null;
        
        List<Long> cityIds = movie.getCities() != null ? 
                movie.getCities().stream()
                        .map(City::getId)
                        .collect(Collectors.toList()) : null;

        return MovieDTO.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .description(movie.getDescription())
                .durationMinutes(movie.getDurationMinutes())
                .language(movie.getLanguage())
                .genre(movie.getGenre())
                .releaseDate(movie.getReleaseDate())
                .posterUrl(movie.getPosterUrl())
                .trailerUrl(movie.getTrailerUrl())
                .rating(movie.getRating())
                .isActive(movie.getIsActive())
                .cityIds(cityIds)
                .createdAt(movie.getCreatedAt())
                .updatedAt(movie.getUpdatedAt())
                .build();
    }

    @Override
    public Movie convertToEntity(MovieDTO movieDTO) {
        if (movieDTO == null) return null;
        
        return Movie.builder()
                .id(movieDTO.getId())
                .title(movieDTO.getTitle())
                .description(movieDTO.getDescription())
                .durationMinutes(movieDTO.getDurationMinutes())
                .language(movieDTO.getLanguage())
                .genre(movieDTO.getGenre())
                .releaseDate(movieDTO.getReleaseDate())
                .posterUrl(movieDTO.getPosterUrl())
                .trailerUrl(movieDTO.getTrailerUrl())
                .rating(movieDTO.getRating())
                .isActive(movieDTO.getIsActive())
                .build();
    }

    @Override
    public List<MovieDTO> convertToDTOList(List<Movie> movies) {
        return movies.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}
