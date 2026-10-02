package com.example.is.controller;

import com.example.is.dto.history.ImportHistoryDTO;
import com.example.is.dto.response.CursorPage;
import com.example.is.dto.response.ImportHistoryResponse;
import com.example.is.entity.UserRole;
import com.example.is.service.AuthHelper;
import com.example.is.service.ImportHistoryService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/history")
public class ImportHistoryController {
    private final ImportHistoryService importHistoryService;
    private final AuthHelper authHelper;

    public ImportHistoryController(ImportHistoryService importHistoryService, AuthHelper authHelper) {
        this.importHistoryService = importHistoryService;
        this.authHelper = authHelper;
    }

    @GetMapping
    public CursorPage<ImportHistoryResponse> getHistory(
            @RequestParam(required = false) Instant cursorCreatedAt,
            @RequestParam(required = false) Long cursorId,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication
    ) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        UserRole role = authHelper.getUserRole(userDetails);


        ImportHistoryDTO dto = new ImportHistoryDTO(
                cursorCreatedAt,
                cursorId,
                size,
                userDetails.getUsername(),
                role
        );

        return importHistoryService.getHistory(dto);
    }
}
