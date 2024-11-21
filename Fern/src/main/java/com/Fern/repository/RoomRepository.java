package com.Fern.repository;

import com.Fern.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    Optional<Room> findByRoomNumber(String roomNumber);

    List<Room> findByFloorNumber(int floorNumber);

    List<Room> findByRoomTypeId(Long roomTypeId);

    List<Room> findByRoomAvailabilityIsAvailable(boolean isAvailable);

    List<Room> findByPricePerNightBetween(Double minPrice, Double maxPrice);

    boolean existsByRoomNumber(String roomNumber);

}
