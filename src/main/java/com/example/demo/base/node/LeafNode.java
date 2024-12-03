package com.example.demo.base.node;

import com.example.demo.base.NodeEntity;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

/**
 * Base class for simple questions.
 * <p>
 * A simple question is a question that does not have any sub-questions / sub-nodes.
 * It is a leaf node in the question tree.
 */
@Getter
@Setter

@MappedSuperclass
public abstract class LeafNode extends NodeEntity<LeafNode>  {

    /**
     * Simple questions do not have child nodes.
     *
     * @return false
     */
    protected final boolean isAddingChildNodesAllowed() {
        return false;
    }

    /**
     * Simple questions do not have child nodes.
     *
     * @return empty
     */
    @Override
    protected final Optional<LeafNode> createNewChildNode() {
        return Optional.empty();
    }

}
