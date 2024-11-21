package com.Fern.service;

import com.Fern.dto.RoomAvailabilityDTO;
import com.Fern.entity.Room;
import com.Fern.entity.RoomAvailability;
import com.Fern.repository.RoomAvailabilityRepository;
import com.Fern.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoomAvailabilityServiceImpl implements RoomAvailabilityService {

    @Autowired
    private RoomAvailabilityRepository roomAvailabilityRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Override
    public RoomAvailabilityDTO createOrUpdateRoomAvailability(Long roomId, RoomAvailabilityDTO roomAvailabilityDTO) {

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        RoomAvailability roomAvailability = roomAvailabilityRepository.findByRoomId(roomId)
                .orElse(new RoomAvailability());

        roomAvailability.setRoom(room);
        roomAvailability.setStatus(roomAvailabilityDTO.getStatus());
        roomAvailability.setBookingStartDate(roomAvailabilityDTO.getBookingStartDate());
        roomAvailability.setBookingEndDate(roomAvailabilityDTO.getBookingEndDate());

        RoomAvailability savedRoomAvailability = roomAvailabilityRepository.save(roomAvailability);
        RoomAvailabilityDTO savedDTO = new RoomAvailabilityDTO();
        savedDTO.setId(savedRoomAvailability.getId());
        savedDTO.setStatus(savedRoomAvailability.getStatus());
        savedDTO.setBookingStartDate(savedRoomAvailability.getBookingStartDate());
        savedDTO.setBookingEndDate(savedRoomAvailability.getBookingEndDate());

        return savedDTO;
    }

    @Override
    public Optional<RoomAvailabilityDTO> getRoomAvailabilityByRoomId(Long roomId) {
        return roomAvailabilityRepository.findByRoomId(roomId)
                .map(this::convertToDTO);
    }

    @Override
    public List<RoomAvailabilityDTO> getRoomAvailabilityByStatus(String status) {
        List<RoomAvailability> roomAvailabilityList = roomAvailabilityRepository.findByStatus(status);
        return roomAvailabilityList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<RoomAvailabilityDTO> getRoomAvailabilityById(Long id) {
        return roomAvailabilityRepository.findById(id)
                .map(this::convertToDTO);
    }

    private RoomAvailabilityDTO convertToDTO(RoomAvailability roomAvailability) {
        RoomAvailabilityDTO dto = new RoomAvailabilityDTO();
        dto.setId(roomAvailability.getId());
        dto.setStatus(roomAvailability.getStatus());
        dto.setBookingStartDate(roomAvailability.getBookingStartDate());
        dto.setBookingEndDate(roomAvailability.getBookingEndDate());
        return dto;
    }

}
