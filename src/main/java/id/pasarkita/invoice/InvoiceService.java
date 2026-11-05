package id.pasarkita.invoice;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class InvoiceService {

    private final InvoiceRepository invoices;

    InvoiceService(InvoiceRepository invoices) {
        this.invoices = invoices;
    }

    @Transactional
    public void createForOrder(Long orderId, Map<Long, BigDecimal> amountPerSeller) {
        amountPerSeller.forEach((sellerId, amount) -> invoices.save(new Invoice(orderId, sellerId, amount)));
    }
}
