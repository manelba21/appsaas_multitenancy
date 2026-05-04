package com.mba.saasapp.mappers;

import com.mba.saasapp.entities.Category;
import com.mba.saasapp.entities.Product;
import com.mba.saasapp.entities.requests.ProductRequest;
import com.mba.saasapp.entities.responses.ProductResponse;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {


    public Product toEntity(final ProductRequest request) {
        return Product.builder()
                .name(request.getName())
                .reference(request.getReference())
                .description(request.getDescription())
                .price(request.getPrice())
                .alertThreshold(request.getAlertThreshold())
                .category(
                        Category.builder()
                                .id(request.getCategoryId())
                                .build()
                )

                .build();
    }

    public ProductResponse toResponse(final Product product) {
        return ProductResponse.builder()
                .name(product.getName())
                .reference(product.getReference())
                .description(product.getDescription())
                .price(product.getPrice())
                .alertThreshold(product.getAlertThreshold())
                .categoryName(product.getCategory().getName())
                // .availableQuantity() to be later implemented
                .build();
    }
}
