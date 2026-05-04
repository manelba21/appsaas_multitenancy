package com.mba.saasapp.services.imp;

import com.mba.saasapp.common.PageResponse;
import com.mba.saasapp.entities.Product;
import com.mba.saasapp.entities.requests.ProductRequest;
import com.mba.saasapp.entities.responses.ProductResponse;
import com.mba.saasapp.mappers.ProductMapper;
import com.mba.saasapp.repositories.CategoryRepository;
import com.mba.saasapp.repositories.ProductRepository;
import com.mba.saasapp.services.ProductService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;



    @Service
    @RequiredArgsConstructor
    @Slf4j
    public class ProductServiceImpl implements ProductService {

        private final ProductRepository productRepository;
        private final CategoryRepository categoryRepository;
        private final ProductMapper productMapper;

        @Override
        public void create(final ProductRequest request) {

            // check if product already exists
            checkIfProductAlreadyExistsByReference(request.getReference());

            // check if category exists
            checkIfCategoryExistById(request.getCategoryId());

            final Product entity = this.productMapper.toEntity(request);
            this.productRepository.save(entity);
        }



            @Override
            public void update(final String id, final ProductRequest request) {

                // check if product exists
                final Optional<Product> productExists = this.productRepository.findById(id);


                if (productExists.isEmpty()) {
                    log.debug("Product does not exist");
                    throw new EntityNotFoundException("Product does not exist");
                }

                 checkIfProductAlreadyExistsByReference(request.getReference() );
                checkIfCategoryExistById(request.getCategoryId());

                final Product productToUpdate = this.productMapper.toEntity(request);
                productToUpdate.setId(  id);
                this.productRepository.save(productToUpdate);

            }
                @Override
                public PageResponse<ProductResponse> findAll ( final int page, final int size){

                    final PageRequest pageRequest = PageRequest.of(page, size);

                    final Page<Product> products = this.productRepository.findAll(pageRequest);

                    final Page<ProductResponse> productResponses =
                            products.map(this.productMapper::toResponse);

                    return PageResponse.of(productResponses);
                }

                @Override
                public ProductResponse findById ( final String id){

                    return this.productRepository.findById(id)
                            .map(this.productMapper::toResponse)
                            .orElseThrow(() -> new EntityNotFoundException("Product does not exist"));
                }

                @Override
                public void delete ( final String id){

                    final Product product = this.productRepository.findById(id)
                            .orElseThrow(() -> new EntityNotFoundException("Product does not exist"));

                    this.productRepository.delete(product);
                }


                private void checkIfProductAlreadyExistsByReference ( final String reference){

                    final Optional<Product> product =
                            this.productRepository.findByReferenceIgnoreCase(reference);

                    if (product.isPresent()) {
                        log.debug("Product already exists");
                        throw new RuntimeException("Product already exists");
                    }
                }



        private void checkIfCategoryExistById(String categoryId) {
            boolean exists = categoryRepository.existsById(categoryId);
            if (!exists) {
                throw new RuntimeException("Category not found with id: " + categoryId);
            }
        }





}
