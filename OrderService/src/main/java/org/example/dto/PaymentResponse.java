package org.example.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private Long id;
    private Long orderId;
    private String status;
    private String message;

    // Constructor for successful responses
    public PaymentResponse(Long id, Long orderId, String status) {
        this.id = id;
        this.orderId = orderId;
        this.status = status;
        this.message = "Payment retrieved successfully";
    }
}
