package org.insertcoin.insertcoinpaymentservice.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.insertcoin.insertcoinpaymentservice.utils.JwtUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String token = jwtUtil.getJwtFromRequest(request);

        Claims claims = null;
        if (token != null) {
            claims = jwtUtil.validateToken(token);
        }

        if (claims != null) {
            //System.out.println("\n==== JWT RECEBIDO NO PAYMENT-SERVICE ====");
            //claims.forEach((k, v) -> System.out.println(k + " : " + v));
            //System.out.println("=======================================\n");

            String email = claims.get("email", String.class);

            List<String> roles = objectMapper.convertValue(claims.get("roles"), new TypeReference<List<String>>() {});
            List<String> permissions = objectMapper.convertValue(claims.get("permissions"), new TypeReference<List<String>>() {});

            Set<GrantedAuthority> authorities = new HashSet<>();

            if (permissions != null) {
                permissions.forEach(p ->
                        authorities.add(new SimpleGrantedAuthority(p)));
            }

            if (roles != null) {
                roles.forEach(r ->
                        authorities.add(new SimpleGrantedAuthority("ROLE_" + r)));
            }

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            email,
                            null,
                            authorities
                    );

            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }
}
