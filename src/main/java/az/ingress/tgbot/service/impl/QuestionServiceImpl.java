package az.ingress.tgbot.service.impl;

import az.ingress.tgbot.dto.question.QuestionCreateRequest;
import az.ingress.tgbot.dto.question.QuestionResponse;
import az.ingress.tgbot.dto.question.QuestionUpdateRequest;
import az.ingress.tgbot.entity.Question;
import az.ingress.tgbot.entity.Step;
import az.ingress.tgbot.exception.ResourceNotFoundException;
import az.ingress.tgbot.mapper.QuestionMapper;
import az.ingress.tgbot.repository.QuestionRepository;
import az.ingress.tgbot.repository.StepRepository;
import az.ingress.tgbot.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final StepRepository stepRepository;
    private final QuestionMapper questionMapper;

    @Override
    @Transactional
    public QuestionResponse create(
            Long stepId,
            QuestionCreateRequest request
    ) {

        Step step = stepRepository.findById(stepId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Step not found with id: " + stepId
                        )
                );

        Question question = questionMapper.toEntity(request);
        question.setStep(step);

        if (question.getOptions() != null && !question.getOptions().isEmpty()) {
            question.getOptions().forEach(option -> option.setQuestion(question));
        }

        Question savedQuestion = questionRepository.save(question);

        return questionMapper.toResponse(savedQuestion);
    }

    @Override
    @Transactional
    public QuestionResponse update(
            Long id,
            QuestionUpdateRequest request
    ) {

        Question question = questionRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Question not found with id: " + id
                        )
                );

        questionMapper.updateEntity(question, request);

        Question updatedQuestion = questionRepository.save(question);

        return questionMapper.toResponse(updatedQuestion);
    }

    @Override
    public QuestionResponse getById(Long id) {

        Question question = questionRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Question not found with id: " + id
                        )
                );

        return questionMapper.toResponse(question);
    }

    @Override
    public List<QuestionResponse> getByStepId(Long stepId) {

        if (!stepRepository.existsById(stepId)) {
            throw new ResourceNotFoundException(
                    "Step not found with id: " + stepId
            );
        }

        return questionRepository
                .findByStepIdOrderByOrderIndexAsc(stepId)
                .stream()
                .map(questionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {

        if (!questionRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Question not found with id: " + id
            );
        }

        questionRepository.deleteById(id);
    }
}