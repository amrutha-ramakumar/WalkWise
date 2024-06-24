package com.ecommerce.library.service.impl;

import com.ecommerce.library.dto.ProductDto;
import com.ecommerce.library.model.Image;
import com.ecommerce.library.model.Product;
import com.ecommerce.library.model.ProductSizeQuantity;
import com.ecommerce.library.model.Size;
import com.ecommerce.library.repository.ImageRepository;
import com.ecommerce.library.repository.ProductRepository;
import com.ecommerce.library.repository.ProductSizeQuantityRepository;
import com.ecommerce.library.repository.SizeRepository;
import com.ecommerce.library.service.ProductService;
import com.ecommerce.library.utils.ImageUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Pageable;

import java.util.*;

import jakarta.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;
@Service
public class ProductServiceImpl implements ProductService {

    private ProductRepository productRepository;
    private ImageRepository imageRepository;
    private SizeRepository sizeRepository;


    ProductSizeQuantityRepository productSizeQuantityRepository;


    private ImageUpload imageUpload;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, SizeRepository sizeRepository,
                              ImageUpload imageUpload,ImageRepository imageRepository,ProductSizeQuantityRepository productSizeQuantityRepository) {

        this.imageRepository=imageRepository;
        this.productRepository = productRepository;
        this.sizeRepository = sizeRepository;
        this.imageUpload = imageUpload;
        this.productSizeQuantityRepository=productSizeQuantityRepository;
    }




    public void addOrUpdateProduct(Product product, Map<Size, Integer> sizeQuantities) {
        int totalQuantity = sizeQuantities.values().stream().mapToInt(Integer::intValue).sum();
        if (totalQuantity != product.getCurrentQuantity()) {
            throw new IllegalArgumentException("The sum of size quantities must equal the product's current quantity");
        }

        // Create or update ProductSizeQuantity entities
        List<ProductSizeQuantity> productSizes = sizeQuantities.entrySet().stream().map(entry -> {
            ProductSizeQuantity productSize = new ProductSizeQuantity();
            productSize.setProduct(product);
            productSize.setSize(entry.getKey());
            productSize.setQuantity(entry.getValue());
            return productSize;
        }).collect(Collectors.toList());

        product.setProductSizesQuantity(productSizes);

        // Save the product using your repository
        productRepository.save(product);
    }



    // Method to retrieve all products
    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Product findByName(String name) {
        return productRepository.findByName(name);
    }

    @Override
    public Page<Product> findAllByCategoryName(String categoryName, Pageable pageable) {
        return productRepository.findAllByCategoryName(categoryName, pageable);
    }
    public Page<Product> searchProductsByCategoryAndName(String key, String categoryName,int pageNo) {
        Pageable pageable = PageRequest.of(pageNo, 3);
        List<Product> products=productRepository.findByCategoryNameAndProductNameContainingIgnoreCase(categoryName, key);
        return toPage(products,pageable);
    }


    // Method to transfer data from Product entities to ProductDto objects
    private List<ProductDto> transferData(List<Product> products) {
        List<ProductDto> productDtos = new ArrayList<>();
        for (Product product : products) {
            ProductDto productDto = new ProductDto();
            // Transfer data from Product to ProductDto
            productDto.setId(product.getId());
            productDto.setName(product.getName());
            productDto.setCurrentQuantity(product.getCurrentQuantity());
            productDto.setCostPrice(product.getCostPrice());
            productDto.setSalePrice(product.getSalePrice());
            productDto.setShortDescription(product.getShortDescription());
            productDto.setLongDescription(product.getLongDescription());
            productDto.setBrand(product.getBrand());
            productDto.setImage(product.getImage());
            productDto.setCategory(product.getCategory());
            productDto.setActivated(product.is_activated());
            productDtos.add(productDto);
        }
        return productDtos;
    }

    // Method to save a product

