package com.example.demo.controller;

import com.example.demo.AleAntrag;
import com.example.demo.base.Node;
import com.example.demo.controller.dto.AleAntragMetadataDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/ale_antrag")
public class AleAntragController {

    @Autowired
    private AleAntragService aleAntragService;

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

    // Transactional is required to avoid LazyInitializationException
    @Transactional(readOnly = true)
    @GetMapping("/{id}")
    public ResponseEntity<AleAntrag> getAleAntrag(@PathVariable UUID id) {
        return aleAntragService.getAleAntrag(id).map(aleAntrag -> ResponseEntity.ok(aleAntrag)).orElse(ResponseEntity.notFound().build());

//        Optional<AleAntrag> aleAntrag = aleAntragRepository.findByIdWithChildNodes(id);
//        return aleAntrag.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAleAntrag(@PathVariable UUID id) {
        aleAntragService.deleteAleAntrag(id);
        return ResponseEntity.noContent().build();
    }

    // generic node crud endpoints

    // add a new node
    @PostMapping("/{antragId}/{parentId}/child-nodes")
    public ResponseEntity<Node<?>> addAleAntragNode(@PathVariable UUID antragId, @PathVariable UUID parentId) {
        return ResponseEntity.ok(aleAntragService.addAleAntragNode(antragId, parentId));
    }

    // update any node from the antrag
    @PatchMapping("/{antragId}/child-nodes")
    public ResponseEntity<Node<?>> updateChildNode(@PathVariable UUID antragId, @RequestBody Node<?> updateNode) {
        return ResponseEntity.ok(aleAntragService.updateChildNode(antragId, updateNode));
    }

    // delete any node by id from the antrag
    @DeleteMapping("/{antragId}/child-nodes/{childId}")
    public ResponseEntity<Void> deleteChildNode(@PathVariable UUID antragId, @PathVariable UUID childId) {
        aleAntragService.deleteChildNode(antragId, childId);
        return ResponseEntity.noContent().build();
    }


}
