package com.example.demo.questions;

import com.example.demo.base.Node;
import com.example.demo.base.Question;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
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
public class IbanQuestion extends Question<Node<?>> {

    private String iban;

    @Override
    protected String updateQuestion(Question<Node<?>> question) {
        // update iban
        setIban(((IbanQuestion) question).getIban());

        return "iban question updated";
    }

    @Override
    public Optional<Node<?>> createNewChildNode() {
        return Optional.empty();
    }
}
