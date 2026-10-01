package com.example.lab1.controller;

import com.example.lab1.dto.request.VenueRequest;
import com.example.lab1.dto.response.VenueResponse;
import com.example.lab1.service.VenueService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/venues")
public class VenueController {

    private final VenueService service;

    public VenueController(VenueService service) {
        this.service = service;
    }

    @GetMapping
    public List<VenueResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public VenueResponse getById(@PathVariable Integer id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VenueResponse create(@Valid @RequestBody VenueRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public VenueResponse update(@PathVariable Integer id, @Valid @RequestBody VenueRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id, @RequestParam(required = false) Integer replacementId) {
        service.delete(id, replacementId);
    }
}
