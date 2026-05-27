package se.jensen.daniela.userorderservice.repository;

import org.springframework.data.repository.CrudRepository;
import se.jensen.daniela.userorderservice.entity.Order;

import java.util.List;

public interface OrderRepository extends CrudRepository<Order, Long> {
    List<Order> findByUserId(Long userId);
}
