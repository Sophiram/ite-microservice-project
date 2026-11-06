package kh.edu.istad.platform.product.restapi.controller;

import jakarta.validation.Valid;
import kh.edu.istad.platform.product.domain.dto.CreateProductCommand;
import kh.edu.istad.platform.product.domain.dto.UpdateProductCommand;
import kh.edu.istad.platform.product.domain.usecase.CreateProductUseCase;
import kh.edu.istad.platform.product.domain.usecase.QueryProductUseCase;
import kh.edu.istad.platform.product.domain.usecase.UpdateProductUseCase;
import kh.edu.istad.platform.product.restapi.dto.ProductRequest;
import kh.edu.istad.platform.product.restapi.dto.ProductResponse;
import kh.edu.istad.platform.product.restapi.dto.ProductUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {
    private final CreateProductUseCase createProduct;
    private final UpdateProductUseCase updateProduct;
    private final QueryProductUseCase queryProducts;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse create(@Valid @RequestBody ProductRequest request) {
        var command = new CreateProductCommand(request.businessId(), request.sku(), request.name(),
                request.description(), request.unitPrice(), request.currency(), request.availableQuantity());
        return ProductResponse.from(createProduct.execute(command));
    }

    @PutMapping("/{id}")
    public ProductResponse update(@PathVariable UUID id, @Valid @RequestBody ProductUpdateRequest request) {
        var command = new UpdateProductCommand(id, request.sku(), request.name(), request.description(), request.unitPrice(),
                request.currency(), request.availableQuantity(), request.status());
        return ProductResponse.from(updateProduct.execute(command));
    }

    @GetMapping("/{id}")
    public ProductResponse getById(@PathVariable UUID id) {
        return ProductResponse.from(queryProducts.findById(id));
    }

    @GetMapping
    public List<ProductResponse> getAll() {
        return queryProducts.findAll().stream().map(ProductResponse::from).toList();
    }
}
