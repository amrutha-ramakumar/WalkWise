package com.ecommerce.library.repository;

import com.ecommerce.library.dto.ProductDto;
import com.ecommerce.library.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {

    Page<Product> findAllByCategoryName(String categoryName, Pageable pageable);
    Product findByName(String name);
    @Query("SELECT p FROM Product p WHERE p.category.name = :categoryName AND LOWER(p.name) LIKE LOWER(CONCAT('%', :key, '%'))")
    List<Product> findByCategoryNameAndProductNameContainingIgnoreCase(@Param("categoryName") String categoryName, @Param("key") String key);

    //    List<Product> findByCategoryNameAndProductNameContainingIgnoreCase(String categoryName, String key);
    @Query("select p from Product p where p.name like %?1% or p.shortDescription like %?1%")
    Page<Product> searchProducts(String keyword,Pageable pageable);

    @Query("select p from Product p where p.name like %?1% or p.shortDescription like %?1%")
    List<Product> findAllByNameOrDescription(String keyword);
    Product findById(long id);

    @Query(value = "select * from products where is_activated = true and category_id = :id", nativeQuery = true)
    List<Product> findAllByActivatedTrue(@Param("id") long id);

    @Query(value = "select * from products where is_activated = true", nativeQuery = true)
    List<Product> findAllByActivatedTrue();

    @Query(value = "select * from products ORDER BY product_id DESC",nativeQuery = true)
    List<Product> findAllByOrderById();

    @Query(value = "select count(*) from Product")
    Long CountAllProducts();

    List<Product> findAllByCategoryId(long id);

//    @Query(value = "SELECT * FROM products WHERE is_activated = true ORDER BY CASE WHEN :sort = 'lowToHigh' THEN cost_price END ASC, CASE WHEN :sort = 'highToLow' THEN cost_price END DESC", nativeQuery = true)
//    List<Product> findAllByActivatedTrueAndSortBy(@Param("sort") String sort);

    @Query(value = "select p from Product p where p.is_activated = true order by p.salePrice desc")
    List<Product> filterPriceDesc();

    @Query(value = "select p from Product p order by p.salePrice ")
    List<Product> filterPriceAsc();

    @Query(value = "select p from Product p where p.is_activated = true order by p.id desc LIMIT 10")
    List<Product> filterLatest();
    @Query(value = "select p from Product p where p.is_activated = true order by p.name")
    List<Product> filterAtoZ();

    @Query(value = "select p from Product p where p.is_activated = true order by p.name desc")
    List<Product> filterZtoA();

    @Query("SELECT DISTINCT p.brand FROM Product p WHERE p.brand IN ('Adidas', 'Nike', 'Asian', 'Puma', 'Bata','US Polo')")
    List<String> findDistinctBrands();
//    @Query(value = "SELECT p.name FROM Product p WHERE p.id IN(SELECT od.product.id FROM OrderDetails od GROUP BY od.product.id HAVING COUNT(od.product.id) > 2 )ORDER BY p.name DESC")
//    List<Product> filterPopularity();

    @Query(value = "SELECT p.*, c.category_id AS cat_id \n" +
            "FROM products p \n" +
            "JOIN categories c ON p.category_id = c.category_id \n" +
            "WHERE p.is_activated = TRUE \n" +
            "AND c.is_deleted = FALSE \n" +
            "AND (LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')))",nativeQuery = true)
    List<Product> listViewProductsUserSide(String keyword);

    @Query(value = "SELECT p.* " +
            "FROM products p " +
            "JOIN (" +
            "  SELECT od.product_id, SUM(od.quantity) as total_sold " +
            "  FROM order_details od " +
            "  GROUP BY od.product_id " +
            "  ORDER BY total_sold DESC " +
            "  LIMIT 10" +
            ") top_selling ON p.product_id = top_selling.product_id " +
            "WHERE LOWER(p.name) LIKE CONCAT('%', LOWER(:keyword), '%') " +
            "ORDER BY top_selling.total_sold DESC",
            nativeQuery = true)
    List<Product> findTopSellingProductsWithKeyword(@Param("keyword") String keyword);
    @Query(value = "SELECT * FROM products WHERE is_deleted = false ORDER BY random() LIMIT 10", nativeQuery = true)
    List<Product> randomProduct();
    @Query(value = "SELECT p.* FROM products p " +
            "JOIN categories c ON p.category_id = c.category_id " +
            "WHERE p.is_deleted = false AND p.product_id IN :ids AND " +
            "(LOWER(p.name) LIKE CONCAT('%', LOWER(:keyword), '%') " +
            "OR LOWER(c.name) LIKE CONCAT('%', LOWER(:keyword), '%'))", nativeQuery = true)
    List<Product> randomProductWithKeyword(@Param("ids") List<Long> ids, @Param("keyword") String keyword);
    @Query(value = "SELECT product_id FROM products WHERE is_deleted = false ORDER BY product_id DESC LIMIT 10", nativeQuery = true)
    List<Long> findLast10ProductIds();
    @Query(value = "SELECT p.* FROM products p " +
            "JOIN categories c ON p.category_id = c.category_id " +
            "WHERE p.is_activated = true AND p.product_id IN :ids AND " +
            "(LOWER(p.name) LIKE CONCAT('%', LOWER(:keyword), '%') " +
            "OR LOWER(c.name) LIKE CONCAT('%', LOWER(:keyword), '%'))", nativeQuery = true)
    List<Product> findProductsByIdsAndKeyword(@Param("ids") List<Long> ids, @Param("keyword") String keyword);

    @Query("SELECT p FROM Product p WHERE lower(p.category.name) = lower(:categoryName) AND (lower(p.name) ILIKE %:keyword% OR lower(p.category.name) ILIKE %:keyword%)")
    List<Product> findByCategoryNameAndKeywordIgnoreCase(@Param("categoryName") String categoryName, @Param("keyword") String keyword);

}

