package com.megamart.service;

import com.megamart.dto.product.ProductRequest;
import com.megamart.dto.product.ProductResponse;
import com.megamart.exception.BusinessException;
import com.megamart.exception.NotFoundException;
import com.megamart.model.Product;
import com.megamart.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> list(String category, Pageable pageable) {
        Page<Product> products = category == null || category.isBlank()
                ? productRepository.findByActiveTrue(pageable)
                : productRepository.findByActiveTrueAndCategoryIgnoreCase(category.trim(), pageable);
        return products.map(ProductResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> listAll(Pageable pageable) {
        return productRepository.findAll(pageable).map(ProductResponse::from);
    }

    @Transactional(readOnly = true)
    public List<String> categories() {
        return productRepository.findActiveCategories();
    }

    @Transactional(readOnly = true)
    public ProductResponse getBySku(String sku) {
        if (sku == null || sku.isBlank()) {
            throw new BusinessException("SKU is required.");
        }
        Product product = productRepository.findBySkuIgnoreCase(sku.trim())
                .filter(Product::isActive)
                .orElseThrow(() -> new NotFoundException("Product not found."));
        return ProductResponse.from(product);
    }

    @Transactional(readOnly = true)
    public ProductResponse get(Long id) {
        Product product = productRepository.findById(id)
                .filter(Product::isActive)
                .orElseThrow(() -> new NotFoundException("Product not found."));
        return ProductResponse.from(product);
    }

    @Transactional(readOnly = true)
    public ProductResponse getForAdmin(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found."));
        return ProductResponse.from(product);
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        Product product = new Product(clean(request.sku()).toUpperCase(Locale.ROOT), clean(request.name()), clean(request.category()),
                request.price(), cleanOptional(request.weight()), cleanOptional(request.heightPreference()),
                request.stockQuantity(), cleanOptional(request.imageUrl()));
        return ProductResponse.from(productRepository.save(product));
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = productRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new NotFoundException("Product not found."));
        product.update(clean(request.sku()).toUpperCase(Locale.ROOT), clean(request.name()), clean(request.category()),
                request.price(), cleanOptional(request.weight()), cleanOptional(request.heightPreference()),
                request.stockQuantity(), cleanOptional(request.imageUrl()));
        return ProductResponse.from(product);
    }

    private String clean(String value) { return value.trim(); }
    private String cleanOptional(String value) { return value == null || value.isBlank() ? null : value.trim(); }
}
