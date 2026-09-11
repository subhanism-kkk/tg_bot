package az.ingress.tgbot.security;

import az.ingress.tgbot.entity.AdminUser;
import az.ingress.tgbot.repository.AdminUserRepository;
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
    private final AdminUserRepository adminUserRepository;

    @Override
    protected boolean shouldNotFilter(
            HttpServletRequest request
    ) {

        String uri = request.getRequestURI();

        return request.getMethod()
                .equalsIgnoreCase("OPTIONS")

                || uri.startsWith("/api/admin/auth/")

                || uri.startsWith("/swagger-ui/")

                || uri.startsWith("/v3/api-docs/")

                || uri.startsWith("/swagger-resources/")

                || uri.startsWith("/webjars/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader =
                request.getHeader("Authorization");

        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        String token =
                authHeader.substring(7);

        try {

            if (!jwtService.isTokenValid(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            String username =
                    jwtService.extractUsername(token);

            if (username != null &&
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication() == null) {

                AdminUser admin =
                        adminUserRepository
                                .findByUsernameIgnoreCase(username)
                                .orElse(null);

                if (admin != null &&
                        admin.isActive() &&
                        jwtService.isTokenValid(token, admin)) {

                    List<SimpleGrantedAuthority> authorities =
                            List.of(
                                    new SimpleGrantedAuthority(
                                            "ROLE_" +
                                                    admin.getRole().name()
                                    )
                            );

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    admin,
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
                }
            }

        } catch (Exception ignored) {
            /*
             * Invalid JWT must not crash the request.
             * Spring Security will simply see the request
             * as unauthenticated.
             */
        }

        filterChain.doFilter(request, response);
    }
}