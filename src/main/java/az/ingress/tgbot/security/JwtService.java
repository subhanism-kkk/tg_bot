package az.ingress.tgbot.security;

import az.ingress.tgbot.entity.RegisteredUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration}") long accessTokenExpiration,
            @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration
    ) {

        this.secretKey = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );

        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public String generateAccessToken(RegisteredUser user) {

        return generateToken(
                user,
                accessTokenExpiration,
                "ACCESS"
        );
    }

    public String generateRefreshToken(RegisteredUser user) {

        return generateToken(
                user,
                refreshTokenExpiration,
                "REFRESH"
        );
    }

    private String generateToken(
            RegisteredUser user,
            long expiration,
            String tokenType
    ) {

        Date now = new Date();

        Date expirationDate =
                new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(user.getUsername())
                .claim("role", user.getRole())
                .claim("tokenType", tokenType)
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(secretKey)
                .compact();
    }

    public String extractUsername(String token) {

        return extractClaim(
                token,
                Claims::getSubject
        );
    }

    public String extractRole(String token) {

        return extractAllClaims(token)
                .get("role", String.class);
    }

    public String extractTokenType(String token) {

        return extractAllClaims(token)
                .get("tokenType", String.class);
    }

    public boolean isTokenValid(String token) {

        try {

            extractAllClaims(token);

            return !isTokenExpired(token);

        } catch (Exception e) {

            return false;
        }
    }

    public boolean isAccessToken(String token) {

        try {

            return "ACCESS".equals(
                    extractTokenType(token)
            );

        } catch (Exception e) {

            return false;
        }
    }

    public boolean isRefreshToken(String token) {

        try {

            return "REFRESH".equals(
                    extractTokenType(token)
            );

        } catch (Exception e) {

            return false;
        }
    }

    public boolean isTokenValid(
            String token,
            RegisteredUser user
    ) {

        try {

            String username =
                    extractUsername(token);

            return username != null
                    && username.equalsIgnoreCase(
                    user.getUsername()
            )
                    && !isTokenExpired(token);

        } catch (Exception e) {

            return false;
        }
    }

    private boolean isTokenExpired(String token) {

        return extractExpiration(token)
                .before(new Date());
    }

    private Date extractExpiration(String token) {

        return extractClaim(
                token,
                Claims::getExpiration
        );
    }

    private <T> T extractClaim(
            String token,
            Function<Claims, T> resolver
    ) {

        Claims claims =
                extractAllClaims(token);

        return resolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}