package com.Fern.service;

import com.Fern.entity.Image;
import com.Fern.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ImageServiceImpl implements ImageService {

    @Autowired
    private ImageRepository imageRepository;

    @Override
    public Image create(Image image) {
        return imageRepository.save(image);
    }

    @Override
    public List<Image> viewAll() {
        return (List<Image>) imageRepository.findAll();
    }

    @Override
    public Image viewById(long id) {
        return imageRepository.findById(id).get();
    }

    public Image findByUserEmail(String email) {
        return imageRepository.findByUserEmail(email);
    }

    public void update(Image image) {
        imageRepository.save(image);
    }


}
