package com.Fern.controller;

import com.Fern.dto.RoomTypeDTO;
import com.Fern.service.RoomTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/roomtype")
public class RoomTypeController {

    @Autowired
    private RoomTypeService roomTypeService;

    public RoomTypeController(RoomTypeService roomTypeService) {
        this.roomTypeService = roomTypeService;
    }

    @GetMapping("/list")
    public String getAllRoomTypes(Model model) {
        List<RoomTypeDTO> roomTypes = roomTypeService.getAllRoomTypes();
        model.addAttribute("roomtype", roomTypes);
        return "list_amenity";
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomTypeDTO> getRoomTypeById(@PathVariable int id) {
        RoomTypeDTO roomTypeDTO = roomTypeService.getRoomTypeById(id);
        return new ResponseEntity<>(roomTypeDTO, HttpStatus.OK);
    }

    @PostMapping("/{id}")
    public ResponseEntity<String> deleteRoomTypeById(@PathVariable int id) {
        roomTypeService.deleteRoomTypeById(id);
        return new ResponseEntity<>("Amenity deleted successfully.", HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<String> addMultipleRoomTypes(@RequestBody List<RoomTypeDTO> roomTypeDTOList) {
        try {
            roomTypeService.addMultipleRoomTypes(roomTypeDTOList);
            return ResponseEntity.ok("Room types added successfully!");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to add room types: " + ex.getMessage());
        }
    }

}
