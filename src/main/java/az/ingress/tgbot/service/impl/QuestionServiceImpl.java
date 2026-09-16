package az.ingress.tgbot.service.impl;

import az.ingress.tgbot.dto.question.QuestionCreateRequest;
import az.ingress.tgbot.dto.question.QuestionResponse;
import az.ingress.tgbot.dto.question.QuestionUpdateRequest;
import az.ingress.tgbot.entity.Question;
import az.ingress.tgbot.entity.Step;
import az.ingress.tgbot.exception.BadRequestException;
import az.ingress.tgbot.exception.ResourceNotFoundException;
import az.ingress.tgbot.mapper.QuestionMapper;
import az.ingress.tgbot.repository.QuestionRepository;
import az.ingress.tgbot.repository.StepRepository;
import az.ingress.tgbot.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final StepRepository stepRepository;
    private final QuestionMapper questionMapper;

    @Override
    @Transactional
    public QuestionResponse create(Long stepId, QuestionCreateRequest request) {
        Step step = stepRepository.findById(stepId)
                .orElseThrow(() -> new ResourceNotFoundException("Step not found with id: " + stepId));

        if (request.getText() != null &&
                questionRepository.existsByStepIdAndTextIgnoreCase(stepId, request.getText().trim())) {
            throw new BadRequestException("Question with text '" + request.getText() + "' already exists in this step");
        }

        Long targetOrderIndex = request.getOrderIndex();
        if (targetOrderIndex == null) {
            targetOrderIndex = questionRepository.findMaxOrderIndexByStepId(stepId)
                    .map(max -> max + 1)
                    .orElse(1L);
        } else if (questionRepository.existsByStepIdAndOrderIndex(stepId, targetOrderIndex)) {
            throw new BadRequestException("Question with order index " + targetOrderIndex + " already exists in this step");
        }

        Question question = questionMapper.toEntity(request);
        question.setStep(step);
        question.setOrderIndex(targetOrderIndex);

        if (question.getOptions() != null && !question.getOptions().isEmpty()) {
            question.getOptions().forEach(option -> option.setQuestion(question));
        }

        Question savedQuestion = questionRepository.save(question);
        return questionMapper.toResponse(savedQuestion);
    }

    @Override
    @Transactional
    public QuestionResponse update(Long id, QuestionUpdateRequest request) {
        Question question = fetchQuestion(id);
        Long stepId = question.getStep().getId();

        if (request.getText() != null && !request.getText().trim().equalsIgnoreCase(question.getText())) {
            if (questionRepository.existsByStepIdAndTextIgnoreCaseAndIdNot(stepId, request.getText().trim(), id)) {
                throw new BadRequestException("Question with text '" + request.getText() + "' already exists in this step");
            }
        }

        if (request.getOrderIndex() != null && !request.getOrderIndex().equals(question.getOrderIndex())) {
            if (questionRepository.existsByStepIdAndOrderIndexAndIdNot(stepId, request.getOrderIndex(), id)) {
                throw new BadRequestException("Question with order index " + request.getOrderIndex() + " already exists in this step");
            }
        }

        questionMapper.updateEntity(question, request);

        Question updatedQuestion = questionRepository.save(question);
        return questionMapper.toResponse(updatedQuestion);
    }

    @Override
    public QuestionResponse getById(Long id) {
        return questionMapper.toResponse(fetchQuestion(id));
    }

    @Override
    public List<QuestionResponse> getByStepId(Long stepId) {
        if (!stepRepository.existsById(stepId)) {
            throw new ResourceNotFoundException("Step not found with id: " + stepId);
        }

        return questionRepository.findByStepIdOrderByOrderIndexAsc(stepId)
                .stream()
                .map(questionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!questionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Question not found with id: " + id);
        }

        questionRepository.deleteById(id);
    }


    @Override
    @Transactional
    public void reorder(Long stepId, List<Long> questionIds) {

        List<Question> questions =
                questionRepository.findByStepIdOrderByOrderIndexAsc(stepId);

        if (questions.size() != questionIds.size()) {
            throw new IllegalArgumentException(
                    "All questions of the step must be included in the reorder request."
            );
        }

        Map<Long, Question> questionMap = questions.stream()
                .collect(Collectors.toMap(
                        Question::getId,
                        question -> question
                ));

        for (Long questionId : questionIds) {

            if (!questionMap.containsKey(questionId)) {
                throw new IllegalArgumentException(
                        "Question " + questionId +
                                " does not belong to step " + stepId
                );
            }
        }

        for (int i = 0; i < questionIds.size(); i++) {

            Long questionId = questionIds.get(i);

            Question question = questionMap.get(questionId);

            question.setOrderIndex((long) (i + 1));
        }

        questionRepository.saveAll(questions);
    }


    private Question fetchQuestion(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + id));
    }
}