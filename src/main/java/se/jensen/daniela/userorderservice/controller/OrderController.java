package se.jensen.daniela.userorderservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.jensen.daniela.userorderservice.entity.CustomerOrder;
import se.jensen.daniela.userorderservice.order.CreateOrderRequest;
import se.jensen.daniela.userorderservice.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<CustomerOrder> createOrder(@RequestBody @Valid CreateOrderRequest req) {
        return ResponseEntity.ok(orderService.createOrder(req));
    }

    @GetMapping
    public ResponseEntity<List<CustomerOrder>> getMyOrders() {

        return ResponseEntity.ok(orderService.getMyOrders());
    }
}
