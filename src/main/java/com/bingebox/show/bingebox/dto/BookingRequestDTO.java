package com.bingebox.show.bingebox.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequestDTO {
    
    @NotNull(message = "Show ID is required")
    private Long showId;
    
    @NotEmpty(message = "Seat numbers are required")
    @Size(min = 1, max = 20, message = "Booking must have between 1 and 20 seats")
    private List<@NotBlank(message = "Seat number cannot be blank") String> seatNumbers;
    
    @NotBlank(message = "Customer name is required")
    @Size(max = 100, message = "Customer name must not exceed 100 characters")
    private String customerName;
    
    @NotBlank(message = "Customer email is required")
    @Email(message = "Customer email must be valid")
    @Size(max = 100, message = "Customer email must not exceed 100 characters")
    private String customerEmail;
    
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Customer phone must be valid")
    private String customerPhone;
    
    @Size(max = 500, message = "Special requests must not exceed 500 characters")
    private String specialRequests;
}
