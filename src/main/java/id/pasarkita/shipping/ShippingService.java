package id.pasarkita.shipping;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShippingService {

    private final ShipmentRepository shipments;

    ShippingService(ShipmentRepository shipments) {
        this.shipments = shipments;
    }

    @Transactional
    public void request(Long orderId) {
        shipments.save(new Shipment(orderId, "REQUESTED"));
    }
}
