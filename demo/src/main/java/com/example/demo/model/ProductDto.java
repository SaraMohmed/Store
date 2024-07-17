package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ProductDto {

    private int id;
    @NotEmpty(message = "The name is required")
    private String name;
    @NotEmpty(message = "The brand is required")
    private String brand;
    @NotEmpty(message = "The category is required")
    private String category;
    @Min(0)
    private double price;
    @Size(min = 10, message = "The description should be at least 10 characters")
    @Size(max = 2000, message = "The description cannot exceed 2000 characters")
    private String description;

    private MultipartFile imageFile;
}
