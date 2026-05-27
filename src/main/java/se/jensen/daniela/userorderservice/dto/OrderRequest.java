package se.jensen.daniela.userorderservice.dto;

import lombok.Data;

@Data

public class OrderRequest {
    private Long productId;
    private String productTitle;
    private double price;
    private int quantity;
}
