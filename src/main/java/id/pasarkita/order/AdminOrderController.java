package id.pasarkita.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
class AdminOrderController {

    private final OrderRepository orders;

    AdminOrderController(OrderRepository orders) {
        this.orders = orders;
    }

    @GetMapping("/api/admin/orders")
    Page<Order> list(@RequestParam(required = false) String status,
                     @RequestParam(defaultValue = "0") int page,
                     @RequestParam(defaultValue = "20") int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return status == null ? orders.findAll(pageable) : orders.findByStatus(status, pageable);
    }
}
