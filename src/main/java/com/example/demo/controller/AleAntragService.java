package com.example.demo.controller;

import com.example.demo.AleAntrag;
import com.example.demo.base.repository.NodeEntityRepository;
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
        if (byIdWithChildNodes.isPresent()) {
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

    private void assertOwnerUserId(UUID antragId) {
        Optional<String> ownerUserIdByAntragId = aleAntragRepository.getOwnerUserIdByAntragId(antragId);
        ownerUserIdByAntragId.orElseThrow(() -> new IllegalArgumentException("Antrag not found"));

        if (!ownerUserId.equals(ownerUserIdByAntragId.get())) {
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
