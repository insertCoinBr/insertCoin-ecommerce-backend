package org.insertcoin.insertcoin_auth_service.services;

import org.insertcoin.insertcoin_auth_service.components.CustomUserDetails;
import org.insertcoin.insertcoin_auth_service.components.JwtUtil;
import org.insertcoin.insertcoin_auth_service.dtos.SignupDTO;
import org.insertcoin.insertcoin_auth_service.entities.RoleEntity;
import org.insertcoin.insertcoin_auth_service.entities.UserEntity;
import org.insertcoin.insertcoin_auth_service.repositories.RoleRepository;
import org.insertcoin.insertcoin_auth_service.repositories.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

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

}
