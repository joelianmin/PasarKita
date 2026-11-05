package id.pasarkita.payment;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.PAYMENT_REQUIRED)
public class InsufficientBalanceException extends RuntimeException {

    public InsufficientBalanceException(Long customerId) {
        super("Saldo customer " + customerId + " tidak cukup");
    }
}
