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
@Table(name = "screens")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Screen {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theatre_id", nullable = false)
    private Theatre theatre;
    
    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats;
    
    @Column(name = "rows_count", nullable = false)
    private Integer rowsCount;
    
    @Column(name = "seats_per_row", nullable = false)
    private Integer seatsPerRow;
    
    @OneToMany(mappedBy = "screen", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ShowSeat> showSeats;
    
    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
