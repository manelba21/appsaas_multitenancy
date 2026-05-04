package com.mba.saasapp.repositories;

import com.mba.saasapp.entities.StockMvt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockMvtRepository   extends JpaRepository<StockMvt,String> {
}
