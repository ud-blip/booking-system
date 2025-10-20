package com.example.bookingsystem.application.service;

import com.example.bookingsystem.application.exception.*;
import com.example.bookingsystem.application.mapper.BookingMapper;
import com.example.bookingsystem.domain.*;
import com.example.bookingsystem.infrastructure.repository.*;
import com.example.bookingsystem.presentation.dto.*;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ResourceRepository resourceRepository;
    private final BookingMapper bookingMapper;
    private final ApplicationEventPublisher eventPublisher;

    public BookingService(BookingRepository bookingRepository, UserRepository userRepository,
                          ResourceRepository resourceRepository, BookingMapper bookingMapper,
                          ApplicationEventPublisher eventPublisher) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.resourceRepository = resourceRepository;
        this.bookingMapper = bookingMapper;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public BookingResponseDTO createBooking(BookingRequestDTO dto) {
        User user = getCurrentUser();
        Resource resource = resourceRepository.findById(dto.getResourceId())
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found: " + dto.getResourceId()));

        validateBookingTime(resource, dto.getStartTime(), dto.getEndTime());
        validateBookingLimits(user, dto.getStartTime());

        Booking booking = bookingMapper.toBooking(dto);
        booking.setUser(user);
        booking.setResource(resource);
        booking.setStatus(BookingStatus.CONFIRMED);

        Booking savedBooking = bookingRepository.save(booking);
        eventPublisher.publishEvent(new BookingCreatedEvent(
                user.getEmail(),
                resource.getName(),
                savedBooking.getStartTime(),
                savedBooking.getEndTime()
        ));

        return bookingMapper.toBookingResponseDTO(savedBooking);
    }

    @Transactional
    @CacheEvict(value = "availableSlots", allEntries = true)
    public BookingResponseDTO updateBooking(Long id, BookingUpdateRequestDTO dto) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + id));

        if (!booking.getVersion().equals(dto.getVersion())) {
            throw new org.springframework.orm.ObjectOptimisticLockingFailureException(Booking.class, id);
        }

        User user = getCurrentUser();
        if (!booking.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Unauthorized to update this booking");
        }

        Resource resource = booking.getResource();
        validateBookingTime(resource, dto.getStartTime(), dto.getEndTime());

        booking.setStartTime(dto.getStartTime());
        booking.setEndTime(dto.getEndTime());
        booking.setPurpose(dto.getPurpose());
        booking.setParticipants(dto.getParticipants());

        Booking updatedBooking = bookingRepository.save(booking);
        eventPublisher.publishEvent(new BookingUpdatedEvent(
                user.getEmail(),
                resource.getName(),
                updatedBooking.getStartTime(),
                updatedBooking.getEndTime()
        ));

        return bookingMapper.toBookingResponseDTO(updatedBooking);
    }

    @Transactional
    @CacheEvict(
            value = "availableSlots",
            key = "{#result.resource.id, #result.startTime.toLocalDate()}",
            beforeInvocation = false
    )
    public Booking cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + id));

        User user = getCurrentUser();
        if (!booking.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Unauthorized to cancel this booking");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        Booking savedBooking = bookingRepository.save(booking);
        eventPublisher.publishEvent(new BookingCancelledEvent(
                user.getEmail(),
                booking.getResource().getName(),
                booking.getStartTime(),
                booking.getEndTime()
        ));
        return savedBooking;
    }

    public List<BookingResponseDTO> getUserBookings() {
        User user = getCurrentUser();
        return bookingRepository.findByUserId(user.getId())
                .stream()
                .map(bookingMapper::toBookingResponseDTO)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "availableSlots", key = "{#resourceId, #date}")
    public List<TimeSlot> getAvailableSlots(Long resourceId, LocalDateTime date) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found: " + resourceId));

        LocalDateTime startOfDay = date.truncatedTo(ChronoUnit.DAYS)
                .with(resource.getWorkingHoursStart());
        LocalDateTime endOfDay = date.truncatedTo(ChronoUnit.DAYS)
                .with(resource.getWorkingHoursEnd());

        List<Booking> bookings = bookingRepository.findConflictingBookings(
                resourceId, startOfDay, endOfDay);

        List<TimeSlot> availableSlots = new java.util.ArrayList<>();
        LocalDateTime current = startOfDay;
        while (current.isBefore(endOfDay)) {
            final LocalDateTime slotStart = current;
            LocalDateTime slotEnd = current.plusMinutes(30);
            boolean isBooked = bookings.stream().anyMatch(b ->
                    b.getStartTime().isBefore(slotEnd) && b.getEndTime().isAfter(slotStart));
            if (!isBooked) {
                availableSlots.add(new TimeSlot(current, slotEnd));
            }
            current = slotEnd;
        }
        return availableSlots;
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    private void validateBookingTime(Resource resource, LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end) || start.equals(end)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        long durationMinutes = ChronoUnit.MINUTES.between(start, end);
        if (durationMinutes < Constants.MIN_BOOKING_DURATION_MINUTES ||
                durationMinutes > Constants.MAX_BOOKING_DURATION_HOURS * 60) {
            throw new IllegalArgumentException("Booking duration must be between " +
                    Constants.MIN_BOOKING_DURATION_MINUTES + " minutes and " +
                    Constants.MAX_BOOKING_DURATION_HOURS + " hours");
        }

        LocalDateTime workingStart = start.with(resource.getWorkingHoursStart());
        LocalDateTime workingEnd = start.with(resource.getWorkingHoursEnd());
        if (start.toLocalTime().isBefore(resource.getWorkingHoursStart()) ||
                end.toLocalTime().isAfter(resource.getWorkingHoursEnd()) ||
                !start.toLocalDate().equals(end.toLocalDate())) {
            throw new IllegalArgumentException("Booking must be within working hours and on the same day");
        }

        List<Booking> conflicts = bookingRepository.findConflictingBookings(resource.getId(), start, end);
        if (!conflicts.isEmpty()) {
            throw new BookingConflictException("Time slot is already booked");
        }
    }

    private void validateBookingLimits(User user, LocalDateTime startTime) {
        long count = bookingRepository.countByUserIdAndDate(user.getId(), startTime);
        if (count >= Constants.MAX_BOOKINGS_PER_USER_PER_DAY) {
            throw new BookingLimitExceededException("User has reached the daily booking limit");
        }
    }
}
