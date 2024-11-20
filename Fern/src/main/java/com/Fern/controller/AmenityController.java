package com.Fern.controller;

import com.Fern.dto.AmenityDTO;
import com.Fern.service.AmenityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/amenities")
public class AmenityController {

    @Autowired
    private AmenityService amenityService;

    public AmenityController(AmenityService amenityService) {
        this.amenityService = amenityService;
    }

//    @PostMapping("/admin/amenities/add")
//    public ResponseEntity<String> addAmenity(@RequestBody AmenityDTO amenityDTO) {
//        try {
//            amenityService.addAmenity(amenityDTO);
//            return new ResponseEntity<>("Amenity added successfully", HttpStatus.CREATED);
//        } catch (IllegalArgumentException ex) {
//            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
//        } catch (Exception ex) {
//            return new ResponseEntity<>("An error occurred while adding the amenity", HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    @GetMapping("/list")
        public String getAllAmenities(Model model) {
            List<AmenityDTO> amenities = amenityService.getAllAmenities();
            model.addAttribute("amenities", amenities);
              return "list_amenity";

       }

    @GetMapping("/{id}")
    public ResponseEntity<AmenityDTO> getAmenityById(@PathVariable int id) {
        AmenityDTO amenityDTO = amenityService.getAmenityById(id);
        return new ResponseEntity<>(amenityDTO, HttpStatus.OK);
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<String> deleteAmenityById(@PathVariable int id) {
        amenityService.deleteAmenityById(id);
        return new ResponseEntity<>("Amenity deleted successfully.", HttpStatus.OK);
    }

}
