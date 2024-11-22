package com.Fern.controller;


import com.Fern.entity.Amenity;
import com.Fern.entity.Room;
import com.Fern.entity.RoomAvailability;
import com.Fern.entity.RoomType;
import com.Fern.repository.AmenityRepository;
import com.Fern.repository.RoomTypeRepository;
import com.Fern.service.RoomService;
import com.Fern.service.RoomServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

@Controller
public class RoomController {

    @Autowired
    private RoomService roomService;

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    @Autowired
    private AmenityRepository amenityRepository;

    @Autowired
    private RoomServiceImpl roomServiceImpl;


    public RoomController(RoomService roomService, RoomTypeRepository roomTypeRepository,
                          AmenityRepository amenityRepository) {
        this.roomService = roomService;
        this.roomTypeRepository = roomTypeRepository;
        this.amenityRepository = amenityRepository;
    }

    @GetMapping("/admin/rooms/add")
    public String showAddRoomForm(Model model) {
        model.addAttribute("roomTypes", roomTypeRepository.findAll());
        model.addAttribute("amenities", amenityRepository.findAll());
        return "add_room";
    }


    @PostMapping("/admin/rooms/add")
    public  String addRoom(
            @RequestParam("roomNumber") String roomNumber,
            @RequestParam("floorNumber") int floorNumber,
            @RequestParam("size") double size,
            @RequestParam("description") String description,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam("pricePerNight") double pricePerNight,
            @RequestParam("roomTypeId") Long roomTypeId,
            @RequestParam("amenityIds") Set<Long> amenityIds
    ) throws IOException, SQLException {

        byte[] imageBytes = image != null ? image.getBytes() : null;

        RoomType roomType = roomTypeRepository.findById(roomTypeId)
                .orElseThrow(() -> new IllegalArgumentException("Room type not found"));

        Set<Amenity> amenities = new HashSet<>();
        for (Long amenityId : amenityIds) {
            Amenity amenity = amenityRepository.findById(amenityId)
                    .orElseThrow(() -> new IllegalArgumentException("Amenity not found"));
            amenities.add(amenity);
        }

        RoomAvailability roomAvailability = new RoomAvailability();
        roomAvailability.setStatus("Available");

        Room addedRoom = roomService.addRoom(
                roomNumber, floorNumber, size, description, imageBytes,
                pricePerNight, roomType, roomAvailability, amenities
        );

        return "redirect:/rooms/all";

    }


    @GetMapping("/rooms/image/{id}")
    public ResponseEntity<byte[]> getRoomImage(@PathVariable Long id) {

        Optional<Room> room = roomServiceImpl.getRoomById(id);

        if (room.isPresent() && room.get().getImage() != null) {
            try {
                byte[] imageBytes = room.get().getImage().getBinaryStream().readAllBytes();
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(imageBytes);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
        return ResponseEntity.notFound().build();
    }



    @GetMapping("/rooms/all")
    public String getAllRoomsDetailed(Model model) {
        List<Map<String, Object>> roomResponses = roomService.getAllRooms();
        model.addAttribute("rooms", roomResponses);
        return "list_rooms";
    }


//    @GetMapping("/all")
//    @ResponseBody
//    public List<Map<String, Object>> getAllRooms() {
//        return roomService.getAllRooms(); // Assuming roomService.getAllRooms() returns List<Map<String, Object>>
//    }

    @GetMapping("/rooms/{roomId}")
    public String getRoomDetailsById(@PathVariable Long roomId, Model model) {
        Optional<Map<String, Object>> roomDetails = roomService.getRoomsById(roomId);
        if (roomDetails.isPresent()) {
            model.addAttribute("room", roomDetails.get());
            return "list_rooms_id"; // Thymeleaf template to display room details
        } else {
            return "error/404"; // Render a 404 error page if the room is not found
        }
    }

    @PostMapping("/rooms/delete/{roomId}")
    public ResponseEntity<String> deleteRoom(@PathVariable Long roomId) {
        try {
            roomService.deleteRoom(roomId);
            return ResponseEntity.ok("Room with ID " + roomId + " has been successfully deleted.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Room with ID " + roomId + " not found.");
        }
    }


}
