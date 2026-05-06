package com.example.bloodbank.controller;

import com.example.bloodbank.dto.MedicalReportResponse;
import com.example.bloodbank.entity.MedicalReportSubmission;
import com.example.bloodbank.entity.User;
import com.example.bloodbank.service.MedicalReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Slf4j
public class MedicalReportController {

    private final MedicalReportService reportService;

    @PostMapping("/submit")
    public ResponseEntity<MedicalReportResponse> submitReport(
            @RequestParam("file") MultipartFile file,
            @RequestParam("diseaseId") Long diseaseId,
            @AuthenticationPrincipal User user) {
        try {
            log.info("Receiving medical report from Aadhaar: {} for diseaseId: {}", user.getUsername(), diseaseId);
            MedicalReportResponse response = reportService.submitReport(user.getUsername(), diseaseId, file);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            log.error("Failed to process file upload", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<List<MedicalReportResponse>> getPendingReports() {
        List<MedicalReportResponse> reports = reportService.getPendingReports();
        log.info("Returning {} pending reports to Hospital", reports.size());
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/me")
    public ResponseEntity<List<MedicalReportResponse>> getMyReports(@AuthenticationPrincipal User user) {
        List<MedicalReportResponse> reports = reportService.getPatientReports(user.getUsername());
        log.info("Returning {} reports for patient Aadhaar {}", reports.size(), user.getUsername());
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/next-disease")
    public ResponseEntity<com.example.bloodbank.dto.DiseaseDTO> getNextDisease(@AuthenticationPrincipal User user) {
        com.example.bloodbank.dto.DiseaseDTO nextDisease = reportService.getNextDiseaseToClaim(user.getUsername());
        if(nextDisease == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(nextDisease);
    }

    @GetMapping("/{id}/file")
    public ResponseEntity<byte[]> getReportFile(@PathVariable Long id, @RequestParam(required = false, defaultValue = "false") boolean download) {
        MedicalReportSubmission submission = reportService.getReportFile(id);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(submission.getFileType()));
        
        if (download) {
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"report_" + id + "\"");
        } else {
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"report_" + id + "\"");
        }

        return new ResponseEntity<>(submission.getFileData(), headers, HttpStatus.OK);
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<MedicalReportResponse> approveReport(@PathVariable Long id) {
        return ResponseEntity.ok(reportService.approveReport(id));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<MedicalReportResponse> rejectReport(@PathVariable Long id) {
        return ResponseEntity.ok(reportService.rejectReport(id));
    }
}
