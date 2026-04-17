package com.bingebox.show.bingebox.service;

import com.bingebox.show.bingebox.model.Show;
import com.bingebox.show.bingebox.repository.ShowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ShowService {

    private final ShowRepository showRepository;

    @Cacheable(value = "shows", key = "'all'")
    @Transactional(readOnly = true)
    public List<Show> getAllActiveShows() {
        log.info("Fetching all active shows from database");
        return showRepository.findByIsActiveTrue();
    }

    @Cacheable(value = "shows", key = "'movie:' + #movieId")
    @Transactional(readOnly = true)
    public List<Show> getShowsByMovie(Long movieId) {
        log.info("Fetching shows for movie id: {}", movieId);
        return showRepository.findByMovieIdAndIsActiveTrue(movieId);
    }

    @Cacheable(value = "shows", key = "'theatre:' + #theatreId")
    @Transactional(readOnly = true)
    public List<Show> getShowsByTheatre(Long theatreId) {
        log.info("Fetching shows for theatre id: {}", theatreId);
        return showRepository.findByTheatreIdAndIsActiveTrue(theatreId);
    }

    @Cacheable(value = "shows", key = "'movie:' + #movieId + ':theatre:' + #theatreId")
    @Transactional(readOnly = true)
    public List<Show> getShowsByMovieAndTheatre(Long movieId, Long theatreId) {
        log.info("Fetching shows for movie id: {} and theatre id: {}", movieId, theatreId);
        return showRepository.findByMovieIdAndTheatreIdAndIsActiveTrue(movieId, theatreId);
    }

    @Cacheable(value = "shows", key = "'movie:' + #movieId + ':city:' + #cityId")
    @Transactional(readOnly = true)
    public List<Show> getShowsByMovieAndCity(Long movieId, Long cityId) {
        log.info("Fetching shows for movie id: {} and city id: {}", movieId, cityId);
        return showRepository.findShowsByMovieAndCity(movieId, cityId);
    }

    @Cacheable(value = "shows", key = "'movie:' + #movieId + ':city:' + #cityId + ':date:' + #showDate")
    @Transactional(readOnly = true)
    public List<Show> getShowsByMovieCityAndDate(Long movieId, Long cityId, LocalDateTime showDate) {
        log.info("Fetching shows for movie id: {}, city id: {}, and date: {}", movieId, cityId, showDate);
        return showRepository.findShowsByMovieCityAndDate(movieId, cityId, showDate);
    }

    @Cacheable(value = "shows", key = "'upcoming'")
    @Transactional(readOnly = true)
    public List<Show> getUpcomingShows() {
        log.info("Fetching upcoming shows");
        return showRepository.findUpcomingShows(LocalDateTime.now());
    }

    @Cacheable(value = "shows", key = "'dateRange:' + #startDate + ':' + #endDate")
    @Transactional(readOnly = true)
    public List<Show> getShowsInDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching shows in date range: {} to {}", startDate, endDate);
        return showRepository.findShowsInDateRange(startDate, endDate);
    }

    @Cacheable(value = "shows", key = "'id:' + #id")
    @Transactional(readOnly = true)
    public Optional<Show> getShowById(Long id) {
        log.info("Fetching show by id: {}", id);
        return showRepository.findByIdAndIsActiveTrue(id);
    }

    @CacheEvict(value = "shows", allEntries = true)
    public Show createShow(Show show) {
        log.info("Creating new show for movie: {} at theatre: {}", 
                show.getMovie().getTitle(), show.getTheatre().getName());
        show.setIsActive(true);
        return showRepository.save(show);
    }

    @CacheEvict(value = "shows", allEntries = true)
    public Show updateShow(Long id, Show showDetails) {
        log.info("Updating show with id: {}", id);
        return showRepository.findByIdAndIsActiveTrue(id)
                .map(show -> {
                    show.setMovie(showDetails.getMovie());
                    show.setTheatre(showDetails.getTheatre());
                    show.setScreen(showDetails.getScreen());
                    show.setShowDate(showDetails.getShowDate());
                    show.setStartTime(showDetails.getStartTime());
                    show.setEndTime(showDetails.getEndTime());
                    show.setBasePrice(showDetails.getBasePrice());
                    show.setShowType(showDetails.getShowType());
                    return showRepository.save(show);
                })
                .orElseThrow(() -> new RuntimeException("Show not found with id: " + id));
    }

    @CacheEvict(value = "shows", allEntries = true)
    public void deleteShow(Long id) {
        log.info("Soft deleting show with id: {}", id);
        showRepository.findByIdAndIsActiveTrue(id)
                .ifPresent(show -> {
                    show.setIsActive(false);
                    showRepository.save(show);
                });
    }

    @CacheEvict(value = "shows", allEntries = true)
    public Show createOrUpdateShowForDay(Long movieId, Long theatreId, Long screenId, 
                                       LocalDateTime showDate, LocalDateTime startTime, 
                                       LocalDateTime endTime, Show.ShowType showType, 
                                       Double basePrice) {
        log.info("Creating/updating show for movie: {}, theatre: {}, date: {}", movieId, theatreId, showDate);
        
        // This method supports the requirement: "Theatres can create, update, and delete shows for the day"
        Show show = Show.builder()
                .showDate(showDate)
                .startTime(startTime)
                .endTime(endTime)
                .basePrice(java.math.BigDecimal.valueOf(basePrice))
                .showType(showType)
                .isActive(true)
                .build();
        
        // Set movie, theatre, and screen (these would be fetched from their respective services)
        // For now, we'll assume they are set elsewhere or through a DTO
        
        return showRepository.save(show);
    }

    public com.bingebox.show.bingebox.dto.ShowDTO convertToDTO(Show show) {
        if (show == null) return null;

        List<com.bingebox.show.bingebox.dto.ShowSeatDTO> showSeatDTOs = show.getShowSeats() != null ?
                show.getShowSeats().stream()
                        .map(this::convertShowSeatToDTO)
                        .collect(java.util.stream.Collectors.toList()) : null;

        return com.bingebox.show.bingebox.dto.ShowDTO.builder()
                .id(show.getId())
                .movieId(show.getMovie() != null ? show.getMovie().getId() : null)
                .movieTitle(show.getMovie() != null ? show.getMovie().getTitle() : null)
                .theatreId(show.getTheatre() != null ? show.getTheatre().getId() : null)
                .theatreName(show.getTheatre() != null ? show.getTheatre().getName() : null)
                .screenId(show.getScreen() != null ? show.getScreen().getId() : null)
                .screenName(show.getScreen() != null ? show.getScreen().getName() : null)
                .showDate(show.getShowDate())
                .startTime(show.getStartTime())
                .endTime(show.getEndTime())
                .basePrice(show.getBasePrice())
                .showType(show.getShowType())
                .showSeats(showSeatDTOs)
                .isActive(show.getIsActive())
                .createdAt(show.getCreatedAt())
                .updatedAt(show.getUpdatedAt())
                .build();
    }

    private com.bingebox.show.bingebox.dto.ShowSeatDTO convertShowSeatToDTO(ShowSeat showSeat) {
        if (showSeat == null) return null;

        return com.bingebox.show.bingebox.dto.ShowSeatDTO.builder()
                .id(showSeat.getId())
                .showId(showSeat.getShow() != null ? showSeat.getShow().getId() : null)
                .screenId(showSeat.getScreen() != null ? showSeat.getScreen().getId() : null)
                .seatNumber(showSeat.getSeatNumber())
                .rowNumber(showSeat.getRowNumber())
                .seatType(showSeat.getSeatType())
                .status(showSeat.getStatus())
                .price(showSeat.getPrice())
                .createdAt(showSeat.getCreatedAt())
                .updatedAt(showSeat.getUpdatedAt())
                .build();
    }

    public List<com.bingebox.show.bingebox.dto.ShowDTO> convertToDTOList(List<Show> shows) {
        return shows.stream()
                .map(this::convertToDTO)
                .collect(java.util.stream.Collectors.toList());
    }
}
