package com.bingebox.show.bingebox.service;

import com.bingebox.show.bingebox.model.City;
import com.bingebox.show.bingebox.repository.CityRepository;
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
public class CityService {

    private final CityRepository cityRepository;

    @Cacheable(value = "cities", key = "'all'")
    @Transactional(readOnly = true)
    public List<City> getAllActiveCities() {
        log.info("Fetching all active cities from database");
        return cityRepository.findByIsActiveTrue();
    }

    @Cacheable(value = "cities", key = "'id:' + #id")
    @Transactional(readOnly = true)
    public Optional<City> getCityById(Long id) {
        log.info("Fetching city by id: {}", id);
        return cityRepository.findByIdAndIsActiveTrue(id);
    }

    @Cacheable(value = "cities", key = "'name:' + #name")
    @Transactional(readOnly = true)
    public Optional<City> getCityByName(String name) {
        log.info("Fetching city by name: {}", name);
        return cityRepository.findByNameAndIsActiveTrue(name);
    }

    @Cacheable(value = "cities", key = "'search:' + #searchTerm")
    @Transactional(readOnly = true)
    public List<City> searchCities(String searchTerm) {
        log.info("Searching cities with term: {}", searchTerm);
        return cityRepository.searchCities(searchTerm);
    }

    @CacheEvict(value = "cities", allEntries = true)
    public City createCity(City city) {
        log.info("Creating new city: {}", city.getName());
        if (cityRepository.existsByNameAndIsActiveTrue(city.getName())) {
            throw new RuntimeException("City already exists with name: " + city.getName());
        }
        city.setIsActive(true);
        return cityRepository.save(city);
    }

    @CacheEvict(value = "cities", allEntries = true)
    public City updateCity(Long id, City cityDetails) {
        log.info("Updating city with id: {}", id);
        return cityRepository.findByIdAndIsActiveTrue(id)
                .map(city -> {
                    if (!city.getName().equals(cityDetails.getName()) && 
                        cityRepository.existsByNameAndIsActiveTrue(cityDetails.getName())) {
                        throw new RuntimeException("City already exists with name: " + cityDetails.getName());
                    }
                    city.setName(cityDetails.getName());
                    city.setState(cityDetails.getState());
                    city.setCountryCode(cityDetails.getCountryCode());
                    return cityRepository.save(city);
                })
                .orElseThrow(() -> new RuntimeException("City not found with id: " + id));
    }

    @CacheEvict(value = "cities", allEntries = true)
    public void deleteCity(Long id) {
        log.info("Soft deleting city with id: {}", id);
        cityRepository.findByIdAndIsActiveTrue(id)
                .ifPresent(city -> {
                    city.setIsActive(false);
                    cityRepository.save(city);
                });
    }
}
