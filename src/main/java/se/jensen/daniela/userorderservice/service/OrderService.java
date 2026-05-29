package se.jensen.daniela.userorderservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import se.jensen.daniela.userorderservice.entity.CustomerOrder;
import se.jensen.daniela.userorderservice.entity.OrderItem;
import se.jensen.daniela.userorderservice.entity.User;
import se.jensen.daniela.userorderservice.order.CreateOrderItemRequest;
import se.jensen.daniela.userorderservice.order.CreateOrderRequest;
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

    public CustomerOrder createOrder(CreateOrderRequest request) {
        User user = getCurrentUser();

        List<Product> products = productClient.getProducts();

        CustomerOrder order = new CustomerOrder();
        order.setUser(user);

        for (CreateOrderItemRequest itemRequest : request.items()) {
            Product product = findProduct(products, itemRequest.productId());
            OrderItem item = new OrderItem(
                    product.id(),
                    product.title(),
                    product.price(),
                    itemRequest.quantity()
            );
            order.addItem(item);
        }


        return orderRepository.save(order);
    }

    public List<CustomerOrder> getMyOrders() {

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
