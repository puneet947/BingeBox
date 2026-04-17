package com.bingebox.show.bingebox.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "show_seats")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ShowSeat {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_id", nullable = false)
    private Show show;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_id", nullable = false)
    private Screen screen;
    
    @Column(name = "seat_number", nullable = false)
    private String seatNumber;
    
    @Column(name = "row_number", nullable = false)
    private String rowNumber;
    
    @Column(name = "seat_type", nullable = false)
    private String seatType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "seat_status", nullable = false)
    @Builder.Default
    private SeatStatus status = SeatStatus.AVAILABLE;
    
    @Column(name = "price", nullable = false)
    private BigDecimal price;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum SeatStatus {
        AVAILABLE, BOOKED, LOCKED, MAINTENANCE
    }

    // Explicit getters and setters to fix compilation issues
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Show getShow() {
        return show;
    }

    public void setShow(Show show) {
        this.show = show;
    }

    public Screen getScreen() {
        return screen;
    }

    public void setScreen(Screen screen) {
        this.screen = screen;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(String rowNumber) {
        this.rowNumber = rowNumber;
    }

    public String getSeatType() {
        return seatType;
    }

    public void setSeatType(String seatType) {
        this.seatType = seatType;
    }

    public SeatStatus getStatus() {
        return status;
    }

    public void setStatus(SeatStatus status) {
        this.status = status;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
