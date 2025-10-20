package com.example.bookingsystem.application.mapper;

import com.example.bookingsystem.domain.Booking;
import com.example.bookingsystem.domain.Resource;
import com.example.bookingsystem.domain.User;
import com.example.bookingsystem.presentation.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface BookingMapper {
    // User mappings
    @Mapping(target = "role", expression = "java(dto.getRole() != null ? com.example.bookingsystem.domain.Role.valueOf(dto.getRole()) : com.example.bookingsystem.domain.Role.USER)")
    User toUser(UserRegistrationDTO dto);

    @Mapping(target = "role", expression = "java(user.getRole().name())")
    UserProfileDTO toUserProfileDTO(User user);

    // Resource mappings
    ResourceDTO toResourceDTO(Resource resource);

    Resource toResource(ResourceDTO dto);

    // Booking mappings
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "resource", ignore = true)
    @Mapping(target = "status", constant = "PENDING")
    Booking toBooking(BookingRequestDTO dto);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "resourceId", source = "resource.id")
    BookingResponseDTO toBookingResponseDTO(Booking booking);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "resource", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "id", ignore = true)
    Booking toBooking(BookingUpdateRequestDTO dto);
}
