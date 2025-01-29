package com.Fern.repository;

import com.Fern.entity.Events;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface Event_repository extends JpaRepository<Events, Integer> {


    void deleteById(int id);


}
