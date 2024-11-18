package com.example.demo.questions;

import com.example.demo.base.YesNoQuestion;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.util.Optional;

@Entity
@DiscriminatorValue("work_ability")
public class WorkAbilityQuestion extends YesNoQuestion<WorkAbilityAnswer> {
    @Override
    public Optional<WorkAbilityAnswer> createNewChildNode() {
        return Optional.of(WorkAbilityAnswer.builder().build());
    }
}
