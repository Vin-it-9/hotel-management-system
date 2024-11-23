package com.Fern.service;

import com.Fern.dto.BookingDTO;
import com.Fern.entity.Booking;

import java.util.Date;
import java.util.List;

public interface BookingService {


    Booking createBooking(BookingDTO bookingDTO);

    List<Booking> getAllBookings();

    Booking getBookingById(Long id);

    boolean isRoomAvailable(Long roomId, Date checkInDate, Date checkOutDate);

    List<Booking> getBookingsByRoomId(Long roomId);

    Booking cancelBooking(Long bookingId);



}
