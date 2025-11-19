package org.insertcoin.productservice.components;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    public AuthTokenFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String jwt = jwtUtils.getJwtFromRequest(request);

            if (jwt != null) {
                // Valida o token. Se retornar claims, é válido.
                Claims claims = jwtUtils.validateToken(jwt);

                if (claims != null) {
                    // Extrai informações salvas no token (baseado no seu generateToken)
                    // Nota: "sub" geralmente é o ID ou username padrão do JWT, mas você usou "email" e "id" explicitamente
                    String email = claims.get("email", String.class);

                    // Opcional: Recuperar roles para usar @PreAuthorize("hasRole('ADMIN')")
                    List<?> rawRoles = claims.get("roles", List.class);
                    List<SimpleGrantedAuthority> authorities = new ArrayList<>();

                    if (rawRoles != null) {
                        authorities = rawRoles.stream()
                                .map(role -> new SimpleGrantedAuthority(role.toString()))
                                .collect(Collectors.toList());
                    }

                    // Cria o objeto de autenticação do Spring
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(email, null, authorities);

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Autentica o usuário no contexto da requisição atual
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception e) {
            logger.error("Não foi possível definir a autenticação do usuário: {}", e);
        }

        filterChain.doFilter(request, response);
    }
}