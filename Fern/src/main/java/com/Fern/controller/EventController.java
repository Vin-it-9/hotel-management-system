package com.Fern.controller;


import com.Fern.dto.EventsRequest;
import com.Fern.entity.*;
import com.Fern.service.EvenetsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.*;
import java.sql.Blob;
import java.util.List;

@Controller
@RequestMapping("/event")
public class EventController {


    @Autowired
    EvenetsService evenetsService;


    @GetMapping("/add")
    public String addEventsForm() {
        return "admin/add_Events";
    }

    @PostMapping("/save")
    public ResponseEntity<String> addEvents(
            @RequestParam("name") String name,
            @RequestParam("capacity") int capacity,
            @RequestParam("description") String description,
            @RequestParam(value = "image", required = false) MultipartFile image) throws IOException, SQLException {

        byte[] imageBytes = null;
        if (image != null && !image.isEmpty()) {
            imageBytes = image.getBytes();
        }

        evenetsService.addEvent(name, capacity, description, imageBytes);

        return ResponseEntity.ok("Event added successfully");
    }


    @GetMapping("")
    public String getAllEvents(Model model) {

        List<Events> events = evenetsService.getAllEvents();

        List<Map<String, Object>> eventDataList = new ArrayList<>();

        for (Events event : events) {
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("event", event);

            if (event.getImage() != null) {
                try {
                    byte[] imageBytes = event.getImage().getBytes(1, (int) event.getImage().length());
                    String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                    eventData.put("base64Image", base64Image);
                } catch (Exception e) {
                    e.printStackTrace();
                    eventData.put("base64Image", null);
                }
            } else {
                eventData.put("base64Image", null);
            }

            eventDataList.add(eventData);
        }

        model.addAttribute("eventDataList", eventDataList);

        return "list_events";

    }


}


