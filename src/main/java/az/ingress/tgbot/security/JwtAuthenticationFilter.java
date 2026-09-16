package az.ingress.tgbot.security;

import az.ingress.tgbot.entity.RegisteredUser;
import az.ingress.tgbot.repository.RegisteredUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter
        extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final RegisteredUserRepository registeredUserRepository;

    @Override
    protected boolean shouldNotFilter(
            HttpServletRequest request
    ) {

        String uri = request.getRequestURI();

        return uri.startsWith("/api/admin/auth/")
                || uri.startsWith("/swagger-ui/")
                || uri.startsWith("/v3/api-docs/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader =
                request.getHeader("Authorization");

        if (authHeader == null
                || !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }

        String token =
                authHeader.substring(7);

        try {

            if (!jwtService.isTokenValid(token)
                    || !jwtService.isAccessToken(token)) {

                filterChain.doFilter(
                        request,
                        response
                );

                return;
            }

            String username =
                    jwtService.extractUsername(token);

            RegisteredUser user =
                    registeredUserRepository
                            .findByUsernameIgnoreCase(username)
                            .orElse(null);

            if (user == null || !user.isActive()) {

                filterChain.doFilter(
                        request,
                        response
                );

                return;
            }

            if (!jwtService.isTokenValid(
                    token,
                    user
            )) {

                filterChain.doFilter(
                        request,
                        response
                );

                return;
            }

            String role =
                    user.getRole().name();

            List<SimpleGrantedAuthority> authorities =
                    List.of(
                            new SimpleGrantedAuthority(
                                    "ROLE_" + role
                            )
                    );

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            authorities
                    );

            authentication.setDetails(
                    new WebAuthenticationDetailsSource()
                            .buildDetails(request)
            );

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);

        } catch (Exception e) {

            SecurityContextHolder
                    .clearContext();
        }

        filterChain.doFilter(
                request,
                response
        );
    }
}