package se.jensen.daniela.userorderservice.product;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductClient productClient;

    @GetMapping
    public List<Product> getAllProducts() {
        return productClient.getProducts();
    }
}
