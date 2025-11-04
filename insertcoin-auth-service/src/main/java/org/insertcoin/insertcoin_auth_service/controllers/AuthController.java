package org.insertcoin.insertcoin_auth_service.controllers;

import org.insertcoin.insertcoin_auth_service.dtos.*;
import org.insertcoin.insertcoin_auth_service.services.EmailVerificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.web.bind.annotation.*;
import org.insertcoin.insertcoin_auth_service.entities.UserEntity;
import org.insertcoin.insertcoin_auth_service.services.UserService;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationConfiguration authConfig;
    private final EmailVerificationService emailVerificationService;

    public AuthController(UserService userService, AuthenticationConfiguration authConfig, EmailVerificationService emailVerificationService) {
        this.userService = userService;
        this.authConfig = authConfig;
        this.emailVerificationService = emailVerificationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserResponseDTO> signup(
            @RequestBody SignupDTO dto
    ) throws Exception {
        if (!emailVerificationService.isVerified(dto.email())) {
            throw new IllegalArgumentException("E-mail não foi verificado.");
        }
        UserEntity user = userService.createUser(dto);
        UserResponseDTO response = new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getActive(),
                user.getRoles().stream().map(r -> r.getName()).collect(Collectors.toSet())
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/signin")
    public ResponseEntity<SigninResponseDTO> signin(
            @RequestBody SigninDTO dto
    ) throws Exception {
        userService.authenticate(dto.email(), dto.password());
        String token = userService.generateJwtToken(dto.email());

        return ResponseEntity.ok(new SigninResponseDTO(token));
    }

    @PostMapping("/verify-email")
    public ResponseEntity<VerifyEmailResponseDTO> sendVerificationCode(
            @RequestBody Map<String, String> request
    ) {
        String email = request.get("email");

        if (userService.existsByEmail(email)) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new VerifyEmailResponseDTO(
                            "Email already registered in the system",
                            email
                    ));
        }

        String code = String.format("%06d", new Random().nextInt(999999));

        try {
            String result = emailVerificationService.createOrUpdateVerification(email, code);

            if ("WAIT".equals(result)) {
                return ResponseEntity
                        .status(HttpStatus.TOO_MANY_REQUESTS)
                        .body(new VerifyEmailResponseDTO(
                                "Please wait 1 minute before requesting a new code.",
                                email
                        ));
            }

            return ResponseEntity.ok(new VerifyEmailResponseDTO(
                    "Code sent successfully.",
                    email
            ));

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new VerifyEmailResponseDTO(
                            "Error generating or sending verification code.",
                            email
                    ));
        }
    }

    @PostMapping("/validate-code")
    public ResponseEntity<ValidateEmailResponseDTO> validateEmailCode(
            @RequestBody Map<String, String> request
    ) {
        String email = request.get("email");
        String code = request.get("code");

        boolean valid = emailVerificationService.validateCode(email, code);

        if (!valid) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ValidateEmailResponseDTO(
                            "Invalid or expired verification code",
                            email
                    ));
        }

        return ResponseEntity.ok(new ValidateEmailResponseDTO(
                "Email successfully verified",
                email
        ));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<UpdatePasswordResponseDTO> updatePassword(
            @RequestBody UpdatePasswordRequestDTO request
    ) {
        String email = request.email();
        String newPassword = request.newPassword();

        try {
            boolean isEmailVerified = emailVerificationService.isEmailVerified(email);

            if (!isEmailVerified) {
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(new UpdatePasswordResponseDTO(
                                "Email not verified. Please verify your email before updating the password.",
                                email
                        ));
            }

            boolean updated = userService.updatePassword(email, newPassword);

            if (!updated) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(new UpdatePasswordResponseDTO(
                                "User not found.",
                                email
                        ));
            }

            return ResponseEntity.ok(new UpdatePasswordResponseDTO(
                    "Password updated successfully.",
                    email
            ));

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new UpdatePasswordResponseDTO(
                            "Error while updating password.",
                            email
                    ));
        }
    }


}
