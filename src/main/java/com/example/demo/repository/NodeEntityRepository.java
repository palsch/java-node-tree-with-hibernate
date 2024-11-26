package com.example.demo.repository;

import com.example.demo.base.NodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface NodeEntityRepository extends JpaRepository<NodeEntity<?>, UUID> {

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

    /**
     * Update the updated_at timestamp of the given node by id
     */
    @Modifying
    @Query(value = """
            UPDATE node
            SET updated_at = now()
            WHERE id = ?1
            """, nativeQuery = true)
    void updateNodeUpdatedAt(UUID id);
}
