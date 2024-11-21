package com.Fern.service;


import com.Fern.dto.RoomTypeDTO;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class RoomTypeServiceImpl implements RoomTypeService {


    @Override
    public void addRoomType(RoomTypeDTO roomTypeDTO) {

    }

    @Override
    public List<RoomTypeDTO> getAllRoomTypes() {
        return List.of();
    }

    @Override
    public RoomTypeDTO getRoomTypeById(int id) {
        return null;
    }

    @Override
    public void deleteRoomTypeById(int id) {

    }
}
