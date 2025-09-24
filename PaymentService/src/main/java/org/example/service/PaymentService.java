package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.PaymentRequest;
import org.example.dto.PaymentResponse;
import org.example.entity.Payment;
import org.example.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentResponse createPayment(PaymentRequest paymentRequest) {
        try {
            // Check if payment already exists for this order
            Optional<Payment> existingPayment = paymentRepository.findByOrderId(paymentRequest.getOrderId());
            if (existingPayment.isPresent()) {
                log.warn("Payment already exists for orderId: {}", paymentRequest.getOrderId());
                return new PaymentResponse("Payment already exists for this order");
            }

            // Create new payment
            Payment payment = new Payment();
            payment.setOrderId(paymentRequest.getOrderId());
            payment.setStatus(paymentRequest.getStatus());

            Payment savedPayment = paymentRepository.save(payment);
            log.info("Payment created successfully for orderId: {}", paymentRequest.getOrderId());

            return new PaymentResponse(
                    savedPayment.getId(),
                    savedPayment.getOrderId(),
                    savedPayment.getStatus()
            );

        } catch (Exception e) {
            log.error("Error creating payment for orderId: {}", paymentRequest.getOrderId(), e);
            return new PaymentResponse("Failed to create payment: " + e.getMessage());
        }
    }

    public PaymentResponse getPaymentByOrderId(Long orderId) {
        try {
            Optional<Payment> paymentOpt = paymentRepository.findByOrderId(orderId);

            if (paymentOpt.isPresent()) {
                Payment payment = paymentOpt.get();
                log.info("Payment found for orderId: {}", orderId);

                return new PaymentResponse(
                        payment.getId(),
                        payment.getOrderId(),
                        payment.getStatus()
                );
            } else {
                log.warn("No payment found for orderId: {}", orderId);
                return null; // Return null for 404 handling in controller
            }

        } catch (Exception e) {
            log.error("Error retrieving payment for orderId: {}", orderId, e);
            return new PaymentResponse("Error retrieving payment: " + e.getMessage());
        }
    }

    public PaymentResponse updatePaymentStatus(Long orderId, String newStatus) {
        try {
            Optional<Payment> paymentOpt = paymentRepository.findByOrderId(orderId);

            if (paymentOpt.isPresent()) {
                Payment payment = paymentOpt.get();
                payment.setStatus(newStatus);
                Payment updatedPayment = paymentRepository.save(payment);

                log.info("Payment status updated for orderId: {} to {}", orderId, newStatus);

                return new PaymentResponse(
                        updatedPayment.getId(),
                        updatedPayment.getOrderId(),
                        updatedPayment.getStatus()
                );
            } else {
                log.warn("No payment found to update for orderId: {}", orderId);
                return null;
            }

        } catch (Exception e) {
            log.error("Error updating payment for orderId: {}", orderId, e);
            return new PaymentResponse("Error updating payment: " + e.getMessage());
        }
    }
}
