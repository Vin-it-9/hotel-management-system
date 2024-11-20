package com.Fern.controller;

import com.Fern.dto.AmenityDTO;
import com.Fern.service.AmenityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/amenities")
public class AmenityController {

    @Autowired
    private AmenityService amenityService;

    @PostMapping("/add")
    public ResponseEntity<String> addAmenity(@RequestBody AmenityDTO amenityDTO) {
        try {
            amenityService.addAmenity(amenityDTO);
            return new ResponseEntity<>("Amenity added successfully", HttpStatus.CREATED);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return new ResponseEntity<>("An error occurred while adding the amenity", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<AmenityDTO>> getAllAmenities() {
        List<AmenityDTO> amenities = amenityService.getAllAmenities();
        return new ResponseEntity<>(amenities, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AmenityDTO> getAmenityById(@PathVariable int id) {
        AmenityDTO amenityDTO = amenityService.getAmenityById(id);
        return new ResponseEntity<>(amenityDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAmenityById(@PathVariable int id) {
        amenityService.deleteAmenityById(id);
        return new ResponseEntity<>("Amenity deleted successfully.", HttpStatus.OK);
    }

}
