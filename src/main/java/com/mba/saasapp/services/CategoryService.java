package com.mba.saasapp.services;

import com.mba.saasapp.entities.requests.CategoryRequest;
import com.mba.saasapp.entities.responses.CategoryResponse;
import jakarta.transaction.Transactional;

import java.util.List;

public    interface  CategoryService extends BasicService<CategoryRequest, CategoryResponse> {



}
