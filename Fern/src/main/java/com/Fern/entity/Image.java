package com.Fern.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Blob;
import java.util.Date;

@Entity
@Table(name = "image_table")
@Data
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Lob
    private Blob image;

    private Date date = new Date();

    public void setDate(Date date) {
        this.date = date;
    }

    @OneToOne
    @JoinColumn(name = "user_email", referencedColumnName = "email", nullable = false, unique = true)
    private User user;


}
