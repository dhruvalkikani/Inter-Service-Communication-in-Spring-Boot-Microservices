package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private String productName;
    private Integer quantity;
    private String paymentStatus; // This will hold the payment status from Payment Service
    private String message;

    // Constructor for order with payment status
    public OrderResponse(Long id, String productName, Integer quantity, String paymentStatus) {
        this.id = id;
        this.productName = productName;
        this.quantity = quantity;
        this.paymentStatus = paymentStatus;
        this.message = "Order retrieved successfully";
    }

    // Constructor for order creation (without payment status)
    public OrderResponse(Long id, String productName, Integer quantity) {
        this.id = id;
        this.productName = productName;
        this.quantity = quantity;
        this.paymentStatus = null;
        this.message = "Order created successfully";
    }
}
