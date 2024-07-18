package com.compass.desafio3.config.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.compass.desafio3.domain.models.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class TokenService {

    @Value("${spring.api.security.token.secret}")
    private String secret;

    public String gerarToken(Usuario usuario){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create()
                    .withIssuer("Desafio3 - Compass")
                    .withSubject(usuario.getEmail())
                    .withExpiresAt(expiresAt())
                    .withIssuedAt(issuedAt())
                    .sign(algorithm);
            return token;
        } catch (JWTCreationException ex){
            throw new RuntimeException("Erro ao gerar token", ex);
        }
    }

    public String validarToken(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("Desafio3 - Compass")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException ex){
            return "";
        }
    }

    private Instant expiresAt(){
        return Instant.now().plusSeconds(86400); // 1 dia
    }

    private Instant issuedAt(){
        return Instant.now();
    }

}
