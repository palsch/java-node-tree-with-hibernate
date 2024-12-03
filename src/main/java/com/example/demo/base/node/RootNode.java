package com.example.demo.base.node;

import com.example.demo.base.NodeEntity;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

/**
 * Base class for root nodes
 */
@Getter
@Setter

@MappedSuperclass
public abstract class RootNode extends NodeEntity<NodeEntity<?>> {

    /**
     * No parent node for root nodes allowed.
     * <p>
     * Adding a root node as a child node is not allowed.
     */
    @Override
    final public boolean isRootNode() {
        return true;
    }
}
