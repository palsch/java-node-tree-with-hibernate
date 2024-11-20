package com.example.demo.controller;

import com.example.demo.AleAntrag;
import com.example.demo.base.NodeEntity;
import com.example.demo.controller.dto.AleAntragMetadataDto;
import com.example.demo.repository.AleAntragRepository;
import com.example.demo.repository.NodeEntityRepository;
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

    @Autowired
    private NodeEntityRepository nodeEntityRepository;

    // Overview
    public Iterable<AleAntragMetadataDto> getAleAntrags() {
        List<AleAntragMetadataDto> antragList = aleAntragRepository.findAllByOwnerUserId(ownerUserId);
        return antragList.stream().map(aleAntragMetadataDto -> aleAntragMetadataDto.withNodeCount(nodeEntityRepository.countChildNodes(aleAntragMetadataDto.antragId()))).toList();
    }

    // ALE Antrag CRUD
    @Transactional
    public AleAntrag createAleAntrag() {
        return aleAntragRepository.save(new AleAntrag());
    }

    // Transactional is required to avoid LazyInitializationException
    @Transactional(readOnly = true)
    public Optional<AleAntrag> getAleAntrag(UUID id) {
        assertOwnerUserId(id);

        Optional<AleAntrag> byIdWithChildNodes = aleAntragRepository.findById(id);
        if(byIdWithChildNodes.isPresent()) {
            AleAntrag aleAntrag = (AleAntrag) byIdWithChildNodes.get();
            return Optional.of(aleAntrag);
        }
        return Optional.empty();
    }

    public void deleteAleAntrag(UUID id) {
        assertOwnerUserId(id);
        assertEditable(id);

        aleAntragRepository.deleteById(id);
    }

    // generic node crud endpoints

    // add a new node
    @Transactional
    public NodeEntity<?> addAleAntragNode(UUID antragId, UUID parentId) {
        assertOwnerUserId(antragId);
        assertEditable(antragId);
        nodeEntityRepository.updateNodeUpdatedAt(antragId);

        NodeEntity<?> nodeEntity = nodeEntityRepository.findById(parentId).orElseThrow();
        NodeEntity<?> newChildNodeEntity = nodeEntity.addNewChildNode().orElseThrow();
        nodeEntityRepository.save(newChildNodeEntity);
        return newChildNodeEntity;
    }

    // update any node from the antrag
    @Transactional
    public NodeEntity<?> updateChildNode(UUID antragId, NodeEntity<?> updateNodeEntity) {
        assertOwnerUserId(antragId);
        assertEditable(antragId);
        nodeEntityRepository.updateNodeUpdatedAt(antragId);

        AleAntrag aleAntrag = aleAntragRepository.findById(antragId).orElseThrow();
        NodeEntity<?> nodeEntityToUpdate = aleAntrag.findNode(updateNodeEntity).orElseThrow();
        nodeEntityToUpdate.update(updateNodeEntity);
        aleAntragRepository.save(aleAntrag);
        return nodeEntityToUpdate;
    }

    // delete any node by id from the antrag
    @Transactional
    public void deleteChildNode(UUID antragId, UUID childId) {
        assertOwnerUserId(antragId);
        assertEditable(antragId);
        nodeEntityRepository.updateNodeUpdatedAt(antragId);

        AleAntrag aleAntrag = aleAntragRepository.findById(antragId).orElseThrow();
        aleAntrag.removeNodeById(childId);
        aleAntragRepository.save(aleAntrag);
    }

    private void assertOwnerUserId(UUID antragId) {
        String ownerUserIdByAntragId = aleAntragRepository.getOwnerUserIdByAntragId(antragId);
        if (!ownerUserIdByAntragId.equals(ownerUserId)) {
            throw new IllegalArgumentException("OwnerUserId does not match");
        }
    }

    private void assertEditable(UUID antragId) {
        String status = aleAntragRepository.getStatusByAntragId(antragId);
        if (!status.equals("DRAFT")) {
            throw new IllegalArgumentException("Is not editable");
        }
    }
}
