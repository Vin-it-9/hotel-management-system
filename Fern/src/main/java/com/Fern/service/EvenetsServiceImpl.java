package com.Fern.service;

import com.Fern.entity.Events;
import com.Fern.repository.Event_repository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;

@Service
@Transactional
public class EvenetsServiceImpl implements EvenetsService {


    @Autowired
    Event_repository event_repository;

    @Override
    public Events addEvent(String name, int capacity, String description, byte[] imageBytes) throws SQLException {

        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name is null or empty");
        }

        if (description == null || description.isEmpty()) {
            throw new IllegalArgumentException("Description is null or empty");
        }

        Events event = new Events();
        event.setName(name);
        event.setCapacity(capacity);
        event.setDescription(description);

        if (imageBytes != null) {
            Blob eventImage = new SerialBlob(imageBytes);
            event.setImage(eventImage);
        } else {
            event.setImage(null);
        }

        return event_repository.save(event);
    }

    @Override
    public void deleteEvent(int id) throws SQLException {
        event_repository.deleteById(id);
    }

    @Override
    public List<Events> getAllEvents() {
        return event_repository.findAll();
    }


}
