package com.Fern.service;

import com.Fern.dto.RoomAvailabilityDTO;

import java.util.List;
import java.util.Optional;

public interface RoomAvailabilityService {

    RoomAvailabilityDTO createOrUpdateRoomAvailability(Long roomId, RoomAvailabilityDTO roomAvailabilityDTO);

    Optional<RoomAvailabilityDTO> getRoomAvailabilityByRoomId(Long roomId);

    List<RoomAvailabilityDTO> getRoomAvailabilityByStatus(String status);

    Optional<RoomAvailabilityDTO> getRoomAvailabilityById(Long id);


}
