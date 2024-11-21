package com.Fern.repository;

import com.Fern.entity.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface AmenityRepository extends JpaRepository<Amenity, Long> {

    Optional<Amenity> findByName(String name);

    boolean existsByName(String name);

    Optional<Object> findById(int id);

}
