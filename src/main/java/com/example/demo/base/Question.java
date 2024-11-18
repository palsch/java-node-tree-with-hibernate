package com.example.demo.base;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

@MappedSuperclass

// reset json deserializer to prevent endless loop during deserialization
//@JsonDeserialize(using = JsonDeserializer.None.class)
public abstract class Question<T extends Node<?>> extends Node<T> {

    @Override
    protected String updateNode(Node<?> node) {
        assertIsAQuestion(node);
        Question<T> question = (Question<T>) node;

        // update the question
        return updateQuestion(question);
    }

    protected abstract String updateQuestion(Question<T> question);

    private void assertIsAQuestion(Node<?> question) {
        if (!(question instanceof Question<?>)) {
            throw new IllegalArgumentException("Not a question");
        }
    }

}
