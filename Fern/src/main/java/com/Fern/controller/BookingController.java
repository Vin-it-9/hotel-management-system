package com.Fern.controller;

import com.Fern.dto.BookingDTO;
import com.Fern.entity.Booking;
import com.Fern.repository.AmenityRepository;
import com.Fern.repository.RoomTypeRepository;
import com.Fern.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private BookingServiceImpl bookingServiceImpl;


    @Autowired
    private RoomTypeServiceImpl roomTypeServiceImpl;

    @Autowired
    private RoomServiceImpl roomServiceImpl;

    @Autowired
    private AmenityService amenityService;

    @Autowired
    private RoomTypeService roomTypeService;

    public BookingController(BookingService bookingService, RoomService roomService, RoomTypeServiceImpl roomTypeServiceImpl, RoomTypeService roomTypeService) {
        this.bookingService = bookingService;
        this.roomService = roomService;
        this.roomTypeServiceImpl = roomTypeServiceImpl;
        this.roomTypeService = roomTypeService;
    }

    @GetMapping("/create")
    public String create() {

        return "booking";
    }

    @PostMapping("/create")
    public ResponseEntity<Booking> createBooking(@ModelAttribute BookingDTO bookingDTO) {
        try {
            Booking createdBooking = bookingService.createBooking(bookingDTO);
            return ResponseEntity
                    .created(URI.create("/bookings/" + createdBooking.getId()))
                    .body(createdBooking);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception ex) {
            return ResponseEntity.status(500).body(null);
        }
    }


//    @PostMapping("/create")
//    public ResponseEntity<Booking> createBooking(@ModelAttribute BookingDTO bookingDTO) {
//        try {
//            Booking createdBooking = bookingService.createBooking(bookingDTO);
//            return ResponseEntity
//                    .created(URI.create("/bookings/" + createdBooking.getId()))
//                    .body(createdBooking);
//        } catch (IllegalArgumentException ex) {
//            return ResponseEntity.badRequest().body(null);
//        } catch (Exception ex) {
//            return ResponseEntity.status(500).body(null);
//        }
//    }


//    @PostMapping("/rooms/available")
//    public ResponseEntity<List<Map<String, Object>>> getAvailableRooms(@RequestBody Map<String, String> dateRequest) {
//        Date checkInDate;
//        Date checkOutDate;
//        try {
//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//            checkInDate = dateFormat.parse(dateRequest.get("checkInDate"));
//            checkOutDate = dateFormat.parse(dateRequest.get("checkOutDate"));
//        } catch (ParseException e) {
//            return ResponseEntity.badRequest().body(Collections.emptyList());
//        }
//        List<Map<String, Object>> availableRooms = bookingServiceImpl.getAvailableRooms(checkInDate, checkOutDate);
//
//        return ResponseEntity.ok(availableRooms);
//
//    }


    @PostMapping("/rooms/available")
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

        List<Map<String, Object>> availableRooms = bookingServiceImpl.getAvailableRooms(checkInDate, checkOutDate);

        model.addAttribute("checkInDate", checkInDate);
        model.addAttribute("checkOutDate", checkOutDate);
        model.addAttribute("rooms", availableRooms);

        return "list_rooms";
    }



}
