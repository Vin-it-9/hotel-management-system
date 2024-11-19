package com.Fern.entity;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
public class RoomAvailability {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String status; // E.g., Available, Booked, Under Maintenance

    @Temporal(TemporalType.DATE)
    private Date bookingStartDate;

    @Temporal(TemporalType.DATE)
    private Date bookingEndDate;

    @OneToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;
}
