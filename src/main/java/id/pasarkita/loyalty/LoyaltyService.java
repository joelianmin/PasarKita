package id.pasarkita.loyalty;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class LoyaltyService {

    private static final BigDecimal RUPIAH_PER_POINT = BigDecimal.valueOf(10_000);

    private final LoyaltyPointRepository points;

    LoyaltyService(LoyaltyPointRepository points) {
        this.points = points;
    }

    @Transactional
    public void award(Long customerId, Long orderId, BigDecimal orderTotal) {
        int earned = orderTotal.divide(RUPIAH_PER_POINT, 0, RoundingMode.DOWN).intValue();
        points.save(new LoyaltyPoint(customerId, orderId, earned));
    }
}
