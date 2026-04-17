package com.bingebox.show.bingebox.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "booking_seats")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class BookingSeat {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_seat_id", nullable = false)
    private ShowSeat showSeat;
    
    @Column(name = "seat_number", nullable = false)
    private String seatNumber;
    
    @Column(name = "row_number", nullable = false)
    private String rowNumber;
    
    @Column(name = "seat_price", nullable = false)
    private BigDecimal seatPrice;
    
    @Column(name = "discount_amount")
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;
    
    @Column(name = "final_price", nullable = false)
    private BigDecimal finalPrice;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
