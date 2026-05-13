/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globalisosecurity.backend.controllers;

import com.globalisosecurity.backend.dto.ReporteResumenDTO;
import com.globalisosecurity.backend.services.ReporteService;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    @GetMapping("/empresa/{empresaId}")
    public ReporteResumenDTO obtenerReportePorEmpresa(@PathVariable Long empresaId) {
        return reporteService.obtenerReportePorEmpresa(empresaId);
    }

    @GetMapping("/empresa/{empresaId}/excel")
    public ResponseEntity<byte[]> exportarExcel(@PathVariable Long empresaId) {
        try {
            ReporteResumenDTO reporte = reporteService.obtenerReportePorEmpresa(empresaId);

            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Reporte");

            int rowNum = 0;

            Row row0 = sheet.createRow(rowNum++);
            row0.createCell(0).setCellValue("Empresa ID");
            row0.createCell(1).setCellValue(reporte.getEmpresaId());

            Row row1 = sheet.createRow(rowNum++);
            row1.createCell(0).setCellValue("Total Servicios");
            row1.createCell(1).setCellValue(reporte.getTotalServicios());

            Row row2 = sheet.createRow(rowNum++);
            row2.createCell(0).setCellValue("Total Evaluaciones");
            row2.createCell(1).setCellValue(reporte.getTotalEvaluaciones());

            Row row3 = sheet.createRow(rowNum++);
            row3.createCell(0).setCellValue("Total Firmas");
            row3.createCell(1).setCellValue(reporte.getTotalFirmas());

            Row row4 = sheet.createRow(rowNum++);
            row4.createCell(0).setCellValue("Total Capacitaciones");
            row4.createCell(1).setCellValue(reporte.getTotalCapacitaciones());

            rowNum++;
            Row headerEstados = sheet.createRow(rowNum++);
            headerEstados.createCell(0).setCellValue("Estados de Servicios");

            for (String estado : reporte.getEstadosServicios()) {
                Row rowEstado = sheet.createRow(rowNum++);
                rowEstado.createCell(0).setCellValue(estado);
            }

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            workbook.close();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            ));
            headers.setContentDisposition(
                    ContentDisposition.attachment()
                            .filename("reporte_empresa_" + empresaId + ".xlsx")
                            .build()
            );

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(out.toByteArray());

        } catch (Exception e) {
            throw new RuntimeException("Error al generar el Excel", e);
        }
    }

    @GetMapping("/empresa/{empresaId}/pdf")
    public ResponseEntity<byte[]> exportarPdf(@PathVariable Long empresaId) {
        try {
            ReporteResumenDTO reporte = reporteService.obtenerReportePorEmpresa(empresaId);

            Document document = new Document();
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            PdfWriter.getInstance(document, out);
            document.open();

            document.add(new Paragraph("Reporte de Empresa"));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Empresa ID: " + reporte.getEmpresaId()));
            document.add(new Paragraph("Total Servicios: " + reporte.getTotalServicios()));
            document.add(new Paragraph("Total Evaluaciones: " + reporte.getTotalEvaluaciones()));
            document.add(new Paragraph("Total Firmas: " + reporte.getTotalFirmas()));
            document.add(new Paragraph("Total Capacitaciones: " + reporte.getTotalCapacitaciones()));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Estados de Servicios:"));
            for (String estado : reporte.getEstadosServicios()) {
                document.add(new Paragraph("- " + estado));
            }

            document.close();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(
                    ContentDisposition.attachment()
                            .filename("reporte_empresa_" + empresaId + ".pdf")
                            .build()
            );

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(out.toByteArray());

        } catch (Exception e) {
            throw new RuntimeException("Error al generar el PDF", e);
        }
    }
}