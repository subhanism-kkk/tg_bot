package az.ingress.tgbot.entity;

import az.ingress.tgbot.entity.base.BaseIdEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User  extends BaseIdEntity {

    @Column(name = "telegram_id", unique = true, nullable = false)
    private Long telegramId;

    @Column(name = "chat_id", nullable = false)
    private Long chatId;

    private String firstName;
    private String username;

    @Column(name = "current_step_index")
    private Long currentStepIndex;
}