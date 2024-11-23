package com.Fern.controller;

import com.Fern.dto.BookingDTO;
import com.Fern.entity.Booking;
import com.Fern.service.BookingService;
import com.Fern.service.BookingServiceImpl;
import com.Fern.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private RoomService roomService;
    @Autowired
    private BookingServiceImpl bookingServiceImpl;


    @PostMapping("/create")
    public ResponseEntity<Booking> createBooking(@RequestBody BookingDTO bookingDTO) {
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

    @PostMapping("/rooms/available")
    public ResponseEntity<List<Map<String, Object>>> getAvailableRooms(@RequestBody Map<String, String> dateRequest) {
        Date checkInDate;
        Date checkOutDate;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            checkInDate = dateFormat.parse(dateRequest.get("checkInDate"));
            checkOutDate = dateFormat.parse(dateRequest.get("checkOutDate"));
        } catch (ParseException e) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }
        List<Map<String, Object>> availableRooms = bookingServiceImpl.getAvailableRooms(checkInDate, checkOutDate);

        return ResponseEntity.ok(availableRooms);

    }



}
