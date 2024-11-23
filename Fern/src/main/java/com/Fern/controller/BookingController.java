package com.Fern.controller;

import com.Fern.dto.BookingDTO;
import com.Fern.entity.Booking;
import com.Fern.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    /**
     * Endpoint to create a new booking.
     *
     * @param bookingDTO DTO containing booking details.
     * @return ResponseEntity with booking details and HTTP status.
     */
    @PostMapping("/create")
    public ResponseEntity<Booking> createBooking(@RequestBody BookingDTO bookingDTO) {
        try {
            // Call the service layer to create the booking
            Booking createdBooking = bookingService.createBooking(bookingDTO);

            // Return response with the created booking
            return ResponseEntity
                    .created(URI.create("/bookings/" + createdBooking.getId()))
                    .body(createdBooking);
        } catch (IllegalArgumentException ex) {
            // Handle validation errors
            return ResponseEntity.badRequest().body(null);
        } catch (Exception ex) {
            // Handle generic errors
            return ResponseEntity.status(500).body(null);
        }
    }
}
