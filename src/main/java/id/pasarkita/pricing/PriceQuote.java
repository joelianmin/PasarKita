package id.pasarkita.pricing;

import java.math.BigDecimal;

public record PriceQuote(BigDecimal subtotal, BigDecimal tax, BigDecimal total) {
}
