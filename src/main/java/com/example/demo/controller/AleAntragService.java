package com.example.demo.controller;

import com.example.demo.AleAntrag;
import com.example.demo.base.NodeEntity;
import com.example.demo.controller.dto.AleAntragMetadataDto;
import com.example.demo.repository.AleAntragRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        List<AleAntragMetadataDto> antragList = aleAntragRepository.findAllByOwnerUserId(ownerUserId);
        return antragList.stream().map(aleAntragMetadataDto -> aleAntragMetadataDto.withNodeCount(aleAntragRepository.countChildNodes(aleAntragMetadataDto.antragId()))).toList();
    }

    // ALE Antrag CRUD
    @Transactional
    public AleAntrag createAleAntrag() {
        return aleAntragRepository.save(new AleAntrag());
    }

    // Transactional is required to avoid LazyInitializationException
    @Transactional(readOnly = true)
    public Optional<AleAntrag> getAleAntrag(UUID id) {
        Optional<AleAntrag> byIdWithChildNodes = aleAntragRepository.findById(id);
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
    public NodeEntity<?> addAleAntragNode(UUID antragId, UUID parentId) {
        AleAntrag aleAntrag = aleAntragRepository.findById(antragId).orElseThrow();
        NodeEntity<?> question = aleAntrag.findNodeById(parentId).orElseThrow();
        NodeEntity<?> newChildNodeEntity = question.addNewChildNode().orElseThrow();
        aleAntragRepository.saveAndFlush(aleAntrag);
        return newChildNodeEntity;
    }

    // update any node from the antrag
    @Transactional
    public NodeEntity<?> updateChildNode(UUID antragId, NodeEntity<?> updateNodeEntity) {
        AleAntrag aleAntrag = aleAntragRepository.findById(antragId).orElseThrow();
        NodeEntity<?> nodeEntityToUpdate = aleAntrag.findNode(updateNodeEntity).orElseThrow();
        nodeEntityToUpdate.update(updateNodeEntity);
        aleAntragRepository.save(aleAntrag);
        return nodeEntityToUpdate;
    }

    // delete any node by id from the antrag
    @Transactional
    public void deleteChildNode(UUID antragId, UUID childId) {
        AleAntrag aleAntrag = aleAntragRepository.findById(antragId).orElseThrow();
        aleAntrag.removeNodeById(childId);
        aleAntragRepository.save(aleAntrag);
    }
}
