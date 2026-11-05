package id.pasarkita.pricing;

import java.math.BigDecimal;

public record PriceLine(Long productId, BigDecimal unitPrice, int quantity) {
}
