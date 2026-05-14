package com.mba.saasapp.entities.requests;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryRequest {


    @NotBlank(message = "Category name should not be empty")
    @Size(min = 3, max = 255, message = "Category name should be between 3 and 255 characters")
    private String name;
    private String description;

}
