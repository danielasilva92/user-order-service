package se.jensen.daniela.userorderservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.jensen.daniela.userorderservice.dto.OrderRequest;
import se.jensen.daniela.userorderservice.entity.Order;
import se.jensen.daniela.userorderservice.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequest req) {
        return ResponseEntity.ok(orderService.createOrder(req));
    }

    @GetMapping
    public ResponseEntity<List<Order>> getMyOrders() {
        return ResponseEntity.ok(orderService.getMyOrders());
    }
}
