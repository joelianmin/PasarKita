package id.pasarkita.order;

import id.pasarkita.catalog.Product;
import id.pasarkita.catalog.ProductRepository;
import id.pasarkita.customer.Customer;
import id.pasarkita.customer.CustomerRepository;
import id.pasarkita.inventory.InventoryService;
import id.pasarkita.invoice.InvoiceService;
import id.pasarkita.loyalty.LoyaltyService;
import id.pasarkita.notification.NotificationService;
import id.pasarkita.payment.PaymentService;
import id.pasarkita.pricing.PriceLine;
import id.pasarkita.pricing.PriceQuote;
import id.pasarkita.pricing.PricingService;
import id.pasarkita.shipping.ShippingService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CheckoutService {

    private final CartItemRepository cart;
    private final OrderRepository orders;
    private final ProductRepository products;
    private final CustomerRepository customers;
    private final PricingService pricing;
    private final InventoryService inventory;
    private final PaymentService payments;
    private final InvoiceService invoices;
    private final LoyaltyService loyalty;
    private final NotificationService notifications;
    private final ShippingService shipping;

    CheckoutService(CartItemRepository cart, OrderRepository orders, ProductRepository products,
                    CustomerRepository customers, PricingService pricing, InventoryService inventory,
                    PaymentService payments, InvoiceService invoices, LoyaltyService loyalty,
                    NotificationService notifications, ShippingService shipping) {
        this.cart = cart;
        this.orders = orders;
        this.products = products;
        this.customers = customers;
        this.pricing = pricing;
        this.inventory = inventory;
        this.payments = payments;
        this.invoices = invoices;
        this.loyalty = loyalty;
        this.notifications = notifications;
        this.shipping = shipping;
    }

    @Transactional
    public CheckoutResult checkout(Long customerId) {
        List<CartItem> items = cart.findByCustomerId(customerId);
        if (items.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Keranjang kosong");
        }
        Customer customer = customers.findById(customerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer tidak ditemukan"));

        Map<Long, Product> productById = products.findAllById(items.stream().map(CartItem::getProductId).toList())
                .stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));
        for (CartItem item : items) {
            if (!productById.containsKey(item.getProductId())) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Produk " + item.getProductId() + " tidak ditemukan");
            }
        }

        List<PriceLine> priceLines = items.stream()
                .map(i -> new PriceLine(i.getProductId(), productById.get(i.getProductId()).getPrice(), i.getQuantity()))
                .toList();
        PriceQuote quote = pricing.quote(priceLines);

        for (CartItem item : items) {
            inventory.decrease(item.getProductId(), item.getQuantity());
        }

        payments.debit(customerId, quote.total());

        Order order = new Order(customer, quote.total(), quote.tax());
        Map<Long, BigDecimal> amountPerSeller = new HashMap<>();
        for (CartItem item : items) {
            Product product = productById.get(item.getProductId());
            order.addLine(new OrderLine(product, item.getQuantity(), product.getPrice()));
            amountPerSeller.merge(product.getSeller().getId(),
                    product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())), BigDecimal::add);
        }
        orders.save(order);

        invoices.createForOrder(order.getId(), amountPerSeller);
        loyalty.award(customerId, order.getId(), quote.total());
        notifications.sendOrderConfirmation(customerId, order.getId());
        shipping.request(order.getId());

        cart.deleteByCustomerId(customerId);
        return new CheckoutResult(order.getId(), quote.total(), order.getStatus());
    }

    public record CheckoutResult(Long orderId, BigDecimal total, String status) {
    }
}
