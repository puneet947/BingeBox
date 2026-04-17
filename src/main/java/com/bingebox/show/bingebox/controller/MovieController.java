package com.bingebox.show.bingebox.controller;

import com.bingebox.show.bingebox.dto.MovieDTO;
import com.bingebox.show.bingebox.model.Movie;
import com.bingebox.show.bingebox.service.MovieService;
import com.bingebox.show.bingebox.service.MovieServiceInterface;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/movies")
@RequiredArgsConstructor
@Slf4j
public class MovieController {

    private final MovieService movieService;

    @GetMapping
    public ResponseEntity<List<MovieDTO>> getAllMovies() {
        log.info("GET /api/v1/movies - Fetching all movies");
        return ResponseEntity.ok(movieService.convertToDTOList(movieService.getAllActiveMovies()));
    }

    @GetMapping("/page")
    public ResponseEntity<Page<MovieDTO>> getAllMoviesPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("GET /api/v1/movies/page - Fetching movies page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<Movie> moviePage = movieService.getAllActiveMovies(pageable);
        return ResponseEntity.ok(moviePage.map(movieService::convertToDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieDTO> getMovieById(@PathVariable Long id) {
        log.info("GET /api/v1/movies/{} - Fetching movie by id", id);
        return movieService.getMovieById(id)
                .map(movie -> ResponseEntity.ok(movieService.convertToDTO(movie)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/city/{cityId}")
    public ResponseEntity<List<MovieDTO>> getMoviesByCity(@PathVariable Long cityId) {
        log.info("GET /api/v1/movies/city/{} - Fetching movies by city", cityId);
        return ResponseEntity.ok(movieService.convertToDTOList(movieService.getMoviesByCity(cityId)));
    }

    @GetMapping("/city/{cityId}/language/{language}")
    public ResponseEntity<List<MovieDTO>> getMoviesByCityAndLanguage(
            @PathVariable Long cityId,
            @PathVariable String language) {
        log.info("GET /api/v1/movies/city/{}/language/{} - Fetching movies by city and language", cityId, language);
        return ResponseEntity.ok(movieService.convertToDTOList(movieService.getMoviesByCityAndLanguage(cityId, language)));
    }

    @GetMapping("/city/{cityId}/genre/{genre}")
    public ResponseEntity<List<MovieDTO>> getMoviesByCityAndGenre(
            @PathVariable Long cityId,
            @PathVariable Movie.Genre genre) {
        log.info("GET /api/v1/movies/city/{}/genre/{} - Fetching movies by city and genre", cityId, genre);
        return ResponseEntity.ok(movieService.convertToDTOList(movieService.getMoviesByCityAndGenre(cityId, genre)));
    }

    @GetMapping("/city/{cityId}/language/{language}/genre/{genre}")
    public ResponseEntity<List<MovieDTO>> getMoviesByCityLanguageAndGenre(
            @PathVariable Long cityId,
            @PathVariable String language,
            @PathVariable Movie.Genre genre) {
        log.info("GET /api/v1/movies/city/{}/language/{}/genre/{} - Fetching movies by city, language and genre", 
                cityId, language, genre);
        return ResponseEntity.ok(movieService.convertToDTOList(movieService.getMoviesByCityLanguageAndGenre(cityId, language, genre)));
    }

    @GetMapping("/language/{language}")
    public ResponseEntity<List<MovieDTO>> getMoviesByLanguage(@PathVariable String language) {
        log.info("GET /api/v1/movies/language/{} - Fetching movies by language", language);
        return ResponseEntity.ok(movieService.convertToDTOList(movieService.getMoviesByLanguage(language)));
    }

    @GetMapping("/genre/{genre}")
    public ResponseEntity<List<MovieDTO>> getMoviesByGenre(@PathVariable Movie.Genre genre) {
        log.info("GET /api/v1/movies/genre/{} - Fetching movies by genre", genre);
        return ResponseEntity.ok(movieService.convertToDTOList(movieService.getMoviesByGenre(genre)));
    }

    @GetMapping("/language/{language}/genre/{genre}")
    public ResponseEntity<List<MovieDTO>> getMoviesByLanguageAndGenre(
            @PathVariable String language,
            @PathVariable Movie.Genre genre) {
        log.info("GET /api/v1/movies/language/{}/genre/{} - Fetching movies by language and genre", language, genre);
        return ResponseEntity.ok(movieService.convertToDTOList(movieService.getMoviesByLanguageAndGenre(language, genre)));
    }

    @GetMapping("/search")
    public ResponseEntity<List<MovieDTO>> searchMovies(@RequestParam String q) {
        log.info("GET /api/v1/movies/search?q={} - Searching movies", q);
        return ResponseEntity.ok(movieService.convertToDTOList(movieService.searchMovies(q)));
    }

    @GetMapping("/city/{cityId}/search")
    public ResponseEntity<List<MovieDTO>> searchMoviesByCity(
            @PathVariable Long cityId,
            @RequestParam String q) {
        log.info("GET /api/v1/movies/city/{}/search?q={} - Searching movies in city", cityId, q);
        return ResponseEntity.ok(movieService.convertToDTOList(movieService.searchMoviesByCity(cityId, q)));
    }

    @PostMapping
    public ResponseEntity<MovieDTO> createMovie(@Valid @RequestBody MovieDTO movieDTO) {
        log.info("POST /api/v1/movies - Creating new movie: {}", movieDTO.getTitle());
        Movie movie = movieService.convertToEntity(movieDTO);
        Movie createdMovie = movieService.createMovie(movie);
        return ResponseEntity.ok(movieService.convertToDTO(createdMovie));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovieDTO> updateMovie(@PathVariable Long id, @Valid @RequestBody MovieDTO movieDTO) {
        log.info("PUT /api/v1/movies/{} - Updating movie", id);
        try {
            Movie movieDetails = movieService.convertToEntity(movieDTO);
            Movie updatedMovie = movieService.updateMovie(id, movieDetails);
            return ResponseEntity.ok(movieService.convertToDTO(updatedMovie));
        } catch (RuntimeException e) {
            log.error("Error updating movie with id {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        log.info("DELETE /api/v1/movies/{} - Deleting movie", id);
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{movieId}/cities/{cityId}")
    public ResponseEntity<MovieDTO> addCityToMovie(
            @PathVariable Long movieId,
            @PathVariable Long cityId) {
        log.info("POST /api/v1/movies/{}/cities/{} - Adding city to movie", movieId, cityId);
        try {
            Movie updatedMovie = movieService.addCityToMovie(movieId, cityId);
            return ResponseEntity.ok(movieService.convertToDTO(updatedMovie));
        } catch (RuntimeException e) {
            log.error("Error adding city {} to movie {}: {}", cityId, movieId, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{movieId}/cities/{cityId}")
    public ResponseEntity<MovieDTO> removeCityFromMovie(
            @PathVariable Long movieId,
            @PathVariable Long cityId) {
        log.info("DELETE /api/v1/movies/{}/cities/{} - Removing city from movie", movieId, cityId);
        try {
            Movie updatedMovie = movieService.removeCityFromMovie(movieId, cityId);
            return ResponseEntity.ok(movieService.convertToDTO(updatedMovie));
        } catch (RuntimeException e) {
            log.error("Error removing city {} from movie {}: {}", cityId, movieId, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
