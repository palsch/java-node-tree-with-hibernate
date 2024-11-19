package com.example.demo.questions.children;

import com.example.demo.base.YesNoQuestion;
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

@Entity
@DiscriminatorValue("child_personal_data_question")
public class ChildPersonalDataQuestion extends YesNoQuestion<ChildPersonalDataAnswer> {

    /**
     * Only one answer is allowed
     */
    @Override
    public Optional<ChildPersonalDataAnswer> createNewChildNode() {
        return Optional.empty();
    }

    @Override
    protected void initializeNode() {
        setYesNo(true);
        addNode(ChildPersonalDataAnswer.builder()
                .vorname("Kind")
                .build());
    }

}
