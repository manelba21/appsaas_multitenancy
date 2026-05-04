package com.mba.saasapp.services.imp;

import com.mba.saasapp.common.PageResponse;
import com.mba.saasapp.entities.Product;
import com.mba.saasapp.entities.StockMvt;
import com.mba.saasapp.entities.requests.StockMvtRequest;
import com.mba.saasapp.entities.responses.StockMvtResponse;
import com.mba.saasapp.mappers.StockMvtMapper;
import com.mba.saasapp.repositories.ProductRepository;
import com.mba.saasapp.repositories.StockMvtRepository;
import com.mba.saasapp.services.MvtStockService;
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
public class StockMvtImpl implements MvtStockService {


     private final StockMvtRepository  stockMvtRepository;
     private final StockMvtMapper  stockMvtMapper;
     private final ProductRepository productRepository ;
    @Override
    public void create(final StockMvtRequest request) {

        // 1. Validate that the product exists
        checkIfProductExistsById(request.getProductId());

        // 2. Map request → entity
        final StockMvt entity = this.stockMvtMapper.toEntity(request);

        // 3. Persist entity
        this.stockMvtRepository.save(entity);
    }

    @Override
    public void update(final String id, final StockMvtRequest request) {
        final   Optional<StockMvt> stockMvt = this.stockMvtRepository.findById(id);
        if(stockMvt.isEmpty()){
            log.debug("StockMvt doesn't exist") ;
             throw new EntityNotFoundException("StockMvt does not exist");
        }

        checkIfProductExistsById(request.getProductId());
        // update fields manually OR via mapper
        final   StockMvt    stockMvtToUpdate =  this.stockMvtMapper.toEntity(request);
         stockMvtToUpdate.setId(id);
        this.stockMvtRepository.save( stockMvtToUpdate);
    }

    @Override
    public PageResponse<StockMvtResponse> findAll(final int page, final int size) {
        final PageRequest pageRequest = PageRequest.of(page, size);
        final Page<StockMvt> stockMvts = this.stockMvtRepository.findAll(pageRequest);
        final Page<StockMvtResponse> stockMvtResponses =
                stockMvts.map(this.stockMvtMapper::toResponse);
        return PageResponse.of(stockMvtResponses);
    }

    @Override
    public StockMvtResponse findById(final String id) {
        return this.stockMvtRepository.findById(id)
                .map(this.stockMvtMapper::toResponse)
                .orElseThrow(() ->
                        new EntityNotFoundException("StockMvt does not exist"));
    }

    @Override
    public void delete(final String id) {
        final StockMvt stockMvt = this.stockMvtRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("StockMvt does not exist"));

        this.stockMvtRepository.delete(stockMvt);
    }


    private void checkIfProductExistsById(final String productId) {
        final Optional<Product> product = this.productRepository.findById(productId);
        if (product.isEmpty()) {
            log.debug("Product does not exist");
            throw new EntityNotFoundException("Product does not exist");
        }
    }

}
