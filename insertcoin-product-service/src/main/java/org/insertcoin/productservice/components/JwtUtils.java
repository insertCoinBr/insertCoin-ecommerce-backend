package org.insertcoin.productservice.components;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class JwtUtils {

    // MANTENHA A MESMA CHAVE DO AUTH SERVICE
    private static final String SECRET_KEY = "chaveSuperSecretaParaJWTdeExemplo!@#123";

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    // Método generateToken REMOVIDO pois este serviço não cria usuários

    // Valida e retorna as informações (Claims) se o token estiver ok
    public Claims validateToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            // Token inválido, expirado ou modificado
            return null;
        }
    }

    public String getJwtFromRequest(HttpServletRequest request) {
        String jwt = request.getHeader("Authorization");

        // Fallback para minúsculo caso algum proxy altere o header
        if (jwt == null || jwt.isEmpty()) {
            jwt = request.getHeader("authorization");
        }

        if (jwt != null && jwt.startsWith("Bearer ")) {
            return jwt.substring(7);
        }
        return null;
    }
}