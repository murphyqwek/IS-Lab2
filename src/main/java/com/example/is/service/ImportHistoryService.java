package com.example.is.service;

import com.example.is.entity.ImportHistory;
import com.example.is.entity.ImportHistoryStatus;
import com.example.is.repository.ImportHistoryRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;

@Service
public class ImportHistoryService {
    private ImportHistoryRepository importHistoryRepository;

    public ImportHistoryRepository getImportHistoryRepository() {
        return importHistoryRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveSuccess(String username, int countAdded) {
        ImportHistory importHistory = new ImportHistory();

        importHistory.setUsername(username);
        importHistory.setImportedObjectsCount(countAdded);
        importHistory.setStatus(ImportHistoryStatus.SUCCESS);

        importHistoryRepository.save(importHistory);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveFailed(String username) {
        ImportHistory importHistory = new ImportHistory();
        importHistory.setUsername(username);
        importHistory.setImportedObjectsCount(0);
        importHistory.setStatus(ImportHistoryStatus.FAILED);
        importHistoryRepository.save(importHistory);
    }
}
