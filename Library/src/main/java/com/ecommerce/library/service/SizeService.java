package com.ecommerce.library.service;

import com.ecommerce.library.model.Size;

import java.util.List;

public interface SizeService {

    List<Size> allSizeVariations();

    Size findById(long id);
}

