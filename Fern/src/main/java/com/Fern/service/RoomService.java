package com.Fern.service;

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


    Optional<Map<String, Object>> getRoomsById(Long roomId);

    Optional<Room> getRoomById(Long roomId);

    List<Map<String, Object>> getAllRooms();

    List<Map<String, Object>> getRoomsByRoomTypeId(Long roomTypeId);

    List<Map<String, Object>> getRoomsByAmenity(Set<Amenity> amenities );

    List<Map<String, Object>>  getRoomsByAvailability(boolean isAvailable);


    List<Map<String, Object>> getRoomsByPriceRange(Double minPrice, Double maxPrice);

    void deleteRoom(Long roomId);

    List<Map<String, Object>> getFilteredRooms(Double minPrice, Double maxPrice, Long roomTypeId);


}
