package id.pasarkita.order;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
class CheckoutController {

    private final CheckoutService checkout;

    CheckoutController(CheckoutService checkout) {
        this.checkout = checkout;
    }

    @PostMapping("/api/checkout")
    CheckoutService.CheckoutResult checkout(@RequestHeader("X-Customer-Id") Long customerId) {
        return checkout.checkout(customerId);
    }
}
