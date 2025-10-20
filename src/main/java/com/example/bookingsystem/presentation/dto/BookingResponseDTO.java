package com.example.bookingsystem.presentation.dto;

import com.example.bookingsystem.domain.BookingStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BookingResponseDTO {
    private Long id;
    private Long userId;
    private Long resourceId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String purpose;
    private List<String> participants;
    private BookingStatus status;
    private Long version;
}
