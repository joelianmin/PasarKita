package id.pasarkita.payment;

import org.springframework.data.jpa.repository.JpaRepository;

interface WalletRepository extends JpaRepository<Wallet, Long> {
}
