package com.bingebox.show.bingebox.repository;

import com.bingebox.show.bingebox.model.Theatre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TheatreRepository extends JpaRepository<Theatre, Long> {
    
    List<Theatre> findByIsActiveTrue();
    
    List<Theatre> findByCityIdAndIsActiveTrue(Long cityId);
    
    Optional<Theatre> findByIdAndIsActiveTrue(Long id);
    
    @Query("SELECT t FROM Theatre t WHERE t.isActive = true AND " +
           "(LOWER(t.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(t.address) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Theatre> searchTheatres(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT t FROM Theatre t WHERE t.city.id = :cityId AND t.isActive = true AND " +
           "(LOWER(t.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(t.address) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Theatre> searchTheatresByCity(@Param("cityId") Long cityId, @Param("searchTerm") String searchTerm);
    
    boolean existsByNameAndIsActiveTrue(String name);
}
