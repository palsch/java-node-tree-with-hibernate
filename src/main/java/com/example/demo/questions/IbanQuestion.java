package com.example.demo.questions;

import com.example.demo.base.NodeEntity;
import com.example.demo.base.Question;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

/**
 * IbanQuestion
 * (simple question)
 */
@Getter
@Setter

@Entity
@DiscriminatorValue("iban_question")
public class IbanQuestion extends Question<NodeEntity<?>> {

    private String iban;

    @Override
    protected String updateQuestion(Question<NodeEntity<?>> question) {
        // update iban
        setIban(((IbanQuestion) question).getIban());

        return "iban question updated";
    }

    @Override
    public Optional<NodeEntity<?>> createNewChildNode() {
        return Optional.empty();
    }
}
