package id.pasarkita.payment;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class PaymentService {

    private final WalletRepository wallets;
    private final PaymentGatewayClient gateway;

    PaymentService(WalletRepository wallets, PaymentGatewayClient gateway) {
        this.wallets = wallets;
        this.gateway = gateway;
    }

    @Transactional
    public void debit(Long customerId, BigDecimal amount) {
        gateway.authorize(customerId, amount);
        Wallet wallet = wallets.findById(customerId).orElseThrow(() -> new InsufficientBalanceException(customerId));
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException(customerId);
        }
        wallet.setBalance(wallet.getBalance().subtract(amount));
    }
}
