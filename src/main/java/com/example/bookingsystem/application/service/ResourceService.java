package com.example.bookingsystem.application.service;

import com.example.bookingsystem.application.exception.ResourceNotFoundException;
import com.example.bookingsystem.application.mapper.BookingMapper;
import com.example.bookingsystem.domain.Resource;
import com.example.bookingsystem.infrastructure.repository.ResourceRepository;
import com.example.bookingsystem.presentation.dto.ResourceDTO;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final BookingMapper bookingMapper;

    public ResourceService(ResourceRepository resourceRepository, BookingMapper bookingMapper) {
        this.resourceRepository = resourceRepository;
        this.bookingMapper = bookingMapper;
    }

    public ResourceDTO createResource(ResourceDTO dto) {
        Resource resource = bookingMapper.toResource(dto);
        Resource savedResource = resourceRepository.save(resource);
        return bookingMapper.toResourceDTO(savedResource);
    }

    public ResourceDTO updateResource(Long id, ResourceDTO dto) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found: " + id));
        if (!resource.getVersion().equals(dto.getVersion())) {
            throw new org.springframework.orm.ObjectOptimisticLockingFailureException(Resource.class, id);
        }
        bookingMapper.toResource(dto).setId(id);
        Resource updatedResource = resourceRepository.save(bookingMapper.toResource(dto));
        return bookingMapper.toResourceDTO(updatedResource);
    }

    public void deleteResource(Long id) {
        if (!resourceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Resource not found: " + id);
        }
        resourceRepository.deleteById(id);
    }

    public List<ResourceDTO> getAllResources(String location, Integer minCapacity) {
        return resourceRepository.findByFilters(location, minCapacity)
                .stream()
                .map(bookingMapper::toResourceDTO)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "resources", key = "#id")
    public ResourceDTO getResourceById(Long id) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found: " + id));
        return bookingMapper.toResourceDTO(resource);
    }
}
