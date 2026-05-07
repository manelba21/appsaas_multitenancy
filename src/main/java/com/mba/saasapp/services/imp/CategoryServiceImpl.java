package com.mba.saasapp.services.imp;

import com.mba.saasapp.common.PageResponse;
import com.mba.saasapp.config.TenantContext;
import com.mba.saasapp.entities.Category;
import com.mba.saasapp.entities.requests.CategoryRequest;
import com.mba.saasapp.entities.responses.CategoryResponse;
import com.mba.saasapp.mappers.CategoryMapper;
import com.mba.saasapp.repositories.CategoryRepository;
import com.mba.saasapp.services.CategoryService;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CategoryServiceImpl  implements CategoryService {


    private final     CategoryRepository categoryRepository;
       private final CategoryMapper categoryMapper;

    @Override

    public void create(final CategoryRequest request) {
        // check if category already exists
        checkIfCategoryAlreadyExistsByName(request.getName());

        final Category entity = this.categoryMapper.toEntity(request);

        this.categoryRepository.save(entity);
    }

     @Override
     public void update (final String id , final CategoryRequest request) {


         checkIfCategoryAlreadyExistsByName(request.getName());

         final Category entity = this.categoryMapper.toEntity(request);
         final Category   CategoryToUpdate = this.categoryMapper.toEntity(request) ;
         CategoryToUpdate.setId(String.valueOf(id));
              this.categoryRepository.save(CategoryToUpdate);


     }

    @Override
    public PageResponse<CategoryResponse> findAll( final int page,  final int size) {

        final PageRequest pageRequest = PageRequest.of( page ,size );
        final  Page <Category> categories =  this.categoryRepository.findAll(pageRequest);
          final Page <CategoryResponse> categoryResponses =  categories.map(this.categoryMapper::toResponse);

        return PageResponse.of(categoryResponses);
    }


    @Override
    public CategoryResponse findById(final String id) {
        return this.categoryRepository.findById(id)
                .map(this.categoryMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Category does not exist"));
    }

    @Override
    public void delete(final String id) {
        final Category category = this.categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category does not exist"));

        this.categoryRepository.delete(category);
    }

    private void checkIfCategoryAlreadyExistsByName(final String categoryName) {
        final Optional<Category> category =
                this.categoryRepository.findByNameIgnoreCase(categoryName);

        if (category.isPresent()) {
            log.debug("Category already exists");
            throw new RuntimeException("Category already exists"); // we will use custom exception later
        }
    }
}
