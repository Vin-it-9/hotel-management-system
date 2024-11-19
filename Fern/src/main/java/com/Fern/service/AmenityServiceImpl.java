package com.Fern.service;

import com.Fern.dto.AmenityDTO;
import com.Fern.entity.Amenity;
import com.Fern.repository.AmenityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AmenityServiceImpl implements AmenityService {

    @Autowired
    private AmenityRepository amenityRepository;


    @Override
    public void addAmenity(AmenityDTO amenityDTO) {
        if (StringUtils.isEmpty(amenityDTO.getName())) {
            throw new IllegalArgumentException("Amenity name cannot be empty");
        }

        if (amenityRepository.existsByName(amenityDTO.getName())) {
            throw new IllegalArgumentException("Amenity with this name already exists");
        }

        Amenity amenity = new Amenity();
        amenity.setName(amenityDTO.getName());
        amenity.setDescription(amenityDTO.getDescription());
        amenity.setCreatedAt(amenityDTO.getCreatedAt());

        amenityRepository.save(amenity);
    }

    @Override
    public List<AmenityDTO> getAllAmenities() {

        List<Amenity> amenities = amenityRepository.findAll();
        return amenities.stream()
                .map(amenity -> {
                    AmenityDTO amenityDTO = new AmenityDTO();
                    amenityDTO.setName(amenity.getName());
                    amenityDTO.setDescription(amenity.getDescription());
                    amenityDTO.setCreatedAt(amenity.getCreatedAt());
                    return amenityDTO;
                })
                .collect(Collectors.toList());
    }
}
