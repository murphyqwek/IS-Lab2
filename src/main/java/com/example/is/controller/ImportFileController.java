package com.example.is.controller;

import com.example.is.service.FileImportService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
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
    public void importFile(@RequestParam MultipartFile file, Authentication authentication) {
        UserDetails user = (UserDetails) authentication.getPrincipal();
        fileImportService.importFile(file, user.getUsername());
    }
}
