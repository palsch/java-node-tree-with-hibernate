package com.example.demo.controller;

import com.example.demo.AleAntrag;
import com.example.demo.base.controller.NodeController;
import com.example.demo.base.service.NodeService;
import com.example.demo.controller.dto.AleAntragMetadataDto;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/ale_antrag")
public class AleAntragController {

    private final AleAntragService aleAntragService;

    public AleAntragController(AleAntragService aleAntragService) {
        this.aleAntragService = aleAntragService;
    }

    // Overview
    @GetMapping
    public ResponseEntity<Iterable<AleAntragMetadataDto>> getAleAntrags() {
        return ResponseEntity.ok(aleAntragService.getAleAntrags());
    }

    // ALE Antrag CRUD
    @PostMapping
    public ResponseEntity<AleAntrag> createAleAntrag() {
        return ResponseEntity.ok(aleAntragService.createAleAntrag());
    }

    // TODO: Transactional is required to avoid LazyInitializationException - is this still correct?
    @Transactional(readOnly = true)
    @GetMapping("/{id}")
    public ResponseEntity<AleAntrag> getAleAntrag(@PathVariable UUID id) {
        return aleAntragService.getAleAntrag(id).map(aleAntrag -> ResponseEntity.ok(aleAntrag)).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAleAntrag(@PathVariable UUID id) {
        aleAntragService.deleteAleAntrag(id);
        return ResponseEntity.noContent().build();
    }
}
