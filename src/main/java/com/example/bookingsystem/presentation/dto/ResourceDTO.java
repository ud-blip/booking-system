package com.example.bookingsystem.presentation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalTime;

@Data
public class ResourceDTO {
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    private String location;

    @NotNull(message = "Working hours start is required")
    private LocalTime workingHoursStart;

    @NotNull(message = "Working hours end is required")
    private LocalTime workingHoursEnd;

    private Double costPerHour;

    private Long version;
}
