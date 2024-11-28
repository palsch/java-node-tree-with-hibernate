package com.example.demo.repository;

import com.example.demo.AleAntrag;
import com.example.demo.controller.dto.AleAntragMetadataDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AleAntragRepository extends JpaRepository<AleAntrag, UUID> {

    @Query("SELECT a.metadaten.ownerUserId FROM AleAntrag a WHERE a.id = :id")
    Optional<String> getOwnerUserIdByAntragId(UUID id);

    @Query("SELECT a.metadaten.status FROM AleAntrag a WHERE a.id = :id")
    String getStatusByAntragId(UUID id);

    @Query("SELECT new com.example.demo.controller.dto.AleAntragMetadataDto(a.id, a.metadaten.status, a.updatedAt, 0) FROM AleAntrag a WHERE a.metadaten.ownerUserId = :ownerUserId" +
            " ORDER BY a.updatedAt DESC")
    List<AleAntragMetadataDto> findAllByOwnerUserId(String ownerUserId);

    @Query(value = """
            WITH RECURSIVE child_nodes AS (
                SELECT id, parent_id
                FROM node
                WHERE id = ?1
                UNION ALL
                SELECT n.id, n.parent_id
                FROM node n
                         INNER JOIN child_nodes c ON c.id = n.parent_id
            )
            SELECT COUNT(*)
            FROM child_nodes;
            """, nativeQuery = true)
    int countChildNodes(UUID id);

}
