package az.ingress.tgbot.service.impl;

import az.ingress.tgbot.dto.step.StepCreateRequest;
import az.ingress.tgbot.dto.step.StepResponse;
import az.ingress.tgbot.dto.step.StepUpdateRequest;
import az.ingress.tgbot.entity.Step;
import az.ingress.tgbot.entity.Survey;
import az.ingress.tgbot.exception.BadRequestException;
import az.ingress.tgbot.exception.ResourceNotFoundException;
import az.ingress.tgbot.mapper.StepMapper;
import az.ingress.tgbot.repository.StepRepository;
import az.ingress.tgbot.repository.SurveyRepository;
import az.ingress.tgbot.service.StepService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StepServiceImpl implements StepService {
    private final StepRepository repository;
    private final StepMapper mapper;
    private final SurveyRepository surveyRepository;

    @Override
    @Transactional
    public StepResponse create(Long surveyId, StepCreateRequest request) {

        if (!surveyRepository.existsById(surveyId)) {
            throw new ResourceNotFoundException(
                    "Survey not found with id: " + surveyId);
        }
        Survey survey = surveyRepository.getReferenceById(surveyId);

        Step step = mapper.toEntity(request);

        step.setSurvey(survey);

        Step saved = repository.save(step);
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    public StepResponse update(Long id, StepUpdateRequest request) {

        Step step = fetchStep(id);

        mapper.updateEntity(step, request);

        Step updated = repository.save(step);
        return mapper.toResponse(updated);
    }

    @Override
    public StepResponse getById(Long id) {
        return mapper.toResponse(fetchStep(id));
    }

    @Override
    public List<StepResponse> getAll() {
        return repository.findAll(Sort.by(Sort.Direction.ASC, "orderIndex"))
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public List<StepResponse> getBySurveyId(Long surveyId) {
        if (!surveyRepository.existsById(surveyId)) {
            throw new ResourceNotFoundException(
                    "Survey not found with id : " + surveyId);
        }
        return repository.findBySurveyIdOrderByOrderIndexAsc(surveyId).
                stream().
                map(mapper::toResponse).
                toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Step not found with id: " + id);
        }

        repository.deleteById(id);
    }

    private Step fetchStep(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Step not found with id: " + id));
    }
}

