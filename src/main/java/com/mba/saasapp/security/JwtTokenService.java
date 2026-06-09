package com.mba.saasapp.security;

import com.mba.saasapp.Properties.JwtProperties;
import com.mba.saasapp.exceptions.UnauthorizedException;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import jakarta.annotation.Nonnull ;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.security.PublicKey;

import java.util.Date;


@RequiredArgsConstructor
@Slf4j
@Service
public class JwtTokenService {
    private final JwtProperties jwtProperties;
  //  @Value("${app.jwt.private-key-path}")
    private PrivateKey privateKey;
  //  @Value("${app.jwt.public-key-path}")
    private PublicKey publicKey;
    @Value("${app.jwt.access-token-expiration:86400000}")
    private long jwtExpiration = 86400000L;


    @PostConstruct
    public void init() {
        try {
            // Chemins en dur : plus besoin de jwtProperties !
            this.privateKey = loadPrivateKey("certs/private_key.pem");
            this.publicKey = loadPublicKey("certs/public_key.pem");

            log.info("Private & Public key loaded successfully");
        } catch (final Exception e) {
            log.error("Error loading private key", e);
            throw new RuntimeException("Error loading private key", e);
        }
    }

    public String getUserIdFromToken(final String token) {
        final Claims claims = getClaimsFromToken(token);
        return claims.getSubject();
    }
    public String getTenantIdFromToken(final String token) {
        final Claims claims = getClaimsFromToken(token);
        return claims.get("tenant_id", String.class);
    }

    public String getRoleFromToken(final String token) {
        final Claims claims = getClaimsFromToken(token);
        return claims.get("role", String.class);
    }

    public boolean validateToken(final String token) {
        try {
            Jwts.parser()
                    .verifyWith(this.publicKey)
                    .build()
                    .parseSignedClaims(token);
             return true;

     //   } catch (final ExpiredJwtException e) {
          //  throw new UnauthorizedException("Token has expired");
        } catch (final UnsupportedJwtException e) {
            throw new UnauthorizedException("Token is not signed");
        } catch (final MalformedJwtException e) {
            throw new UnauthorizedException("Token is malformed");
        } catch (final SecurityException e) {
            throw new UnauthorizedException("Invalid JWT Signature");
        } catch (final IllegalArgumentException e) {
            throw new UnauthorizedException("JWT claims string is empty.");
        }
    }

    private Claims getClaimsFromToken(final String token) {
        return Jwts.parser()
                .verifyWith(this.publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


    public String generateAccessToken(
            @Nonnull final String tenantId,
            @Nonnull final String userId,
            final String role
    ) {
        final Date now = new Date();
        final Date expiration = new Date(System.currentTimeMillis() + this.jwtProperties.getAccessTokenExpiration());

        return Jwts.builder()
                .subject(userId)
                .claim("tenant_id", tenantId)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiration)
                .issuer("stock-saas-app")
                .signWith(this.privateKey, Jwts.SIG.RS256)
                .compact();
    }

    private PrivateKey loadPrivateKey(final String path) throws Exception {
        try (var is = new org.springframework.core.io.ClassPathResource(path).getInputStream()) {
            final String keyStr = new String(is.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
            final String privateKeyPEM = keyStr
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", ""); // Supprime tous les espaces et sauts de ligne

            // Utilisation du MimeDecoder (beaucoup plus tolérant sur le formatage PEM)
            final byte[] decodedKey = java.util.Base64.getMimeDecoder().decode(privateKeyPEM);
            final java.security.spec.PKCS8EncodedKeySpec spec = new java.security.spec.PKCS8EncodedKeySpec(decodedKey);
            final java.security.KeyFactory kf = java.security.KeyFactory.getInstance("RSA");
            return kf.generatePrivate(spec);
        }
    }

    private PublicKey loadPublicKey(final String path) throws Exception {
        try (var is = new org.springframework.core.io.ClassPathResource(path).getInputStream()) {
            final String keyStr = new String(is.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
            final String publicKeyPEM = keyStr
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s+", "");

            final byte[] decodedKey = java.util.Base64.getMimeDecoder().decode(publicKeyPEM);
            final java.security.spec.X509EncodedKeySpec spec = new java.security.spec.X509EncodedKeySpec(decodedKey);
            final java.security.KeyFactory kf = java.security.KeyFactory.getInstance("RSA");
            return kf.generatePublic(spec);
        }
    }





}

