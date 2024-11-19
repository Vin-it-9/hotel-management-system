package com.Fern.service;

import com.Fern.dto.AmenityDTO;
import com.Fern.entity.Amenity;
import com.Fern.repository.AmenityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AmenityServiceImpl implements AmenityService {

    @Autowired
    private AmenityRepository amenityRepository;


    @Override
    public void addAmenity(AmenityDTO amenityDTO) {
        if (StringUtils.isEmpty(amenityDTO.getName())) {
            throw new IllegalArgumentException("Amenity name cannot be empty");
        }

        // Check if the amenity already exists
        if (amenityRepository.existsByName(amenityDTO.getName())) {
            throw new IllegalArgumentException("Amenity with this name already exists");
        }

        Amenity amenity = new Amenity();
        amenity.setName(amenityDTO.getName());
        amenity.setDescription(amenityDTO.getDescription()); // If present in the DTO
        amenity.setCreatedAt(amenityDTO.getCreatedAt());  // If using createdAt in DTO

        // Save the new amenity to the database
        amenityRepository.save(amenity);
    }
}
