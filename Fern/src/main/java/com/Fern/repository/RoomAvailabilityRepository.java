package com.Fern.repository;

import com.Fern.entity.RoomAvailability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;


public interface RoomAvailabilityRepository extends JpaRepository<RoomAvailability, Long> {

    Optional<RoomAvailability> findByRoomId(Long roomId);

    List<RoomAvailability> findByStatus(String status);


}
