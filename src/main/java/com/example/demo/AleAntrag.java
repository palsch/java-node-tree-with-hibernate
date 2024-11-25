package com.example.demo;

import com.example.demo.base.NodeEntity;
import com.example.demo.questions.ChildQuestion;
import com.example.demo.questions.IbanQuestion;
import com.example.demo.questions.WorkAbilityQuestion;
import com.example.demo.questions.insurance.DisabilityInsuranceQuestion;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

import static com.example.demo.TestData.ownerUserId;

@Getter
@Setter

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue("ale_antrag")
public class AleAntrag extends NodeEntity<NodeEntity<?>> {

    @Embedded
    private AleAntragMetadata metadaten;

    @Override
    protected String updateNode(NodeEntity<?> nodeEntity) {
        return "";
    }

    @Override
    public Optional<NodeEntity<?>> createNewChildNode() {
        return Optional.empty();
    }

    @Override
    protected final void initializeNode() {
        System.out.println("Initialize AleAntrag");

        // set metadata
        setMetadaten(AleAntragMetadata.builder()
                .ownerUserId(ownerUserId)
                .status("DRAFT")
                .build());

        // add questions
        addNode(new IbanQuestion()); // question 2
        addNode(new WorkAbilityQuestion()); // question 3
        addNode(new ChildQuestion()); // question 4
        addNode(new DisabilityInsuranceQuestion()); // question 5c
    }
}
