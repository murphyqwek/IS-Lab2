package com.example.is.mapper;

import com.example.is.dto.response.ImportHistoryResponse;
import com.example.is.entity.ImportHistory;
import org.springframework.stereotype.Component;

@Component
public class ImportHistoryMapper {
    public ImportHistoryResponse mapToResponse(ImportHistory history) {
        return new ImportHistoryResponse(
                history.getId(),
                history.getUsername(),
                history.getImportedObjectsCount(),
                history.getCreatedAt(),
                history.getStatus()
        );
    }
}
