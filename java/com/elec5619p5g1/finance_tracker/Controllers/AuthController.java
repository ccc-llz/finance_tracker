package com.elec5619p5g1.finance_tracker.Controllers;

import com.elec5619p5g1.finance_tracker.DTO.Authenticate.LoginRequest;
import com.elec5619p5g1.finance_tracker.DTO.Authenticate.LoginResponse;
import com.elec5619p5g1.finance_tracker.DTO.Authenticate.RegisterRequest;
import com.elec5619p5g1.finance_tracker.DTO.Authenticate.RegisterResponse;
import com.elec5619p5g1.finance_tracker.Entity.User;
import com.elec5619p5g1.finance_tracker.Security.JwtUtil;
import com.elec5619p5g1.finance_tracker.Services.AccountService;
import com.elec5619p5g1.finance_tracker.Services.CategoryService;
import com.elec5619p5g1.finance_tracker.Services.LedgerService;
import com.elec5619p5g1.finance_tracker.Services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
@RequiredArgsConstructor
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final CategoryService categoryService;
    private final AccountService accountService;
    private final LedgerService ledgerService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Optional<User> userOptional = userService.findByEmail(loginRequest.getEmail());

            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid email or password"));
            }

            User user = userOptional.get();
            if (!userService.userPasswordMatches(user, loginRequest.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid email or password"));
            }

            String accessToken = jwtUtil.generateToken(user);
            String refreshToken = jwtUtil.generateRefreshToken(user);

            LoginResponse loginResponse = new LoginResponse(
                    accessToken,
                    refreshToken,
                    "Bearer",
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getRole() != null ? user.getRole().name() : "USER"
            );

            logger.info("User {} logged in successfully!", user.getUsername());
            return ResponseEntity.ok(loginResponse);
        } catch (Exception e) {
            logger.error("Login error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error occurred during logging in"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            if (userService.findByUsername(registerRequest.getUsername()).isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Username already exists"));
            }
            if (userService.findByEmail(registerRequest.getEmail()).isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Email already exists"));
            }

            User newUser;
            if(registerRequest.isAdmin()) {
                newUser = userService.createAdminUser(registerRequest.getUsername(), registerRequest.getEmail(), registerRequest.getPassword());
            } else {
                newUser = userService.createUser(registerRequest.getUsername(), registerRequest.getEmail(), registerRequest.getPassword());
                categoryService.impartUserDefaultCategories(newUser);
                accountService.create(newUser.getId(), "Default", BigDecimal.ZERO, newUser.getDefaultCurrencyType(), false, null);
                ledgerService.createNewLedger(newUser.getId(), "General", "personal");
            }

            logger.info("New {} registered: {}", newUser.getRole().name(), newUser.getUsername());

            RegisterResponse registerResponse = new RegisterResponse(String.valueOf(newUser.getId()), newUser.getUsername(), newUser.getEmail(), newUser.getRole().name());

            return ResponseEntity.status(HttpStatus.CREATED).body(registerResponse);
        } catch (Exception e) {
            logger.error("Registration error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error occurred during registration"));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String refreshToken) {
        try {
            if (refreshToken.startsWith("Bearer ")) {
                refreshToken = refreshToken.substring(7);
            }

            if (!jwtUtil.isTokenValid(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid refresh token"));
            }

            String username = jwtUtil.extractUsername(refreshToken);
            Optional<User> userOptional = userService.findByUsername(username);

            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not found"));
            }

            User user = userOptional.get();
            String newAccessToken = jwtUtil.generateToken(user);

            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", newAccessToken);
            response.put("tokenType", "Bearer");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Refresh token error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error occurred during refreshing token"));
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            if (jwtUtil.isTokenValid(token)) {
                Map<String, Object> response = new HashMap<>();
                response.put("valid", true);
                response.put("username", jwtUtil.extractUsername(token));
                response.put("userId", jwtUtil.extractUserId(token));
                response.put("role", jwtUtil.extractRole(token));
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.ok(Map.of("valid", false));
            }
        } catch (Exception e) {
            logger.error("Validate token error: ", e);
            return ResponseEntity.ok(Map.of("valid", false));
        }
    }
}
