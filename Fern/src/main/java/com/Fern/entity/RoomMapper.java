package com.Fern.entity;

import java.util.*;
import java.util.stream.Collectors;

public class RoomMapper {

    public static Map<String, Object> mapRoomToDTO(Room room) {
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
        }

        if (room.getRoomAvailability() != null) {
            Map<String, Object> availabilityData = new HashMap<>();
            availabilityData.put("id", room.getRoomAvailability().getId());
            availabilityData.put("status", room.getRoomAvailability().getStatus());
            availabilityData.put("bookingStartDate", room.getRoomAvailability().getBookingStartDate());
            availabilityData.put("bookingEndDate", room.getRoomAvailability().getBookingEndDate());
            roomData.put("roomAvailability", availabilityData);
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
    }
}
