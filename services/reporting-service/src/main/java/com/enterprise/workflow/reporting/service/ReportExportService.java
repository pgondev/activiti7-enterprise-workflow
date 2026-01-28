package com.enterprise.workflow.reporting.service;

import com.enterprise.workflow.reporting.dto.ReportingDTOs.ExportRequestDTO;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class ReportExportService {
    public Resource exportToExcel(ExportRequestDTO request) {
        return null; // Stub
    }
    public Resource exportToPdf(ExportRequestDTO request) {
        return null; // Stub
    }
    public Resource exportToCsv(ExportRequestDTO request) {
        return null; // Stub
    }
}
