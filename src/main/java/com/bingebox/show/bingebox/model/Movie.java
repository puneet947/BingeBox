package com.bingebox.show.bingebox.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "movies")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Movie {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;
    
    @Column(nullable = false)
    private String language;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Genre genre;
    
    @Column(name = "release_date")
    private LocalDateTime releaseDate;
    
    @Column(name = "poster_url")
    private String posterUrl;
    
    @Column(name = "trailer_url")
    private String trailerUrl;
    
    @Column(name = "rating")
    private Double rating;
    
    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "movie_cities",
        joinColumns = @JoinColumn(name = "movie_id"),
        inverseJoinColumns = @JoinColumn(name = "city_id")
    )
    private List<City> cities;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum Genre {
        ACTION, COMEDY, DRAMA, HORROR, ROMANCE, THRILLER, 
        SCIENCE_FICTION, ANIMATION, DOCUMENTARY, ADVENTURE,
        CRIME, FANTASY, MYSTERY, FAMILY, MUSICAL
    }
}
