package com.example.bookingsystem.infrastructure.repository;

import com.example.bookingsystem.domain.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ResourceRepository extends JpaRepository<Resource, Long> {
    List<Resource> findByLocation(String location);
    List<Resource> findByCapacityGreaterThanEqual(int capacity);

    @Query("SELECT r FROM Resource r WHERE (:location IS NULL OR r.location = :location) AND (:minCapacity IS NULL OR r.capacity >= :minCapacity)")
    List<Resource> findByFilters(@Param("location") String location, @Param("minCapacity") Integer minCapacity);
}
