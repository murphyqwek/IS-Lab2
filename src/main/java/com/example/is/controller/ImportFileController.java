package com.example.is.controller;

import com.example.is.service.FileImportService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/import")
public class ImportFileController {
    private final FileImportService fileImportService;

    public ImportFileController(FileImportService fileImportService) {
        this.fileImportService = fileImportService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void importFile(@RequestParam MultipartFile file) {
        fileImportService.importFile(file);
    }
}
