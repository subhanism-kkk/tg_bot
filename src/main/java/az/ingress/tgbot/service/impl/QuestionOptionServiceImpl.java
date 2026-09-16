package az.ingress.tgbot.service.impl;

import az.ingress.tgbot.dto.questionOption.QuestionOptionCreateRequest;
import az.ingress.tgbot.dto.questionOption.QuestionOptionResponse;
import az.ingress.tgbot.dto.questionOption.QuestionOptionUpdateRequest;
import az.ingress.tgbot.entity.Question;
import az.ingress.tgbot.entity.QuestionOption;
import az.ingress.tgbot.exception.ConflictException;
import az.ingress.tgbot.exception.ResourceNotFoundException;
import az.ingress.tgbot.mapper.QuestionOptionMapper;
import az.ingress.tgbot.repository.QuestionOptionRepository;
import az.ingress.tgbot.repository.QuestionRepository;
import az.ingress.tgbot.service.QuestionOptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionOptionServiceImpl implements QuestionOptionService {

    private final QuestionOptionRepository questionOptionRepository;
    private final QuestionRepository questionRepository;
    private final QuestionOptionMapper questionOptionMapper;

    @Override
    @Transactional
    public QuestionOptionResponse create(Long questionId, QuestionOptionCreateRequest request) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + questionId));

        if (request.getText() != null &&
                questionOptionRepository.existsByQuestionIdAndTextIgnoreCase(questionId, request.getText().trim())) {
            throw new ConflictException("Option with text '" + request.getText() + "' already exists for this question.");
        }

        Long targetOrderIndex = request.getOrderIndex();
        if (targetOrderIndex == null) {
            targetOrderIndex = questionOptionRepository.findMaxOrderIndexByQuestionId(questionId)
                    .map(max -> max + 1)
                    .orElse(1L);
        } else if (questionOptionRepository.existsByQuestionIdAndOrderIndex(questionId, targetOrderIndex)) {
            throw new ConflictException("Option with order index " + targetOrderIndex + " already exists for this question.");
        }

        QuestionOption option = questionOptionMapper.toEntity(request);
        option.setQuestion(question);
        option.setOrderIndex(targetOrderIndex);

        QuestionOption savedOption = questionOptionRepository.save(option);
        return questionOptionMapper.toResponse(savedOption);
    }

    @Override
    @Transactional
    public QuestionOptionResponse update(Long id, QuestionOptionUpdateRequest request) {
        QuestionOption option = questionOptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question option not found with id: " + id));

        Long questionId = option.getQuestion().getId();

        if (request.getText() != null && !request.getText().trim().equalsIgnoreCase(option.getText())) {
            if (questionOptionRepository.existsByQuestionIdAndTextIgnoreCaseAndIdNot(questionId, request.getText().trim(), id)) {
                throw new ConflictException("Option with text '" + request.getText() + "' already exists for this question.");
            }
        }

        if (request.getOrderIndex() != null && !request.getOrderIndex().equals(option.getOrderIndex())) {
            if (questionOptionRepository.existsByQuestionIdAndOrderIndexAndIdNot(questionId, request.getOrderIndex(), id)) {
                throw new ConflictException("Option with order index " + request.getOrderIndex() + " already exists for this question.");
            }
        }

        questionOptionMapper.updateEntity(option, request);

        QuestionOption updatedOption = questionOptionRepository.save(option);
        return questionOptionMapper.toResponse(updatedOption);
    }

    @Override
    public QuestionOptionResponse getById(Long id) {
        QuestionOption option = questionOptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question option not found with id: " + id));

        return questionOptionMapper.toResponse(option);
    }

    @Override
    public List<QuestionOptionResponse> getByQuestionId(Long questionId) {
        if (!questionRepository.existsById(questionId)) {
            throw new ResourceNotFoundException("Question not found with id: " + questionId);
        }

        return questionOptionRepository.findByQuestionId(questionId).stream()
                .map(questionOptionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!questionOptionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Question option not found with id: " + id);
        }
        questionOptionRepository.deleteById(id);
    }
}