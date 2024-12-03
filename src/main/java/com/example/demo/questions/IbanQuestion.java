package com.example.demo.questions;

import com.example.demo.base.NodeEntity;
import com.example.demo.base.node.LeafNode;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

/**
 * IbanQuestion
 */
@Getter
@Setter

@Entity
@DiscriminatorValue("iban_question")
public class IbanQuestion extends LeafNode {

    private String iban;

    /**
     * @param node the nodeEntity to update
     * @return change log // TODO: change log return type
     */
    @Override
    protected String updateNode(NodeEntity<?> node) {
        IbanQuestion question = assertIsIbanQuestion(node);

        // update iban
        setIban(question.getIban());

        return "iban question updated";
    }

    private IbanQuestion assertIsIbanQuestion(NodeEntity<?> node) {
        if (node instanceof IbanQuestion) {
            return (IbanQuestion) node;
        } else {
            throw new IllegalArgumentException("Not an IbanQuestion");
        }
    }
}
