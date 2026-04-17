package com.bingebox.show.bingebox.controller;

import com.bingebox.show.bingebox.model.Show;
import com.bingebox.show.bingebox.service.ShowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/shows")
@RequiredArgsConstructor
@Slf4j
public class ShowController {

    private final ShowService showService;

    @GetMapping
    public ResponseEntity<List<Show>> getAllShows() {
        log.info("GET /api/v1/shows - Fetching all shows");
        return ResponseEntity.ok(showService.getAllActiveShows());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Show> getShowById(@PathVariable Long id) {
        log.info("GET /api/v1/shows/{} - Fetching show by id", id);
        return showService.getShowById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<List<Show>> getShowsByMovie(@PathVariable Long movieId) {
        log.info("GET /api/v1/shows/movie/{} - Fetching shows by movie", movieId);
        return ResponseEntity.ok(showService.getShowsByMovie(movieId));
    }

    @GetMapping("/theatre/{theatreId}")
    public ResponseEntity<List<Show>> getShowsByTheatre(@PathVariable Long theatreId) {
        log.info("GET /api/v1/shows/theatre/{} - Fetching shows by theatre", theatreId);
        return ResponseEntity.ok(showService.getShowsByTheatre(theatreId));
    }

    @GetMapping("/movie/{movieId}/theatre/{theatreId}")
    public ResponseEntity<List<Show>> getShowsByMovieAndTheatre(
            @PathVariable Long movieId,
            @PathVariable Long theatreId) {
        log.info("GET /api/v1/shows/movie/{}/theatre/{} - Fetching shows by movie and theatre", movieId, theatreId);
        return ResponseEntity.ok(showService.getShowsByMovieAndTheatre(movieId, theatreId));
    }

    @GetMapping("/movie/{movieId}/city/{cityId}")
    public ResponseEntity<List<Show>> getShowsByMovieAndCity(
            @PathVariable Long movieId,
            @PathVariable Long cityId) {
        log.info("GET /api/v1/shows/movie/{}/city/{} - Fetching shows by movie and city", movieId, cityId);
        return ResponseEntity.ok(showService.getShowsByMovieAndCity(movieId, cityId));
    }

    @GetMapping("/movie/{movieId}/city/{cityId}/date/{showDate}")
    public ResponseEntity<List<Show>> getShowsByMovieCityAndDate(
            @PathVariable Long movieId,
            @PathVariable Long cityId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime showDate) {
        log.info("GET /api/v1/shows/movie/{}/city/{}/date/{} - Fetching shows by movie, city and date", 
                movieId, cityId, showDate);
        return ResponseEntity.ok(showService.getShowsByMovieCityAndDate(movieId, cityId, showDate));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<Show>> getUpcomingShows() {
        log.info("GET /api/v1/shows/upcoming - Fetching upcoming shows");
        return ResponseEntity.ok(showService.getUpcomingShows());
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<Show>> getShowsInDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("GET /api/v1/shows/date-range?startDate={}&endDate={} - Fetching shows in date range", 
                startDate, endDate);
        return ResponseEntity.ok(showService.getShowsInDateRange(startDate, endDate));
    }

    @PostMapping
    public ResponseEntity<Show> createShow(@RequestBody Show show) {
        log.info("POST /api/v1/shows - Creating new show");
        try {
            Show createdShow = showService.createShow(show);
            return ResponseEntity.ok(createdShow);
        } catch (RuntimeException e) {
            log.error("Error creating show: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Show> updateShow(@PathVariable Long id, @RequestBody Show show) {
        log.info("PUT /api/v1/shows/{} - Updating show", id);
        try {
            Show updatedShow = showService.updateShow(id, show);
            return ResponseEntity.ok(updatedShow);
        } catch (RuntimeException e) {
            log.error("Error updating show with id {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShow(@PathVariable Long id) {
        log.info("DELETE /api/v1/shows/{} - Deleting show", id);
        showService.deleteShow(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/day")
    public ResponseEntity<Show> createOrUpdateShowForDay(@RequestBody ShowDayRequest request) {
        log.info("POST /api/v1/shows/day - Creating/updating show for day");
        try {
            Show show = showService.createOrUpdateShowForDay(
                    request.getMovieId(),
                    request.getTheatreId(),
                    request.getScreenId(),
                    request.getShowDate(),
                    request.getStartTime(),
                    request.getEndTime(),
                    request.getShowType(),
                    request.getBasePrice()
            );
            return ResponseEntity.ok(show);
        } catch (RuntimeException e) {
            log.error("Error creating/updating show for day: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    public static class ShowDayRequest {
        private Long movieId;
        private Long theatreId;
        private Long screenId;
        private LocalDateTime showDate;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Show.ShowType showType;
        private Double basePrice;

        // Getters and Setters
        public Long getMovieId() { return movieId; }
        public void setMovieId(Long movieId) { this.movieId = movieId; }
        public Long getTheatreId() { return theatreId; }
        public void setTheatreId(Long theatreId) { this.theatreId = theatreId; }
        public Long getScreenId() { return screenId; }
        public void setScreenId(Long screenId) { this.screenId = screenId; }
        public LocalDateTime getShowDate() { return showDate; }
        public void setShowDate(LocalDateTime showDate) { this.showDate = showDate; }
        public LocalDateTime getStartTime() { return startTime; }
        public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
        public LocalDateTime getEndTime() { return endTime; }
        public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
        public Show.ShowType getShowType() { return showType; }
        public void setShowType(Show.ShowType showType) { this.showType = showType; }
        public Double getBasePrice() { return basePrice; }
        public void setBasePrice(Double basePrice) { this.basePrice = basePrice; }
    }
}
