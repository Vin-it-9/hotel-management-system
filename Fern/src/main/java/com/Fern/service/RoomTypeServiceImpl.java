package com.Fern.service;


import com.Fern.dto.RoomTypeDTO;
import com.Fern.entity.Amenity;
import com.Fern.entity.RoomType;
import com.Fern.repository.RoomTypeRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;


@Service
@Transactional
public class RoomTypeServiceImpl implements RoomTypeService {


    private final RoomTypeRepository roomTypeRepository;

    public RoomTypeServiceImpl(RoomTypeRepository roomTypeRepository) {
        this.roomTypeRepository = roomTypeRepository;
    }

    @Override
    public void addRoomType(RoomTypeDTO roomTypeDTO) {

        if (StringUtils.isEmpty(roomTypeDTO.getTypeName())) {
            throw new IllegalArgumentException("TypeName cannot be empty");
        }

        if (roomTypeRepository.existsByTypeName(roomTypeDTO.getTypeName())) {
            throw new IllegalArgumentException("RoomType with this name already exists");
        }

        RoomType roomType = new RoomType();
        roomType.setTypeName(roomTypeDTO.getTypeName());
        roomType.setDescription(roomTypeDTO.getDescription());
        roomType.setPurpose(roomTypeDTO.getPurpose());

        roomTypeRepository.save(roomType);
    }
    @Override
    public List<RoomTypeDTO> getAllRoomTypes() {
        List<RoomType> roomTypes = roomTypeRepository.findAll();

        return roomTypes.stream()
                .map(roomType -> {
                    RoomTypeDTO roomTypeDTO = new RoomTypeDTO();
                    roomTypeDTO.setId(roomType.getId());
                    roomTypeDTO.setTypeName(roomType.getTypeName());
                    roomTypeDTO.setDescription(roomType.getDescription());
                    roomTypeDTO.setPurpose(roomType.getPurpose());
                    return roomTypeDTO;
                })
                .collect(Collectors.toList());
    }


    @Override
    public RoomTypeDTO getRoomTypeById(int id) {

        RoomType roomType = (RoomType) roomTypeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Amenity with ID " + id + " not found"));
        RoomTypeDTO roomTypeDTO = new RoomTypeDTO();
        roomTypeDTO.setId(roomType.getId());
        roomTypeDTO.setTypeName(roomType.getTypeName());
        roomTypeDTO.setDescription(roomType.getDescription());
        roomTypeDTO.setPurpose(roomType.getPurpose());

        return roomTypeDTO;

    }

    @Override
    public void deleteRoomTypeById(int id) {
        RoomType roomType = (RoomType) roomTypeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Amenity with ID " + id + " not found"));
        roomTypeRepository.delete(roomType);
    }
}
