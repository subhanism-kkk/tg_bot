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
import java.util.Map;
import java.util.stream.Collectors;

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
            throw new ResourceNotFoundException("Survey not found with id: " + surveyId);
        }

        if (request.getTitle() != null &&
                repository.existsBySurveyIdAndTitleIgnoreCase(surveyId, request.getTitle().trim())) {
            throw new BadRequestException("Step with title '" + request.getTitle() + "' already exists in this survey");
        }

        Long targetOrderIndex = request.getOrderIndex();

        // Auto-increment logic fix: Handle null or non-positive values cleanly
        if (targetOrderIndex == null || targetOrderIndex <= 0) {
            Long maxIndex = repository.findMaxOrderIndexBySurveyId(surveyId).orElse(0L);
            targetOrderIndex = maxIndex + 1;
        } else if (repository.existsBySurveyIdAndOrderIndex(surveyId, targetOrderIndex)) {
            throw new BadRequestException("Step with order index " + targetOrderIndex + " already exists in this survey");
        }

        Survey survey = surveyRepository.getReferenceById(surveyId);
        Step step = mapper.toEntity(request);
        step.setSurvey(survey);
        step.setOrderIndex(targetOrderIndex);

        Step saved = repository.save(step);
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    public StepResponse update(Long id, StepUpdateRequest request) {
        Step step = fetchStep(id);
        Long surveyId = step.getSurvey().getId();

        if (request.getTitle() != null && !request.getTitle().trim().equalsIgnoreCase(step.getTitle())) {
            if (repository.existsBySurveyIdAndTitleIgnoreCaseAndIdNot(surveyId, request.getTitle().trim(), id)) {
                throw new BadRequestException("Step with title '" + request.getTitle() + "' already exists in this survey");
            }
        }

        if (request.getOrderIndex() != null && !request.getOrderIndex().equals(step.getOrderIndex())) {
            if (repository.existsBySurveyIdAndOrderIndexAndIdNot(surveyId, request.getOrderIndex(), id)) {
                throw new BadRequestException("Step with order index " + request.getOrderIndex() + " already exists in this survey");
            }
        }

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
            throw new ResourceNotFoundException("Survey not found with id: " + surveyId);
        }
        return repository.findBySurveyIdOrderByOrderIndexAsc(surveyId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Step not found with id: " + id);
        }

        repository.deleteById(id);
    }

    @Override
    @Transactional
    public void reorderSteps(Long surveyId, List<Long> stepIds) {

        List<Step> steps =
                repository.findBySurveyIdOrderByOrderIndexAsc(surveyId);

        if (steps.size() != stepIds.size()) {
            throw new IllegalArgumentException(
                    "The provided step list does not match the survey steps."
            );
        }

        Map<Long, Step> stepMap = steps.stream()
                .collect(Collectors.toMap(
                        Step::getId,
                        step -> step
                ));

        for (int i = 0; i < stepIds.size(); i++) {

            Long stepId = stepIds.get(i);

            Step step = stepMap.get(stepId);

            if (step == null) {
                throw new ResourceNotFoundException(
                        "Step with id " + stepId +
                                " does not belong to survey " + surveyId
                );
            }

            step.setOrderIndex((long) (i + 1));
        }

        repository.saveAll(steps);
    }

    private Step fetchStep(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Step not found with id: " + id));
    }
}