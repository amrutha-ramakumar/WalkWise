package com.ecommerce.library.service.impl;

import com.ecommerce.library.dto.OfferDto;
import com.ecommerce.library.model.Category;
import com.ecommerce.library.model.Offer;
import com.ecommerce.library.model.Product;
import com.ecommerce.library.repository.CategoryRepository;
import com.ecommerce.library.repository.OfferRepository;
import com.ecommerce.library.repository.ProductRepository;
import com.ecommerce.library.service.OfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OfferServiceImpl implements OfferService {
    private OfferRepository offerRepository;
    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;

    @Autowired
    public OfferServiceImpl(OfferRepository offerRepository,ProductRepository productRepository,CategoryRepository categoryRepository) {
        this.offerRepository = offerRepository;
        this.productRepository=productRepository;
        this.categoryRepository=categoryRepository;
    }

    @Override
    public void saveOffer(OfferDto offerDto) {
        Offer offer=new Offer();
        if(offerDto.getCategory()!=null) {
            offer.setOfferName(offerDto.getOfferName());
            offer.setDescription(offerDto.getDescription());
            offer.setOfferPercentage(offerDto.getOfferPercentage());
            offer.setOfferType(offerDto.getOfferType());
            offer.setCategory(offerDto.getCategory());
            offer.setActive(false);
            offerRepository.save(offer);
        }
        else{
            offer.setOfferName(offerDto.getOfferName());
            offer.setDescription(offerDto.getDescription());
            offer.setOfferPercentage(offerDto.getOfferPercentage());
            offer.setOfferType(offerDto.getOfferType());
            offer.setProduct(offerDto.getProduct());
            offer.setActive(false);
            offerRepository.save(offer);
        }
    }
    @Override
    public void updateOffer(OfferDto offerDto) {
        Offer offer=offerRepository.getReferenceById(offerDto.getId());
        offer.setOfferName(offerDto.getOfferName());
        offer.setDescription(offerDto.getDescription());
        offer.setOfferPercentage(offerDto.getOfferPercentage());
        offerRepository.save(offer);
    }

    @Override
    public Offer findById(Long id) {
        return offerRepository.getReferenceById(id);
    }

    @Override
    public void enableOffer(Long id) {
        Offer offer=offerRepository.getReferenceById(id);
        offer.setActive(true);
        offerRepository.save(offer);
        if(offer.getOfferType().equals("Product Offer")){
            Product product=productRepository.getReferenceById(offer.getProduct().getId());
            double costPrice= product.getCostPrice();
            int offerPercentage=offer.getOfferPercentage();
            double offers=(offerPercentage * costPrice)/100;
            product.setSalePrice(costPrice-offers);
            productRepository.save(product);
        }
        else{
            Category category=categoryRepository.getReferenceById(offer.getCategory().getId());
//            String categories=category.getName();
            List<Product> products=productRepository.findAllByCategoryId(offer.getCategory().getId());
            for (Product product:products){
                double costPrice= product.getCostPrice();
                int offerPercentage=offer.getOfferPercentage();
                double offers=(offerPercentage * costPrice)/100;
                product.setSalePrice(costPrice-offers);
                productRepository.save(product);
            }
        }
    }

    @Override
    public void disableOffer(Long id) {
        Offer offer=offerRepository.getReferenceById(id);
        offer.setActive(false);
        offerRepository.save(offer);
        if(offer.getOfferType().equals("Product Offer")){
            Product product=productRepository.getReferenceById(offer.getProduct().getId());
            product.setSalePrice(product.getCostPrice());
            productRepository.save(product);
        }
        else{
            Category category=categoryRepository.getReferenceById(offer.getCategory().getId());
//            String categories=category.getName();
            List<Product> products=productRepository.findAllByCategoryId(offer.getCategory().getId());
            for (Product product:products){
                product.setSalePrice(product.getCostPrice());
                productRepository.save(product);
            }
        }
    }


    @Override
    public Page<Offer> findAllOffer(int pageNo) {
        Pageable pageable = PageRequest.of(pageNo, 5);
        List<Offer> offers= offerRepository.findAll();
        Page<Offer> offerPage=toPage(offers,pageable);
        return offerPage;
    }



    private Page toPage(List list, Pageable pageable) {
        if (pageable.getOffset() >= list.size()) {
            return Page.empty();
        }
        int startIndex = (int) pageable.getOffset();
        int endIndex = ((pageable.getOffset() + pageable.getPageSize()) > list.size())
                ? list.size()
                : (int) (pageable.getOffset() + pageable.getPageSize());
        List subList = list.subList(startIndex, endIndex);
        return new PageImpl(subList, pageable, list.size());
    }
}
