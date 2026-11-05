package id.pasarkita.loyalty;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "loyalty_point")
public class LoyaltyPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long customerId;

    private Long orderId;

    private int points;

    private Instant createdAt = Instant.now();

    protected LoyaltyPoint() {
    }

    LoyaltyPoint(Long customerId, Long orderId, int points) {
        this.customerId = customerId;
        this.orderId = orderId;
        this.points = points;
    }

    public Long getId() {
        return id;
    }

    public int getPoints() {
        return points;
    }
}
