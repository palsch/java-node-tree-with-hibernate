package com.example.demo.controller;

import com.example.demo.AleAntrag;
import com.example.demo.base.Node;
import com.example.demo.controller.dto.AleAntragMetadataDto;
import com.example.demo.repository.AleAntragRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.demo.TestData.ownerUserId;

@Service
public class AleAntragService {

    @Autowired
    private AleAntragRepository aleAntragRepository;

    // Overview
    public Iterable<AleAntragMetadataDto> getAleAntrags() {
        return aleAntragRepository.findAllByOwnerUserId(ownerUserId);
    }

    // ALE Antrag CRUD
    @Transactional
    public AleAntrag createAleAntrag() {
        return aleAntragRepository.save(new AleAntrag());
    }

    // Transactional is required to avoid LazyInitializationException
    @Transactional(readOnly = true)
    public Optional<AleAntrag> getAleAntrag(UUID id) {
        Optional<Node<?>> byIdWithChildNodes = aleAntragRepository.findByIdWithChildNodes(id);
        if(byIdWithChildNodes.isPresent()) {
            AleAntrag aleAntrag = (AleAntrag) byIdWithChildNodes.get();
            return Optional.of(aleAntrag);
        }
        return Optional.empty();
    }

    public void deleteAleAntrag(UUID id) {
        aleAntragRepository.deleteById(id);
    }

    // generic node crud endpoints

    // add a new node
    @Transactional
    public Node<?> addAleAntragNode(UUID antragId, UUID parentId) {
        AleAntrag aleAntrag = aleAntragRepository.findById(antragId).orElseThrow();
        Node<?> question = aleAntrag.findNodeById(parentId).orElseThrow();
        Node<?> newChildNode = question.addNewChildNode().orElseThrow();
        aleAntragRepository.saveAndFlush(aleAntrag);
        return newChildNode;
    }

    // update any node from the antrag
    @Transactional
    public Node<?> updateChildNode(UUID antragId, Node<?> updateNode) {
        AleAntrag aleAntrag = aleAntragRepository.findById(antragId).orElseThrow();
        Node<?> nodeToUpdate = aleAntrag.findNode(updateNode).orElseThrow();
        nodeToUpdate.update(updateNode);
        aleAntragRepository.save(aleAntrag);
        return nodeToUpdate;
    }

    // delete any node by id from the antrag
    @Transactional
    public void deleteChildNode(UUID antragId, UUID childId) {
        AleAntrag aleAntrag = aleAntragRepository.findById(antragId).orElseThrow();
        aleAntrag.removeNodeById(childId);
        aleAntragRepository.save(aleAntrag);
    }
}
