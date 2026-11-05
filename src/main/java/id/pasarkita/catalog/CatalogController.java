package id.pasarkita.catalog;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/products")
class CatalogController {

    private final ProductRepository products;

    CatalogController(ProductRepository products) {
        this.products = products;
    }

    @GetMapping
    Page<Product> list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "20") int size) {
        return products.findAll(PageRequest.of(page, size));
    }

    @GetMapping("/search")
    Page<Product> search(@RequestParam String q,
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "20") int size) {
        return products.search(q, PageRequest.of(page, size));
    }

    @GetMapping("/{id}")
    Product detail(@PathVariable Long id) {
        return products.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
