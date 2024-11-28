package com.example.demo.base.question;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

/**
 * Base class for simple questions with documents.
 * <p>
 * A simple question is a question that does not have any sub-questions / sub-nodes.
 * It is a leaf node in the question tree.
 */
@Getter
@Setter

@MappedSuperclass
public abstract class SimpleNodeWithDocuments extends NodeWithDocuments<SimpleNodeWithDocuments> {

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
    protected final Optional<SimpleNodeWithDocuments> createNewChildNode() {
        return Optional.empty();
    }

}
