package org.insertcoin.insertcoin_auth_service.controllers;

import org.insertcoin.insertcoin_auth_service.dtos.*;
import org.insertcoin.insertcoin_auth_service.entities.PermissionEntity;
import org.insertcoin.insertcoin_auth_service.entities.VerificationType;
import org.insertcoin.insertcoin_auth_service.services.EmailService;
import org.insertcoin.insertcoin_auth_service.services.EmailVerificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.insertcoin.insertcoin_auth_service.entities.UserEntity;
import org.insertcoin.insertcoin_auth_service.services.UserService;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationConfiguration authConfig;
    private final EmailVerificationService emailVerificationService;
    private final EmailService emailService;

    public AuthController(UserService userService, AuthenticationConfiguration authConfig, EmailVerificationService emailVerificationService, EmailService emailService) {
        this.userService = userService;
        this.authConfig = authConfig;
        this.emailVerificationService = emailVerificationService;
        this.emailService = emailService;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserResponseDTO> signup(
            @RequestBody SignupDTO dto
    ) throws Exception {
        if (!emailVerificationService.isVerified(dto.email(), VerificationType.VERIFY_EMAIL)) {
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
            String result = emailVerificationService.createOrUpdateVerification(email, code, VerificationType.VERIFY_EMAIL);

            if ("WAIT".equals(result)) {
                return ResponseEntity
                        .status(HttpStatus.TOO_MANY_REQUESTS)
                        .body(new VerifyEmailResponseDTO(
                                "Please wait 1 minute before requesting a new code.",
                                email
                        ));
            }

            emailService.sendVerificationEmail(email, code);

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
        String type = request.get("type");

        VerificationType verificationType = VerificationType.valueOf(type.toUpperCase());

        boolean valid = emailVerificationService.validateCode(email, code, verificationType);

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

    @PostMapping("/reset-password")
    public ResponseEntity<UpdatePasswordResponseDTO> updatePassword(
            @RequestBody UpdatePasswordRequestDTO request
    ) {
        String email = request.email();
        String newPassword = request.newPassword();

        try {
            boolean isEmailVerified = emailVerificationService.isVerified(email, VerificationType.FORGOT_PASSWORD);

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

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponseDTO> getAuthenticatedUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = authentication.getName();

        try {
            UserEntity user = userService.findByEmail(email);

            Set<String> authoritiesRoles = user.getRoles().stream()
                    .map(role -> "ROLE_" + role.getName())
                    .collect(Collectors.toSet());

            Set<String> authoritiesPermissions = user.getRoles().stream()
                    .flatMap(role -> role.getPermissions().stream())
                    .map(PermissionEntity::getName)
                    .collect(Collectors.toSet());

            UserProfileResponseDTO response = new UserProfileResponseDTO(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getActive(),
                    user.getPoint() != null ? user.getPoint() : 0,
                    authoritiesRoles,
                    authoritiesPermissions
            );

            return ResponseEntity.ok(response);

        } catch (UsernameNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<VerifyEmailResponseDTO> forgotPassword(
            @RequestBody Map<String, String> request
    ) {
        String email = request.get("email");

        if (!userService.existsByEmail(email)) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new VerifyEmailResponseDTO(
                            "E-mail não encontrado no sistema.",
                            email
                    ));
        }

        String code = String.format("%06d", new Random().nextInt(999999));

        try {
            String result = emailVerificationService.createOrUpdateVerification(email, code, VerificationType.FORGOT_PASSWORD);

            if ("WAIT".equals(result)) {
                return ResponseEntity
                        .status(HttpStatus.TOO_MANY_REQUESTS)
                        .body(new VerifyEmailResponseDTO(
                                "Aguarde 1 minuto antes de solicitar um novo código.",
                                email
                        ));
            }

            emailService.sendPasswordResetEmail(email, code);

            return ResponseEntity.ok(new VerifyEmailResponseDTO(
                    "Código de recuperação enviado com sucesso.",
                    email
            ));

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new VerifyEmailResponseDTO(
                            "Erro ao gerar ou enviar o código de recuperação.",
                            email
                    ));
        }
    }



}
