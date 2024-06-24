package com.ecommerce.library.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class OrderDto {
    private String pdfReport;
    private String csvReport;
    private String orderStatus;
}
