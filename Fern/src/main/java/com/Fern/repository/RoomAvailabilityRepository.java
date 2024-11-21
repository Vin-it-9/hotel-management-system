package com.Fern.repository;

import com.Fern.entity.RoomAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;


@Repository
public interface RoomAvailabilityRepository extends JpaRepository<RoomAvailability, Long> {

    Optional<RoomAvailability> findByRoomId(Long roomId);

    Optional<RoomAvailability> findById(Long id);

    List<RoomAvailability> findByStatus(String status);


}
