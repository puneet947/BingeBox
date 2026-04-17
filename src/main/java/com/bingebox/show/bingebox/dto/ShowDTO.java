package com.bingebox.show.bingebox.dto;

import com.bingebox.show.bingebox.model.Show;
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
public class ShowDTO {
    private Long id;
    private Long movieId;
    private String movieTitle;
    private Long theatreId;
    private String theatreName;
    private Long screenId;
    private String screenName;
    private LocalDateTime showDate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal basePrice;
    private Show.ShowType showType;
    private List<ShowSeatDTO> showSeats;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
