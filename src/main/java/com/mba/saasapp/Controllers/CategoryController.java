package com.mba.saasapp.Controllers;

import com.mba.saasapp.common.PageResponse;
import com.mba.saasapp.entities.requests.CategoryRequest;
import com.mba.saasapp.entities.responses.CategoryResponse;
import com.mba.saasapp.services.CategoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService service ;

     @PostMapping
        public ResponseEntity<Void> createCategory(
             @RequestBody
                @Valid
                @NotNull(message = "Category ID cannot be null")
                final CategoryRequest request
        ) {

         this.service.create(request);
         return ResponseEntity.status(HttpStatus.CREATED).build();

        }

        @PutMapping("/{category-id}")
        public ResponseEntity<Void> updateCategory(
                @RequestBody
                @Valid

                final CategoryRequest request,
                @PathVariable("category-id")
                @NotNull(message = "Category ID cannot be null")
                final String id
        ) {
            this.service.update(id, request) ;
            return ResponseEntity.accepted().build();
        }

        @GetMapping("/{category-id}")
        public ResponseEntity<CategoryResponse> findCategoryById(
                @PathVariable("category-id")
                @NotNull(message = "Category ID cannot be null")
                final String id
        ) {
            return ResponseEntity.ok(this.service.findById(id));
        }


@GetMapping
public ResponseEntity<PageResponse<CategoryResponse>> findAllCategories(

        @RequestParam(name = "page", defaultValue = "0")
        final int page,
        @RequestParam(name = "size", defaultValue = "10")
        final int size
   ) {
    return ResponseEntity.ok(this.service.findAll(page, size));
}



    @DeleteMapping("/{category-id}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable("category-id")
            @NotNull(message = "Category ID cannot be null")
            final String id
    ) {
        this.service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

