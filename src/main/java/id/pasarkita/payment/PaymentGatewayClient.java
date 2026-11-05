package id.pasarkita.payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Map;

@Component
class PaymentGatewayClient {

    private final RestClient rest;

    PaymentGatewayClient(RestClient.Builder builder, @Value("${pasarkita.payment-gateway.url}") String baseUrl) {
        this.rest = builder.baseUrl(baseUrl).build();
    }

    void authorize(Long customerId, BigDecimal amount) {
        rest.post()
                .uri("/payments/authorize")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("customerId", customerId, "amount", amount))
                .retrieve()
                .toBodilessEntity();
    }
}
