package com.example.demo.base.service;

import com.example.demo.base.NodeEntity;
import com.example.demo.base.repository.NodeEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service implementation of the CRUD operations for the node entity.
 * <p>
 * <b>IMPORTANT</b>:
 * <li>No security checks are implemented in this class.
 * Do all required security checks before calling the methods of this class. </li>
 * <li>No editable checks are implemented in this class.
 * Do all required editable checks before calling the methods of this class. </li>
 */
@RequiredArgsConstructor
@Slf4j

@Service
public class NodeService {

    private final NodeEntityRepository nodeEntityRepository;

    /**
     * Add a new child node to the given node.
     */
    @Transactional
    public NodeEntity<?> addChildNode(UUID nodeId) {
        updateNodeUpdatedAt(nodeId);

        NodeEntity<?> nodeEntity = nodeEntityRepository.findById(nodeId).orElseThrow();
        NodeEntity<?> newChildNodeEntity = nodeEntity.addNewChildNode().orElseThrow();

        // save the new child node first, so the @PrePersist method lifecycle can create the child nodes
        newChildNodeEntity = nodeEntityRepository.save(newChildNodeEntity);

        // save all child nodes explicitly, because Hibernate does not save them automatically if they were created inside the @PrePersist method lifecycle
        nodeEntityRepository.saveAll(newChildNodeEntity.getAllChildNodes());
        return newChildNodeEntity;
    }

    /**
     * Update the node with the given node entity.
     * If child nodes exists, they are updated as well.
     * <p>
     * Unknown child nodes are ignored.
     * Missing child nodes are ignored.
     */
    @Transactional
    public NodeEntity<?> updateNode(UUID nodeId, NodeEntity<?> updateNodeEntity) {
        updateNodeUpdatedAt(nodeId);

        NodeEntity<?> nodeEntityToUpdate = nodeEntityRepository.findById(updateNodeEntity.getId()).orElseThrow();
        nodeEntityToUpdate.update(updateNodeEntity);

        // save all child nodes first explicitly, so they are not remove by Hibernate from the parent node on save,
        // because Hibernate does not save them automatically if they were created inside the @PrePersist method lifecycle
        nodeEntityRepository.saveAll(nodeEntityToUpdate.getAllChildNodes());
        // save the updated node
        nodeEntityRepository.save(nodeEntityToUpdate);
        return nodeEntityToUpdate;
    }

    /**
     * Delete the given node if possible / allowed
     */
    @Transactional
    public void deleteNode(UUID nodeId) {
        updateNodeUpdatedAt(nodeId);

        NodeEntity<?> nodeToDelete = nodeEntityRepository.findById(nodeId).orElseThrow();
        if (nodeToDelete.isRootNode()) {
            nodeEntityRepository.delete(nodeToDelete);
            // TODO: domain event for node deleted
            log.debug("Root Node deleted: {}", nodeToDelete.getId());
        } else {
            NodeEntity<?> parent = nodeToDelete.getParent();
            parent.removeNode(nodeToDelete);
            nodeEntityRepository.save(nodeToDelete);
        }
    }

    private void updateNodeUpdatedAt(UUID nodeId) {
        nodeEntityRepository.updateNodeUpdatedAt(nodeId);
    }
}
