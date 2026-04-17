package com.bingebox.show.bingebox.dto;

import com.bingebox.show.bingebox.model.ShowSeat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShowSeatDTO {
    private Long id;
    private Long showId;
    private Long screenId;
    private String seatNumber;
    private String rowNumber;
    private String seatType;
    private ShowSeat.SeatStatus status;
    private BigDecimal price;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
