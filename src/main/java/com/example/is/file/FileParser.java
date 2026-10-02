package com.example.is.file;

import com.example.is.dto.request.TicketRequest;
import com.example.is.exception.FileImportException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Component
public class FileParser {
    private final ObjectMapper objectMapper;

    public FileParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<TicketRequest> parseFile(MultipartFile file) {
        try {
            return objectMapper.readValue(file.getInputStream(), new TypeReference<List<TicketRequest>>() {});
        } catch (Exception e) {
            throw new FileImportException("Не удалось распарсить файл. Повторите попытку позже");
        }
    }
}
