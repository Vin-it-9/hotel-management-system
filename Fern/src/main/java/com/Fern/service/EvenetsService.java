package com.Fern.service;

import com.Fern.entity.*;

import java.sql.SQLException;
import java.util.List;

public interface EvenetsService {


    Events addEvent(String name, int capacity, String description,
                    byte[] imageBytes)
            throws SQLException;

    void deleteEvent(int id) throws SQLException;

    List<Events> getAllEvents() ;




}
