package id.pasarkita.pricing;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class PricingService {

    private final TaxClient taxClient;

    PricingService(TaxClient taxClient) {
        this.taxClient = taxClient;
    }

    public PriceQuote quote(List<PriceLine> lines) {
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal tax = BigDecimal.ZERO;
        for (PriceLine line : lines) {
            BigDecimal amount = line.unitPrice().multiply(BigDecimal.valueOf(line.quantity()));
            subtotal = subtotal.add(amount);
            tax = tax.add(amount.multiply(taxClient.rateFor(line.productId())));
        }
        tax = tax.setScale(2, RoundingMode.HALF_UP);
        return new PriceQuote(subtotal, tax, subtotal.add(tax));
    }
}
