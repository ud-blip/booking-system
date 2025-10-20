package com.example.bookingsystem.application.service;

import java.time.LocalDateTime;

public class BookingCancelledEvent {
    private final String userEmail;
    private final String resourceName;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;

    public BookingCancelledEvent(String userEmail, String resourceName, LocalDateTime startTime, LocalDateTime endTime) {
        this.userEmail = userEmail;
        this.resourceName = resourceName;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getUserEmail() { return userEmail; }
    public String getResourceName() { return resourceName; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
}