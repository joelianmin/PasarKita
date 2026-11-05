package id.pasarkita.invoice;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "invoice")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;

    private Long sellerId;

    private BigDecimal amount;

    private Instant issuedAt = Instant.now();

    protected Invoice() {
    }

    Invoice(Long orderId, Long sellerId, BigDecimal amount) {
        this.orderId = orderId;
        this.sellerId = sellerId;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Instant getIssuedAt() {
        return issuedAt;
    }
}
