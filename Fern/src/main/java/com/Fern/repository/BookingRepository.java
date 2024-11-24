package com.Fern.repository;

import com.Fern.entity.Booking;
import com.Fern.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.*;


@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {



    @Query("SELECT b FROM Booking b WHERE b.room = :room AND " +
            "(b.checkInDate < :endDate AND b.checkOutDate > :startDate)")
    List<Booking> findByRoomAndDateRange(
            @Param("room") Room room,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);




    List<Booking> findByRoomId(Long roomId);



}
