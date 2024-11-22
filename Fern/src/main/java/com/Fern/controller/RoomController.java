package com.Fern.controller;


import com.Fern.dto.RoomDTO;
import com.Fern.dto.RoomRequest;
import com.Fern.entity.Amenity;
import com.Fern.entity.Room;
import com.Fern.entity.RoomAvailability;
import com.Fern.entity.RoomType;
import com.Fern.repository.AmenityRepository;
import com.Fern.repository.RoomAvailabilityRepository;
import com.Fern.repository.RoomRepository;
import com.Fern.repository.RoomTypeRepository;
import com.Fern.service.AmenityService;
import com.Fern.service.RoomService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @Autowired
    private AmenityService amenityService;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomTypeRepository roomTypeRepository;
    @Autowired
    private AmenityRepository amenityRepository;

    public RoomController(RoomService roomService, RoomTypeRepository roomTypeRepository,
                          AmenityRepository amenityRepository) {
        this.roomService = roomService;
        this.roomTypeRepository = roomTypeRepository;
        this.amenityRepository = amenityRepository;
    }


//    @PostMapping("/add")
//    public ResponseEntity<RoomDTO> addRoom(@RequestBody RoomDTO roomDTO) {
//        try {
//
//            RoomDTO savedRoomDTO = roomService.addRoom(roomDTO);
//            return new ResponseEntity<>(savedRoomDTO, HttpStatus.CREATED);
//
//        } catch (Exception e) {
//            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
//        }
//    }

//
//    @PostMapping("/add")
//    public ResponseEntity<?> addRoom(@RequestBody RoomRequest roomRequest) throws SQLException {
//
//        // Process uploaded image if provided
//        byte[] imageBytes = roomRequest.getImage() != null ? roomRequest.getImage() : null;
//
//        // Fetch RoomType
//        RoomType roomType = roomTypeRepository.findById(roomRequest.getRoomTypeId())
//                .orElseThrow(() -> new IllegalArgumentException("Room type not found"));
//
//        // Fetch Amenities
//        Set<Amenity> amenities = new HashSet<>();
//        for (Long amenityId : roomRequest.getAmenityIds()) {
//            Amenity amenity = amenityRepository.findById(amenityId)
//                    .orElseThrow(() -> new IllegalArgumentException("Amenity not found"));
//            amenities.add(amenity);
//        }
//
//        // Create RoomAvailability object
//        RoomAvailability roomAvailability = new RoomAvailability();
//        roomAvailability.setStatus("Available");
//
//        // Add Room using Service
//        Room addedRoom = roomService.addRoom(roomRequest.getRoomNumber(), roomRequest.getFloorNumber(),
//                roomRequest.getSize(), roomRequest.getDescription(), imageBytes,
//                roomRequest.getPricePerNight(), roomType, roomAvailability, amenities);
//
//        // Return ResponseEntity with the created Room's details
//        return ResponseEntity.ok(Map.of(
//                "message", "Room added successfully",
//                "room", addedRoom
//        ));
//    }

    @PostMapping("/add")
    public ResponseEntity<?> addRoom(
            @RequestParam("roomNumber") String roomNumber,
            @RequestParam("floorNumber") int floorNumber,
            @RequestParam("size") double size,
            @RequestParam("description") String description,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam("pricePerNight") double pricePerNight,
            @RequestParam("roomTypeId") Long roomTypeId,
            @RequestParam("amenityIds") Set<Long> amenityIds
    ) throws IOException, SQLException {

        // Process image bytes if provided
        byte[] imageBytes = image != null ? image.getBytes() : null;

        // Fetch RoomType
        RoomType roomType = roomTypeRepository.findById(roomTypeId)
                .orElseThrow(() -> new IllegalArgumentException("Room type not found"));

        // Fetch Amenities
        Set<Amenity> amenities = new HashSet<>();
        for (Long amenityId : amenityIds) {
            Amenity amenity = amenityRepository.findById(amenityId)
                    .orElseThrow(() -> new IllegalArgumentException("Amenity not found"));
            amenities.add(amenity);
        }

        // Create RoomAvailability object
        RoomAvailability roomAvailability = new RoomAvailability();
        roomAvailability.setStatus("Available");

        // Add Room using Service
        Room addedRoom = roomService.addRoom(
                roomNumber, floorNumber, size, description, imageBytes,
                pricePerNight, roomType, roomAvailability, amenities
        );

        // Return ResponseEntity with the created Room's details
        return ResponseEntity.ok(addedRoom);
    }
}
