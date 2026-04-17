package com.bingebox.show.bingebox.dto;

import com.bingebox.show.bingebox.model.Booking;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDTO {
    private Long id;
    
    @NotBlank(message = "Booking reference is required")
    @Size(max = 50, message = "Booking reference must not exceed 50 characters")
    private String bookingReference;
    
    @NotNull(message = "Movie ID is required")
    private Long movieId;
    
    private String movieTitle;
    
    @NotNull(message = "Theatre ID is required")
    private Long theatreId;
    
    private String theatreName;
    
    @NotNull(message = "Show ID is required")
    private Long showId;
    
    @NotNull(message = "Show date is required")
    @Future(message = "Show date must be in the future")
    private LocalDateTime showDate;
    
    @NotBlank(message = "Customer name is required")
    @Size(max = 100, message = "Customer name must not exceed 100 characters")
    private String customerName;
    
    @NotBlank(message = "Customer email is required")
    @Email(message = "Customer email must be valid")
    @Size(max = 100, message = "Customer email must not exceed 100 characters")
    private String customerEmail;
    
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Customer phone must be valid")
    private String customerPhone;
    
    @NotEmpty(message = "Booking seats are required")
    @Size(min = 1, max = 20, message = "Booking must have between 1 and 20 seats")
    private List<BookingSeatDTO> bookingSeats;
    
    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.01", message = "Total amount must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Total amount must have maximum 10 integer digits and 2 decimal digits")
    private BigDecimal totalAmount;
    
    @DecimalMin(value = "0.0", message = "Discount amount must be at least 0")
    @Digits(integer = 10, fraction = 2, message = "Discount amount must have maximum 10 integer digits and 2 decimal digits")
    private BigDecimal discountAmount;
    
    @NotNull(message = "Final amount is required")
    @DecimalMin(value = "0.01", message = "Final amount must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Final amount must have maximum 10 integer digits and 2 decimal digits")
    private BigDecimal finalAmount;
    
    @NotNull(message = "Booking status is required")
    private Booking.BookingStatus status;
    
    @NotNull(message = "Payment status is required")
    private Booking.PaymentStatus paymentStatus;
    
    @NotNull(message = "Booking date is required")
    private LocalDateTime bookingDate;
    
    @Size(max = 500, message = "Special requests must not exceed 500 characters")
    private String specialRequests;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
