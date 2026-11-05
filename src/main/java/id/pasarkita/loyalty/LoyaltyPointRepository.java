package id.pasarkita.loyalty;

import org.springframework.data.jpa.repository.JpaRepository;

interface LoyaltyPointRepository extends JpaRepository<LoyaltyPoint, Long> {
}
