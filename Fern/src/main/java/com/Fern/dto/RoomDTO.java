package com.Fern.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Lob;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Blob;
import java.util.Set;


@Data
@EntityListeners(AuditingEntityListener.class)
public class RoomDTO {

    private Long id;
    private String roomNumber;
    private int floorNumber;
    private double size;
    private double pricePerNight;

    private Long roomTypeId;
    private Set<Long> amenityIds;
    private  String description;
    private RoomAvailabilityDTO roomAvailability;

    @Lob
    @Column(nullable = true)
    private Blob image;

    public Blob getImage() {
        return image;
    }

    public void setImage(Blob image) {
        this.image = image;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }



    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public void setFloorNumber(int floorNumber) {
        this.floorNumber = floorNumber;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public Long getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(Long roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    public Set<Long> getAmenityIds() {
        return amenityIds;
    }

    public void setAmenityIds(Set<Long> amenityIds) {
        this.amenityIds = amenityIds;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RoomAvailabilityDTO getRoomAvailability() {
        return roomAvailability;
    }

    public void setRoomAvailability(RoomAvailabilityDTO roomAvailability) {
        this.roomAvailability = roomAvailability;
    }
}
