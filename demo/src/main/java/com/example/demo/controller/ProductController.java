package com.example.demo.controller;

import com.example.demo.model.Product;
import com.example.demo.model.ProductDto;
import com.example.demo.service.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/home")
    public String showHomePage (Model model){
        return "products/home";
    }

    @GetMapping({"","/"})
    public String showProductList (Model model){
        List<Product> products = productRepository.findAll();
        model.addAttribute("products",products);
        return "products/index";
    }

    @GetMapping("/create")
    public String showCreatePage (Model model){
        ProductDto productDto = new ProductDto();
        model.addAttribute("productDto",productDto);
        return "products/CreateProduct";
    }

    @PostMapping("/create")
    public String createProduct (@Valid @ModelAttribute ProductDto productDto, BindingResult result){

        if (productDto.getImageFile().isEmpty()){
            result.addError(new FieldError("productDto","imageFile","The image file is required"));
        }

        if (result.hasErrors()){
            return "products/CreateProduct";
        }

        MultipartFile image = productDto.getImageFile();
        Date createdAt = new Date();
        String storageFileName = createdAt.getTime()+ "_" + image.getOriginalFilename();

        try {
            String uploadDir = "public/images/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)){
                Files.createDirectories(uploadPath);
            }

            try (InputStream inputStream = image.getInputStream()){

                Files.copy(inputStream, Paths.get(uploadDir + storageFileName),
                        StandardCopyOption.REPLACE_EXISTING);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Product product = new Product();
        product.setName(productDto.getName());
        product.setBrand(productDto.getBrand());
        product.setCategory(productDto.getCategory());
        product.setPrice(productDto.getPrice());
        product.setDescription(productDto.getDescription());
        product.setCreatedAt(createdAt);
        product.setImage(storageFileName);

        productRepository.save(product);

        return "redirect:/products";
    }



    @GetMapping("/edit")
    public String showEditPage(Model model, @RequestParam int id) {
        try {
            Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
            ProductDto productDto = new ProductDto();
            productDto.setName(product.getName());
            productDto.setBrand(product.getBrand());
            productDto.setCategory(product.getCategory());
            productDto.setPrice(product.getPrice());
            productDto.setDescription(product.getDescription());

            model.addAttribute("product", product);
            model.addAttribute("productDto", productDto);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            return "redirect:/products";
        }
        return "products/EditProduct";
    }

    @PostMapping("/edit")
    public String updateProduct(Model model, @RequestParam int id, @Valid @ModelAttribute ProductDto productDto, BindingResult result) {

        try {
            Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
            model.addAttribute("product", product);

            if (result.hasErrors()) {
                return "products/EditProduct";
            }
            if (!productDto.getImageFile().isEmpty()) {
                String uploadDir = "public/images/";
                Path oldImagePath = Paths.get(uploadDir + product.getImage());

                try {
                    Files.delete(oldImagePath);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                MultipartFile image = productDto.getImageFile();
                Date createdAt = new Date();
                String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();

                try (InputStream inputStream = image.getInputStream()) {

                    Files.copy(inputStream, Paths.get(uploadDir + storageFileName),
                            StandardCopyOption.REPLACE_EXISTING);
                }
                product.setImage(storageFileName);

            }
            product.setName(productDto.getName());
            product.setBrand(productDto.getBrand());
            product.setCategory(productDto.getCategory());
            product.setPrice(productDto.getPrice());
            product.setDescription(productDto.getDescription());

            productRepository.save(product);

        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());

        }
        return "redirect:/products";

    }


    @GetMapping("/delete")
    public String deleteProduct(@RequestParam int id) {
        try {
            Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));

            Path imagePath = Paths.get("public/images/" + product.getImage());
            try {
                Files.delete(imagePath);
            }catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());

            }

            productRepository.delete(product);

        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());

        }
        return "redirect:/products";
    }


}
