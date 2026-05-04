package com.mba.saasapp.entities.requests;

import com.mba.saasapp.entities.TypeMvt;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class StockMvtRequest {

        private TypeMvt typeMvt;
        private Integer quantity;
        private LocalDate dateMvt;
        private String comment;
        private String productId;

    }



