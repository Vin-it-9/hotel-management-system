package com.Fern.repository;

import com.Fern.entity.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomTypeRepository extends JpaRepository<RoomType, Long> {

    Optional<RoomType> findByTypeName(String typeName);

    Optional<Object> findById(int id);

    boolean existsByTypeName(String typeName);



}
