package com.Fern.service;


import com.Fern.dto.RoomDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RoomServiceImpl implements RoomService {

    @Override
    public RoomDTO addRoom(RoomDTO roomDTO) {
        return null;
    }

    @Override
    public RoomDTO updateRoom(Long roomId, RoomDTO roomDTO) {
        return null;
    }

    @Override
    public Optional<RoomDTO> getRoomById(Long roomId) {
        return Optional.empty();
    }

    @Override
    public List<RoomDTO> getAllRooms() {
        return List.of();
    }

    @Override
    public Optional<RoomDTO> getRoomByRoomNumber(String roomNumber) {
        return Optional.empty();
    }

    @Override
    public List<RoomDTO> getRoomsByFloorNumber(int floorNumber) {
        return List.of();
    }

    @Override
    public List<RoomDTO> getRoomsByRoomTypeId(Long roomTypeId) {
        return List.of();
    }

    @Override
    public List<RoomDTO> getRoomsByAvailability(boolean isAvailable) {
        return List.of();
    }

    @Override
    public List<RoomDTO> getRoomsByPriceRange(Double minPrice, Double maxPrice) {
        return List.of();
    }

    @Override
    public boolean roomExistsByRoomNumber(String roomNumber) {
        return false;
    }

    @Override
    public void deleteRoom(Long roomId) {

    }
}
