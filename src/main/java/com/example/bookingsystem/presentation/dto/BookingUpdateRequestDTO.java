package com.example.bookingsystem.presentation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BookingUpdateRequestDTO {
    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    private LocalDateTime endTime;

    private String purpose;

    private List<String> participants;

    @NotNull(message = "Version is required")
    private Long version;
}
