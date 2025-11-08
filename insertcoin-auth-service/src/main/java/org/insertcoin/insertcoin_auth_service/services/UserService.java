package org.insertcoin.insertcoin_auth_service.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.insertcoin.insertcoin_auth_service.components.CustomUserDetails;
import org.insertcoin.insertcoin_auth_service.components.JwtUtil;
import org.insertcoin.insertcoin_auth_service.dtos.*;
import org.insertcoin.insertcoin_auth_service.entities.RoleEntity;
import org.insertcoin.insertcoin_auth_service.entities.UserEntity;
import org.insertcoin.insertcoin_auth_service.repositories.RoleRepository;
import org.insertcoin.insertcoin_auth_service.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;

    public UserService(UserRepository userRepository, RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder, AuthenticationConfiguration authConfig) throws Exception {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authManager = authConfig.getAuthenticationManager();
    }

    public UserEntity createUser(SignupDTO dto) {
        UserEntity user = new UserEntity();
        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setActive(true);
        user.setPoint(0);

        RoleEntity roleUser = roleRepository.findByName("CLIENT")
                .orElseThrow(() -> new RuntimeException("CLIENT não encontrada"));
        user.setRoles(Set.of(roleUser));

        return userRepository.save(user);
    }

    public void authenticate(String email, String password) throws AuthenticationException {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
    }

    public String generateJwtToken(String email) {
        UserEntity user = findByEmail(email);
        return JwtUtil.generateToken(user);
    }

    public UserEntity findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    }

    @Override
    public CustomUserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
        return new CustomUserDetails(user);
    }

    public UserEntity createAdminUser(String name, String email, String password, String roleName) throws Exception {
        UserEntity user = new UserEntity();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setActive(true);
        user.setPoint(0);

        RoleEntity role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new Exception("Role não encontrada"));

        user.getRoles().add(role);

        return userRepository.save(user);
    }

    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public boolean updatePassword(String email, String newPassword) {
        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) return false;

        UserEntity user = userOpt.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return true;
    }

    public UserResponseDTO findUserById(UUID id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));//preciso arrumar isso

        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getActive(),
                user.getRoles()
                        .stream()
                        .map(RoleEntity::getName)
                        .collect(Collectors.toSet())
        );
    }

    public Page<UserEmailDTO> searchEmployees(String email, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        List<String> roles = Arrays.asList("MANAGER_STORE", "COMMERCIAL");
        return userRepository.findAllByRolesAndEmailContaining(roles, email, pageable);
    }

    public Page<UserEmailDTO> searchClient(String email, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("email").ascending());
        List<String> roles = List.of("CLIENT");
        return userRepository.findAllByRolesAndEmailContaining(roles, email, pageable);
    }

    @Transactional
    public boolean updateEmployee(UUID id, UpdateEmployeeRequestDTO request) {
        Optional<UserEntity> optionalUser = userRepository.findById(id);

        if (optionalUser.isEmpty()) {
            return false;
        }

        UserEntity user = optionalUser.get();

        boolean hasAdminPermission = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .anyMatch(permission -> permission.getName().contains("ADMIN"));

        if (!hasAdminPermission) {
            throw new IllegalArgumentException("You can only update admin accounts.");
        }


        if (request.name() != null && !request.name().isBlank()) {
            user.setName(request.name());
        }

        if (request.password() != null && !request.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }

        if (request.role() != null && !request.role().isBlank()) {
            RoleEntity newRole = roleRepository.findByName(request.role())
                    .orElseThrow(() -> new EntityNotFoundException("Role not found: " + request.role()));

            user.getRoles().clear();
            user.getRoles().add(newRole);
        }

        if (request.active() != null) {
            user.setActive(request.active());
        }

        userRepository.save(user);
        return true;
    }

}
