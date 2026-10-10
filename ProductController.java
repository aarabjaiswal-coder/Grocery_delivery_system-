package com.megamart.api.controller;

import com.megamart.api.ApiPageRequest;
import com.megamart.api.dto.PageResponse;
import com.megamart.dto.product.ProductResponse;
import com.megamart.service.ProductService;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public PageResponse<ProductResponse> list(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "24") int size) {
        return PageResponse.from(productService.list(category,
                ApiPageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"))));
    }

    @GetMapping("/categories")
    public List<String> categories() {
        return productService.categories();
    }

    @GetMapping("/sku/{sku}")
    public ProductResponse getBySku(@PathVariable String sku) {
        return productService.getBySku(sku);
    }

    @GetMapping("/{id}")
    public ProductResponse get(@PathVariable Long id) {
        return productService.get(id);
    }
}
