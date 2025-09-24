package org.example.config;

import org.example.dto.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "payment-service",
        url = "${payment.service.url}"
)
public interface PaymentServiceFeignClient {

    @GetMapping("/payments/{orderId}")
    PaymentResponse getPaymentByOrderId(@PathVariable("orderId") Long orderId);
}
