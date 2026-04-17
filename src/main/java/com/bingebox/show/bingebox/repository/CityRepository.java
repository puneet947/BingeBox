package com.bingebox.show.bingebox.repository;

import com.bingebox.show.bingebox.model.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {
    
    List<City> findByIsActiveTrue();
    
    Optional<City> findByIdAndIsActiveTrue(Long id);
    
    Optional<City> findByNameAndIsActiveTrue(String name);
    
    @Query("SELECT c FROM City c WHERE c.isActive = true AND " +
           "(LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.state) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<City> searchCities(@Param("searchTerm") String searchTerm);
    
    boolean existsByNameAndIsActiveTrue(String name);
}
