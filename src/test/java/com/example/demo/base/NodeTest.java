package com.example.demo.base;

import jakarta.persistence.DiscriminatorValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class NodeTest {

    private NodeImpl node;
    private NodeImpl childNode;

    @BeforeEach
    void setUp() {
        node = new NodeImpl();
        childNode = new NodeImpl();
    }

    @Test
    void testAddNewChildNode() {
        // Use case: Add a new child node
        Optional<NodeImpl> newChildNode = node.addNewChildNode();
        assertTrue(newChildNode.isPresent());
        assertEquals(1, node.getChildNodes().size());
    }

    @Test
    void testRemoveNodeById() {
        // Use case: Remove a child node by ID
        node.addNode(childNode);
        UUID childId = childNode.getId();
        assertTrue(node.removeNodeById(childId));
        assertEquals(0, node.getChildNodes().size());
    }

    @Test
    void testRemoveNodeById_NotFound() {
        // Use case: Attempt to remove a non-existent child node by ID
        UUID nonExistentId = UUID.randomUUID();
        assertFalse(node.removeNodeById(nonExistentId));
    }

    @Test
    void testFindNodeById() {
        // Use case: Find a node by ID
        node.addNode(childNode);
        UUID childId = childNode.getId();
        Optional<Node<?>> foundNode = node.findNodeById(childId);
        assertTrue(foundNode.isPresent());
        assertEquals(childNode, foundNode.get());
    }

    @Test
    void testFindNodeById_NotFound() {
        // Use case: Attempt to find a non-existent node by ID
        UUID nonExistentId = UUID.randomUUID();
        Optional<Node<?>> foundNode = node.findNodeById(nonExistentId);
        assertFalse(foundNode.isPresent());
    }

    @Test
    void testRemoveNodes() {
        // Use case: Remove all child nodes
        node.addNode(childNode);
        node.removeNodes();
        assertEquals(0, node.getChildNodes().size());
    }

    @ParameterizedTest
    @CsvSource({
        "true, true",
        "false, false"
    })
    void testIsRemoveLastChildNodeAllowed(boolean isAllowed, boolean expected) {
        // Use case: Check if removing the last child node is allowed
        node.setRemoveLastChildNodeAllowed(isAllowed);
        assertEquals(expected, node.isRemoveLastChildNodeAllowed());
    }

    @ParameterizedTest
    @CsvSource({
        "true, true",
        "false, false"
    })
    void testIsRemoveChildNodesAllowed(boolean isAllowed, boolean expected) {
        // Use case: Check if removing child nodes is allowed
        node.setRemoveChildNodesAllowed(isAllowed);
        assertEquals(expected, node.isRemoveChildNodesAllowed());
    }

    // Mock implementation of Node for testing purposes
    @DiscriminatorValue("node")
    static class NodeImpl extends Node<NodeImpl> {

        public NodeImpl() {
            super();
            setId(UUID.randomUUID());
        }

        @Override
        protected String updateNode(Node<?> node) {
            return "Node updated";
        }

        @Override
        public Optional<NodeImpl> createNewChildNode() {
            return Optional.of(new NodeImpl());
        }

        private boolean removeLastChildNodeAllowed = true;
        private boolean removeChildNodesAllowed = true;

        @Override
        protected boolean isRemoveLastChildNodeAllowed() {
            return removeLastChildNodeAllowed;
        }

        @Override
        protected boolean isRemoveChildNodesAllowed() {
            return removeChildNodesAllowed;
        }

        public void setRemoveLastChildNodeAllowed(boolean allowed) {
            this.removeLastChildNodeAllowed = allowed;
        }

        public void setRemoveChildNodesAllowed(boolean allowed) {
            this.removeChildNodesAllowed = allowed;
        }
    }
}
