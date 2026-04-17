package com.bingebox.show.bingebox.controller;

import com.bingebox.show.bingebox.model.Theatre;
import com.bingebox.show.bingebox.service.TheatreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/theatres")
@RequiredArgsConstructor
@Slf4j
public class TheatreController {

    private final TheatreService theatreService;

    @GetMapping
    public ResponseEntity<List<Theatre>> getAllTheatres() {
        log.info("GET /api/v1/theatres - Fetching all theatres");
        return ResponseEntity.ok(theatreService.getAllActiveTheatres());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Theatre> getTheatreById(@PathVariable Long id) {
        log.info("GET /api/v1/theatres/{} - Fetching theatre by id", id);
        return theatreService.getTheatreById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/city/{cityId}")
    public ResponseEntity<List<Theatre>> getTheatresByCity(@PathVariable Long cityId) {
        log.info("GET /api/v1/theatres/city/{} - Fetching theatres by city", cityId);
        return ResponseEntity.ok(theatreService.getTheatresByCity(cityId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Theatre>> searchTheatres(@RequestParam String q) {
        log.info("GET /api/v1/theatres/search?q={} - Searching theatres", q);
        return ResponseEntity.ok(theatreService.searchTheatres(q));
    }

    @GetMapping("/city/{cityId}/search")
    public ResponseEntity<List<Theatre>> searchTheatresByCity(
            @PathVariable Long cityId,
            @RequestParam String q) {
        log.info("GET /api/v1/theatres/city/{}/search?q={} - Searching theatres in city", cityId, q);
        return ResponseEntity.ok(theatreService.searchTheatresByCity(cityId, q));
    }

    @PostMapping
    public ResponseEntity<Theatre> createTheatre(@RequestBody Theatre theatre) {
        log.info("POST /api/v1/theatres - Creating new theatre: {}", theatre.getName());
        try {
            Theatre createdTheatre = theatreService.createTheatre(theatre);
            return ResponseEntity.ok(createdTheatre);
        } catch (RuntimeException e) {
            log.error("Error creating theatre {}: {}", theatre.getName(), e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Theatre> updateTheatre(@PathVariable Long id, @RequestBody Theatre theatre) {
        log.info("PUT /api/v1/theatres/{} - Updating theatre", id);
        try {
            Theatre updatedTheatre = theatreService.updateTheatre(id, theatre);
            return ResponseEntity.ok(updatedTheatre);
        } catch (RuntimeException e) {
            log.error("Error updating theatre with id {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTheatre(@PathVariable Long id) {
        log.info("DELETE /api/v1/theatres/{} - Deleting theatre", id);
        theatreService.deleteTheatre(id);
        return ResponseEntity.noContent().build();
    }
}
