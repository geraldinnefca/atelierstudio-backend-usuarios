package com.atelierstudio.backend_usuarios.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // Clave secreta (debe tener al menos 32 caracteres)
    private final String SECRET_KEY = "AtelierStudioClaveSecretaSuperSeguraParaElProyectoDuoc";
    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    // El token durará 2 horas (en milisegundos)
    private final long TIEMPO_EXPIRACION = 7200000;

    // 1. Generar el Token (El que usamos en el AuthController)
    public String generarToken(String correo, String rol) {
        return Jwts.builder()
                .setSubject(correo)
                .claim("rol", rol)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + TIEMPO_EXPIRACION))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 2. Extraer el correo del token (Para el JwtRequestFilter)
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // 3. Validar si el token pertenece al usuario y no ha expirado
    public boolean validateToken(String token, String correoUsuario) {
        final String correoEnToken = extractUsername(token);
        return (correoEnToken.equals(correoUsuario) && !isTokenExpired(token));
    }

    // 4. Comprobar si el token ya venció
    private boolean isTokenExpired(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .before(new Date());
    }
}