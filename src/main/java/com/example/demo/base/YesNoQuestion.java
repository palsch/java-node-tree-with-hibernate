package com.example.demo.base;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

@MappedSuperclass
public abstract class YesNoQuestion<TAnswer extends Node<?>> extends Question<TAnswer> {

    private Boolean yesNo;

    /**
     * Create a new child node if the answer is "yes"
     * <p>
     * Override this method if you want to create the answer on "no"
     */
    protected boolean addAnswersOnYes() {
        return true;
    }

    /**
     * Removing child nodes is allowed for YesNoQuestions
     */
    @Override
    protected boolean isRemoveChildNodesAllowed() {
        return true;
    }

    /**
     * Remove last child node is not allowed if the yes/no answer is the same as the addAnswersOnYes
     */
    @Override
    protected boolean isRemoveLastChildNodeAllowed() {
        return !(getYesNo() == addAnswersOnYes());
    }

    @Override
    protected String updateQuestion(Question<TAnswer> question) {
        assertIsYesNoQuestion(question);

        YesNoQuestion<TAnswer> updatedQuestion = (YesNoQuestion<TAnswer>) question;

        // update yes/no
        this.setYesNo(updatedQuestion.getYesNo());

        return handleYesNoChange();
    }

    /**
     * Add or remove answers based on the yes/no answer
     */
    private String handleYesNoChange() {
        if (this.yesNo == addAnswersOnYes()) {
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

    private void assertIsYesNoQuestion(Question<TAnswer> question) {
        if (!(question instanceof YesNoQuestion<?>)) {
            throw new IllegalArgumentException("Not a YesNoQuestion");
        }
    }

}
