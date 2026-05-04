package com.mba.saasapp.mappers;

import com.mba.saasapp.entities.Category;
import com.mba.saasapp.entities.Product;
import com.mba.saasapp.entities.StockMvt;
import com.mba.saasapp.entities.requests.ProductRequest;
import com.mba.saasapp.entities.requests.StockMvtRequest;
import com.mba.saasapp.entities.responses.ProductResponse;
import com.mba.saasapp.entities.responses.StockMvtResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
@Component
public class StockMvtMapper {


    public StockMvt toEntity(final StockMvtRequest request) {
        return   StockMvt.builder()
                .typeMvt(request.getTypeMvt())
                .dateMvt(request.getDateMvt())
                .comment(request.getComment())
                .quantity(request.getQuantity())
                .product(Product.builder()
                                .id(request.getProductId())
                                .build()
                )

                .build();
    }

    public StockMvtResponse toResponse(final StockMvt   entity ) {
        return  StockMvtResponse.builder()
                .typeMvt( entity.getTypeMvt())
                .dateMvt( entity.getDateMvt())
                .comment( entity.getComment())
                .quantity( entity.getQuantity())
                // .availableQuantity() to be later implemented
                .build();
    }
}
