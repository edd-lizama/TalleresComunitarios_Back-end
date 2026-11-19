package com.municipalidad.talleresph.service_catalogo_sucursales.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil 
{
    @Value("${jwt.secret}")
    private String secretKey;

    public String extraerCorreo(String token) 
    {
        return extraerClaim(token, Claims::getSubject);
    }

    public String extraerRol(String token) 
    {
        return extraerClaim(token, claims -> claims.get("rol", String.class));
    }

    public Long extraerIdUsuario(String token) 
    {
        return extraerClaim(token, claims -> {
            Object id = claims.get("idUsuario");
            if (id instanceof Number num) {
                return num.longValue();
            }
            if (id instanceof String s && !s.isBlank()) {
                try {
                    return Long.parseLong(s);
                } catch (NumberFormatException ignored) {}
            }
            return null;
        });
    }

    public <T> T extraerClaim(String token, Function<Claims, T> claimsResolver) 
    {
        final Claims claims = extraerTodosLosClaims(token);
        return claimsResolver.apply(claims);
    }

    public boolean esTokenValido(String token) {
        try {
            return !estaTokenExpirado(token);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean estaTokenExpirado(String token) 
    {
        return extraerClaim(token, Claims::getExpiration).before(new Date());
    }

    private Claims extraerTodosLosClaims(String token) 
    {
        return Jwts.parser()
                .verifyWith(obtenerClaveFirma())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey obtenerClaveFirma() 
    {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
