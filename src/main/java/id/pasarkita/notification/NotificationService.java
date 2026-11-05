package id.pasarkita.notification;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class NotificationService {

    private final RestClient rest;

    NotificationService(RestClient.Builder builder, @Value("${pasarkita.notification.url}") String baseUrl) {
        this.rest = builder.baseUrl(baseUrl).build();
    }

    public void sendOrderConfirmation(Long customerId, Long orderId) {
        rest.post()
                .uri("/notifications/send")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("customerId", customerId, "orderId", orderId, "template", "order-confirmation"))
                .retrieve()
                .toBodilessEntity();
    }
}
