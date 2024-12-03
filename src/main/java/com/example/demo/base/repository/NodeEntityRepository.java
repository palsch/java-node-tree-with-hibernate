package com.example.demo.base.repository;

import com.example.demo.base.NodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface NodeEntityRepository extends JpaRepository<NodeEntity<?>, UUID> {

    /**
     * Count the number of child nodes of the given node.
     */
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
     * Update the updated_at timestamp of the given node by id and the complete parent node tree to the root node.
     */
    @Modifying
    @Query(value = """
             WITH RECURSIVE parent_nodes AS (
                SELECT id, parent_id
                FROM node
                WHERE id = ?1
                UNION ALL
                SELECT n.id, n.parent_id
                FROM node n
                         INNER JOIN parent_nodes p ON p.parent_id = n.id
            )
            UPDATE node
            SET updated_at = now()
            WHERE id IN (SELECT id FROM parent_nodes);
            """, nativeQuery = true)
    void updateNodeUpdatedAt(UUID id);

    /**
     * Get the root node of the given node.
     */
    @Query(value = """
            WITH RECURSIVE parent_nodes AS (
                SELECT id, parent_id
                FROM node
                WHERE id = ?1
                UNION ALL
                SELECT n.id, n.parent_id
                FROM node n
                         INNER JOIN parent_nodes p ON p.parent_id = n.id
            )
            SELECT id
            FROM parent_nodes
            WHERE parent_id IS NULL;
            """, nativeQuery = true)
    UUID getRootNodeId(UUID id);
}
