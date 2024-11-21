package com.Fern.service;

import com.Fern.dto.AmenityDTO;
import com.Fern.dto.RoomTypeDTO;
import org.springframework.stereotype.Service;

import java.util.List;


public interface RoomTypeService {

    void addRoomType(RoomTypeDTO roomTypeDTO);

    public List<RoomTypeDTO> getAllRoomTypes();

    public RoomTypeDTO getRoomTypeById(int id);

    void deleteRoomTypeById(int id);


}
