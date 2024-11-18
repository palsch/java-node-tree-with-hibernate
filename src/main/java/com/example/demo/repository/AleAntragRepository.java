package com.example.demo.repository;

import com.example.demo.AleAntrag;
import com.example.demo.base.Node;
import com.example.demo.controller.dto.AleAntragMetadataDto;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AleAntragRepository extends JpaRepository<AleAntrag, UUID> {

    @Query("SELECT new com.example.demo.controller.dto.AleAntragMetadataDto(a.id, a.metadaten.status) FROM AleAntrag a")
    List<AleAntragMetadataDto> findAllByOwnerUserId(String ownerUserId);


    @EntityGraph(value = "Node.childNodes", type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT n FROM Node n WHERE n.id = :id")
    Optional<Node<?>> findByIdWithChildNodes(UUID id);
}