//    @Override
//    public void save(ProductDto productDto, MultipartFile[] imageProducts) throws Exception {
//        // Check for duplicate product names
//        Product existsByName=productRepository.findByName(productDto.getName());
//
//        if (existsByName != null) {
//            throw new Exception("Product with this name already exists.");
//        }
//
//        Product product = new Product();
//        product.setName(productDto.getName());
//        product.setBrand(productDto.getBrand());
//        product.setCategory(productDto.getCategory());
//        product.setShortDescription(productDto.getShortDescription());
//        product.setLongDescription(productDto.getLongDescription());
//        product.setCostPrice(productDto.getCostPrice());
//        product.setSalePrice(productDto.getSalePrice());
//        product.setCurrentQuantity(productDto.getCurrentQuantity());
//        product.set_activated(true);
//        // Save the product first to get an ID
//        product = productRepository.save(product);
//
//        // Save size quantities
//        for (Map.Entry<Long, Integer> entry : productDto.getSizeQuantities().entrySet()) {
//            Long sizeId = entry.getKey();
//            Integer quantity = entry.getValue();
//            Size size = sizeRepository.findById(sizeId).orElseThrow(() -> new Exception("Size not found"));
//            ProductSizeQuantity productSizeQuantity = new ProductSizeQuantity();
////            if(quantity != null) {
//                productSizeQuantity.setProduct(product);
//                productSizeQuantity.setSize(size);
//                productSizeQuantity.setQuantity(quantity);
//                productSizeQuantityRepository.save(productSizeQuantity);
////            }
//        }
//
//        // Save images
//        if (imageProducts != null) {
//            List<Image> imagesList = new ArrayList<>();
//            for (MultipartFile imageProduct : imageProducts) {
//                Image image = new Image();
//                String imageName = imageUpload.storeFile(imageProduct);
//                image.setName(imageName);
//                image.setProduct(product);
//                imageRepository.save(image);
//                imagesList.add(image);
//            }
//            product.setImage(imagesList);
//        }
//
//        productRepository.save(product);
//    }

    @Override
    public void save(ProductDto productDto, MultipartFile[] imageProducts) throws Exception {
        // Check for duplicate product names
        Product existsByName=productRepository.findByName(productDto.getName());

        if (existsByName != null) {
            throw new Exception("Product with this name already exists.");
        }

        Product product = new Product();
        product.setName(productDto.getName());
        product.setBrand(productDto.getBrand());
        product.setCategory(productDto.getCategory());
        product.setShortDescription(productDto.getShortDescription());
        product.setLongDescription(productDto.getLongDescription());
        product.setCostPrice(productDto.getCostPrice());
        product.setSalePrice(productDto.getSalePrice());
        product.setCurrentQuantity(productDto.getCurrentQuantity());
        product.set_activated(true);
        // Save the product first to get an ID
        product = productRepository.save(product);

        // Save size quantities
        for (Map.Entry<Long, Integer> entry : productDto.getSizeQuantities().entrySet()) {
            Long sizeId = entry.getKey();
            Integer quantity = entry.getValue();
            Size size = sizeRepository.findById(sizeId).orElseThrow(() -> new Exception("Size not found"));
            ProductSizeQuantity productSizeQuantity = new ProductSizeQuantity();
//            if(quantity != null) {
            productSizeQuantity.setProduct(product);
            productSizeQuantity.setSize(size);
            productSizeQuantity.setQuantity(quantity);
            productSizeQuantityRepository.save(productSizeQuantity);
//            }
        }

        // Save images
        if (imageProducts != null) {
            List<Image> imagesList = new ArrayList<>();
            for (MultipartFile imageProduct : imageProducts) {
                Image image = new Image();
                String imageName = imageUpload.storeFile(imageProduct);
                image.setName(imageName);
                image.setProduct(product);
                imageRepository.save(image);
                imagesList.add(image);
            }
            product.setImage(imagesList);
        }

        productRepository.save(product);
    }
    // Method to find a product by its ID
    @Override
    public ProductDto findById(long id) {
        // Code to find a product by its ID and convert it to a DTO
        Product product = productRepository.findById(id);
        ProductSizeQuantity productSizeQuantity=productSizeQuantityRepository.getReferenceById(id);
        ProductDto productDto=new ProductDto();
        productDto.setId(product.getId());
        productDto.setImage(product.getImage());
        productDto.setName(product.getName());
        productDto.setShortDescription(product.getShortDescription());
        productDto.setLongDescription(product.getLongDescription());
        productDto.setBrand(product.getBrand());
//        productDto.setSizes(product.getSizes());
        List<ProductSizeQuantity> productSizeQuantities=productSizeQuantityRepository.findProductSizeQuantitiesByProductId(id);
        Map<Long, Integer> sizeQuantities = new HashMap<>();
        for (ProductSizeQuantity psq : productSizeQuantities) {
            sizeQuantities.put(psq.getSize().getId(), psq.getQuantity());
        }
        productDto.setSizeQuantities(sizeQuantities);
        productDto.setCurrentQuantity(product.getCurrentQuantity());
        productDto.setCostPrice(product.getCostPrice());
        productDto.setSalePrice(product.getSalePrice());
        productDto.setCategory(product.getCategory());
        productDto.setActivated(product.is_activated());
        return productDto;
    }



    private boolean isImageExistsForProduct(Long productId, String imageName) {
        // Check if an image with the same name exists for the given product ID
        return imageRepository.existsByNameAndProductId(imageName, productId);
    }

    @Override
    public Product update(List<MultipartFile> imageProducts, ProductDto productDto) {
        try {
            long id = productDto.getId();
            Product productUpdate = productRepository.getById(productDto.getId());
            productUpdate.setCategory(productDto.getCategory());
            productUpdate.setName(productDto.getName());
            productUpdate.setShortDescription(productDto.getShortDescription());
            productUpdate.setLongDescription(productDto.getLongDescription());
            productUpdate.setCostPrice(productDto.getCostPrice());
            productUpdate.setSalePrice(productDto.getSalePrice());
            productUpdate.setCurrentQuantity(productDto.getCurrentQuantity());
//            productUpdate.setSizes(sizes);
            productRepository.save(productUpdate);

            // Retrieve and update ProductSizeQuantities
            List<ProductSizeQuantity> productSizeQuantityUpdate = productSizeQuantityRepository.findProductSizeQuantitiesByProductId(id);
            Map<Long, Integer> sizeQuantities = productDto.getSizeQuantities(); // Assuming productDto has a Map<Long, Integer> sizeQuantities

            for (ProductSizeQuantity psq : productSizeQuantityUpdate) {
                Long sizeId = psq.getSize().getId();
                if (sizeQuantities.containsKey(sizeId)) {
                    psq.setQuantity(sizeQuantities.get(sizeId));
                    productSizeQuantityRepository.save(psq);
                }
            }
            if (imageProducts != null && !imageProducts.isEmpty()) {
                List<Image> imagesList = new ArrayList<>();
                for (MultipartFile imageProduct : imageProducts) {
                    String imageName = imageUpload.storeFile(imageProduct);
                    if (!isImageExistsForProduct(id, imageName)) {
                        Image newImage = new Image();
                        newImage.setName(imageName);
                        newImage.setProduct(productUpdate);
                        imageRepository.save(newImage);
                        imagesList.add(newImage);
                    }
                }
                productUpdate.setImage(imagesList);
            }
            return productRepository.save(productUpdate);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    // Method to disable a product
    @Override
    public void disable(long id) {
        Product product=productRepository.findById(id);
        // Setting activated to false
        product.set_activated(false);
        productRepository.save(product);
    }

    // Method to enable a product
    @Override
    public void enable(long id) {
        Product product=productRepository.findById(id);
        // setting activated to true
        product.set_activated(true);
        productRepository.save(product);

    }

    // Method to find all activated products with pagination
    @Override
    public Page<ProductDto> findAllByActivated(long id,int pageNo) {
        // Code to find activated products with pagination, possibly sorted
        List<Product> products=productRepository.findAllByActivatedTrue(id);
        List<ProductDto>productDtoList = transferData(products);
        Pageable pageable = PageRequest.of(pageNo, 5);
        Page<ProductDto> dtoPage = toPage(productDtoList, pageable);
        return dtoPage;
    }


    // Method to retrieve all activated products
    @Override
    public List<ProductDto> findAllProducts() {
        List<Product> products=productRepository.findAllByActivatedTrue();
        List<ProductDto>productDtoList = transferData(products);
        return productDtoList;
    }



    // Method to retrieve all products sorted by ID in descending order
    @Override
    public List<ProductDto> findAllByOrderDesc() {
        List<Product> products = productRepository.findAllByOrderById();
        List<ProductDto> productDtos = transferData(products);
        return productDtos;
    }



    @Override
    public Long countAllProducts() {
        return productRepository.CountAllProducts();
    }


    @Override
    public Product findBYId(long id) {
        return productRepository.findById(id);
    }

    @Override
    public List<Product> findProductsByCategory(long id) {
        return productRepository.findAllByCategoryId(id);
    }

    // Method to retrieve all products as DTOs
    @Override
    public List<ProductDto> allProduct() {
        List<Product> products = productRepository.findAll().reversed();
        List<ProductDto> productDtos = transferData(products);
        return productDtos;
    }

    // Method to retrieve all products as paginated DTOs
    @Override
    public Page<ProductDto> getAllProducts(int pageNo) {
        Pageable pageable = PageRequest.of(pageNo, 5);
        List<ProductDto> productDtoLists = this.allProduct();
        Page<ProductDto> productDtoPage = toPage(productDtoLists, pageable);
        return productDtoPage;
    }
    @Override
    public Page<ProductDto> getProductsShop(int pageNo) {
        Pageable pageable = PageRequest.of(pageNo, 3);
        List<Product> productDtoLists = this.findAll();
        Page<ProductDto> productDtoPage = toPage(productDtoLists, pageable);
        return productDtoPage;
    }

    // Method to search products by keyword
    @Override
    public Page<ProductDto> searchProducts(int pageNo, String keyword) {
        List<Product> products = productRepository.findAllByNameOrDescription(keyword);
        List<ProductDto> productDtoList = transferData(products);
        Pageable pageable = PageRequest.of(pageNo, 5);
        Page<ProductDto> dtoPage = toPage(productDtoList, pageable);
        return dtoPage;
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



    // Method to delete a product
    @Override
    @Transactional
    public void deleteProduct(long id) {
        // Code to delete a product and associated data (images, sizes)
        Product product = productRepository.findById(id);
        imageRepository.deleteImagesByProductId(id);
        List<ProductSizeQuantity> quantities=productSizeQuantityRepository.findProductSizeQuantitiesByProductId(id);
        for(ProductSizeQuantity quantity : quantities){
            long quantity_id = quantity.getId();
             productSizeQuantityRepository.deleteById(id);
        }
        productRepository.delete(product);
    }
    @Override
    public Product getProductById(Long id) {
        return productRepository.getReferenceById(id);
    }

//    @Override
//    public Page<ProductDto> filterHighPrice(int pageNo) {
//        Pageable pageable = PageRequest.of(pageNo, 4);
//        List<Product> products = productRepository.filterPriceDesc();
//        List<ProductDto> productDtoLists =  transferData(products);
//        Page<ProductDto> productDtoPage = toPage(productDtoLists, pageable);
//        return productDtoPage;
//    }

//    @Override
//    public List<Product> filterLowerPrice() {
//        return productRepository.filterPriceAsc();
//    }
//    @Override
//    public Page<ProductDto> filterLowerPrice(int pageNo) {
//        Pageable pageable = PageRequest.of(pageNo, 4);
//        List<Product> products = productRepository.filterPriceAsc();
//        List<ProductDto> productDtoLists =  transferData(products);
//        Page<ProductDto> productDtoPage = toPage(productDtoLists, pageable);
//        return productDtoPage;
//    }


    @Override
    public Page<ProductDto> getAllProductsLowtoHigh(int pageNo) {
        Pageable pageable = PageRequest.of(pageNo, 3);
        List<Product> products = productRepository.filterPriceAsc();
        List<ProductDto> productDtoLists =  transferData(products);
        Page<ProductDto> productDtoPage = toPage(productDtoLists, pageable);
        return productDtoPage;
    }
    @Override
    public Page<ProductDto> getAllProductsHighToLow(int pageNo) {
        Pageable pageable = PageRequest.of(pageNo, 3);
        List<Product> products = productRepository.filterPriceDesc();
        List<ProductDto> productDtoLists =  transferData(products);
        Page<ProductDto> productDtoPage = toPage(productDtoLists, pageable);
        return productDtoPage;
    }
    @Override
    public Page<ProductDto> getAllProductsAToZ(int pageNo) {
        Pageable pageable = PageRequest.of(pageNo, 3);
        List<Product> products = productRepository.filterAtoZ();
        List<ProductDto> productDtoLists =  transferData(products);
        Page<ProductDto> productDtoPage = toPage(productDtoLists, pageable);
        return productDtoPage;
    }
    @Override
    public Page<ProductDto> getAllProductsZToA(int pageNo) {
        Pageable pageable = PageRequest.of(pageNo, 3);
        List<Product> products = productRepository.filterZtoA();
        List<ProductDto> productDtoLists =  transferData(products);
        Page<ProductDto> productDtoPage = toPage(productDtoLists, pageable);
        return productDtoPage;
    }
    @Override
    public Page<ProductDto> getAllProductsLatest(int pageNo) {
        Pageable pageable = PageRequest.of(pageNo, 3);
        List<Product> products = productRepository.filterLatest();
        List<ProductDto> productDtoLists =  transferData(products);
        Page<ProductDto> productDtoPage = toPage(productDtoLists, pageable);
        return productDtoPage;
    }


//    @Override
//    public Page<ProductDto> filterNewArrivals() {
//        return productRepository.filterLatest();
//    }
//
//    @Override
//    public List<Product> filterAtoZ() {
//        return productRepository.filterAtoZ();
//    }
//
//    @Override
//    public List<Product> filterZtoA() {
//        return productRepository.filterZtoA();
//    }

//    @Override
//    public List<Product> filterPopularity() {
//        return productRepository.filterPopularity();
//    }
    @Override
    public List<Product> searchProduct(String keyword) {
        List<Product> products=productRepository.listViewProductsUserSide(keyword);
        return products;
    }
    @Override
    public List<Product> findTopSellingProductsWithKeyword(String keyword) {
        return productRepository.findTopSellingProductsWithKeyword(keyword);
    }

    @Override
    public List<Product> randomProductWithKeyword(String keyword) {
        List<Product> randomProducts = productRepository.randomProduct();
        List<Long> productIds = randomProducts.stream().map(Product::getId).collect(Collectors.toList());
        if (productIds.isEmpty()) {
            return List.of();
        }
        return productRepository.randomProductWithKeyword(productIds, keyword);
    }
    @Override
    public List<Product> filterByIdDescendingWithKeyword(String keyword) {
        List<Long> last10Ids = productRepository.findLast10ProductIds();
        return productRepository.findProductsByIdsAndKeyword(last10Ids, keyword);
    }
    @Override
    public List<Product> findProductsByCategoryNameAndKeyword(String categoryName, String keyword) {
        return productRepository.findByCategoryNameAndKeywordIgnoreCase(categoryName, keyword);
    }
}






















//package com.ecommerce.library.service.impl;
//
//import com.ecommerce.library.dto.ProductDto;
//import com.ecommerce.library.model.Image;
//import com.ecommerce.library.model.Product;
//import com.ecommerce.library.model.ProductSizeQuantity;
//import com.ecommerce.library.model.Size;
//import com.ecommerce.library.repository.ImageRepository;
//import com.ecommerce.library.repository.ProductRepository;
//import com.ecommerce.library.repository.ProductSizeQuantityRepository;
//import com.ecommerce.library.repository.SizeRepository;
//import com.ecommerce.library.service.ProductService;
//import com.ecommerce.library.utils.ImageUpload;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.dao.DataIntegrityViolationException;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//import org.springframework.data.domain.Pageable;
//
//import java.util.*;
//
//import jakarta.transaction.Transactional;
//
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//
//import java.util.List;
//import java.util.stream.Collectors;
//@Service
//public class ProductServiceImpl implements ProductService {
//
//    private ProductRepository productRepository;
//    private ImageRepository imageRepository;
//    private SizeRepository sizeRepository;
//
//
//    ProductSizeQuantityRepository productSizeQuantityRepository;
//
//
//    private ImageUpload imageUpload;
//
//    @Autowired
//    public ProductServiceImpl(ProductRepository productRepository, SizeRepository sizeRepository,
//                              ImageUpload imageUpload,ImageRepository imageRepository,ProductSizeQuantityRepository productSizeQuantityRepository) {
//
//        this.imageRepository=imageRepository;
//        this.productRepository = productRepository;
//        this.sizeRepository = sizeRepository;
//        this.imageUpload = imageUpload;
//        this.productSizeQuantityRepository=productSizeQuantityRepository;
//    }
//
//
//
//
//    public void addOrUpdateProduct(Product product, Map<Size, Integer> sizeQuantities) {
//        int totalQuantity = sizeQuantities.values().stream().mapToInt(Integer::intValue).sum();
//        if (totalQuantity != product.getCurrentQuantity()) {
//            throw new IllegalArgumentException("The sum of size quantities must equal the product's current quantity");
//        }
//
//        // Create or update ProductSizeQuantity entities
//        List<ProductSizeQuantity> productSizes = sizeQuantities.entrySet().stream().map(entry -> {
//            ProductSizeQuantity productSize = new ProductSizeQuantity();
//            productSize.setProduct(product);
//            productSize.setSize(entry.getKey());
//            productSize.setQuantity(entry.getValue());
//            return productSize;
//        }).collect(Collectors.toList());
//
//        product.setProductSizesQuantity(productSizes);
//
//        // Save the product using your repository
//        productRepository.save(product);
//    }
//
//
//
//    // Method to retrieve all products
//    @Override
//    public List<Product> findAll() {
//        return productRepository.findAll();
//    }
//
//    @Override
//    public Product findByName(String name) {
//        return productRepository.findByName(name);
//    }
//
//
//    // Method to transfer data from Product entities to ProductDto objects
//    private List<ProductDto> transferData(List<Product> products) {
//        List<ProductDto> productDtos = new ArrayList<>();
//        for (Product product : products) {
//            ProductDto productDto = new ProductDto();
//            // Transfer data from Product to ProductDto
//            productDto.setId(product.getId());
//            productDto.setName(product.getName());
//            productDto.setCurrentQuantity(product.getCurrentQuantity());
//            productDto.setCostPrice(product.getCostPrice());
//            productDto.setSalePrice(product.getSalePrice());
//            productDto.setShortDescription(product.getShortDescription());
//            productDto.setLongDescription(product.getLongDescription());
//            productDto.setBrand(product.getBrand());
//            productDto.setImage(product.getImage());
//            productDto.setCategory(product.getCategory());
//            productDto.setActivated(product.is_activated());
//            productDtos.add(productDto);
//        }
//        return productDtos;
//    }
//
//    // Method to save a product
//
//    @Override
//    public void save(ProductDto productDto, MultipartFile[] imageProducts) throws Exception {
//        // Check for duplicate product names
//        Product existsByName=productRepository.findByName(productDto.getName());
//
//        if (existsByName != null) {
//            throw new Exception("Product with this name already exists.");
//        }
//
//        Product product = new Product();
//        product.setName(productDto.getName());
//        product.setBrand(productDto.getBrand());
//        product.setCategory(productDto.getCategory());
//        product.setShortDescription(productDto.getShortDescription());
//        product.setLongDescription(productDto.getLongDescription());
//        product.setCostPrice(productDto.getCostPrice());
//        product.setSalePrice(productDto.getSalePrice());
//        product.setCurrentQuantity(productDto.getCurrentQuantity());
//
//        // Save the product first to get an ID
//        product = productRepository.save(product);
//
//        // Save size quantities
//        for (Map.Entry<Long, Integer> entry : productDto.getSizeQuantities().entrySet()) {
//            Long sizeId = entry.getKey();
//            Integer quantity = entry.getValue();
//            Size size = sizeRepository.findById(sizeId).orElseThrow(() -> new Exception("Size not found"));
//            ProductSizeQuantity productSizeQuantity = new ProductSizeQuantity();
//            if(quantity != null) {
//                productSizeQuantity.setProduct(product);
//                productSizeQuantity.setSize(size);
//                productSizeQuantity.setQuantity(quantity);
//                productSizeQuantityRepository.save(productSizeQuantity);
//            }
//        }
//
//        // Save images
//        if (imageProducts != null) {
//            List<Image> imagesList = new ArrayList<>();
//            for (MultipartFile imageProduct : imageProducts) {
//                Image image = new Image();
//                String imageName = imageUpload.storeFile(imageProduct);
//                image.setName(imageName);
//                image.setProduct(product);
//                imageRepository.save(image);
//                imagesList.add(image);
//            }
//            product.setImage(imagesList);
//        }
//
//        productRepository.save(product);
//    }
//
//    // Method to find a product by its ID
//    @Override
//    public ProductDto findById(long id) {
//        // Code to find a product by its ID and convert it to a DTO
//        Product product = productRepository.findById(id);
//        ProductSizeQuantity productSizeQuantity=productSizeQuantityRepository.getReferenceById(id);
//        ProductDto productDto=new ProductDto();
//        productDto.setId(product.getId());
//        productDto.setImage(product.getImage());
//        productDto.setName(product.getName());
//        productDto.setShortDescription(product.getShortDescription());
//        productDto.setLongDescription(product.getLongDescription());
//        productDto.setBrand(product.getBrand());
////        productDto.setSizes(product.getSizes());
//        productDto.setCurrentQuantity(product.getCurrentQuantity());
//        productDto.setCostPrice(product.getCostPrice());
//        productDto.setSalePrice(product.getSalePrice());
//        productDto.setCategory(product.getCategory());
//        productDto.setActivated(product.is_activated());
//        return productDto;
//    }
//
//
//
//    private boolean isImageExistsForProduct(Long productId, String imageName) {
//        // Check if an image with the same name exists for the given product ID
//        return imageRepository.existsByNameAndProductId(imageName, productId);
//    }
//
//    @Override
//    public Product update(List<MultipartFile> imageProducts, ProductDto productDto,List<Long> size_id) {
//        try {
//            long id = productDto.getId();
//            Product productUpdate = productRepository.getById(productDto.getId());
//            List<Size> sizes = sizeRepository.findAllById(size_id);
//            productUpdate.setCategory(productDto.getCategory());
//            productUpdate.setName(productDto.getName());
//            productUpdate.setShortDescription(productDto.getShortDescription());
//            productUpdate.setLongDescription(productDto.getLongDescription());
//            productUpdate.setCostPrice(productDto.getCostPrice());
//            productUpdate.setSalePrice(productDto.getSalePrice());
//            productUpdate.setCurrentQuantity(productDto.getCurrentQuantity());
////            productUpdate.setSizes(sizes);
//            productRepository.save(productUpdate);
//            if (imageProducts != null && !imageProducts.isEmpty()) {
//                List<Image> imagesList = new ArrayList<>();
//                for (MultipartFile imageProduct : imageProducts) {
//                    String imageName = imageUpload.storeFile(imageProduct);
//                    if (!isImageExistsForProduct(id, imageName)) {
//                        Image newImage = new Image();
//                        newImage.setName(imageName);
//                        newImage.setProduct(productUpdate);
//                        imageRepository.save(newImage);
//                        imagesList.add(newImage);
//                    }
//                }
//                productUpdate.setImage(imagesList);
//            }
//            return productRepository.save(productUpdate);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    // Method to disable a product
//    @Override
//    public void disable(long id) {
//        Product product=productRepository.findById(id);
//        // Setting activated to false
//        product.set_activated(false);
//        productRepository.save(product);
//    }
//
//    // Method to enable a product
//    @Override
//    public void enable(long id) {
//        Product product=productRepository.findById(id);
//        // setting activated to true
//        product.set_activated(true);
//        productRepository.save(product);
//
//    }
//
//    // Method to find all activated products with pagination
//    @Override
//    public Page<ProductDto> findAllByActivated(long id,int pageNo) {
//        // Code to find activated products with pagination, possibly sorted
//        List<Product> products=productRepository.findAllByActivatedTrue(id);
//        List<ProductDto>productDtoList = transferData(products);
//        Pageable pageable = PageRequest.of(pageNo, 5);
//        Page<ProductDto> dtoPage = toPage(productDtoList, pageable);
//        return dtoPage;
//    }
//
//
//    // Method to retrieve all activated products
//    @Override
//    public List<ProductDto> findAllProducts() {
//        List<Product> products=productRepository.findAllByActivatedTrue();
//        List<ProductDto>productDtoList = transferData(products);
//        return productDtoList;
//    }
//
//
//
//    // Method to retrieve all products sorted by ID in descending order
//    @Override
//    public List<ProductDto> findAllByOrderDesc() {
//        List<Product> products = productRepository.findAllByOrderById();
//        List<ProductDto> productDtos = transferData(products);
//        return productDtos;
//    }
//
//
//
//    @Override
//    public Long countAllProducts() {
//        return productRepository.CountAllProducts();
//    }
//
//
//    @Override
//    public Product findBYId(long id) {
//        return productRepository.findById(id);
//    }
//
//    @Override
//    public List<Product> findProductsByCategory(long id) {
//        return productRepository.findAllByCategoryId(id);
//    }
//
//    // Method to retrieve all products as DTOs
//    @Override
//    public List<ProductDto> allProduct() {
//        List<Product> products = productRepository.findAll().reversed();
//        List<ProductDto> productDtos = transferData(products);
//        return productDtos;
//    }
//
//    // Method to retrieve all products as paginated DTOs
//    @Override
//    public Page<ProductDto> getAllProducts(int pageNo) {
//        Pageable pageable = PageRequest.of(pageNo, 5);
//        List<ProductDto> productDtoLists = this.allProduct();
//        Page<ProductDto> productDtoPage = toPage(productDtoLists, pageable);
//        return productDtoPage;
//    }
//
//    // Method to search products by keyword
//    @Override
//    public Page<ProductDto> searchProducts(int pageNo, String keyword) {
//        List<Product> products = productRepository.findAllByNameOrDescription(keyword);
//        List<ProductDto> productDtoList = transferData(products);
//        Pageable pageable = PageRequest.of(pageNo, 5);
//        Page<ProductDto> dtoPage = toPage(productDtoList, pageable);
//        return dtoPage;
//    }
//
//    private Page toPage(List list, Pageable pageable) {
//        if (pageable.getOffset() >= list.size()) {
//            return Page.empty();
//        }
//        int startIndex = (int) pageable.getOffset();
//        int endIndex = ((pageable.getOffset() + pageable.getPageSize()) > list.size())
//                ? list.size()
//                : (int) (pageable.getOffset() + pageable.getPageSize());
//        List subList = list.subList(startIndex, endIndex);
//        return new PageImpl(subList, pageable, list.size());
//    }
//
//
//
//    // Method to delete a product
//    @Override
//    @Transactional
//    public void deleteProduct(long id) {
//        // Code to delete a product and associated data (images, sizes)
//        Product product = productRepository.findById(id);
//        imageRepository.deleteImagesByProductId(id);
////        List<ProductSizeQuantity> sizes=product.getProductSizesQuantity();
////        for(ProductSizeQuantity size : sizes){
////            long size_id = size.getQuantity();
////             productSizeQuantityRepository.deleteById();
////        }
//        productRepository.delete(product);
//    }
//
//    //    CUSTOMER
//
//    @Override
//    public Product getProductById(Long id) {
//        return productRepository.getReferenceById(id);
//    }
//
//    @Override
//    public List<Product> filterHighPrice() {
//        return productRepository.filterPriceDesc();
//    }
//
//    @Override
//    public List<Product> filterLowerPrice() {
//        return productRepository.filterPriceAsc();
//    }
//
//    @Override
//    public List<Product> filterNewArrivals() {
//        return productRepository.filterLatest();
//    }
//
//    @Override
//    public List<Product> filterAtoZ() {
//        return productRepository.filterAtoZ();
//    }
//
//    @Override
//    public List<Product> filterZtoA() {
//        return productRepository.filterZtoA();
//    }
//
////    @Override
////    public List<Product> filterPopularity() {
////        return productRepository.filterPopularity();
////    }


//}































////package com.ecommerce.library.service.impl;
////
////import com.ecommerce.library.dto.ProductDto;
////import com.ecommerce.library.model.Image;
////import com.ecommerce.library.model.Product;
////import com.ecommerce.library.model.ProductSizeQuantity;
////import com.ecommerce.library.model.Size;
////import com.ecommerce.library.repository.ImageRepository;
////import com.ecommerce.library.repository.ProductRepository;
////import com.ecommerce.library.repository.ProductSizeQuantityRepository;
////import com.ecommerce.library.repository.SizeRepository;
////import com.ecommerce.library.service.ProductService;
////import com.ecommerce.library.utils.ImageUpload;
////import org.springframework.beans.factory.annotation.Autowired;
////import org.springframework.dao.DataIntegrityViolationException;
////import org.springframework.stereotype.Service;
////import org.springframework.web.multipart.MultipartFile;
////import org.springframework.data.domain.Pageable;
////
////import java.util.*;
////
////import jakarta.transaction.Transactional;
////
////import org.springframework.data.domain.Page;
////import org.springframework.data.domain.PageImpl;
////import org.springframework.data.domain.PageRequest;
////import org.springframework.data.domain.Pageable;
////
////import java.util.List;
////import java.util.stream.Collectors;
////@Service
////public class ProductServiceImpl implements ProductService {
////
////    private ProductRepository productRepository;
////    private ImageRepository imageRepository;
////    private SizeRepository sizeRepository;
////
////
////    ProductSizeQuantityRepository productSizeQuantityRepository;
////
////
////    private ImageUpload imageUpload;
////
////    @Autowired
////    public ProductServiceImpl(ProductRepository productRepository, SizeRepository sizeRepository,
////                              ImageUpload imageUpload,ImageRepository imageRepository,ProductSizeQuantityRepository productSizeQuantityRepository) {
////
////        this.imageRepository=imageRepository;
////        this.productRepository = productRepository;
////        this.sizeRepository = sizeRepository;
////        this.imageUpload = imageUpload;
////        this.productSizeQuantityRepository=productSizeQuantityRepository;
////    }
////
////
////
////
////    public void addOrUpdateProduct(Product product, Map<Size, Integer> sizeQuantities) {
////        int totalQuantity = sizeQuantities.values().stream().mapToInt(Integer::intValue).sum();
////        if (totalQuantity != product.getCurrentQuantity()) {
////            throw new IllegalArgumentException("The sum of size quantities must equal the product's current quantity");
////        }
////
////        // Create or update ProductSizeQuantity entities
////        List<ProductSizeQuantity> productSizes = sizeQuantities.entrySet().stream().map(entry -> {
////            ProductSizeQuantity productSize = new ProductSizeQuantity();
////            productSize.setProduct(product);
////            productSize.setSize(entry.getKey());
////            productSize.setQuantity(entry.getValue());
////            return productSize;
////        }).collect(Collectors.toList());
////
////        product.setProductSizesQuantity(productSizes);
////
////        // Save the product using your repository
////        productRepository.save(product);
////    }
////
////
////
////    // Method to retrieve all products
////    @Override
////    public List<Product> findAll() {
////        return productRepository.findAll();
////    }
////
////    @Override
////    public Product findByName(String name) {
////        return productRepository.findByName(name);
////    }
////
////
////    // Method to transfer data from Product entities to ProductDto objects
////    private List<ProductDto> transferData(List<Product> products) {
////        List<ProductDto> productDtos = new ArrayList<>();
////        for (Product product : products) {
////            ProductDto productDto = new ProductDto();
////            // Transfer data from Product to ProductDto
////            productDto.setId(product.getId());
////            productDto.setName(product.getName());
////            productDto.setCurrentQuantity(product.getCurrentQuantity());
////            productDto.setCostPrice(product.getCostPrice());
////            productDto.setSalePrice(product.getSalePrice());
////            productDto.setShortDescription(product.getShortDescription());
////            productDto.setLongDescription(product.getLongDescription());
////            productDto.setBrand(product.getBrand());
////            productDto.setImage(product.getImage());
////            productDto.setCategory(product.getCategory());
////            productDto.setActivated(product.is_activated());
////            productDtos.add(productDto);
////        }
////        return productDtos;
////    }
////
////    // Method to save a product
////
////    @Override
////    public void save(ProductDto productDto, MultipartFile[] imageProducts) throws Exception {
////        // Check for duplicate product names
////        Product existsByName=productRepository.findByName(productDto.getName());
////
////        if (existsByName != null) {
////            throw new Exception("Product with this name already exists.");
////        }
////
////        Product product = new Product();
////        product.setName(productDto.getName());
////        product.setBrand(productDto.getBrand());
////        product.setCategory(productDto.getCategory());
////        product.setShortDescription(productDto.getShortDescription());
////        product.setLongDescription(productDto.getLongDescription());
////        product.setCostPrice(productDto.getCostPrice());
////        product.setSalePrice(productDto.getSalePrice());
////        product.setCurrentQuantity(productDto.getCurrentQuantity());
////
////        // Save the product first to get an ID
////        product = productRepository.save(product);
////
////        // Save size quantities
////        for (Map.Entry<Long, Integer> entry : productDto.getSizeQuantities().entrySet()) {
////            Long sizeId = entry.getKey();
////            Integer quantity = entry.getValue();
////            Size size = sizeRepository.findById(sizeId).orElseThrow(() -> new Exception("Size not found"));
////            ProductSizeQuantity productSizeQuantity = new ProductSizeQuantity();
////            if(quantity != null) {
////                productSizeQuantity.setProduct(product);
////                productSizeQuantity.setSize(size);
////                productSizeQuantity.setQuantity(quantity);
////                productSizeQuantityRepository.save(productSizeQuantity);
////            }
////        }
////
////        // Save images
////        if (imageProducts != null) {
////            List<Image> imagesList = new ArrayList<>();
////            for (MultipartFile imageProduct : imageProducts) {
////                Image image = new Image();
////                String imageName = imageUpload.storeFile(imageProduct);
////                image.setName(imageName);
////                image.setProduct(product);
////                imageRepository.save(image);
////                imagesList.add(image);
////            }
////            product.setImage(imagesList);
////        }
////
////        productRepository.save(product);
////    }
////
////    // Method to find a product by its ID
////    @Override
////    public ProductDto findById(long id) {
////        // Code to find a product by its ID and convert it to a DTO
////        Product product = productRepository.findById(id);
////        ProductSizeQuantity productSizeQuantity=productSizeQuantityRepository.getReferenceById(id);
////        ProductDto productDto=new ProductDto();
////        productDto.setId(product.getId());
////        productDto.setImage(product.getImage());
////        productDto.setName(product.getName());
////        productDto.setShortDescription(product.getShortDescription());
////        productDto.setLongDescription(product.getLongDescription());
////        productDto.setBrand(product.getBrand());
//////        productDto.setSizes(product.getSizes());
////        productDto.setCurrentQuantity(product.getCurrentQuantity());
////        productDto.setCostPrice(product.getCostPrice());
////        productDto.setSalePrice(product.getSalePrice());
////        productDto.setCategory(product.getCategory());
////        productDto.setActivated(product.is_activated());
////        return productDto;
////    }
////
////
////
////    private boolean isImageExistsForProduct(Long productId, String imageName) {
////        // Check if an image with the same name exists for the given product ID
////        return imageRepository.existsByNameAndProductId(imageName, productId);
////    }
////
////    @Override
////    public Product update(List<MultipartFile> imageProducts, ProductDto productDto,List<Long> size_id) {
////        try {
////            long id = productDto.getId();
////            Product productUpdate = productRepository.getById(productDto.getId());
////            List<Size> sizes = sizeRepository.findAllById(size_id);
////            productUpdate.setCategory(productDto.getCategory());
////            productUpdate.setName(productDto.getName());
////            productUpdate.setShortDescription(productDto.getShortDescription());
////            productUpdate.setLongDescription(productDto.getLongDescription());
////            productUpdate.setCostPrice(productDto.getCostPrice());
////            productUpdate.setSalePrice(productDto.getSalePrice());
////            productUpdate.setCurrentQuantity(productDto.getCurrentQuantity());
//////            productUpdate.setSizes(sizes);
////            productRepository.save(productUpdate);
////            if (imageProducts != null && !imageProducts.isEmpty()) {
////                List<Image> imagesList = new ArrayList<>();
////                for (MultipartFile imageProduct : imageProducts) {
////                    String imageName = imageUpload.storeFile(imageProduct);
////                    if (!isImageExistsForProduct(id, imageName)) {
////                        Image newImage = new Image();
////                        newImage.setName(imageName);
////                        newImage.setProduct(productUpdate);
////                        imageRepository.save(newImage);
////                        imagesList.add(newImage);
////                    }
////                }
////                productUpdate.setImage(imagesList);
////            }
////            return productRepository.save(productUpdate);
////        } catch (Exception e) {
////            e.printStackTrace();
////            return null;
////        }
////    }
////
////    // Method to disable a product
////    @Override
////    public void disable(long id) {
////        Product product=productRepository.findById(id);
////        // Setting activated to false
////        product.set_activated(false);
////        productRepository.save(product);
////    }
////
////    // Method to enable a product
////    @Override
////    public void enable(long id) {
////        Product product=productRepository.findById(id);
////        // setting activated to true
////        product.set_activated(true);
////        productRepository.save(product);
////
////    }
////
////    // Method to find all activated products with pagination
////    @Override
////    public Page<ProductDto> findAllByActivated(long id,int pageNo) {
////        // Code to find activated products with pagination, possibly sorted
////        List<Product> products=productRepository.findAllByActivatedTrue(id);
////        List<ProductDto>productDtoList = transferData(products);
////        Pageable pageable = PageRequest.of(pageNo, 5);
////        Page<ProductDto> dtoPage = toPage(productDtoList, pageable);
////        return dtoPage;
////    }
////
////
////    // Method to retrieve all activated products
////    @Override
////    public List<ProductDto> findAllProducts() {
////        List<Product> products=productRepository.findAllByActivatedTrue();
////        List<ProductDto>productDtoList = transferData(products);
////        return productDtoList;
////    }
////
////
////
////    // Method to retrieve all products sorted by ID in descending order
////    @Override
////    public List<ProductDto> findAllByOrderDesc() {
////        List<Product> products = productRepository.findAllByOrderById();
////        List<ProductDto> productDtos = transferData(products);
////        return productDtos;
////    }
////
////
////
////    @Override
////    public Long countAllProducts() {
////        return productRepository.CountAllProducts();
////    }
////
////
////    @Override
////    public Product findBYId(long id) {
////        return productRepository.findById(id);
////    }
////
////    @Override
////    public List<Product> findProductsByCategory(long id) {
////        return productRepository.findAllByCategoryId(id);
////    }
////
////    // Method to retrieve all products as DTOs
////    @Override
////    public List<ProductDto> allProduct() {
////        List<Product> products = productRepository.findAll().reversed();
////        List<ProductDto> productDtos = transferData(products);
////        return productDtos;
////    }
////
////    // Method to retrieve all products as paginated DTOs
////    @Override
////    public Page<ProductDto> getAllProducts(int pageNo) {
////        Pageable pageable = PageRequest.of(pageNo, 5);
////        List<ProductDto> productDtoLists = this.allProduct();
////        Page<ProductDto> productDtoPage = toPage(productDtoLists, pageable);
////        return productDtoPage;
////    }
////
////    // Method to search products by keyword
////    @Override
////    public Page<ProductDto> searchProducts(int pageNo, String keyword) {
////        List<Product> products = productRepository.findAllByNameOrDescription(keyword);
////        List<ProductDto> productDtoList = transferData(products);
////        Pageable pageable = PageRequest.of(pageNo, 5);
////        Page<ProductDto> dtoPage = toPage(productDtoList, pageable);
////        return dtoPage;
////    }
////
////    private Page toPage(List list, Pageable pageable) {
////        if (pageable.getOffset() >= list.size()) {
////            return Page.empty();
////        }
////        int startIndex = (int) pageable.getOffset();
////        int endIndex = ((pageable.getOffset() + pageable.getPageSize()) > list.size())
////                ? list.size()
////                : (int) (pageable.getOffset() + pageable.getPageSize());
////        List subList = list.subList(startIndex, endIndex);
////        return new PageImpl(subList, pageable, list.size());
////    }
////
////
////
////    // Method to delete a product
////    @Override
////    @Transactional
////    public void deleteProduct(long id) {
////        // Code to delete a product and associated data (images, sizes)
////        Product product = productRepository.findById(id);
////        imageRepository.deleteImagesByProductId(id);
//////        List<ProductSizeQuantity> sizes=product.getProductSizesQuantity();
//////        for(ProductSizeQuantity size : sizes){
//////            long size_id = size.getQuantity();
//////             productSizeQuantityRepository.deleteById();
//////        }
////        productRepository.delete(product);
////    }
////
////    //    CUSTOMER
////
////    @Override
////    public Product getProductById(Long id) {
////        return productRepository.getReferenceById(id);
////    }
////
////    @Override
////    public List<Product> filterHighPrice() {
////        return productRepository.filterHighPrice();
////    }
////
////    @Override
////    public List<Product> filterLowerPrice() {
////        return productRepository.filterLowerPrice();
////    }
////
////    @Override
////    public List<Product> filterNewArrivals() {
////        return productRepository.filterLatest();
////    }
////
////    @Override
////    public List<Product> filterAtoZ() {
////        return productRepository.filterAtoZ();
////    }
////
////    @Override
////    public List<Product> filterZtoA() {
////        return productRepository.filterZtoA();
////    }
////
//////    @Override
//////    public List<Product> filterPopularity() {
//////        return productRepository.filterPopularity();
//////    }
////
////    @Override
////    public List<ProductDto> allProductFilterZtoA() {
////        List<Product> products = productRepository.filterZtoA();
////        List<ProductDto> productDtos = getData(products);
////        return productDtos;
////    }
////    @Override
////    public Page<ProductDto> getProductsfilterZtoA(int pageNo) {
////        Pageable pageable = PageRequest.of(pageNo, 5);
////        List<ProductDto> productDtoLists = this.allProductFilterZtoA();
////        Page<ProductDto> productDtoPage = toPage(productDtoLists, pageable);
////        return productDtoPage;
////    }
////    private List<ProductDto> getData(List<Product> products) {
////        List<ProductDto> productDtos = new ArrayList<>();
////        for (Product product : products) {
////            ProductDto productDto = new ProductDto();
////            // Transfer data from Product to ProductDto
////            productDto.setId(product.getId());
////            productDto.setName(product.getName());
////            productDto.setCurrentQuantity(product.getCurrentQuantity());
////            productDto.setCostPrice(product.getCostPrice());
////            productDto.setSalePrice(product.getSalePrice());
////            productDto.setShortDescription(product.getShortDescription());
////            productDto.setLongDescription(product.getLongDescription());
////            productDto.setBrand(product.getBrand());
////            productDto.setImage(product.getImage());
////            productDto.setCategory(product.getCategory());
////            productDto.setActivated(product.is_activated());
////            productDtos.add(productDto);
////        }
////        return productDtos;
////    }
////
////}
