package com.Fern.service;

import com.Fern.dto.BookingDTO;
import com.Fern.entity.Booking;

import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface BookingService {


    Booking createBooking(BookingDTO bookingDTO, Principal Principal);

    List<Booking> getAllBookings();

    Booking getBookingById(Long id);

    boolean isRoomAvailable(Long roomId, Date checkInDate, Date checkOutDate);

    List<Booking> getBookingsByRoomId(Long roomId);

    Booking cancelBooking(Long bookingId);

    List<Map<String, Object>> getAvailableRooms(Date checkInDate, Date checkOutDate);


}
