package se.jensen.daniela.userorderservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import se.jensen.daniela.userorderservice.dto.OrderRequest;
import se.jensen.daniela.userorderservice.entity.Order;
import se.jensen.daniela.userorderservice.entity.User;
import se.jensen.daniela.userorderservice.repository.OrderRepository;
import se.jensen.daniela.userorderservice.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor

public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Användare hittades inte"));
    }

    public Order createOrder(OrderRequest req) {
        User user = getCurrentUser();
        Order order = new Order();
        order.setUser(user);
        order.setProductId(req.getProductId());
        order.setProductTitle(req.getProductTitle());
        order.setPrice(req.getPrice());
        order.setQuantity(req.getQuantity());
        return orderRepository.save(order);
    }

    public List<Order> getMyOrders() {
        return orderRepository.findByUserId(getCurrentUser().getId());
    }
}
