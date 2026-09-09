package az.ingress.tgbot.entity;

import az.ingress.tgbot.entity.base.BaseIdEntity;
import az.ingress.tgbot.enums.RegistrationStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(name = "telegram_users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TelegramUser extends BaseIdEntity {


    @Column(name = "chat_id", nullable = false, unique = true)
    private Long chatId;

    private String username;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "phone_number", length = 50)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "registration_status", nullable = false, length = 50)
    private RegistrationStatus registrationStatus = RegistrationStatus.NOT_STARTED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_step_id")
    private Step currentStep;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_question_id")
    private Question currentQuestion;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}