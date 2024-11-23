package com.Fern.repository;

import com.Fern.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    Optional<Room> findByRoomNumber(String roomNumber);

    List<Room> findByFloorNumber(int floorNumber);

    List<Room> findByRoomTypeId(Long roomTypeId);

    List<Room> findByPricePerNightBetween(Double minPrice, Double maxPrice);

    boolean existsByRoomNumber(String roomNumber);

    @Query("SELECT r FROM Room r WHERE r.pricePerNight BETWEEN :minPrice AND :maxPrice")
    List<Room> findByPriceRange(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);


    @Query("SELECT r FROM Room r JOIN r.amenities a WHERE " +
            "(COALESCE(:roomTypeId, NULL) IS NULL OR r.roomType.id = :roomTypeId) AND " +
            "(COALESCE(:amenityIds, NULL) IS NULL OR a.id IN :amenityIds)")
    List<Map<String, Object>> findRoomsByCriteria(@Param("roomTypeId") Long roomTypeId, @Param("amenityIds") List<Long> amenityIds);


}
