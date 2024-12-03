package com.example.demo.base;

import jakarta.persistence.DiscriminatorValue;
import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class NodeEntityTest {


    /**
     * Test remove node -> remove each child node one by one and trigger domain event for each removal
     * Test remove node with documents -> remove node and document uploads (trigger document upload removal domain event), all attachments removed
     *    for each attachment and document upload the on destroy was called and the domain event was triggered
     * Test PreRemove -> is triggered on remove node and trigger domain event for each removal
     * Test cascade remove child nodes -> if removing child nodes is not allowed
     * Test removing a child incl. the subtree -> removing child nodes is possible in this case on destroy -> check db, that nodes were removed
     * Test every removed child the destroy was triggered
     */

    /**
     * Initialization Tests:
     * Test that the id is initialized correctly.
     * Test that the dtype is set from the DiscriminatorValue annotation.
     * Test that the init method initializes child nodes.
     * <p>
     * Child Node Management Tests:
     * Test adding a child node.
     * Test removing a child node by ID.
     * Test removing a child node directly.
     * Test preventing the addition of child nodes when not allowed.
     * Test preventing the removal of child nodes when not allowed.
     * Test preventing the removal of the last child node when not allowed.
     * <p>
     * Parent Node Management Tests:
     * Test setting the parent node.
     * Test removing the parent node.
     * <p>
     * Update Tests:
     * Test updating the node itself.
     * Test updating child nodes.
     * <p>
     * Find Node Tests:
     * Test finding a node by ID.
     * Test finding a node recursively.
     * <p>
     * Equality and HashCode Tests:
     * Test equality of nodes.
     * Test hash code generation.
     * <p>
     * Utility Method Tests:
     * Test getting all child nodes recursively.
     * Test getting a read-only list of child nodes.
     * These use cases cover the main functionalities and constraints of the NodeEntity class.
     */


    private NodeEntityImpl node;
    private NodeEntityImpl childNode;

    @BeforeEach
    void setUp() {
        node = new NodeEntityImpl();
        node.init();
        childNode = new NodeEntityImpl();
        childNode.init();
    }


    // Initialization Tests
    @Test
    void testIdInitialization() {
        assertNotNull(node.getId());
    }

    @Test
    void testDtypeInitialization() {
        assertEquals("node", node.getDtype());
    }

    @Test
    void testInitMethod() {
        node.init();
        assertNotNull(node.getChildNodes());
    }

    // Child Node Management Tests
    @Test
    void testAddChildNode() {
        node.addChildNode(childNode);
        assertEquals(1, node.getChildNodes().size());
    }

    @Test
    void testRemoveChildNodeById() {
        node.addChildNode(childNode);
        UUID childId = childNode.getId();
        assertTrue(node.removeNodeById(childId));
        assertEquals(0, node.getChildNodes().size());
    }

    @Test
    void testRemoveChildNodeDirectly() {
        node.addChildNode(childNode);
        node.removeNode(childNode);
        assertEquals(0, node.getChildNodes().size());
    }

    @Test
    void testPreventAddChildNodeWhenNotAllowed() {
        node.setAddChildNodesAllowed(false);
        assertThrows(IllegalArgumentException.class, () -> node.addChildNode(childNode));
    }

    @Test
    void testPreventRemoveChildNodeWhenNotAllowed() {
        node.addChildNode(childNode);
        node.setRemoveChildNodesAllowed(false);
        assertThrows(IllegalArgumentException.class, () -> node.removeNode(childNode));
    }

    @Test
    void testPreventRemoveLastChildNodeWhenNotAllowed() {
        node.addChildNode(childNode);
        node.setRemoveLastChildNodeAllowed(false);
        assertThrows(IllegalArgumentException.class, () -> node.removeNode(childNode));
    }

    // Parent Node Management Tests
    @Test
    void testSetParentNode() {
        childNode.setParent(node);
        assertEquals(node, childNode.getParent());
    }

    @Test
    void testRemoveParentNode() {
        childNode.setParent(node);
        childNode.setParent(null);
        assertNull(childNode.getParent());
    }

    // Update Tests
    @Test
    void testUpdateNode() {
        NodeEntityImpl newNode = new NodeEntityImpl();
        node.update(newNode);
        assertEquals(newNode, node);
    }

    @Test
    void testUpdateChildNodes() {
        node.addChildNode(childNode);
        NodeEntityImpl newChildNode = new NodeEntityImpl();
        setNodeId(newChildNode, childNode.getId());
        node.updateChildNodes(newChildNode);
        assertEquals(1, node.getChildNodes().size());
    }

    // Find Node Tests
    @Test
    void testFindNodeById() {
        node.addChildNode(childNode);
        UUID childId = childNode.getId();
        Optional<NodeEntity<?>> foundNode = node.findNodeById(childId);
        assertTrue(foundNode.isPresent());
        assertEquals(childNode, foundNode.get());
    }

    @Test
    void testFindNodeById_NotFound() {
        UUID nonExistentId = UUID.randomUUID();
        Optional<NodeEntity<?>> foundNode = node.findNodeById(nonExistentId);
        assertFalse(foundNode.isPresent());
    }

    // Equality and HashCode Tests
    @Test
    void testEquality() {
        NodeEntityImpl anotherNode = new NodeEntityImpl();
        setNodeId(anotherNode, node.getId());
        assertEquals(node, anotherNode);
    }

    @Test
    void testHashCode() {
        NodeEntityImpl anotherNode = new NodeEntityImpl();
        setNodeId(anotherNode, node.getId());
        assertEquals(node.hashCode(), anotherNode.hashCode());
    }

    // Utility Method Tests
    @Test
    void testGetAllChildNodes() {
        node.addChildNode(childNode);
        assertEquals(1, node.getAllChildNodes().size());
    }

    @Test
    void testGetChildNodes() {
        node.addChildNode(childNode);
        assertEquals(1, node.getChildNodes().size());
    }


    // OLD TESTS

    @Test
    void testAddNewChildNode() {
        // Use case: Add a new child node
        Optional<NodeEntityImpl> newChildNode = node.addNewChildNode();
        assertTrue(newChildNode.isPresent());
        assertEquals(1, node.getChildNodes().size());
    }

    @Test
    void testRemoveNodeById() {
        // Use case: Remove a child node by ID
        node.addChildNode(childNode);
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
    void testRemoveChildNodes() {
        // Use case: Remove all child nodes
        node.addChildNode(childNode);
        node.removeChildNodes();
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

    // Mock implementation of NodeEntity for testing purposes
    @Setter
    @DiscriminatorValue("node")
    static class NodeEntityImpl extends NodeEntity<NodeEntityImpl> {

        public NodeEntityImpl() {
            super();
        }

        @Override
        protected String updateNode(NodeEntity<?> nodeEntity) {
            return "NodeEntity updated";
        }

        @Override
        public Optional<NodeEntityImpl> createNewChildNode() {
            return Optional.of(new NodeEntityImpl());
        }

        private boolean addChildNodesAllowed = true;
        private boolean removeLastChildNodeAllowed = true;
        private boolean removeChildNodesAllowed = true;

        @Override
        protected boolean isAddingChildNodesAllowed() {
            return addChildNodesAllowed;
        }

        @Override
        protected boolean isRemoveLastChildNodeAllowed() {
            return removeLastChildNodeAllowed;
        }

        @Override
        protected boolean isRemoveChildNodesAllowed() {
            return removeChildNodesAllowed;
        }

    }

    /**
     * use reflection to set the ID of the new child node
     */
    public static void setNodeId(NodeEntityImpl node, UUID id) {
        try {
            Field idField = NodeEntity.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(node, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
