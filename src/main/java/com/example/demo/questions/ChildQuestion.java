package com.example.demo.questions;

import com.example.demo.base.YesNoQuestion;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter

@Entity
@DiscriminatorValue("child_question")
public class ChildQuestion extends YesNoQuestion<Child> {
    @Override
    public Optional<Child> createNewChildNode() {
        return Optional.of(Child.builder().build());
    }
}

