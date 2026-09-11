package az.ingress.tgbot.config;

import az.ingress.tgbot.entity.AdminUser;
import az.ingress.tgbot.enums.UserRole;
import az.ingress.tgbot.repository.AdminUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        if (adminUserRepository.count() > 0) {
            return;
        }

        AdminUser admin = AdminUser.builder()
                .username("admin")
                .passwordHash(
                        passwordEncoder.encode("admin123")
                )
                .role(UserRole.ADMIN)
                .active(true)
                .build();

        adminUserRepository.save(admin);

        System.out.println(
                "======================================"
        );

        System.out.println(
                "Default admin created"
        );

        System.out.println(
                "Username: admin"
        );

        System.out.println(
                "Password: admin123"
        );

        System.out.println(
                "======================================"
        );
    }
}