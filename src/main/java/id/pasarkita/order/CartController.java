package id.pasarkita.order;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
class CartController {

    private final CartItemRepository cart;

    CartController(CartItemRepository cart) {
        this.cart = cart;
    }

    @PostMapping("/api/cart/items")
    @ResponseStatus(HttpStatus.CREATED)
    void add(@RequestHeader("X-Customer-Id") Long customerId, @RequestBody AddItemRequest request) {
        cart.save(new CartItem(customerId, request.productId(), request.quantity()));
    }

    public record AddItemRequest(Long productId, int quantity) {
    }
}
