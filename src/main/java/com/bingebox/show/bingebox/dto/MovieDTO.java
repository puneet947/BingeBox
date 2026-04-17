package com.bingebox.show.bingebox.dto;

import com.bingebox.show.bingebox.model.Movie;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieDTO {
    private Long id;
    
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;
    
    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;
    
    @NotNull(message = "Duration minutes is required")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    @Max(value = 600, message = "Duration must not exceed 600 minutes")
    private Integer durationMinutes;
    
    @NotBlank(message = "Language is required")
    @Size(max = 50, message = "Language must not exceed 50 characters")
    private String language;
    
    @NotNull(message = "Genre is required")
    private Movie.Genre genre;
    
    private LocalDateTime releaseDate;
    
    @URL(message = "Poster URL must be valid")
    private String posterUrl;
    
    @URL(message = "Trailer URL must be valid")
    private String trailerUrl;
    
    @DecimalMin(value = "0.0", message = "Rating must be at least 0.0")
    @DecimalMax(value = "10.0", message = "Rating must not exceed 10.0")
    private Double rating;
    
    private Boolean isActive;
    private List<Long> cityIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
