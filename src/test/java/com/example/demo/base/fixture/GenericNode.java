package com.example.demo.base.fixture;

import com.example.demo.base.NodeEntity;
import jakarta.persistence.DiscriminatorValue;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Setter
@Getter
@DiscriminatorValue("generic-node")
public class GenericNode extends NodeEntity<NodeEntity<?>> {

    @Override
    protected String updateNode(NodeEntity<?> nodeEntity) {
        hasUpdateNodeBeenCalled = true;
        return "NodeEntity updated";
    }

    @Override
    protected Optional<NodeEntity<?>> createNewChildNode() {
        hasCreateNewChildNodeBeenCalled = true;
        return newChildNode == null ? Optional.empty() : Optional.of(newChildNode);
    }

    // Fixture
    private boolean root = false;
    private boolean addChildNodesAllowed = true;
    private boolean removeLastChildNodeAllowed = true;
    private boolean removeChildNodesAllowed = true;
    private NodeEntity<?> newChildNode = null;

    @Override
    public boolean isRootNode() {
        hasIsRootNodeBeenCalled = true;
        return root;
    }

    @Override
    protected boolean isAddingChildNodesAllowed() {
        hasIsAddingChildNodesAllowedBeenCalled = true;
        return addChildNodesAllowed;
    }

    @Override
    protected boolean isRemoveLastChildNodeAllowed() {
        hasIsRemoveLastChildNodeAllowedBeenCalled = true;
        return removeLastChildNodeAllowed;
    }

    @Override
    protected boolean isRemoveChildNodesAllowed() {
        hasIsRemoveChildNodesAllowedBeenCalled = true;
        return removeChildNodesAllowed;
    }

    // Status
    @Setter(lombok.AccessLevel.NONE)
    private boolean hasInitializeNodeBeenCalled = false;
    @Setter(lombok.AccessLevel.NONE)
    private boolean hasDestroyNodeBeenCalled = false;

    @Setter(lombok.AccessLevel.NONE)
    private boolean hasUpdateNodeBeenCalled = false;
    @Setter(lombok.AccessLevel.NONE)
    private boolean hasCreateNewChildNodeBeenCalled = false;
    @Setter(lombok.AccessLevel.NONE)
    private boolean hasIsRootNodeBeenCalled = false;
    @Setter(lombok.AccessLevel.NONE)
    private boolean hasIsAddingChildNodesAllowedBeenCalled = false;
    @Setter(lombok.AccessLevel.NONE)
    private boolean hasIsRemoveLastChildNodeAllowedBeenCalled = false;
    @Setter(lombok.AccessLevel.NONE)
    private boolean hasIsRemoveChildNodesAllowedBeenCalled = false;

    public void resetFixture() {
        hasInitializeNodeBeenCalled = false;
        hasDestroyNodeBeenCalled = false;
        hasUpdateNodeBeenCalled = false;
        hasCreateNewChildNodeBeenCalled = false;
        hasIsRootNodeBeenCalled = false;
        hasIsAddingChildNodesAllowedBeenCalled = false;
        hasIsRemoveLastChildNodeAllowedBeenCalled = false;
        hasIsRemoveChildNodesAllowedBeenCalled = false;
    }
}