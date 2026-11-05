package id.pasarkita.invoice;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
class InvoiceController {

    private final InvoiceRepository invoices;

    InvoiceController(InvoiceRepository invoices) {
        this.invoices = invoices;
    }

    @GetMapping("/api/sellers/{id}/invoices")
    Page<Invoice> list(@PathVariable Long id,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "20") int size) {
        return invoices.findBySellerIdOrderByIssuedAtDesc(id, PageRequest.of(page, size));
    }
}
