package az.ingress.tgbot.entity;

import az.ingress.tgbot.entity.base.BaseIdEntity;
import az.ingress.tgbot.enums.QuestionType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Question extends BaseIdEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "step_id", nullable = false)
    private Step step;

    @Column(nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String text;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private QuestionType type;

    @Column(name = "is_required", nullable = false)
    private Boolean isRequired = true;

    @Column(name = "order_index", nullable = false)
    private Long orderIndex = 0L;

    @Column(name = "validation_regex", length = 500)
    private String validationRegex;

    @Column(name = "next_question_logic", columnDefinition = "NVARCHAR(MAX)")
    private String nextQuestionLogic; // Holds raw JSON string for branching logic

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    @Builder.Default
    private List<QuestionOption> options = new ArrayList<>();
}