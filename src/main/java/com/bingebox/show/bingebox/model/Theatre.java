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
@Table(name = "theatres")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Theatre {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(name = "address", nullable = false)
    private String address;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;
    
    @Column(name = "contact_number")
    private String contactNumber;
    
    @Column(name = "email")
    private String email;
    
    @OneToMany(mappedBy = "theatre", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Show> shows;
    
    @OneToMany(mappedBy = "theatre", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Screen> screens;
    
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
