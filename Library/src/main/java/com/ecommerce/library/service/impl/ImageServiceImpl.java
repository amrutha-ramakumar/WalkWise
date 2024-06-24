package com.ecommerce.library.service.impl;

import com.ecommerce.library.model.Image;
import com.ecommerce.library.repository.ImageRepository;
import com.ecommerce.library.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImageServiceImpl implements ImageService {
    @Autowired
    private ImageRepository imageRepository;
    @Override
    public List<Image> findProductImages(long id) {
        return imageRepository.findImageBy(id);
    }
    @Override
    public List<Image> findAll() {
        return imageRepository.findAll();
    }


    public void deleteImage( String imageName,long id) {
        // Implement logic to delete the image by product id and image name
        imageRepository.deleteByNameAndProductId(imageName,id);
    }

}
