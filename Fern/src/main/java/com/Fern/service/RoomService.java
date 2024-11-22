package com.Fern.service;

import com.Fern.dto.RoomDTO;
import com.Fern.entity.Amenity;
import com.Fern.entity.Room;
import com.Fern.entity.RoomAvailability;
import com.Fern.entity.RoomType;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RoomService {

    // Add a new room
//    RoomDTO addRoom(RoomDTO roomDTO);

    Room addRoom(String roomNumber, int floorNumber, double size, String description,
                 byte[] imageBytes, double pricePerNight, RoomType roomType,
                 RoomAvailability roomAvailability, Set<Amenity> amenities) throws SQLException;

    // Update an existing room
    RoomDTO updateRoom(Long roomId, RoomDTO roomDTO);

    // Get a room by its ID
    Optional<RoomDTO> getRoomById(Long roomId);

    // Get all rooms
    List<RoomDTO> getAllRooms();

    // Get rooms by room number
    Optional<RoomDTO> getRoomByRoomNumber(String roomNumber);

    // Get rooms by floor number
    List<RoomDTO> getRoomsByFloorNumber(int floorNumber);

    // Get rooms by room type
    List<RoomDTO> getRoomsByRoomTypeId(Long roomTypeId);

    // Get rooms by availability
    List<RoomDTO> getRoomsByAvailability(boolean isAvailable);

    // Get rooms within a price range
    List<RoomDTO> getRoomsByPriceRange(Double minPrice, Double maxPrice);

    // Check if a room with a specific room number exists
    boolean roomExistsByRoomNumber(String roomNumber);

    // Delete a room by ID
    void deleteRoom(Long roomId);
}
