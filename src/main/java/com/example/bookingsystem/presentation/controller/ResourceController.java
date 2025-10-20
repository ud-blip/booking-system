package com.example.bookingsystem.presentation.controller;

import com.example.bookingsystem.application.service.ResourceService;
import com.example.bookingsystem.application.service.TimeSlot;
import com.example.bookingsystem.presentation.dto.ResourceDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Resources", description = "Endpoints for managing and viewing resources")
@RestController
@RequestMapping("/api/resources")
public class ResourceController {

    private final ResourceService resourceService;
    private final com.example.bookingsystem.application.service.BookingService bookingService;

    public ResourceController(ResourceService resourceService, com.example.bookingsystem.application.service.BookingService bookingService) {
        this.resourceService = resourceService;
        this.bookingService = bookingService;
    }

    @Operation(summary = "Get available resources", responses = {
            @ApiResponse(responseCode = "200", description = "List of resources returned")
    })
    @GetMapping("/available")
    public ResponseEntity<List<ResourceDTO>> getAvailableResources(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer minCapacity) {
        return ResponseEntity.ok(resourceService.getAllResources(location, minCapacity));
    }

    @Operation(summary = "Get available time slots for a resource", responses = {
            @ApiResponse(responseCode = "200", description = "List of available time slots"),
            @ApiResponse(responseCode = "404", description = "Resource not found")
    })
    @GetMapping("/{id}/slots")
    public ResponseEntity<List<TimeSlot>> getAvailableSlots(
            @PathVariable Long id,
            @RequestParam("date") LocalDateTime date) {
        return ResponseEntity.ok(bookingService.getAvailableSlots(id, date));
    }
}
