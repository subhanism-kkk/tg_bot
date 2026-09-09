package az.ingress.tgbot.service;

import java.time.LocalDate;

public interface ExportService {
    byte[] exportResultsToExcel(Long surveyId, String status, LocalDate from, LocalDate to);
}