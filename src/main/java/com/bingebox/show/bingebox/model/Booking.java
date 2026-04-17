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
import java.util.List;

@Entity
@Table(name = "bookings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Booking {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "booking_reference", nullable = false, unique = true)
    private String bookingReference;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theatre_id", nullable = false)
    private Theatre theatre;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_id", nullable = false)
    private Show show;
    
    @Column(name = "customer_name", nullable = false)
    private String customerName;
    
    @Column(name = "customer_email", nullable = false)
    private String customerEmail;
    
    @Column(name = "customer_phone")
    private String customerPhone;
    
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BookingSeat> bookingSeats;
    
    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;
    
    @Column(name = "discount_amount")
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;
    
    @Column(name = "final_amount", nullable = false)
    private BigDecimal finalAmount;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private BookingStatus status = BookingStatus.PENDING;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    
    @Column(name = "booking_date", nullable = false)
    private LocalDateTime bookingDate;
    
    @Column(name = "special_requests")
    private String specialRequests;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum BookingStatus {
        PENDING, CONFIRMED, CANCELLED, COMPLETED
    }
    
    public enum PaymentStatus {
        PENDING, PAID, FAILED, REFUNDED
    }
}
