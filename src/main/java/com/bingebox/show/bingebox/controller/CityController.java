package com.bingebox.show.bingebox.controller;

import com.bingebox.show.bingebox.model.City;
import com.bingebox.show.bingebox.service.CityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cities")
@RequiredArgsConstructor
@Slf4j
public class CityController {

    private final CityService cityService;

    @GetMapping
    public ResponseEntity<List<City>> getAllCities() {
        log.info("GET /api/v1/cities - Fetching all cities");
        return ResponseEntity.ok(cityService.getAllActiveCities());
    }

    @GetMapping("/{id}")
    public ResponseEntity<City> getCityById(@PathVariable Long id) {
        log.info("GET /api/v1/cities/{} - Fetching city by id", id);
        return cityService.getCityById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<City> getCityByName(@PathVariable String name) {
        log.info("GET /api/v1/cities/name/{} - Fetching city by name", name);
        return cityService.getCityByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<City>> searchCities(@RequestParam String q) {
        log.info("GET /api/v1/cities/search?q={} - Searching cities", q);
        return ResponseEntity.ok(cityService.searchCities(q));
    }

    @PostMapping
    public ResponseEntity<City> createCity(@RequestBody City city) {
        log.info("POST /api/v1/cities - Creating new city: {}", city.getName());
        try {
            City createdCity = cityService.createCity(city);
            return ResponseEntity.ok(createdCity);
        } catch (RuntimeException e) {
            log.error("Error creating city {}: {}", city.getName(), e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<City> updateCity(@PathVariable Long id, @RequestBody City city) {
        log.info("PUT /api/v1/cities/{} - Updating city", id);
        try {
            City updatedCity = cityService.updateCity(id, city);
            return ResponseEntity.ok(updatedCity);
        } catch (RuntimeException e) {
            log.error("Error updating city with id {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCity(@PathVariable Long id) {
        log.info("DELETE /api/v1/cities/{} - Deleting city", id);
        cityService.deleteCity(id);
        return ResponseEntity.noContent().build();
    }
}
