package com.Fern.repository;

import com.Fern.entity.Image;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends CrudRepository<Image, Long> {

    Image findByUserEmail(String email);

}
