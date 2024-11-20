package com.Fern.service;

import com.Fern.dto.AmenityDTO;

import java.util.*;

public interface AmenityService {

    void addAmenity(AmenityDTO amenityDTO);

    public List<AmenityDTO> getAllAmenities();

    public AmenityDTO getAmenityById(int id);

    void deleteAmenityById(int id);
}
