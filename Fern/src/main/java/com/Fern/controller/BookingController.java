package com.Fern.controller;

import com.Fern.dto.BookingDTO;
import com.Fern.entity.Booking;
import com.Fern.entity.User;
import com.Fern.repository.UserRepo;
import com.Fern.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Controller
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private BookingServiceImpl bookingServiceImpl;


    @ModelAttribute
    public void commonUser(Principal p, Model m) {
        if (p != null) {
            String email = p.getName();
            User user = userRepo.findByEmail(email);
            m.addAttribute("user", user);
        }
    }

    @GetMapping("/history")
    public String getAllBookingsByUser(Principal principal, Model model) {
        try {
            List<Booking> bookings = bookingService.getAllBookingsByUser(principal);
            Collections.reverse(bookings);
            model.addAttribute("bookings", bookings);
            return "booking_history";
        } catch (Exception ex) {
            ex.printStackTrace();
            return "redirect:/error";
        }
    }

    @GetMapping("/admin/bookings/list")
    public String getAllBookings(Model model) {

        try {
            List<Booking> bookings = bookingService.getAllBookingsBy();
            Collections.reverse(bookings);
            model.addAttribute("bookings", bookings);
            return "booking_history";
        } catch (Exception ex) {
            ex.printStackTrace();
            return "redirect:/error";
        }

    }

    @GetMapping("/admin/delete-booking")
    public String deleteBooking(@RequestParam Long bookingId, Model model) {

        boolean deleted = bookingServiceImpl.deleteBooking(bookingId);
        if (deleted) {
            model.addAttribute("message", "Booking deleted successfully.");
        } else {
            model.addAttribute("message", "Booking not found.");
        }

        return "redirect:/admin/bookings/list";
    }

    @PostMapping("/rooms/bookings/create")
    public String create(@ModelAttribute BookingDTO bookingDTO, @RequestParam Long roomId, Principal principal, Model model, HttpSession session) {

        Optional<Map<String, Object>> roomDetails = roomService.getRoomsById(roomId);

        boolean isLoggedIn = principal != null;
        model.addAttribute("isLoggedIn", isLoggedIn);

        if (isLoggedIn) {
            String email = principal.getName();
            User user = userRepo.getUserByEmail(email);
            model.addAttribute("username", user.getName());
        }

        Date checkInDate = (Date) session.getAttribute("checkInDate");
        Date checkOutDate = (Date) session.getAttribute("checkOutDate");

        model.addAttribute("checkInDate", checkInDate);
        model.addAttribute("checkOutDate", checkOutDate);

        if (roomDetails.isPresent()) {

            model.addAttribute("room", roomDetails.get());

            if (checkInDate != null && checkOutDate != null) {
                if (checkInDate.after(checkOutDate)) {
                    model.addAttribute("error", "Check-in date cannot be after the check-out date.");
                    return "booking";
                }

                double totalPrice = calculatePrice(
                        checkInDate,
                        checkOutDate,
                        (Double) roomDetails.get().get("pricePerNight")
                );
                model.addAttribute("calculatedPrice", totalPrice);
            }

            return "booking";
        } else {
            return "error/404";
        }
    }

    private double calculatePrice(Date checkInDate, Date checkOutDate, Double pricePerNight) {

        long daysBetween = ChronoUnit.DAYS.between(
                checkInDate.toInstant(),
                checkOutDate.toInstant()
        );

        daysBetween = Math.max(daysBetween, 1);

        return daysBetween * pricePerNight;
    }


    @PostMapping("/create")
    public String createBooking(@ModelAttribute BookingDTO bookingDTO, Principal principal) {

        try {

            Booking createdBooking = bookingService.createBooking(bookingDTO, principal);

            return "redirect:/history";

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return "error/500";
        }

    }




}
