package com.example.demo.base.controller;

import com.example.demo.base.NodeEntity;
import com.example.demo.base.service.NodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Controller implements the CRUD operations for the node entity.
 */
@RestController
@RequestMapping("/api/node")
@RequiredArgsConstructor
public class NodeController {

    private final NodeService nodeService;

    /**
     * Verify that the user is authorized to access the node
     */
    protected void verifyAuthorization(UUID nodeId) {
        // TODO: implement
    }

    /**
     * Verify that the node is editable
     */
    protected void verifyEditable(UUID nodeId) {
        // TODO: implement
    }

    // add a new node
    @PostMapping("/{nodeId}")
    public ResponseEntity<NodeEntity<?>> addChildNode(@PathVariable UUID nodeId) {
        verifyAuthorization(nodeId);
        verifyEditable(nodeId);

        return ResponseEntity.ok(nodeService.addChildNode(nodeId));
    }

    // update any node from the antrag
    @PatchMapping("/{nodeId}")
    public ResponseEntity<NodeEntity<?>> updateNode(@PathVariable UUID nodeId, @RequestBody NodeEntity<?> updateNodeEntity) {
        verifyAuthorization(nodeId);
        verifyEditable(nodeId);

        return ResponseEntity.ok(nodeService.updateNode(nodeId, updateNodeEntity));
    }

    // delete any node by id from the antrag
    @DeleteMapping("/{nodeId}")
    public ResponseEntity<Void> deleteNode(@PathVariable UUID nodeId) {
        verifyAuthorization(nodeId);
        verifyEditable(nodeId);

        nodeService.deleteNode(nodeId);
        return ResponseEntity.noContent().build();
    }
}
