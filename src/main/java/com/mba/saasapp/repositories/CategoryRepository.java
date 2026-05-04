package com.mba.saasapp.repositories;

import com.mba.saasapp.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Locale;
import java.util.Optional;

public interface CategoryRepository   extends JpaRepository<Category,  String> {


    @Override
    Optional<Category> findById(String s);
    Optional<Category>findByNameIgnoreCase(String name);



}
