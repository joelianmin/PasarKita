package id.pasarkita.shipping;

import org.springframework.data.jpa.repository.JpaRepository;

interface ShipmentRepository extends JpaRepository<Shipment, Long> {
}
