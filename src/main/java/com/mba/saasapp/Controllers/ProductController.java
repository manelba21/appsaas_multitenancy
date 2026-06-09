package com.mba.saasapp.Controllers;

import com.mba.saasapp.common.PageResponse;
import com.mba.saasapp.entities.requests.CategoryRequest;
import com.mba.saasapp.entities.requests.ProductRequest;
import com.mba.saasapp.entities.responses.ProductResponse;
import com.mba.saasapp.services.CategoryService;
import com.mba.saasapp.services.ProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Product", description = "Product API")

public class ProductController {


        private final ProductService service;

        @PostMapping
        public ResponseEntity<Void> createProduct(
                @RequestBody
                @Valid
                @NotNull(message = "Product ID cannot be null")
                final ProductRequest request
        ) {
            this.service.create(request);
            return ResponseEntity.ok().build();
        }


        @PutMapping("/{product-id}")
        public ResponseEntity<Void> updateProduct(
                @RequestBody
                @Valid
                final ProductRequest request,
                @PathVariable("product-id")
                @NotNull(message = "Product ID cannot be null")
                final String id
        ) {
            this.service.update(id, request);
            return ResponseEntity.accepted().build();
        }


    @GetMapping("/{product-id}")
    public ResponseEntity<ProductResponse> findProductById(
            @PathVariable("product-id")
            @NotNull(message = "Product ID cannot be null")
            final String id
    ) {
        return ResponseEntity.ok(this.service.findById(id));
    }

    @GetMapping
    public ResponseEntity<PageResponse<ProductResponse>> findAllProducts(
            @RequestParam(name = "page", defaultValue = "0")
            final int page,
            @RequestParam(name = "size", defaultValue = "10")
            @NotNull(message = "Product ID cannot be null")
            final int size
    ) {
        return ResponseEntity.ok(this.service.findAll(page,size));
    }


    @DeleteMapping("/{product-id}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable("product-id")
            @NotNull(message = "Product ID cannot be null")
            final String id
    ) {
        this.service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

