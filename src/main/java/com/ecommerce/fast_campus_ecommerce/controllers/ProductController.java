package com.ecommerce.fast_campus_ecommerce.controllers;

import com.ecommerce.fast_campus_ecommerce.model.PaginatedProductResponse;
import com.ecommerce.fast_campus_ecommerce.model.ProductRequest;
import com.ecommerce.fast_campus_ecommerce.model.ProductResponse;
import com.ecommerce.fast_campus_ecommerce.model.UserInfo;
import com.ecommerce.fast_campus_ecommerce.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> findProductById(@PathVariable Long id) {
        ProductResponse productResponse = productService.findById(id);
        return ResponseEntity.ok(productResponse);
    }

    @GetMapping("")
    public ResponseEntity<PaginatedProductResponse> findAllProducts(
            @RequestParam(defaultValue = "10", name = "size") Integer size,
            @RequestParam(defaultValue = "0",name="page") Integer page,
            @RequestParam(defaultValue = "product_id,asc" , name = "sort") String sort,
            @RequestParam(name = "name",required = false) String name
    ) {
        Sort sorting;
        if(sort.contains(",")) {
            String[] split = sort.split(",");
            sorting = Sort.by(getSorDirection(split[1]),split[0]);
        }else{
            sorting = Sort.by(getSorDirection(sort));
        }
        PaginatedProductResponse productResponses;
        if(name!=null && !name.isEmpty()) {
            productResponses = productService.findAllByNameAndPageable(name,page,size,sorting);
        }else{
            productResponses  = productService.findAllByPage(page, size, sorting);
        }
        return ResponseEntity.ok(productResponses);
    }

    @PostMapping("/products")
    public ResponseEntity<ProductResponse> createProduct(@RequestBody @Valid ProductRequest productRequest) {
        UserInfo principal = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        productRequest.setUser(principal.getUser());
        ProductResponse productResponse = productService.create(productRequest);
        return ResponseEntity.ok(productResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @RequestBody @Valid ProductRequest productRequest) {
        ProductResponse productResponse = productService.update(id, productRequest);
        return ResponseEntity.ok(productResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private Sort.Direction getSorDirection(String sort){
        if(sort.equals("asc")) return Sort.Direction.ASC;
        else return Sort.Direction.DESC;
    }
}
