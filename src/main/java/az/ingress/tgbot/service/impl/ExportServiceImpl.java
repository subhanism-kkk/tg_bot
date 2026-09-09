package az.ingress.tgbot.service.impl;

import az.ingress.tgbot.entity.UserAnswer;
import az.ingress.tgbot.repository.UserAnswerRepository;
import az.ingress.tgbot.service.ExportService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExportServiceImpl implements ExportService {

    private final UserAnswerRepository userAnswerRepository;

    @Override
    public byte[] exportResultsToExcel(Long surveyId, String status, LocalDate from, LocalDate to) {
        LocalDateTime startDateTime = (from != null) ? from.atStartOfDay() : null;
        LocalDateTime endDateTime = (to != null) ? to.atTime(LocalTime.MAX) : null;

        List<UserAnswer> answers = userAnswerRepository.findFilteredAnswers(
                surveyId, status, startDateTime, endDateTime
        );

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Survey Results");

            // Header Style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // Create Header Row
            Row headerRow = sheet.createRow(0);
            String[] headers = {
                    "Answer ID", "Telegram User ID", "Question Text",
                    "Text Answer", "Selected Option IDs", "Submitted At"
            };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Populate Data Rows
            int rowIndex = 1;
            for (UserAnswer answer : answers) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(answer.getId());
                row.createCell(1).setCellValue(
                        answer.getTelegramUser() != null ? answer.getTelegramUser().getId() : 0
                );
                row.createCell(2).setCellValue(
                        answer.getQuestion() != null ? answer.getQuestion().getText() : ""
                );
                row.createCell(3).setCellValue(
                        answer.getAnswerText() != null ? answer.getAnswerText() : ""
                );
                row.createCell(4).setCellValue(
                        answer.getSelectedOptionIds() != null ? answer.getSelectedOptionIds() : ""
                );
                row.createCell(5).setCellValue(
                        answer.getAnsweredAt() != null ? answer.getAnsweredAt().toString() : ""
                );
            }

            // Adjust column widths automatically
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Error occurred while exporting survey results to Excel", e);
        }
    }
}