package com.example.demo.questions;

import com.example.demo.base.NodeEntity;
import com.example.demo.questions.children.ChildPersonalDataQuestion;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
@Builder
@AllArgsConstructor

@Entity
@DiscriminatorValue("child")
public class Child extends NodeEntity<NodeEntity<?>> {

    @Override
    protected void initializeNode() {
        // add questions
        addNode(ChildPersonalDataQuestion.builder().build());
    }

    @Override
    public String updateNode(NodeEntity<?> nodeEntity) {
        // assert that nodeEntity is a child
        assertIsAChild(nodeEntity);

        return "child updated";
    }

    /**
     * All required childred nodes are created in the initializeNode method
     */
    @Override
    public Optional<NodeEntity<?>> createNewChildNode() {
        return Optional.empty();
    }

    private void assertIsAChild(NodeEntity<?> nodeEntity) {
        if (!(nodeEntity instanceof Child)) {
            throw new IllegalArgumentException("Not a child");
        }
    }
}

