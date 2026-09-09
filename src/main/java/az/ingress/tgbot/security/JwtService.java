//package az.ingress.hrmsauthserver.service.auth;
//
//import az.ingress.hrmsauthserver.entity.user.User;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.security.Keys;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import az.ingress.hrmsauthserver.dto.auth.TokenUserGroup;
//import az.ingress.hrmsauthserver.entity.rel.RelUserGroup;
//
//import java.util.HashMap;
//import java.util.List;
//import javax.crypto.SecretKey;
//import java.nio.charset.StandardCharsets;
//import java.util.Date;
//import java.util.Map;
//
//@Service
//public class JwtService {
//
//    private final SecretKey secretKey;
//    private final long expiration;
//
//    public JwtService(
//            @Value("${jwt.secret}") String secret,
//            @Value("${jwt.expiration}") long expiration
//    ) {
//        this.secretKey = Keys.hmacShaKeyFor(
//                secret.getBytes(StandardCharsets.UTF_8)
//        );
//
//        this.expiration = expiration;
//    }
//
//    public String generateToken(
//            User user,
//            Integer selectedUserGroupId
//    ) {
//
//        Date now = new Date();
//
//        Date expiryDate = new Date(
//                now.getTime() + expiration
//        );
//
//        List<Map<String, Object>> userGroups =
//                user.getUserGroups()
//                        .stream()
//                        .map(RelUserGroup::getUserGroup)
//                        .map(group -> {
//
//                            Map<String, Object> groupData =
//                                    new HashMap<>();
//
//                            groupData.put("id", group.getId());
//                            groupData.put("name", group.getName());
//                            groupData.put(
//                                    "selected",
//                                    group.getId().equals(selectedUserGroupId)
//                            );
//
//                            return groupData;
//                        })
//                        .toList();
//
//        return Jwts.builder()
//                .subject(user.getEmail())
//                .claim("userId", user.getId())
//                .claim("name", user.getName())
//                .claim("userGroups", userGroups)
//                .issuedAt(now)
//                .expiration(expiryDate)
//                .signWith(secretKey)
//                .compact();
//    }
//
//    public String extractUsername(String token) {
//
//        return Jwts.parser()
//                .verifyWith(secretKey)
//                .build()
//                .parseSignedClaims(token)
//                .getPayload()
//                .getSubject();
//    }
//
//    public Integer extractUserId(String token) {
//
//        return Jwts.parser()
//                .verifyWith(secretKey)
//                .build()
//                .parseSignedClaims(token)
//                .getPayload()
//                .get("userId", Integer.class);
//    }
//
//
//    public Integer extractSelectedUserGroupId(String token) {
//
//        List<Map<String, Object>> userGroups =
//                Jwts.parser()
//                        .verifyWith(secretKey)
//                        .build()
//                        .parseSignedClaims(token)
//                        .getPayload()
//                        .get("userGroups", List.class);
//
//        if (userGroups == null) {
//            return null;
//        }
//
//        return userGroups.stream()
//                .filter(group ->
//                        Boolean.TRUE.equals(group.get("selected"))
//                )
//                .map(group ->
//                        ((Number) group.get("id")).intValue()
//                )
//                .findFirst()
//                .orElse(null);
//    }
//
//
//    public boolean isTokenValid(String token) {
//
//        try {
//
//            Jwts.parser()
//                    .verifyWith(secretKey)
//                    .build()
//                    .parseSignedClaims(token);
//
//            return true;
//
//        } catch (Exception e) {
//
//            return false;
//        }
//    }
//}