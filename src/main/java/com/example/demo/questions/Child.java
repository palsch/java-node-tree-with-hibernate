package com.example.demo.questions;

import com.example.demo.base.Node;
import com.example.demo.base.Question;
import com.example.demo.questions.children.ChildPersonalDataQuestion;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

@Entity
@DiscriminatorValue("child")
public class Child extends Node<Question<?>> {

    /**
     * Name of the child
     * TODO: how to set vorname here and in personal data???
     */
    private String name;

    protected void initializeNode() {
        if (getId() != null) {
            throw new IllegalArgumentException("Child already setup");
        }

        // add questions
        addNode(ChildPersonalDataQuestion.builder().build());
    }

    @Override
    public String updateNode(Node<?> node) {
        // assert that node is a child
        assertIsAChild(node);

        Child child = (Child) node;

        // update the name
        this.setName(child.getName());

        return "child updated";
    }

    /**
     * All required childred nodes are created in the initializeNode method
     */
    @Override
    public Optional<Question<?>> createNewChildNode() {
        return Optional.empty();
    }

    private void assertIsAChild(Node<?> node) {
        if (!(node instanceof Child)) {
            throw new IllegalArgumentException("Not a child");
        }
    }
}

