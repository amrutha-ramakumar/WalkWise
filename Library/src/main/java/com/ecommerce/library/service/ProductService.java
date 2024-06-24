package com.ecommerce.library.service;


import com.ecommerce.library.dto.ProductDto;
import com.ecommerce.library.model.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.parameters.P;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import java.util.Date;
import java.util.List;

public interface ProductService {

    List<Product> findAll();

    Product findByName(String name);

     Page<Product> findAllByCategoryName(String categoryName, Pageable pageable);

    void save(ProductDto productDto, MultipartFile[] imageProducts) throws Exception;
    ProductDto findById(long id);

    Product update(List<MultipartFile> imageProduct,ProductDto productDto);

    void disable(long id);

    void enable(long id);

    Page<ProductDto> findAllByActivated(long id, int pageNo);
    List<Product> searchProduct(String keyword);
//    Page<ProductDto> findAllByActivated(int pageNo,String sort);

    List<ProductDto> findAllProducts();

    List<ProductDto> findAllByOrderDesc();

    void deleteProduct(long id);

    Long countAllProducts();

    Product findBYId(long id);

    List<Product> findProductsByCategory(long id);
//Pagination
    List<ProductDto> allProduct();
    Page<ProductDto> getAllProducts(int pageNo);
     Page<Product> searchProductsByCategoryAndName(String key, String categoryName,int pageNo) ;


//    Search
//    Page<Product> searchProducts(int pageNo, String keyword);
    Page<ProductDto> searchProducts(int pageNo, String keyword);

    //Customer-module
    Product getProductById(Long id);

//    Page<ProductDto> filterHighPrice(int pageNo);
//
////    List<Product> filterLowerPrice();
//    Page<ProductDto> filterLowerPrice(int pageNo);
////    Page<ProductDto> filterNewArrivals(int pageNo);
////
////    Page<ProductDto> filterAtoZ(int pageNo);
////    Page<ProductDto> filterZtoA(int pageNo);
////    List<Product> filterPopularity();

    Page<ProductDto> getAllProductsLowtoHigh(int pageNo);
    Page<ProductDto> getAllProductsHighToLow(int pageNo);
    Page<ProductDto> getAllProductsAToZ(int pageNo);
    Page<ProductDto> getAllProductsZToA(int pageNo);
    Page<ProductDto> getAllProductsLatest(int pageNo);

    Page<ProductDto> getProductsShop(int pageNo);
    List<Product> findTopSellingProductsWithKeyword(String keyword);
    List<Product> randomProductWithKeyword(String keyword);
    List<Product> filterByIdDescendingWithKeyword(String keyword);
    List<Product> findProductsByCategoryNameAndKeyword(String categoryName, String keyword);

}
