package com.example.bookingsystem.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Table(name = "resources")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer capacity;

    private String location;

    @Column(name = "working_hours_start", nullable = false)
    private LocalTime workingHoursStart;

    @Column(name = "working_hours_end", nullable = false)
    private LocalTime workingHoursEnd;

    @Column(name = "cost_per_hour")
    private Double costPerHour;

    @Version
    private Long version;
}
