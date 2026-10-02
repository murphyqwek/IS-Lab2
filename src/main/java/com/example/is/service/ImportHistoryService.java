package com.example.is.service;

import com.example.is.dto.history.ImportHistoryDTO;
import com.example.is.dto.response.CursorPage;
import com.example.is.dto.response.ImportHistoryResponse;
import com.example.is.entity.ImportHistory;
import com.example.is.entity.ImportHistoryStatus;
import com.example.is.entity.UserRole;
import com.example.is.mapper.ImportHistoryMapper;
import com.example.is.repository.ImportHistoryRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;

import java.util.List;
import java.util.function.Supplier;

@Service
public class ImportHistoryService {
    private final ImportHistoryRepository importHistoryRepository;
    private final ImportHistoryMapper mapper;

    public ImportHistoryService(ImportHistoryRepository importHistoryRepository, ImportHistoryMapper mapper) {
        this.importHistoryRepository = importHistoryRepository;
        this.mapper = mapper;
    }

    private List<ImportHistory> getPageUser(ImportHistoryDTO dto, Pageable pageable) {
        List<ImportHistory> histories;

        if (dto.cursorCreatedAt() == null && dto.cursorId() == null) {
            histories = importHistoryRepository.findFirstPageUser(dto.username(), pageable);
        }
        else {
            histories = importHistoryRepository.findNextPageUser(
                    dto.cursorCreatedAt(),
                    dto.cursorId(),
                    dto.username(),
                    pageable
            );
        }

        return histories;
    }

    private List<ImportHistory> getPageAdmin(ImportHistoryDTO dto, Pageable pageable) {
        List<ImportHistory> histories;

        if (dto.cursorCreatedAt() == null && dto.cursorId() == null) {
            histories = importHistoryRepository.findFirstPage(pageable);
        }
        else {
            histories = importHistoryRepository.findNextPage(
                    dto.cursorCreatedAt(),
                    dto.cursorId(),
                    pageable
            );
        }

        return histories;
    }

    public CursorPage<ImportHistoryResponse> getHistory(ImportHistoryDTO dto) {
        Pageable pageable = PageRequest.of(0, dto.size() + 1);

        List<ImportHistory> histories;

        if(dto.role() == UserRole.ADMIN) {
            histories = getPageAdmin(dto, pageable);
        }
        else {
            histories = getPageUser(dto, pageable);
        }

        boolean hasNext = histories.size() > dto.size();

        if (hasNext) {
            histories = histories.subList(0, dto.size());
        }

        List<ImportHistoryResponse> content = histories.stream().map(mapper::mapToResponse).toList();

        if (histories.isEmpty()) {
            return new CursorPage<>(content, null, null, false);
        }

        ImportHistory last = histories.get(histories.size() - 1);

        return new CursorPage<>(
                content,
                hasNext ? last.getId() : null,
                hasNext ? last.getCreatedAt() : null,
                hasNext
        );
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
