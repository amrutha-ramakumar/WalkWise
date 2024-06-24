package com.ecommerce.library.service;

import com.ecommerce.library.model.Image;

import java.util.List;

public interface ImageService {
    List<Image> findProductImages(long id);
    List<Image> findAll();

    void deleteImage(String imageName,long id);

}
