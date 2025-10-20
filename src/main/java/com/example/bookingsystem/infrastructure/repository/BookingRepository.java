package com.example.bookingsystem.infrastructure.repository;

import com.example.bookingsystem.domain.Booking;
import com.example.bookingsystem.domain.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);

    @Query("SELECT b FROM Booking b WHERE b.resource.id = :resourceId AND b.status != 'CANCELLED' AND b.startTime < :endTime AND b.endTime > :startTime")
    List<Booking> findConflictingBookings(
            @Param("resourceId") Long resourceId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.user.id = :userId AND DATE(b.startTime) = DATE(:date) AND b.status != 'CANCELLED'")
    long countByUserIdAndDate(@Param("userId") Long userId, @Param("date") LocalDateTime date);
}
