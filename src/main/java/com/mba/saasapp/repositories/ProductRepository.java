package com.mba.saasapp.repositories;

import com.mba.saasapp.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public  interface ProductRepository  extends JpaRepository<Product,String> {


    Optional<Product> findByReferenceIgnoreCase(String reference);
}
