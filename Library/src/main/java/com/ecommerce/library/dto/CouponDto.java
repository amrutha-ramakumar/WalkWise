package com.ecommerce.library.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CouponDto {
    private Long id;

    @NotBlank(message = "Code is Required")
    private String couponcode;

    @NotBlank(message = "Description is Required")
    private String couponDescription;

    @Max(value = 80,message = "percentage should less than 80%")
    @Min(value = 1,message = "atleast 1% needed")
    private double offerPercentage;

    @NotNull
    @FutureOrPresent(message = "Start date must be in the present or future")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @NotNull
    @FutureOrPresent(message = "Expiry date must be in the present or future")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate expireDate;

    @NotNull(message = "Minimum order amount is required")
    @DecimalMin(value = "0.01" ,message = "Required")
    private Double minimumOrderAmount;

    @NotNull(message = "Maximum offer amount is required")
    @DecimalMin(value = "0.01" ,message = "Required")
    private Double maximumOfferAmount;

    @Min(value = 1 ,message = "Count is Required")
    private int count;

    private boolean enabled;
}
