package com.Fern.repository;

import com.Fern.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface RoomRepository extends JpaRepository<Room, Long> {

    // Find a room by its number
    Optional<Room> findByRoomNumber(String roomNumber);

    // Find all rooms on a specific floor
    List<Room> findByFloorNumber(int floorNumber);

    // Find all rooms by type
    List<Room> findByRoomTypeId(Long roomTypeId);

    // Find all rooms with a price range
    List<Room> findByPricePerNightBetween(Double minPrice, Double maxPrice);

    // Check if a room exists by its number
    boolean existsByRoomNumber(String roomNumber);
}
