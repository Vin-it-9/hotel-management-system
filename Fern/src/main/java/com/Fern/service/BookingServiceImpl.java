package com.Fern.service;


import com.Fern.dto.BookingDTO;
import com.Fern.entity.Booking;
import com.Fern.entity.Room;
import com.Fern.entity.RoomAvailability;
import com.Fern.entity.RoomMapper;
import com.Fern.repository.BookingRepository;
import com.Fern.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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



}
