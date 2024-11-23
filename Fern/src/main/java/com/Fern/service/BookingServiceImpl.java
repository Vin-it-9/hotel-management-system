package com.Fern.service;


import com.Fern.dto.BookingDTO;
import com.Fern.entity.Booking;
import com.Fern.entity.Room;
import com.Fern.repository.BookingRepository;
import com.Fern.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RoomRepository roomRepository;


    @Override
    public Booking createBooking(BookingDTO bookingDTO) {
        // Validate inputs
        if (bookingDTO.getCheckInDate() == null || bookingDTO.getCheckOutDate() == null) {
            throw new IllegalArgumentException("Check-in and check-out dates are required.");
        }

        if (bookingDTO.getCheckInDate().after(bookingDTO.getCheckOutDate())) {
            throw new IllegalArgumentException("Check-in date cannot be after the check-out date.");
        }

        if (bookingDTO.getRoomId() == null) {
            throw new IllegalArgumentException("Room ID is required.");
        }

        // Fetch the Room entity
        Room room = roomRepository.findById(bookingDTO.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("Room not found."));

        List<Booking> existingBookings = bookingRepository.findByRoomAndDateRange(
                room, bookingDTO.getCheckInDate(), bookingDTO.getCheckOutDate());

        if (!existingBookings.isEmpty()) {
            throw new IllegalArgumentException("Room is not available for the selected dates.");
        }

        // Create a new Booking entity
        Booking booking = new Booking();
        booking.setCheckInDate(bookingDTO.getCheckInDate());
        booking.setCheckOutDate(bookingDTO.getCheckOutDate());
        booking.setCustomerName(bookingDTO.getCustomerName());
        booking.setRoom(room);

        // Generate a unique booking reference
        String uniqueReference = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        booking.setBookingReference(uniqueReference);

        // Calculate total price
        long daysBetween = ChronoUnit.DAYS.between(
                bookingDTO.getCheckInDate().toInstant(), bookingDTO.getCheckOutDate().toInstant());
        double totalPrice = daysBetween * room.getPricePerNight();
        booking.setTotalPrice(totalPrice);

        // Set initial booking status
        booking.setBookingStatus("Confirmed");

        // Save the booking
        return bookingRepository.save(booking);
    }


    @Override
    public List<Booking> getAllBookings() {
        return List.of();
    }

    @Override
    public Booking getBookingById(Long id) {
        return null;
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
}
