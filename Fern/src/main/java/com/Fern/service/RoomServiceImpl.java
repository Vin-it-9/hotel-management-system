package com.Fern.service;


import com.Fern.dto.RoomAvailabilityDTO;
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
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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


//    @Override
//    public RoomDTO addRoom(RoomDTO roomDTO) {
//
//        if (roomDTO.getRoomNumber() == null || roomDTO.getRoomNumber().isEmpty()) {
//            throw new IllegalArgumentException("Room number is required");
//        }
//        if (roomDTO.getRoomTypeId() == null) {
//            throw new IllegalArgumentException("Room type is required");
//        }
//
//        Room room = new Room();
//        room.setRoomNumber(roomDTO.getRoomNumber());
//        room.setFloorNumber(roomDTO.getFloorNumber());
//        room.setSize(roomDTO.getSize());
//        room.setPricePerNight(roomDTO.getPricePerNight());
//        room.setDescription(roomDTO.getDescription()); // Set description from DTO
//
//        RoomType roomType = roomTypeRepository.findById(roomDTO.getRoomTypeId())
//                .orElseThrow(() -> new IllegalArgumentException("Room type not found"));
//        room.setRoomType(roomType);
//
//        Set<Amenity> amenities = new HashSet<>();
//        for (Long amenityId : roomDTO.getAmenityIds()) {
//            Amenity amenity = amenityRepository.findById(amenityId)
//                    .orElseThrow(() -> new IllegalArgumentException("Amenity not found"));
//            amenities.add(amenity);
//        }
//        room.setAmenities(amenities);
//
//        RoomAvailability roomAvailability = new RoomAvailability();
//        roomAvailability.setStatus("Available");
//        roomAvailability.setRoom(room);
//        room.setRoomAvailability(roomAvailability);
//
//        if (roomDTO.getImage() != null) {
//            room.setImage(roomDTO.getImage());
//        }
//
//        Room savedRoom = roomRepository.save(room);
//
//        RoomDTO savedRoomDTO = new RoomDTO();
//        savedRoomDTO.setId(savedRoom.getId());
//        savedRoomDTO.setRoomNumber(savedRoom.getRoomNumber());
//        savedRoomDTO.setFloorNumber(savedRoom.getFloorNumber());
//        savedRoomDTO.setSize(savedRoom.getSize());
//        savedRoomDTO.setPricePerNight(savedRoom.getPricePerNight());
//        savedRoomDTO.setDescription(savedRoom.getDescription());
//        savedRoomDTO.setRoomTypeId(savedRoom.getRoomType().getId());
//
//        Set<Long> amenityIds = new HashSet<>();
//        for (Amenity amenity : savedRoom.getAmenities()) {
//            amenityIds.add(Long.valueOf(amenity.getId()));
//        }
//        savedRoomDTO.setAmenityIds(amenityIds);
//
//        RoomAvailabilityDTO roomAvailabilityDTO = new RoomAvailabilityDTO();
//        roomAvailabilityDTO.setStatus(savedRoom.getRoomAvailability().getStatus());
//        roomAvailabilityDTO.setBookingStartDate(savedRoom.getRoomAvailability().getBookingStartDate());
//        roomAvailabilityDTO.setBookingEndDate(savedRoom.getRoomAvailability().getBookingEndDate());
//        savedRoomDTO.setRoomAvailability(roomAvailabilityDTO);  // Set the full RoomAvailabilityDTO
//
//        return savedRoomDTO;
//    }
//
//    @Override
//    public RoomDTO addRoom(RoomDTO roomDTO) {
//        if (roomDTO.getRoomNumber() == null || roomDTO.getRoomNumber().isEmpty()) {
//            throw new IllegalArgumentException("Room number is required");
//        }
//        if (roomDTO.getRoomTypeId() == null) {
//            throw new IllegalArgumentException("Room type is required");
//        }
//
//        Room room = new Room();
//        room.setRoomNumber(roomDTO.getRoomNumber());
//        room.setFloorNumber(roomDTO.getFloorNumber());
//        room.setSize(roomDTO.getSize());
//        room.setPricePerNight(roomDTO.getPricePerNight());
//        room.setDescription(roomDTO.getDescription());
//
//        RoomType roomType = roomTypeRepository.findById(roomDTO.getRoomTypeId())
//                .orElseThrow(() -> new IllegalArgumentException("Room type not found"));
//        room.setRoomType(roomType);
//
//        Set<Amenity> amenities = new HashSet<>();
//        for (Long amenityId : roomDTO.getAmenityIds()) {
//            Amenity amenity = amenityRepository.findById(amenityId)
//                    .orElseThrow(() -> new IllegalArgumentException("Amenity not found"));
//            amenities.add(amenity);
//        }
//        room.setAmenities(amenities);
//
//        RoomAvailability roomAvailability = new RoomAvailability();
//        roomAvailability.setStatus("Available");
//        roomAvailability.setRoom(room);
//        room.setRoomAvailability(roomAvailability);
//
//
//        Room savedRoom = roomRepository.save(room);
//
//        RoomDTO savedRoomDTO = new RoomDTO();
//        savedRoomDTO.setId(savedRoom.getId());
//        savedRoomDTO.setRoomNumber(savedRoom.getRoomNumber());
//        savedRoomDTO.setFloorNumber(savedRoom.getFloorNumber());
//        savedRoomDTO.setSize(savedRoom.getSize());
//        savedRoomDTO.setPricePerNight(savedRoom.getPricePerNight());
//        savedRoomDTO.setDescription(savedRoom.getDescription());
//        savedRoomDTO.setRoomTypeId(savedRoom.getRoomType().getId());
//
//        Set<Long> amenityIds = new HashSet<>();
//        for (Amenity amenity : savedRoom.getAmenities()) {
//            amenityIds.add(Long.valueOf(amenity.getId()));
//        }
//        savedRoomDTO.setAmenityIds(amenityIds);
//
//        RoomAvailability roomAvailabilitySaved = savedRoom.getRoomAvailability();
//        RoomAvailabilityDTO roomAvailabilityDTO = new RoomAvailabilityDTO();
//        roomAvailabilityDTO.setId(roomAvailabilitySaved.getId());
//        roomAvailabilityDTO.setStatus(roomAvailabilitySaved.getStatus());
//        roomAvailabilityDTO.setBookingStartDate(roomAvailabilitySaved.getBookingStartDate());
//        roomAvailabilityDTO.setBookingEndDate(roomAvailabilitySaved.getBookingEndDate());
//        savedRoomDTO.setRoomAvailability(roomAvailabilityDTO);
//
//        return savedRoomDTO;
//    }

    @Override
    public Room addRoom(String roomNumber, int floorNumber, double size, String description,
                        byte[] imageBytes, double pricePerNight, RoomType roomType,
                        RoomAvailability roomAvailability, Set<Amenity> amenities) throws SQLException {

        // Validate inputs
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

        // Handle image if provided
        if (imageBytes != null) {
            Blob roomImage = new SerialBlob(imageBytes);
            room.setImage(roomImage);
        } else {
            room.setImage(null); // Handle null images gracefully
        }

        // Set Room Availability
        if (roomAvailability == null) {
            roomAvailability = new RoomAvailability();
            roomAvailability.setStatus("Available");
        }
        roomAvailability.setRoom(room); // Establish bidirectional relationship
        room.setRoomAvailability(roomAvailability);

        room.setAmenities(amenities);

        return roomRepository.save(room);

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
