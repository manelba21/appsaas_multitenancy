package com.mba.saasapp.Controllers;

import com.mba.saasapp.common.PageResponse;
import com.mba.saasapp.entities.requests.StockMvtRequest;
import com.mba.saasapp.entities.responses.StockMvtResponse;
import com.mba.saasapp.services.MvtStockService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/stocks")
@RequiredArgsConstructor
@Tag(name = "StockMvt", description = "StockMvt API")
public class StockMvtController {


private final    MvtStockService   service ;

    static class StockMvtControllerService {

    }

    @PostMapping
    public ResponseEntity<Void> createStockMvt(
            @RequestBody
            @Valid
            final StockMvtRequest request
    ) {
        this.service.create(request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{stock-mvt-id}")
    public ResponseEntity<Void> updateStockMvt(
            @RequestBody
            @Valid
            final StockMvtRequest request,
            @PathVariable("stock-mvt-id")
            final String id
    ) {
        this.service.update(id, request);
        return ResponseEntity.accepted().build();
    }


        @GetMapping("/{stock-mvt-id}")
        public ResponseEntity<StockMvtResponse> findStockMvtById(
                @PathVariable("stock-mvt-id")
                @NotNull(message = "Stock Mvt ID cannot be null")
                final String id
        ) {
            return ResponseEntity.ok(this.service.findById(id));
        }

        @GetMapping
        public ResponseEntity<PageResponse<StockMvtResponse>> findAllStockMvts(
                @RequestParam(name = "page", defaultValue = "0")
                final int page,
                @RequestParam(name = "size", defaultValue = "10")
                final int size
        ) {
            return ResponseEntity.ok(this.service.findAll(page, size));
        }



    @DeleteMapping("/{stock-mvt-id}")
    public ResponseEntity<Void> deleteStockMvt(
            @PathVariable("stock-mvt-id")
            @NotNull(message = "Stock Mvt ID cannot be null")
            final String id
    ) {
        this.service.delete(id);
        return ResponseEntity.noContent().build();
    }
    }

