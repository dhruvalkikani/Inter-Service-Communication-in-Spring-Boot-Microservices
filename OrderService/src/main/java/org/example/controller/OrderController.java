package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.OrderRequest;
import org.example.dto.OrderResponse;
import org.example.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest orderRequest) {
        OrderResponse response = orderService.createOrder(orderRequest);
        if (response.getId() != null) {
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Required endpoint - Get order with payment status
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id) {
        OrderResponse response = orderService.getOrderWithPaymentStatus(id);
        if (response != null) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // Demonstrate RestTemplate
    @GetMapping("/{id}/resttemplate")
    public ResponseEntity<OrderResponse> getOrderWithRestTemplate(@PathVariable Long id) {
        OrderResponse response = orderService.getOrderWithPaymentStatusUsingRestTemplate(id);
        return response != null ? ResponseEntity.ok(response) : ResponseEntity.notFound().build();
    }

    // Demonstrate WebClient
    @GetMapping("/{id}/webclient")
    public ResponseEntity<OrderResponse> getOrderWithWebClient(@PathVariable Long id) {
        OrderResponse response = orderService.getOrderWithPaymentStatusUsingWebClient(id);
        return response != null ? ResponseEntity.ok(response) : ResponseEntity.notFound().build();
    }
}
