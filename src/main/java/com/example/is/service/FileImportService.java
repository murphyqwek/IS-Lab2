package com.example.is.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileImportService {

    private final FileImportTransactionService transactionService;
    private final ImportHistoryService importHistoryService;

    public FileImportService(FileImportTransactionService transactionService, ImportHistoryService importHistoryService) {
        this.transactionService = transactionService;
        this.importHistoryService = importHistoryService;
    }

    public void importFile(MultipartFile file, String username) {
        try {
            transactionService.importFile(file, username);
        } catch (RuntimeException exception) {
            try {
                importHistoryService.saveFailed(username);
            } catch (RuntimeException historyException) {
                exception.addSuppressed(historyException);
            }

            throw exception;
        }
    }
}