package com.Fern.service;


import com.Fern.dto.BookingDTO;
import com.Fern.entity.*;
import com.Fern.repository.BookingRepository;
import com.Fern.repository.RoomRepository;
import com.Fern.repository.UserRepo;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private JavaMailSenderImpl mailSender;

    @Autowired
    private UserRepo userRepo;


    @Override
    public Booking createBooking(BookingDTO bookingDTO,
                                 Principal Principal) {

        if (bookingDTO.getCheckInDate() == null || bookingDTO.getCheckOutDate() == null) {
            throw new IllegalArgumentException("Check-in and check-out dates are required.");
        }

        if (bookingDTO.getCheckInDate().after(bookingDTO.getCheckOutDate())) {
            throw new IllegalArgumentException("Check-in date cannot be after the check-out date.");
        }

        if (bookingDTO.getRoomId() == null) {
            throw new IllegalArgumentException("Room ID is required.");
        }

        Room room = roomRepository.findById(bookingDTO.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("Room not found."));

        List<Booking> existingBookings = bookingRepository.findByRoomAndDateRange(
                room, bookingDTO.getCheckInDate(), bookingDTO.getCheckOutDate());

        if (!existingBookings.isEmpty()) {
            throw new IllegalArgumentException("Room is not available for the selected dates.");
        }

        Booking booking = new Booking();
        booking.setCheckInDate(bookingDTO.getCheckInDate());
        booking.setCheckOutDate(bookingDTO.getCheckOutDate());
        booking.setCustomerName(bookingDTO.getCustomerName());
        booking.setRoom(room);



        String uniqueReference = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        booking.setBookingReference(uniqueReference);

        long daysBetween = ChronoUnit.DAYS.between(
                bookingDTO.getCheckInDate().toInstant(), bookingDTO.getCheckOutDate().toInstant());
        double totalPrice = daysBetween * room.getPricePerNight();
        booking.setTotalPrice(totalPrice);

        sendBookingConfirmationEmail(booking ,Principal);

        booking.setBookingStatus("Confirmed");

        return bookingRepository.save(booking);

    }

    @Async
    public void sendBookingConfirmationEmail(Booking booking , Principal principal) {


        String email = principal.getName();
        User user = userRepo.getUserByEmail(email);

        String from = "springboot2559@gmail.com";
        String to = user.getEmail();
        String subject = "Booking Confirmation - " + booking.getBookingReference();

        String content =
                "<div style=\"font-family: Arial, sans-serif; color: #333; line-height: 1.6; max-width: 600px; margin: auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px;\">" +
                        "<h2 style=\"text-align: center; color: #4CAF50; font-size: 24px; margin-bottom: 30px;\">Booking Confirmation</h2>" +

                        "<p style=\"font-size: 16px;\">Dear " + user.getName() + ",</p>" +
                        "<p style=\"font-size: 16px;\">Thank you for booking with us! Please find your booking details below:</p>" +

                        "<div style=\"margin: 30px 0;\">" +
                        "<table style=\"width: 100%; border-collapse: collapse;\">" +
                        "   <tr style=\"background-color: #f9f9f9;\">" +
                        "       <td style=\"padding: 8px; border: 1px solid #ddd; font-weight: bold;\">Booking Reference:</td>" +
                        "       <td style=\"padding: 8px; border: 1px solid #ddd;\">" + booking.getBookingReference() + "</td>" +
                        "   </tr>" +
                        "   <tr>" +
                        "       <td style=\"padding: 8px; border: 1px solid #ddd; font-weight: bold;\">Room:</td>" +
                        "       <td style=\"padding: 8px; border: 1px solid #ddd;\">" + booking.getRoom().getRoomType().getTypeName() + " (" + booking.getRoom().getRoomType().getPurpose() + ")</td>" +
                        "   </tr>" +
                        "   <tr style=\"background-color: #f9f9f9;\">" +
                        "       <td style=\"padding: 8px; border: 1px solid #ddd; font-weight: bold;\">Check-in Date:</td>" +
                        "       <td style=\"padding: 8px; border: 1px solid #ddd;\">" +
                        booking.getCheckInDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                                .format(DateTimeFormatter.ofPattern("EEE, MMM dd, yyyy")) +
                        "</td>" +
                        "   </tr>" +
                        "   <tr>" +
                        "       <td style=\"padding: 8px; border: 1px solid #ddd; font-weight: bold;\">Check-out Date:</td>" +
                        "       <td style=\"padding: 8px; border: 1px solid #ddd;\">" +
                        booking.getCheckOutDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                                .format(DateTimeFormatter.ofPattern("EEE, MMM dd, yyyy")) +
                        "</td>" +
                        "   </tr>" +
                        "   <tr style=\"background-color: #f9f9f9;\">" +
                        "       <td style=\"padding: 8px; border: 1px solid #ddd; font-weight: bold;\">Total Price:</td>" +
                        "       <td style=\"padding: 8px; border: 1px solid #ddd;\">" + booking.getTotalPrice() + " INR</td>" +
                        "   </tr>" +
                        "</table>" +
                        "</div>" +

                        "<p style=\"font-size: 16px;\">We look forward to hosting you.</p>" +
                        "<p style=\"font-size: 16px;\">Best regards,<br>Hotel The Fern</p>" +

                        "<hr style=\"border: 0; border-top: 1px solid #ddd; margin: 30px 0;\">" +
                        "<p style=\"font-size: 12px; color: #999; text-align: center;\">If you have any questions, please contact us at springboot2559@gmail.com.</p>" +
                        "</div>";



        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(from, "Hotel The Fern");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    @Override
    public List<Booking> getAllBookingsByUser(Principal principal) {

        if (principal == null) {
            throw new IllegalArgumentException("User is not logged in.");
        }

        String email = principal.getName();
        User user = userRepo.getUserByEmail(email);

        if (user == null) {
            throw new IllegalArgumentException("User not found.");
        }

        String username = user.getName();
        return bookingRepository.findBookingByCustomerName(username);

    }

    @Override
    public List<Booking> getAllBookingsBy() {
        return bookingRepository.findAll();
    }

    @Override
    public boolean isRoomAvailable(Long roomId, Date checkInDate, Date checkOutDate) {
        return false;
    }

    @Override
    public List<Booking> getBookingsByRoomId(Long roomId) {
        return List.of();
    }

    @Override
    public Booking cancelBooking(Long bookingId) {
        return null;
    }

    @Override
    public List<Map<String, Object>> getAvailableRooms(Date checkInDate, Date checkOutDate) {

        if (checkOutDate.before(checkInDate)) {
            throw new IllegalArgumentException("Check-out date cannot be before check-in date.");
        }

        List<Room> allRooms = roomRepository.findAll();

        List<Map<String, Object>> availableRooms = allRooms.stream()
                .filter(room -> isRoomAvailable(room, checkInDate, checkOutDate))
                .map(RoomMapper::mapRoomToDTO)
                .collect(Collectors.toList());

        return availableRooms;

    }

    private boolean isRoomAvailable(Room room, Date checkInDate, Date checkOutDate) {

        List<Booking> bookings = bookingRepository.findByRoomId(room.getId());

        for (Booking booking : bookings) {
            if ((checkInDate.before(booking.getCheckOutDate()) && checkOutDate.after(booking.getCheckInDate()))) {
                return false;
            }
        }
        return true;
    }

    @Transactional
    public boolean deleteBooking(Long bookingId) {
        Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);

        if (bookingOptional.isPresent()) {
            bookingRepository.deleteById(bookingId);
            return true;  // Successfully deleted
        } else {
            return false;  // Booking not found
        }
    }



}
