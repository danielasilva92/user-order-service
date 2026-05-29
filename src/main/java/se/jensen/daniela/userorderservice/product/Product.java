package se.jensen.daniela.userorderservice.product;

public record Product(
        Long id,
        String title,
        String description,
        Double price,
        String category,
        String image
) {
}