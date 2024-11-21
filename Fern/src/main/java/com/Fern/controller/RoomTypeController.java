package com.Fern.controller;

import com.Fern.dto.AmenityDTO;
import com.Fern.dto.RoomTypeDTO;
import com.Fern.entity.RoomType;
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

    @GetMapping("/add")
    public String addRoomType(Model model) {
        model.addAttribute("roomtype", new RoomTypeDTO());
        return "/admin/add_roomtype";
    }


    @PostMapping("/add")
    public String addRoomType(@RequestParam String typeName, @RequestParam String description, @RequestParam String purpose, Model model) {

        RoomTypeDTO roomTypeDTO = new RoomTypeDTO();
        roomTypeDTO.setTypeName(typeName);
        roomTypeDTO.setDescription(description);
        roomTypeDTO.setPurpose(purpose);

        try {
            roomTypeService.addRoomType(roomTypeDTO);
            model.addAttribute("successMessage", "roomtype added successfully!");
        } catch (Exception ex) {
            model.addAttribute("errorMessage", "Failed to add roomtype: " + ex.getMessage());
        }

        return "redirect:/amenities/list";
    }


//    @GetMapping("/list")
//    public String getAllRoomTypes(Model model) {
//        List<RoomTypeDTO> amenities = roomTypeService.getAllRoomTypes();
//        model.addAttribute("roomtype", amenities);
//        return "list_amenity";
//    }

    @GetMapping("/list")
    @ResponseBody
    public List<RoomTypeDTO> getAllRoomTypes() {
        List<RoomTypeDTO> roomTypes = roomTypeService.getAllRoomTypes();
        return roomTypes;
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

}
