package com.Fern.dto;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class AmenityDTO {

    private String name;
    private String description; // Optional field for a description of the amenity
    private LocalDateTime createdAt; // If you need to track creation time or other fields

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
