package com.ecommerce.library.service.impl;

import com.ecommerce.library.dto.CouponDto;
import com.ecommerce.library.model.Coupon;
import com.ecommerce.library.repository.CouponRepository;
import com.ecommerce.library.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CouponServiceImpl implements CouponService {

    private CouponRepository couponRepository;
    @Autowired
    public CouponServiceImpl(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    @Override
    public void save(CouponDto couponDto) {
        Coupon coupon=new Coupon();
        coupon.setCouponcode(couponDto.getCouponcode());
        coupon.setCouponDescription(couponDto.getCouponDescription());
        coupon.setOfferPercentage(couponDto.getOfferPercentage());
        coupon.setMinimumOrderAmount(couponDto.getMinimumOrderAmount());
        coupon.setMaximumOfferAmount(couponDto.getMaximumOfferAmount());
        coupon.setStartDate(couponDto.getStartDate());
        coupon.setExpireDate(couponDto.getExpireDate());
        coupon.setCount(couponDto.getCount());
        coupon.setEnabled(true);
        couponRepository.save(coupon);
    }

    @Override
    public void updateCoupon(CouponDto couponDto) {
        Coupon coupon=couponRepository.getReferenceById(couponDto.getId());
        if(couponDto.getStartDate()!=null){
            coupon.setStartDate(couponDto.getStartDate());
        }
        else{
            coupon.setStartDate(coupon.getStartDate());
        }
        if(couponDto.getExpireDate()!=null){
            coupon.setExpireDate(couponDto.getExpireDate());
        }
        else{
            coupon.setExpireDate(coupon.getExpireDate());
        }
//        coupon.setCouponcode(couponDto.getCouponCode());
        coupon.setCouponDescription(couponDto.getCouponDescription());
        coupon.setOfferPercentage(couponDto.getOfferPercentage());
        coupon.setMinimumOrderAmount(couponDto.getMinimumOrderAmount());
        coupon.setMaximumOfferAmount(couponDto.getMaximumOfferAmount());
        coupon.setCount(couponDto.getCount());
        couponRepository.save(coupon);
    }

    @Override
    public List<Coupon> findAll() {
        List<Coupon> coupons=couponRepository.findAll();
        return coupons;
    }

    @Override
    public void enableCoupon(Long id) {
        Coupon coupon=couponRepository.getReferenceById(id);
        coupon.setEnabled(true);
        couponRepository.save(coupon);
    }

    @Override
    public void disableCoupon(Long id) {
        Coupon coupon=couponRepository.getReferenceById(id);
        coupon.setEnabled(false);
        couponRepository.save(coupon);
    }

    @Override
    public Coupon findByCouponCode(String couponcode) {

        return couponRepository.findByCouponCode(couponcode);
    }

    @Override
    public void decreaseCoupon(long id) {
        Coupon coupon=couponRepository.getReferenceById(id);
        coupon.setCount(coupon.getCount()-1);
        couponRepository.save(coupon);
    }

    @Override
    public void increaseCoupon(long id) {
        Coupon coupon=couponRepository.getReferenceById(id);
        coupon.setCount(coupon.getCount()+1);
        couponRepository.save(coupon);
    }


    @Override
    public Coupon findByid(Long id) {
        return couponRepository.getReferenceById(id);
    }


}
