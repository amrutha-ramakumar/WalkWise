package com.ecommerce.library.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "products",uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;
    private String name;
    private String brand;
    private String shortDescription;
    @Column(columnDefinition = "TEXT")
    private String longDescription;
    private int currentQuantity;
    private double costPrice;
    private double salePrice;

    @OneToMany(fetch = FetchType.EAGER,mappedBy = "product" ,cascade = {CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH,CascadeType.DETACH})
    private List<Image> image;

    @ManyToOne(cascade = {CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH,CascadeType.DETACH}, fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", referencedColumnName = "category_id")
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductSizeQuantity> productSizesQuantity;

    private boolean is_activated;

    public void addProductSizeQuantity(ProductSizeQuantity productSizeQuantity) {
        this.productSizesQuantity.add(productSizeQuantity);
        productSizeQuantity.setProduct(this);
    }

    public void removeProductSizeQuantity(ProductSizeQuantity productSizeQuantity) {
        this.productSizesQuantity.remove(productSizeQuantity);
        productSizeQuantity.setProduct(null);
    }
}
