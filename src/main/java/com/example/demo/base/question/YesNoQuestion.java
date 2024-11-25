package com.example.demo.base.question;

import com.example.demo.base.NodeEntity;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * Base class for yes/no questions with child nodes.
 * <p>
 * A yes/no question is a question that has a yes/no answer and can have child nodes.
 * This question contains only the yes/no attribute (no more attributes are allowed).
 * The child nodes should then contain the other attributes.
 * <p>
 * The child nodes are created based on the yes/no answer.
 * If the answer is "yes" a new child node is created.
 * If the answer is "no" the child nodes are removed.
 * The child nodes are created by the method {@link #createNewChildNode()}.
 * The add or remove of child nodes can be controlled by the method {@link #addChildNodeOnYes()}.
 * At least one child node must be created if the answer is "yes". This child can't be removed.
 * <p>
 * The child nodes can be any subclass of {@link NodeEntity}.
 *
 * @param <TAnswer>
 */
@Getter
@Setter(AccessLevel.PROTECTED) // yesNo setter is protected, it should not be set directly from outside

@MappedSuperclass
public abstract class YesNoQuestion<TAnswer extends NodeEntity<?>> extends NodeEntity<TAnswer> {

    private Boolean yesNo;

    /**
     * Create a new child node if the answer is "yes"
     * <p>
     * Override this method if you want to create the answer on "no"
     */
    protected boolean addChildNodeOnYes() {
        return true;
    }

    /**
     * Allow adding child nodes if the yes/no answer is the same as the addAnswersOnYes
     */
    @Override
    protected boolean isAddingChildNodesAllowed() {
        return getYesNo() != null && getYesNo() == addChildNodeOnYes();
    }

    /**
     * Removing child nodes is allowed for YesNoQuestions
     */
    @Override
    protected final boolean isRemoveChildNodesAllowed() {
        return true;
    }

    /**
     * Remove last child node is not allowed if the yes/no answer is the same as the addAnswersOnYes
     */
    @Override
    protected final boolean isRemoveLastChildNodeAllowed() {
        return getYesNo() != null && !(getYesNo() == addChildNodeOnYes());
    }

    /**
     * Sets the yes/no answer and updates the child nodes accordingly
     *
     * @param node the nodeEntity to update
     * @return change log // TODO: change log return type
     * @see #addChildNodeOnYes()
     */
    @Override
    protected final String updateNode(NodeEntity<?> node) {
        assertIsYesNoQuestion(node);

        //noinspection unchecked
        YesNoQuestion<TAnswer> newDataNode = (YesNoQuestion<TAnswer>) node;

        // update yes/no
        this.setYesNo(newDataNode.getYesNo());

        return handleYesNoChange();
    }

    /**
     * Add or remove answers based on the yes/no answer
     *
     * @return change log // TODO: change log return type
     */
    private String handleYesNoChange() {
        if (this.yesNo == addChildNodeOnYes()) {
            if (this.getChildNodes().isEmpty()) {
                TAnswer newChildNode = this.createNewChildNode().orElseThrow(() -> new IllegalArgumentException("YesNotQuestion must have a child node: createNewChildNode not correctly implemented"));
                addNode(newChildNode);
                return "added first answer";
            }
        } else {
            removeNodes();
            return "removed answers";
        }
        return "no change";
    }

    private void assertIsYesNoQuestion(NodeEntity<?> question) {
        if (!(question instanceof YesNoQuestion<?>)) {
            throw new IllegalArgumentException("Not a YesNoQuestion");
        }
    }
}
