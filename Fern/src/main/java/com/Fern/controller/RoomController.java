package com.Fern.controller;


import com.Fern.entity.Room;
import com.Fern.repository.AmenityRepository;
import com.Fern.repository.RoomTypeRepository;
import com.Fern.service.AmenityService;
import com.Fern.service.RoomService;
import com.Fern.service.RoomServiceImpl;
import com.Fern.service.RoomTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@Controller
@RequestMapping("/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    @Autowired
    private AmenityRepository amenityRepository;

    @Autowired
    private RoomServiceImpl roomServiceImpl;


    @Autowired
    private AmenityService amenityService;

    @Autowired
    private RoomTypeService roomTypeService;


    public RoomController(RoomService roomService, RoomTypeRepository roomTypeRepository,
                          AmenityRepository amenityRepository) {
        this.roomService = roomService;
        this.roomTypeRepository = roomTypeRepository;
        this.amenityRepository = amenityRepository;
    }


    @GetMapping("/image/{id}")
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


//
//    @GetMapping("/rooms/all")
//    public String getAllRoomsDetailed(Model model) {
//        List<Map<String, Object>> roomResponses = roomService.getAllRooms();
//        model.addAttribute("rooms", roomResponses);
//        return "list_rooms";
//    }


//    @GetMapping("/all")
//    @ResponseBody
//    public List<Map<String, Object>> getAllRooms() {
//        return roomService.getAllRooms(); // Assuming roomService.getAllRooms() returns List<Map<String, Object>>
//    }

    @GetMapping("/{roomId}")
    public String getRoomDetailsById(@PathVariable Long roomId, Model model) {
        Optional<Map<String, Object>> roomDetails = roomService.getRoomsById(roomId);
        if (roomDetails.isPresent()) {
            model.addAttribute("room", roomDetails.get());
            return "list_rooms_id";
        } else {
            return "error/404";
        }
    }

    @PostMapping("/delete/{roomId}")
    public ResponseEntity<String> deleteRoom(@PathVariable Long roomId) {
        try {
            roomService.deleteRoom(roomId);
            return ResponseEntity.ok("Room with ID " + roomId + " has been successfully deleted.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Room with ID " + roomId + " not found.");
        }
    }

    @GetMapping("/byRoomType/{roomTypeId}")
    @ResponseBody
    public List<Map<String, Object>> getRoomsByRoomTypeId(@PathVariable Long roomTypeId) {
        return roomService.getRoomsByRoomTypeId(roomTypeId);
    }

//
//    @GetMapping("/byRoomType/{roomTypeId}")
//    public String getRoomsByRoomTypeId(@PathVariable Long roomTypeId, Model model) {
//        List<Map<String, Object>> room = roomService.getRoomsByRoomTypeId(roomTypeId);
//        model.addAttribute("rooms", room);
//        return "list_rooms";
//    }


    @GetMapping("/byPriceRange")
    public ResponseEntity<List<Map<String, Object>>> getRoomsByPriceRange(
            @RequestParam Double minPrice,
            @RequestParam Double maxPrice) {
        List<Map<String, Object>> rooms = roomService.getRoomsByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(rooms);
    }


    @GetMapping("/filter-options")
    @ResponseBody
    public Map<String, Object> getFilterOptions() {
        Map<String, Object> options = new HashMap<>();
        options.put("roomTypes", roomTypeService.getAllRoomTypes());
        options.put("amenities", amenityService.getAllAmenities());
        return options;
    }

    @GetMapping("/filter")
    public String getAllRoomsFiltered(
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Long roomType,
            Model model) {

        model.addAttribute("roomTypes", roomTypeService.getAllRoomTypes());
        model.addAttribute("amenities", amenityService.getAllAmenities());

        List<Map<String, Object>> rooms = roomService.getFilteredRooms(minPrice, maxPrice, roomType);
        model.addAttribute("rooms", rooms);

        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("selectedRoomType", roomType);

        return "list_rooms";

    }

    @GetMapping("/all")
    public String getAllRoomsDetailed(Model model) {
        List<Map<String, Object>> roomResponses = roomService.getAllRooms();
        model.addAttribute("rooms", roomResponses);
        model.addAttribute("roomTypes", roomTypeService.getAllRoomTypes());
        model.addAttribute("amenities", amenityService.getAllAmenities());
        return "list_rooms";
    }

}
