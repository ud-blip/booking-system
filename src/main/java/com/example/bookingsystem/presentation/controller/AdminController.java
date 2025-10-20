package com.example.bookingsystem.presentation.controller;

import com.example.bookingsystem.application.service.ResourceService;
import com.example.bookingsystem.presentation.dto.ResourceDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Admin", description = "Endpoints for admin operations")
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final ResourceService resourceService;

    public AdminController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @Operation(summary = "Create a new resource", responses = {
            @ApiResponse(responseCode = "200", description = "Resource created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/resources")
    public ResponseEntity<ResourceDTO> createResource(@Valid @RequestBody ResourceDTO dto) {
        return ResponseEntity.ok(resourceService.createResource(dto));
    }

    @Operation(summary = "Update an existing resource", responses = {
            @ApiResponse(responseCode = "200", description = "Resource updated successfully"),
            @ApiResponse(responseCode = "404", description = "Resource not found"),
            @ApiResponse(responseCode = "409", description = "Conflict due to optimistic locking")
    })
    @PutMapping("/resources/{id}")
    public ResponseEntity<ResourceDTO> updateResource(
            @PathVariable Long id,
            @Valid @RequestBody ResourceDTO dto,
            @RequestHeader("If-Match") Long version) {
        dto.setVersion(version);
        return ResponseEntity.ok(resourceService.updateResource(id, dto));
    }

    @Operation(summary = "Delete a resource", responses = {
            @ApiResponse(responseCode = "200", description = "Resource deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Resource not found")
    })
    @DeleteMapping("/resources/{id}")
    public ResponseEntity<Void> deleteResource(@PathVariable Long id) {
        resourceService.deleteResource(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get all resources", responses = {
            @ApiResponse(responseCode = "200", description = "List of resources returned")
    })
    @GetMapping("/resources")
    public ResponseEntity<List<ResourceDTO>> getAllResources(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer minCapacity) {
        return ResponseEntity.ok(resourceService.getAllResources(location, minCapacity));
    }
}
