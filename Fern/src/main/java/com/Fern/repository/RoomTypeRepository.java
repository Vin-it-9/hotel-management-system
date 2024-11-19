package com.Fern.repository;

import com.Fern.entity.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomTypeRepository extends JpaRepository<RoomType, Long> {

    // Find a room type by its name
    Optional<RoomType> findByTypeName(String typeName);

    // Check if a room type exists by its name
    boolean existsByTypeName(String typeName);


}
