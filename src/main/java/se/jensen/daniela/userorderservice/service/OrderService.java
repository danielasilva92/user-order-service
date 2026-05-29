package se.jensen.daniela.userorderservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import se.jensen.daniela.userorderservice.dto.OrderRequest;
import se.jensen.daniela.userorderservice.entity.Order;
import se.jensen.daniela.userorderservice.entity.User;
import se.jensen.daniela.userorderservice.product.Product;
import se.jensen.daniela.userorderservice.product.ProductClient;
import se.jensen.daniela.userorderservice.repository.OrderRepository;
import se.jensen.daniela.userorderservice.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor

public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductClient productClient;

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Användare hittades inte"));
    }

    public Order createOrder(OrderRequest req) {
        User user = getCurrentUser();
        List<Product> products = productClient.getProducts();
        Product product = findProduct(products, req.getProductId());

        Order order = new Order();
        order.setUser(user);
        order.setProductId(req.getProductId());
        order.setProductTitle(product.title());
        order.setPrice(product.price());
        order.setQuantity(req.getQuantity());
        return orderRepository.save(order);
    }

    public List<Order> getMyOrders() {

        return orderRepository.findByUserId(getCurrentUser().getId());
    }

    private Product findProduct(List<Product> products, Long productId) {
        return products.stream()
                .filter(product -> product.id().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Produkt hittades inte i product service"));
    }
}
