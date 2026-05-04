package com.mba.saasapp.mappers;

import com.mba.saasapp.entities.Category;
import com.mba.saasapp.entities.requests.CategoryRequest;
import com.mba.saasapp.entities.responses.CategoryResponse;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {



    public Category toEntity(final CategoryRequest request) {
        return Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
    }

    public CategoryResponse toResponse(final Category entity) {
      final   int nbProduct =  0; //entity.getProducts() ==  null ?  0 : entity.getProducts().size() ;
        return CategoryResponse.builder()
                .name(entity.getName())
                .description(entity.getDescription())
                .nbrProducts(nbProduct)
                .build();
    }

}
