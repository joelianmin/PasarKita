package id.pasarkita.pricing;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

@Component
class TaxClient {

    private final RestClient rest;

    TaxClient(RestClient.Builder builder, @Value("${pasarkita.tax-api.url}") String baseUrl) {
        this.rest = builder.baseUrl(baseUrl).build();
    }

    BigDecimal rateFor(Long productId) {
        TaxRate rate = rest.get()
                .uri("/tax/rate?productId={id}", productId)
                .retrieve()
                .body(TaxRate.class);
        return rate == null || rate.rate() == null ? BigDecimal.ZERO : rate.rate();
    }

    record TaxRate(BigDecimal rate) {
    }
}
