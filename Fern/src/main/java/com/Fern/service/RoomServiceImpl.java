package com.Fern.service;


import com.Fern.dto.RoomDTO;
import com.Fern.entity.Amenity;
import com.Fern.entity.Room;
import com.Fern.entity.RoomAvailability;
import com.Fern.entity.RoomType;
import com.Fern.repository.AmenityRepository;
import com.Fern.repository.RoomAvailabilityRepository;
import com.Fern.repository.RoomRepository;
import com.Fern.repository.RoomTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class RoomServiceImpl implements RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    @Autowired
    private AmenityRepository amenityRepository;

    @Autowired
    private RoomAvailabilityRepository roomAvailabilityRepository;


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
        return roomRepository.findById(roomId).map(room -> {
            Map<String, Object> roomData = new HashMap<>();
            roomData.put("id", room.getId());
            roomData.put("roomNumber", room.getRoomNumber());
            roomData.put("floorNumber", room.getFloorNumber());
            roomData.put("size", room.getSize());
            roomData.put("description", room.getDescription());
            roomData.put("pricePerNight", room.getPricePerNight());

            if (room.getRoomType() != null) {
                Map<String, Object> roomTypeData = new HashMap<>();
                roomTypeData.put("id", room.getRoomType().getId());
                roomTypeData.put("typeName", room.getRoomType().getTypeName());
                roomTypeData.put("purpose", room.getRoomType().getPurpose());
                roomTypeData.put("description", room.getRoomType().getDescription());
                roomData.put("roomType", roomTypeData);
            } else {
                roomData.put("roomType", null);
            }

            if (room.getRoomAvailability() != null) {
                Map<String, Object> roomAvailabilityData = new HashMap<>();
                roomAvailabilityData.put("id", room.getRoomAvailability().getId());
                roomAvailabilityData.put("status", room.getRoomAvailability().getStatus());
                roomAvailabilityData.put("bookingStartDate", room.getRoomAvailability().getBookingStartDate());
                roomAvailabilityData.put("bookingEndDate", room.getRoomAvailability().getBookingEndDate());
                roomData.put("roomAvailability", roomAvailabilityData);
            } else {
                roomData.put("roomAvailability", null);
            }

            List<Map<String, Object>> amenitiesData = room.getAmenities().stream()
                    .map(amenity -> {
                        Map<String, Object> amenityData = new HashMap<>();
                        amenityData.put("id", amenity.getId());
                        amenityData.put("name", amenity.getName());
                        amenityData.put("description", amenity.getDescription());
                        return amenityData;
                    })
                    .collect(Collectors.toList());
            roomData.put("amenities", amenitiesData);

            roomData.put("imageUrl", room.getImage() != null ? "/rooms/image/" + room.getId() : null);

            return roomData;
        });
    }


    @Override
    public List<Map<String, Object>> getAllRooms() {

        List<Room> rooms = roomRepository.findAll();

        return rooms.stream()
                .map(room -> {
                    Map<String, Object> roomData = new HashMap<>();
                    roomData.put("id", room.getId());
                    roomData.put("roomNumber", room.getRoomNumber());
                    roomData.put("floorNumber", room.getFloorNumber());
                    roomData.put("size", room.getSize());
                    roomData.put("description", room.getDescription());
                    roomData.put("pricePerNight", room.getPricePerNight());
                    if (room.getRoomType() != null) {
                        Map<String, Object> roomTypeData = new HashMap<>();
                        roomTypeData.put("id", room.getRoomType().getId());
                        roomTypeData.put("typeName", room.getRoomType().getTypeName());
                        roomTypeData.put("purpose", room.getRoomType().getPurpose());
                        roomData.put("roomType", roomTypeData);
                    } else {
                        roomData.put("roomType", null);
                    }
                    if (room.getRoomAvailability() != null) {
                        Map<String, Object> roomAvailabilityData = new HashMap<>();
                        roomAvailabilityData.put("id", room.getRoomAvailability().getId());
                        roomAvailabilityData.put("status", room.getRoomAvailability().getStatus());
                        roomAvailabilityData.put("bookingStartDate", room.getRoomAvailability().getBookingStartDate());
                        roomAvailabilityData.put("bookingEndDate", room.getRoomAvailability().getBookingEndDate());
                        roomData.put("roomAvailability", roomAvailabilityData);
                    } else {
                        roomData.put("roomAvailability", null);
                    }
                    List<Map<String, Object>> amenitiesData = room.getAmenities().stream()
                            .map(amenity -> {
                                Map<String, Object> amenityData = new HashMap<>();
                                amenityData.put("id", amenity.getId());
                                amenityData.put("name", amenity.getName());
                                amenityData.put("description", amenity.getDescription());
                                return amenityData;
                            })
                            .collect(Collectors.toList());
                    roomData.put("amenities", amenitiesData);
                    roomData.put("imageUrl", room.getImage() != null ? "/rooms/image/" + room.getId() : null);
                    return roomData;

                })
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Room> getRoomByRoomNumber(String roomNumber) {
        return roomRepository.findByRoomNumber(roomNumber);
    }

    @Override
    public List<Room> getRoomsByFloorNumber(int floorNumber) {
        return roomRepository.findByFloorNumber(floorNumber);
    }

    @Override
    public List<Room> getRoomsByRoomTypeId(Long roomTypeId) {
        return roomRepository.findByRoomTypeId(roomTypeId);
    }

    @Override
    public List<Room> getRoomsByAvailability(boolean isAvailable) {
        return null;
    }

    @Override
    public List<Room> getRoomsByPriceRange(Double minPrice, Double maxPrice) {
        return null;
    }

    @Override
    public boolean roomExistsByRoomNumber(String roomNumber) {
        return roomRepository.existsByRoomNumber(roomNumber);
    }

    @Override
    public void deleteRoom(Long roomId) {
        roomRepository.deleteById(roomId);
    }


}
