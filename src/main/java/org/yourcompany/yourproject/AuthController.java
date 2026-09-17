package org.yourcompany.yourproject;

import jakarta.servlet.http.HttpSession;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserAccountRepository repository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthController(UserAccountRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request, HttpSession session) {
        String email = request.email() == null ? "" : request.email().trim().toLowerCase();
        if (request.name() == null || request.name().isBlank() || email.isBlank() || request.password() == null || request.password().length() < 6) {
            return ResponseEntity.badRequest().body(Map.of("message", "Enter your name, a valid email, and a password with 6 or more characters."));
        }
        if (repository.existsByEmailIgnoreCase(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "An account with that email already exists."));
        }
        UserAccount user = repository.save(new UserAccount(request.name().trim(), email, passwordEncoder.encode(request.password())));
        session.setAttribute("userId", user.getId());
        session.setAttribute("userName", user.getName());
        return ResponseEntity.ok(Map.of("name", user.getName()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpSession session) {
        String email = request.email() == null ? "" : request.email().trim().toLowerCase();
        return repository.findByEmailIgnoreCase(email)
                .filter(user -> request.password() != null && passwordEncoder.matches(request.password(), user.getPasswordHash()))
                .map(user -> {
                    session.setAttribute("userId", user.getId());
                    session.setAttribute("userName", user.getName());
                    return ResponseEntity.ok(Map.of("name", user.getName()));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Email or password is incorrect.")));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        String email = request.email() == null ? "" : request.email().trim().toLowerCase();
        if (request.newPassword() == null || request.newPassword().length() < 6) {
            return ResponseEntity.badRequest().body(Map.of("message", "Your new password must have 6 or more characters."));
        }
        return repository.findByEmailIgnoreCase(email).map(user -> {
            user.changePassword(passwordEncoder.encode(request.newPassword()));
            repository.save(user);
            return ResponseEntity.ok(Map.of("message", "Password updated. You can sign in now."));
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "No account was found with that email.")));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(HttpSession session) {
        Object name = session.getAttribute("userName");
        return name == null ? ResponseEntity.status(HttpStatus.UNAUTHORIZED).build() : ResponseEntity.ok(Map.of("name", name));
    }

    public record RegisterRequest(String name, String email, String password) {}
    public record LoginRequest(String email, String password) {}
    public record ForgotPasswordRequest(String email, String newPassword) {}
}