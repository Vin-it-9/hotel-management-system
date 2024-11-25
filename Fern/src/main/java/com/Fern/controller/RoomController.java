package com.Fern.controller;

import com.Fern.dto.RoomRequest;
import com.Fern.entity.*;
import com.Fern.repository.AmenityRepository;
import com.Fern.repository.RoomTypeRepository;
import com.Fern.repository.UserRepo;
import com.Fern.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
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

    @Autowired
    private HttpSession httpSession;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserService userService;

    @ModelAttribute
    public void commonUser(Principal p, Model m) {
        if (p != null) {
            String email = p.getName();
            User user = userRepo.findByEmail(email);
            m.addAttribute("user", user);
        }
    }


    @GetMapping("/all")
    public String getAllRoomsDetailed(Model model,HttpSession session) {

        session.removeAttribute("checkInDate");
        session.removeAttribute("checkOutDate");

        List<Map<String, Object>> roomResponses = roomService.getAllRooms();

        model.addAttribute("rooms", roomResponses);
        model.addAttribute("roomTypes", roomTypeService.getAllRoomTypes());
        model.addAttribute("amenities", amenityService.getAllAmenities());

        return "list_rooms";
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

    @GetMapping("/{roomId}")
    public String getRoomDetailsById(@PathVariable Long roomId, Model model, Principal principal) {

        Optional<Map<String, Object>> roomDetails = roomService.getRoomsById(roomId);

        boolean isLoggedIn = principal != null;
        model.addAttribute("isLoggedIn", isLoggedIn);

        if (isLoggedIn) {
            String email = principal.getName();
            User user = userRepo.getUserByEmail(email);
            model.addAttribute("username", user.getName());
        }

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

    @GetMapping("/byPriceRange")
    public ResponseEntity<List<Map<String, Object>>> getRoomsByPriceRange(
            @RequestParam Double minPrice,
            @RequestParam Double maxPrice) {
        List<Map<String, Object>> rooms = roomService.getRoomsByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/filter")
    public String getAllRoomsFiltered(
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Long roomType,
            Model model) {

        model.addAttribute("roomTypes", roomTypeService.getAllRoomTypes());
        List<Map<String, Object>> rooms = roomService.getFilteredRooms(minPrice, maxPrice, roomType);
        model.addAttribute("rooms", rooms);

        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("selectedRoomType", roomType);

        return "list_rooms";

    }

    @PostMapping("/add")
    public ResponseEntity<String> addRoom(@RequestBody RoomRequest roomRequest) throws IOException, SQLException {

        byte[] imageBytes = null;

        RoomType roomType = roomTypeRepository.findById(roomRequest.getRoomTypeId())
                .orElseThrow(() -> new IllegalArgumentException("Room type not found"));

        Set<Amenity> amenities = new HashSet<>();
        for (Long amenityId : roomRequest.getAmenityIds()) {
            Amenity amenity = amenityRepository.findById(amenityId)
                    .orElseThrow(() -> new IllegalArgumentException("Amenity not found"));
            amenities.add(amenity);
        }

        RoomAvailability roomAvailability = new RoomAvailability();
        roomAvailability.setStatus("Available");

        Room addedRoom = roomService.addRoom(
                roomRequest.getRoomNumber(),
                roomRequest.getFloorNumber(),
                roomRequest.getSize(),
                roomRequest.getDescription(),
                imageBytes,
                roomRequest.getPricePerNight(),
                roomType,
                roomAvailability,
                amenities
        );

        return ResponseEntity.ok("Room added successfully");
    }

    @PostMapping("/available")
    public String getAvailableRooms(@RequestParam("checkInDate") String checkInDateStr,
                                    @RequestParam("checkOutDate") String checkOutDateStr,
                                    HttpSession session, Model model) {
        Date checkInDate;
        Date checkOutDate;

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            checkInDate = dateFormat.parse(checkInDateStr);
            checkOutDate = dateFormat.parse(checkOutDateStr);
        } catch (Exception e) {
            model.addAttribute("error", "Invalid date format. Please try again.");
            return "error";
        }

        session.setAttribute("checkInDate", checkInDate);
        session.setAttribute("checkOutDate", checkOutDate);

        List<Map<String, Object>> availableRooms = bookingService.getAvailableRooms(checkInDate, checkOutDate);

        model.addAttribute("checkInDate", checkInDate);
        model.addAttribute("checkOutDate", checkOutDate);
        model.addAttribute("rooms", availableRooms);

        return "list_rooms";
    }
}
