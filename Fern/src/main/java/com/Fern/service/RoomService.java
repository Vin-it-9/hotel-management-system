package com.Fern.service;

import com.Fern.dto.RoomDTO;
import com.Fern.entity.Amenity;
import com.Fern.entity.Room;
import com.Fern.entity.RoomAvailability;
import com.Fern.entity.RoomType;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface RoomService {


    Room addRoom(String roomNumber, int floorNumber, double size, String description,
                 byte[] imageBytes, double pricePerNight, RoomType roomType,
                 RoomAvailability roomAvailability, Set<Amenity> amenities) throws SQLException;


    public Optional<Map<String, Object>> getRoomsById(Long roomId);

    // Get a room by its ID
    Optional<Room> getRoomById(Long roomId);

    // Get all rooms
    List<Map<String, Object>> getAllRooms();

    // Get rooms by room number
    Optional<Room> getRoomByRoomNumber(String roomNumber);

    // Get rooms by floor number
    List<Room> getRoomsByFloorNumber(int floorNumber);

    // Get rooms by room type
    List<Room> getRoomsByRoomTypeId(Long roomTypeId);

    // Get rooms by availability
    List<Room> getRoomsByAvailability(boolean isAvailable);

    // Get rooms within a price range
    List<Room> getRoomsByPriceRange(Double minPrice, Double maxPrice);

    // Check if a room with a specific room number exists
    boolean roomExistsByRoomNumber(String roomNumber);

    // Delete a room by ID
    void deleteRoom(Long roomId);
}
