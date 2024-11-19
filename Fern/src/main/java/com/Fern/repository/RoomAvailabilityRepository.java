package com.Fern.repository;

import com.Fern.entity.RoomAvailability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;


public interface RoomAvailabilityRepository extends JpaRepository<RoomAvailability, Long> {

    // Find availability by room ID
    Optional<RoomAvailability> findByRoomId(Long roomId);

    // Find all rooms by their availability status
    List<RoomAvailability> findByStatus(String status);


}
