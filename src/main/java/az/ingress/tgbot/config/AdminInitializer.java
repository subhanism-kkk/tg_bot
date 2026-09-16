package az.ingress.tgbot.config;

import az.ingress.tgbot.entity.RegisteredUser;
import az.ingress.tgbot.enums.UserRole;
import az.ingress.tgbot.repository.RegisteredUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final RegisteredUserRepository registeredUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        if (registeredUserRepository.count() > 0) {
            return;
        }

        RegisteredUser user = RegisteredUser.builder()
                .username("admin")
                .passwordHash(
                        passwordEncoder.encode("admin123")
                )
                .role(UserRole.ADMIN)
                .active(true)
                .build();

        registeredUserRepository.save(user);

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