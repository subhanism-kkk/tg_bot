package az.ingress.tgbot.service.impl;

import az.ingress.tgbot.dto.survey.SurveyCreateRequest;
import az.ingress.tgbot.dto.survey.SurveyResponse;
import az.ingress.tgbot.dto.survey.SurveyUpdateRequest;
import az.ingress.tgbot.entity.Survey;
import az.ingress.tgbot.exception.BadRequestException;
import az.ingress.tgbot.exception.ResourceAlreadyExistsException;
import az.ingress.tgbot.exception.ResourceNotFoundException;
import az.ingress.tgbot.mapper.SurveyMapper;
import az.ingress.tgbot.repository.SurveyRepository;
import az.ingress.tgbot.service.SurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SurveyServiceImpl implements SurveyService {

    private final SurveyRepository repository;
    private final SurveyMapper mapper;

    @Override
    @Transactional
    public SurveyResponse create(SurveyCreateRequest request) {
        String trimmedTitle = request.getTitle().trim();

        if (repository.existsByTitleIgnoreCase(trimmedTitle)) {
            throw new ResourceAlreadyExistsException(
                    "Survey with the title '" + trimmedTitle + "' already exists"
            );
        }

        Survey survey = mapper.toEntity(request);
        survey.setTitle(trimmedTitle);

        Survey saved = repository.save(survey);
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    public SurveyResponse update(Long id, SurveyUpdateRequest request) {
        Survey survey = fetchSurvey(id);

        mapper.updateEntity(survey, request);

        Survey updated = repository.save(survey);
        return mapper.toResponse(updated);
    }

    @Override
    public SurveyResponse getById(Long id) {
        return mapper.toResponse(fetchSurvey(id));
    }

    @Override
    public List<SurveyResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Survey survey = fetchSurvey(id);
        repository.delete(survey);
    }

    @Override
    @Transactional
    public void activate(Long id) {
        Survey survey = fetchSurvey(id);

        if (Boolean.TRUE.equals(survey.getIsActive())) {
            throw new BadRequestException("Survey is already active with id: " + id);
        }

        survey.setIsActive(true);
        repository.save(survey);
    }

    @Override
    @Transactional
    public void deactivate(Long id) {
        Survey survey = fetchSurvey(id);

        if (Boolean.FALSE.equals(survey.getIsActive())) {
            throw new BadRequestException("Survey is already inactive with id: " + id);
        }

        survey.setIsActive(false);
        repository.save(survey);
    }

    private Survey fetchSurvey(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Survey not found with id: " + id));
    }
}