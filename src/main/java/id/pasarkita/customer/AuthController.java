package id.pasarkita.customer;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class AuthController {

    private final CustomerRepository customers;
    private final PasswordEncoder passwordEncoder;

    AuthController(CustomerRepository customers, PasswordEncoder passwordEncoder) {
        this.customers = customers;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/api/auth/login")
    ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return customers.findByEmailIgnoreCase(request.email())
                .filter(c -> passwordEncoder.matches(request.password(), c.getPasswordHash()))
                .map(c -> ResponseEntity.ok(new LoginResponse(c.getId(), c.getName())))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    public record LoginRequest(String email, String password) {
    }

    public record LoginResponse(Long customerId, String name) {
    }
}
