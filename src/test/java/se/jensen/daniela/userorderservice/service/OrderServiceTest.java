package se.jensen.daniela.userorderservice.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProductClient productClient;

    @InjectMocks
    private OrderService orderService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("daniela");

        // createOrder()/getMyOrders() läser användarnamnet ur SecurityContext via getCurrentUser()
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("daniela", null));
    }

    @AfterEach
    void tearDown() {
        // Rensa contexten så att den inte läcker mellan tester
        SecurityContextHolder.clearContext();
    }

    @Test
    void createOrder_bygger_order_med_korrekta_items() {
        when(userRepository.findByUsername("daniela")).thenReturn(Optional.of(user));

        Product laptop = new Product(1L, "Laptop", "En bra laptop", 999.0, "tech", "img.png");
        Product mus = new Product(2L, "Mus", "Trådlös mus", 49.0, "tech", "img.png");
        when(productClient.getProducts()).thenReturn(List.of(laptop, mus));

        CreateOrderRequest request = new CreateOrderRequest(List.of(
                new CreateOrderItemRequest(1L, 2),
                new CreateOrderItemRequest(2L, 1)
        ));

        // save() returnerar samma order som skickas in, så man kan asserta på resultatet
        when(orderRepository.save(any(CustomerOrder.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CustomerOrder result = orderService.createOrder(request);

        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(2, result.getItems().size());

        OrderItem first = result.getItems().get(0);
        assertEquals(1L, first.getProductId());
        assertEquals("Laptop", first.getProductTitle());
        assertEquals(999.0, first.getPrice(), 0.001);
        assertEquals(2, first.getQuantity());

        OrderItem second = result.getItems().get(1);
        assertEquals(2L, second.getProductId());
        assertEquals(1, second.getQuantity());

        verify(orderRepository).save(any(CustomerOrder.class));
    }

    @Test
    void createOrder_kastar_404_om_produkt_saknas() {
        when(userRepository.findByUsername("daniela")).thenReturn(Optional.of(user));

        // Product service känner bara till produkt 1 – beställningen frågar efter 999
        when(productClient.getProducts()).thenReturn(List.of(
                new Product(1L, "Laptop", "En bra laptop", 999.0, "tech", "img.png")
        ));

        CreateOrderRequest request = new CreateOrderRequest(List.of(
                new CreateOrderItemRequest(999L, 1)
        ));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> orderService.createOrder(request));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void getMyOrders_returnerar_inloggade_anvandarens_ordrar() {
        when(userRepository.findByUsername("daniela")).thenReturn(Optional.of(user));

        CustomerOrder order1 = new CustomerOrder();
        CustomerOrder order2 = new CustomerOrder();
        when(orderRepository.findByUserId(1L)).thenReturn(List.of(order1, order2));

        List<CustomerOrder> result = orderService.getMyOrders();

        assertEquals(2, result.size());
        verify(orderRepository).findByUserId(1L);
    }
}
