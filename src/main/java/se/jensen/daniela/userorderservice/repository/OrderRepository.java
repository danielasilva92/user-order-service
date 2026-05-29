package se.jensen.daniela.userorderservice.repository;

import org.springframework.data.repository.CrudRepository;
import se.jensen.daniela.userorderservice.entity.CustomerOrder;

import java.util.List;

public interface OrderRepository extends CrudRepository<CustomerOrder, Long> {
    List<CustomerOrder> findByUserId(Long userId);
}
