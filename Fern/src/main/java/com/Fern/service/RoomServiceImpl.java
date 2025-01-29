package com.Fern.service;


import com.Fern.entity.*;
import com.Fern.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class RoomServiceImpl implements RoomService {

    @Autowired
    private RoomRepository roomRepository;


    public void addRoom(Room room) {
        roomRepository.save(room);
    }

    @Override
    public Room addRoom(String roomNumber, int floorNumber, double size, String description,
                        byte[] imageBytes, double pricePerNight, RoomType roomType,
                        RoomAvailability roomAvailability, Set<Amenity> amenities) throws SQLException {

        if (roomNumber == null || roomNumber.isEmpty()) {
            throw new IllegalArgumentException("Room number is required");
        }
        if (roomType == null) {
            throw new IllegalArgumentException("Room type is required");
        }

        Room room = new Room();
        room.setRoomNumber(roomNumber);
        room.setFloorNumber(floorNumber);
        room.setSize(size);
        room.setDescription(description);
        room.setPricePerNight(pricePerNight);
        room.setRoomType(roomType);

        if (imageBytes != null) {
            Blob roomImage = new SerialBlob(imageBytes);
            room.setImage(roomImage);
        } else {
            room.setImage(null);
        }

        if (roomAvailability == null) {
            roomAvailability = new RoomAvailability();
            roomAvailability.setStatus("Available");
        }

        roomAvailability.setRoom(room);

        room.setRoomAvailability(roomAvailability);

        room.setAmenities(amenities);

        return roomRepository.save(room);

    }

    @Override
    public Optional<Room> getRoomById(Long roomId) {
        return roomRepository.findById(roomId);
    }

    @Override
    public Optional<Map<String, Object>> getRoomsById(Long roomId) {
        return roomRepository.findById(roomId).map(RoomMapper::mapRoomToDTO);
    }

    @Override
    public List<Map<String, Object>> getAllRooms() {
        return roomRepository.findAll().stream()
                .map(RoomMapper::mapRoomToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getRoomsByRoomTypeId(Long roomTypeId) {
        return roomRepository.findByRoomTypeId(roomTypeId).stream()
                .map(RoomMapper::mapRoomToDTO)
                .collect(Collectors.toList());
    }


    @Override
    public List<Map<String, Object>> getRoomsByPriceRange(Double minPrice, Double maxPrice) {
        return roomRepository.findByPriceRange(minPrice, maxPrice).stream()
                .map(RoomMapper::mapRoomToDTO)
                .collect(Collectors.toList());
    }


    @Override
    public void deleteRoom(Long roomId) {
        roomRepository.deleteById(roomId);
    }


    @Override
    public List<Map<String, Object>> getFilteredRooms(Double minPrice, Double maxPrice, Long roomType) {
        return roomRepository.findAll().stream()
                .filter(room -> (minPrice == null || room.getPricePerNight() >= minPrice))
                .filter(room -> (maxPrice == null || room.getPricePerNight() <= maxPrice))
                .filter(room -> (roomType == null || room.getRoomType().getId().equals(roomType)))
                .map(RoomMapper::mapRoomToDTO)
                .collect(Collectors.toList());
    }

}
