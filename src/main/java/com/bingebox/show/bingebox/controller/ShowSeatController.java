package com.bingebox.show.bingebox.controller;

import com.bingebox.show.bingebox.model.ShowSeat;
import com.bingebox.show.bingebox.service.ShowSeatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/show-seats")
@RequiredArgsConstructor
@Slf4j
public class ShowSeatController {

    private final ShowSeatService showSeatService;

    @GetMapping("/show/{showId}/available")
    public ResponseEntity<List<ShowSeat>> getAvailableSeatsByShow(@PathVariable Long showId) {
        log.info("GET /api/v1/show-seats/show/{}/available - Fetching available seats", showId);
        return ResponseEntity.ok(showSeatService.getAvailableSeatsByShow(showId));
    }

    @GetMapping("/show/{showId}/all")
    public ResponseEntity<List<ShowSeat>> getActiveSeatsByShow(@PathVariable Long showId) {
        log.info("GET /api/v1/show-seats/show/{}/all - Fetching all active seats", showId);
        return ResponseEntity.ok(showSeatService.getActiveSeatsByShow(showId));
    }

    @GetMapping("/show/{showId}/row/{rowNumber}")
    public ResponseEntity<List<ShowSeat>> getSeatsByRow(
            @PathVariable Long showId,
            @PathVariable String rowNumber) {
        log.info("GET /api/v1/show-seats/show/{}/row/{} - Fetching seats by row", showId, rowNumber);
        return ResponseEntity.ok(showSeatService.getSeatsByRow(showId, rowNumber));
    }

    @GetMapping("/show/{showId}/count/available")
    public ResponseEntity<Long> countAvailableSeatsByShow(@PathVariable Long showId) {
        log.info("GET /api/v1/show-seats/show/{}/count/available - Counting available seats", showId);
        return ResponseEntity.ok(showSeatService.countAvailableSeatsByShow(showId));
    }

    @GetMapping("/show/{showId}/count/booked")
    public ResponseEntity<Long> countBookedSeatsByShow(@PathVariable Long showId) {
        log.info("GET /api/v1/show-seats/show/{}/count/booked - Counting booked seats", showId);
        return ResponseEntity.ok(showSeatService.countBookedSeatsByShow(showId));
    }

    @PostMapping
    public ResponseEntity<ShowSeat> createShowSeat(@RequestBody ShowSeat showSeat) {
        log.info("POST /api/v1/show-seats - Creating show seat: {}", showSeat.getSeatNumber());
        try {
            ShowSeat createdSeat = showSeatService.createShowSeat(showSeat);
            return ResponseEntity.ok(createdSeat);
        } catch (RuntimeException e) {
            log.error("Error creating show seat: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<ShowSeat>> createShowSeats(@RequestBody List<ShowSeat> showSeats) {
        log.info("POST /api/v1/show-seats/bulk - Creating {} show seats", showSeats.size());
        try {
            List<ShowSeat> createdSeats = showSeatService.createShowSeats(showSeats);
            return ResponseEntity.ok(createdSeats);
        } catch (RuntimeException e) {
            log.error("Error creating bulk show seats: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShowSeat> updateShowSeat(@PathVariable Long id, @RequestBody ShowSeat showSeat) {
        log.info("PUT /api/v1/show-seats/{} - Updating show seat", id);
        try {
            ShowSeat updatedSeat = showSeatService.updateShowSeat(id, showSeat);
            return ResponseEntity.ok(updatedSeat);
        } catch (RuntimeException e) {
            log.error("Error updating show seat with id {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShowSeat(@PathVariable Long id) {
        log.info("DELETE /api/v1/show-seats/{} - Deleting show seat", id);
        showSeatService.deleteShowSeat(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/show/{showId}/allocate")
    public ResponseEntity<Void> allocateSeatInventory(
            @PathVariable Long showId,
            @RequestBody List<ShowSeat> seats) {
        log.info("POST /api/v1/show-seats/show/{}/allocate - Allocating {} seats", showId, seats.size());
        try {
            showSeatService.allocateSeatInventory(showId, seats);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            log.error("Error allocating seat inventory: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/show/{showId}/update")
    public ResponseEntity<Void> updateSeatInventory(
            @PathVariable Long showId,
            @RequestBody List<ShowSeat> seats) {
        log.info("PUT /api/v1/show-seats/show/{}/update - Updating {} seats", showId, seats.size());
        try {
            showSeatService.updateSeatInventory(showId, seats);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            log.error("Error updating seat inventory: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/show/{showId}/lock")
    public ResponseEntity<Void> lockSeats(
            @PathVariable Long showId,
            @RequestBody List<String> seatNumbers) {
        log.info("POST /api/v1/show-seats/show/{}/lock - Locking seats: {}", showId, seatNumbers);
        try {
            showSeatService.lockSeats(showId, seatNumbers);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            log.error("Error locking seats: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/show/{showId}/release")
    public ResponseEntity<Void> releaseSeats(
            @PathVariable Long showId,
            @RequestBody List<String> seatNumbers) {
        log.info("POST /api/v1/show-seats/show/{}/release - Releasing seats: {}", showId, seatNumbers);
        try {
            showSeatService.releaseSeats(showId, seatNumbers);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            log.error("Error releasing seats: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/show/{showId}/book")
    public ResponseEntity<Void> bookSeats(
            @PathVariable Long showId,
            @RequestBody List<String> seatNumbers) {
        log.info("POST /api/v1/show-seats/show/{}/book - Booking seats: {}", showId, seatNumbers);
        try {
            showSeatService.bookSeats(showId, seatNumbers);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            log.error("Error booking seats: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
