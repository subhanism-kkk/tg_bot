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

        /*
         * No JWT.
         *
         * Let Spring Security handle the request.
         */
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

            /*
             * Check token validity and make sure
             * this is an ACCESS token.
             */
            if (!jwtService.isTokenValid(token)
                    || !jwtService.isAccessToken(token)) {

                filterChain.doFilter(
                        request,
                        response
                );

                return;
            }

            /*
             * Extract username from JWT.
             */
            String username =
                    jwtService.extractUsername(token);

            /*
             * Load the current user from DB.
             *
             * This is important because role and active
             * status can change after the JWT was issued.
             */
            RegisteredUser user =
                    registeredUserRepository
                            .findByUsernameIgnoreCase(username)
                            .orElse(null);

            if (user == null) {

                filterChain.doFilter(
                        request,
                        response
                );

                return;
            }

            /*
             * Inactive users must immediately lose access,
             * even if they still have a valid JWT.
             */
            if (!user.isActive()) {

                filterChain.doFilter(
                        request,
                        response
                );

                return;
            }

            /*
             * Validate JWT against the current user.
             */
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

            /*
             * ADMIN -> ROLE_ADMIN
             * OPERATOR -> ROLE_OPERATOR
             */
            String authority =
                    "ROLE_" + user.getRole().name();

            List<SimpleGrantedAuthority> authorities =
                    List.of(
                            new SimpleGrantedAuthority(
                                    authority
                            )
                    );

            /*
             * Create authenticated Spring Security user.
             */
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
                    .setAuthentication(
                            authentication
                    );

        } catch (Exception e) {

            /*
             * Never leave a partially authenticated
             * SecurityContext behind.
             */
            SecurityContextHolder
                    .clearContext();
        }

        filterChain.doFilter(
                request,
                response
        );
    }
}