package com.ecommerce.library.Exception;

public class ProductNameAlreadyExistsException extends RuntimeException{
    public ProductNameAlreadyExistsException(String message) {
        super(message);
    }
}
