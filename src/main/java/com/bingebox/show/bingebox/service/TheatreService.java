package com.bingebox.show.bingebox.service;

import com.bingebox.show.bingebox.model.Theatre;
import com.bingebox.show.bingebox.repository.TheatreRepository;
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
public class TheatreService {

    private final TheatreRepository theatreRepository;

    @Cacheable(value = "theatres", key = "'all'")
    @Transactional(readOnly = true)
    public List<Theatre> getAllActiveTheatres() {
        log.info("Fetching all active theatres from database");
        return theatreRepository.findByIsActiveTrue();
    }

    @Cacheable(value = "theatres", key = "'city:' + #cityId")
    @Transactional(readOnly = true)
    public List<Theatre> getTheatresByCity(Long cityId) {
        log.info("Fetching theatres for city id: {}", cityId);
        return theatreRepository.findByCityIdAndIsActiveTrue(cityId);
    }

    @Cacheable(value = "theatres", key = "'id:' + #id")
    @Transactional(readOnly = true)
    public Optional<Theatre> getTheatreById(Long id) {
        log.info("Fetching theatre by id: {}", id);
        return theatreRepository.findByIdAndIsActiveTrue(id);
    }

    @Cacheable(value = "theatres", key = "'search:' + #searchTerm")
    @Transactional(readOnly = true)
    public List<Theatre> searchTheatres(String searchTerm) {
        log.info("Searching theatres with term: {}", searchTerm);
        return theatreRepository.searchTheatres(searchTerm);
    }

    @Cacheable(value = "theatres", key = "'city:' + #cityId + ':search:' + #searchTerm")
    @Transactional(readOnly = true)
    public List<Theatre> searchTheatresByCity(Long cityId, String searchTerm) {
        log.info("Searching theatres in city {} with term: {}", cityId, searchTerm);
        return theatreRepository.searchTheatresByCity(cityId, searchTerm);
    }

    @CacheEvict(value = "theatres", allEntries = true)
    public Theatre createTheatre(Theatre theatre) {
        log.info("Creating new theatre: {}", theatre.getName());
        if (theatreRepository.existsByNameAndIsActiveTrue(theatre.getName())) {
            throw new RuntimeException("Theatre already exists with name: " + theatre.getName());
        }
        theatre.setIsActive(true);
        return theatreRepository.save(theatre);
    }

    @CacheEvict(value = "theatres", allEntries = true)
    public Theatre updateTheatre(Long id, Theatre theatreDetails) {
        log.info("Updating theatre with id: {}", id);
        return theatreRepository.findByIdAndIsActiveTrue(id)
                .map(theatre -> {
                    if (!theatre.getName().equals(theatreDetails.getName()) && 
                        theatreRepository.existsByNameAndIsActiveTrue(theatreDetails.getName())) {
                        throw new RuntimeException("Theatre already exists with name: " + theatreDetails.getName());
                    }
                    theatre.setName(theatreDetails.getName());
                    theatre.setAddress(theatreDetails.getAddress());
                    theatre.setContactNumber(theatreDetails.getContactNumber());
                    theatre.setEmail(theatreDetails.getEmail());
                    theatre.setCity(theatreDetails.getCity());
                    return theatreRepository.save(theatre);
                })
                .orElseThrow(() -> new RuntimeException("Theatre not found with id: " + id));
    }

    @CacheEvict(value = "theatres", allEntries = true)
    public void deleteTheatre(Long id) {
        log.info("Soft deleting theatre with id: {}", id);
        theatreRepository.findByIdAndIsActiveTrue(id)
                .ifPresent(theatre -> {
                    theatre.setIsActive(false);
                    theatreRepository.save(theatre);
                });
    }
}
