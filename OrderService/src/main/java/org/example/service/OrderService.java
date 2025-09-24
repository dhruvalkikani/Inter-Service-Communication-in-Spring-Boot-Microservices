package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.config.PaymentServiceFeignClient;
import org.example.dto.OrderRequest;
import org.example.dto.OrderResponse;
import org.example.dto.PaymentResponse;
import org.example.entity.Order;
import org.example.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;
    private final WebClient webClient;
    private final PaymentServiceFeignClient paymentServiceFeignClient;

    @Value("${payment.service.url}")
    private String paymentServiceUrl;

    public OrderResponse createOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setProductName(orderRequest.getProductName());
        order.setQuantity(orderRequest.getQuantity());

        Order savedOrder = orderRepository.save(order);

        return new OrderResponse(
                savedOrder.getId(),
                savedOrder.getProductName(),
                savedOrder.getQuantity(),
                null // No payment status during creation
        );
    }

    // Using Feign Client (Primary implementation)
    public OrderResponse getOrderWithPaymentStatus(Long orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            return null;
        }

        Order order = orderOpt.get();
        String paymentStatus = "NOT_FOUND";

        try {
            PaymentResponse paymentResponse = paymentServiceFeignClient.getPaymentByOrderId(orderId);
            if (paymentResponse != null) {
                paymentStatus = paymentResponse.getStatus();
            }
        } catch (Exception e) {
            log.error("Error fetching payment status via Feign Client: {}", e.getMessage());
        }

        return new OrderResponse(
                order.getId(),
                order.getProductName(),
                order.getQuantity(),
                paymentStatus
        );
    }

    // Using RestTemplate
    public OrderResponse getOrderWithPaymentStatusUsingRestTemplate(Long orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            return null;
        }

        Order order = orderOpt.get();
        String paymentStatus = "NOT_FOUND";

        try {
            String url = paymentServiceUrl + "/payments/" + orderId;
            PaymentResponse paymentResponse = restTemplate.getForObject(url, PaymentResponse.class);
            if (paymentResponse != null) {
                paymentStatus = paymentResponse.getStatus();
            }
        } catch (Exception e) {
            log.error("Error fetching payment status via RestTemplate: {}", e.getMessage());
        }

        return new OrderResponse(
                order.getId(),
                order.getProductName(),
                order.getQuantity(),
                paymentStatus
        );
    }

    // Using WebClient
    public OrderResponse getOrderWithPaymentStatusUsingWebClient(Long orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            return null;
        }

        Order order = orderOpt.get();
        String paymentStatus = "NOT_FOUND";

        try {
            PaymentResponse paymentResponse = webClient
                    .get()
                    .uri("/payments/{orderId}", orderId)
                    .retrieve()
                    .bodyToMono(PaymentResponse.class)
                    .block();

            if (paymentResponse != null) {
                paymentStatus = paymentResponse.getStatus();
            }
        } catch (Exception e) {
            log.error("Error fetching payment status via WebClient: {}", e.getMessage());
        }

        return new OrderResponse(
                order.getId(),
                order.getProductName(),
                order.getQuantity(),
                paymentStatus
        );
    }
}
