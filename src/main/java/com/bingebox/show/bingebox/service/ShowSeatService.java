package com.bingebox.show.bingebox.service;

import com.bingebox.show.bingebox.model.ShowSeat;
import com.bingebox.show.bingebox.repository.ShowSeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ShowSeatService {

    private final ShowSeatRepository showSeatRepository;

    @Cacheable(value = "showSeats", key = "'show:' + #showId + ':available'")
    @Transactional(readOnly = true)
    public List<ShowSeat> getAvailableSeatsByShow(Long showId) {
        log.info("Fetching available seats for show id: {}", showId);
        return showSeatRepository.findAvailableSeatsByShow(showId);
    }

    @Cacheable(value = "showSeats", key = "'show:' + #showId + ':all'")
    @Transactional(readOnly = true)
    public List<ShowSeat> getActiveSeatsByShow(Long showId) {
        log.info("Fetching all active seats for show id: {}", showId);
        return showSeatRepository.findActiveSeatsByShow(showId);
    }

    @Cacheable(value = "showSeats", key = "'show:' + #showId + ':row:' + #rowNumber")
    @Transactional(readOnly = true)
    public List<ShowSeat> getSeatsByRow(Long showId, String rowNumber) {
        log.info("Fetching seats for show id: {} and row: {}", showId, rowNumber);
        return showSeatRepository.findSeatsByRow(showId, rowNumber);
    }

    @Cacheable(value = "showSeats", key = "'show:' + #showId + ':count:available'")
    @Transactional(readOnly = true)
    public Long countAvailableSeatsByShow(Long showId) {
        log.info("Counting available seats for show id: {}", showId);
        return showSeatRepository.countAvailableSeatsByShow(showId);
    }

    @Cacheable(value = "showSeats", key = "'show:' + #showId + ':count:booked'")
    @Transactional(readOnly = true)
    public Long countBookedSeatsByShow(Long showId) {
        log.info("Counting booked seats for show id: {}", showId);
        return showSeatRepository.countBookedSeatsByShow(showId);
    }

    @CacheEvict(value = "showSeats", allEntries = true)
    public ShowSeat createShowSeat(ShowSeat showSeat) {
        log.info("Creating show seat: {} for show: {}", showSeat.getSeatNumber(), showSeat.getShow().getId());
        return showSeatRepository.save(showSeat);
    }

    @CacheEvict(value = "showSeats", allEntries = true)
    public List<ShowSeat> createShowSeats(List<ShowSeat> showSeats) {
        log.info("Creating {} show seats", showSeats.size());
        return showSeatRepository.saveAll(showSeats);
    }

    @CacheEvict(value = "showSeats", allEntries = true)
    public ShowSeat updateShowSeat(Long id, ShowSeat showSeatDetails) {
        log.info("Updating show seat with id: {}", id);
        return showSeatRepository.findById(id)
                .map(showSeat -> {
                    showSeat.setSeatNumber(showSeatDetails.getSeatNumber());
                    showSeat.setRowNumber(showSeatDetails.getRowNumber());
                    showSeat.setSeatType(showSeatDetails.getSeatType());
                    showSeat.setStatus(showSeatDetails.getStatus());
                    showSeat.setPrice(showSeatDetails.getPrice());
                    return showSeatRepository.save(showSeat);
                })
                .orElseThrow(() -> new RuntimeException("ShowSeat not found with id: " + id));
    }

    @CacheEvict(value = "showSeats", allEntries = true)
    public void deleteShowSeat(Long id) {
        log.info("Deleting show seat with id: {}", id);
        showSeatRepository.findById(id)
                .ifPresent(showSeat -> {
                    showSeat.setStatus(ShowSeat.SeatStatus.MAINTENANCE);
                    showSeatRepository.save(showSeat);
                });
    }

    @CacheEvict(value = "showSeats", allEntries = true)
    public void allocateSeatInventory(Long showId, List<ShowSeat> seats) {
        log.info("Allocating seat inventory for show: {}, seats: {}", showId, seats.size());
        
        // This supports the requirement: "Theatres can allocate seat inventory and update them for the show"
        seats.forEach(seat -> {
            seat.setShowId(showId);
            seat.setStatus(ShowSeat.SeatStatus.AVAILABLE);
        });
        
        showSeatRepository.saveAll(seats);
    }

    @CacheEvict(value = "showSeats", allEntries = true)
    public void updateSeatInventory(Long showId, List<ShowSeat> seats) {
        log.info("Updating seat inventory for show: {}, seats: {}", showId, seats.size());
        
        // This supports the requirement: "Theatres can allocate seat inventory and update them for the show"
        seats.forEach(seat -> {
            if (seat.getShow().getId().equals(showId)) {
                showSeatRepository.save(seat);
            }
        });
    }

    @CacheEvict(value = "showSeats", allEntries = true)
    public void lockSeats(Long showId, List<String> seatNumbers) {
        log.info("Locking seats for show: {}, seats: {}", showId, seatNumbers);
        
        List<ShowSeat> seats = showSeatRepository.findByShowIdAndSeatNumberIn(showId, seatNumbers);
        seats.forEach(seat -> {
            if (seat.getStatus() == ShowSeat.SeatStatus.AVAILABLE) {
                seat.setStatus(ShowSeat.SeatStatus.LOCKED);
            }
        });
        
        showSeatRepository.saveAll(seats);
    }

    @CacheEvict(value = "showSeats", allEntries = true)
    public void releaseSeats(Long showId, List<String> seatNumbers) {
        log.info("Releasing seats for show: {}, seats: {}", showId, seatNumbers);
        
        List<ShowSeat> seats = showSeatRepository.findByShowIdAndSeatNumberIn(showId, seatNumbers);
        seats.forEach(seat -> {
            if (seat.getStatus() == ShowSeat.SeatStatus.LOCKED) {
                seat.setStatus(ShowSeat.SeatStatus.AVAILABLE);
            }
        });
        
        showSeatRepository.saveAll(seats);
    }

    @CacheEvict(value = "showSeats", allEntries = true)
    public void bookSeats(Long showId, List<String> seatNumbers) {
        log.info("Booking seats for show: {}, seats: {}", showId, seatNumbers);
        
        List<ShowSeat> seats = showSeatRepository.findByShowIdAndSeatNumberIn(showId, seatNumbers);
        seats.forEach(seat -> {
            if (seat.getStatus() == ShowSeat.SeatStatus.LOCKED) {
                seat.setStatus(ShowSeat.SeatStatus.BOOKED);
            }
        });
        
        showSeatRepository.saveAll(seats);
    }
}
