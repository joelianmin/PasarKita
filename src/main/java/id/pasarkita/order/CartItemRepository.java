package id.pasarkita.order;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByCustomerId(Long customerId);

    void deleteByCustomerId(Long customerId);
}
