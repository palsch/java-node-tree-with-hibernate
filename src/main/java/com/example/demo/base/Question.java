package com.example.demo.base;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

@MappedSuperclass

// reset json deserializer to prevent endless loop during deserialization
//@JsonDeserialize(using = JsonDeserializer.None.class)
public abstract class Question<T extends NodeEntity<?>> extends NodeEntity<T> {

    @Override
    protected String updateNode(NodeEntity<?> nodeEntity) {
        assertIsAQuestion(nodeEntity);
        Question<T> question = (Question<T>) nodeEntity;

        // update the question
        return updateQuestion(question);
    }

    protected abstract String updateQuestion(Question<T> question);

    private void assertIsAQuestion(NodeEntity<?> question) {
        if (!(question instanceof Question<?>)) {
            throw new IllegalArgumentException("Not a question");
        }
    }

}
