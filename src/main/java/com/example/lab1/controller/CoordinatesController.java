package com.example.lab1.controller;

import com.example.lab1.dto.request.CoordinatesRequest;
import com.example.lab1.dto.response.CoordinatesResponse;
import com.example.lab1.service.CoordinatesService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coordinates")
public class CoordinatesController {

    private final CoordinatesService service;

    public CoordinatesController(CoordinatesService service) {
        this.service = service;
    }

    @GetMapping
    public List<CoordinatesResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public CoordinatesResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CoordinatesResponse create(@Valid @RequestBody CoordinatesRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public CoordinatesResponse update(@PathVariable Long id, @Valid @RequestBody CoordinatesRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, @RequestParam(required = false) Long replacementId) {
        service.delete(id, replacementId);
    }
}
