package com.ecommerce.library.service;

import com.ecommerce.library.dto.OfferDto;
import com.ecommerce.library.model.Offer;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface OfferService {
    void saveOffer(OfferDto offerDto);
    Page<Offer> findAllOffer(int pageNo);
    void updateOffer(OfferDto offerDto);
    Offer findById(Long id);
    void enableOffer(Long id);
    void disableOffer(Long id);
}
