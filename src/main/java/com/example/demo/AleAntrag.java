package com.example.demo;

import com.example.demo.base.NodeEntity;
import com.example.demo.base.node.RootNode;
import com.example.demo.questions.ChildQuestion;
import com.example.demo.questions.IbanQuestion;
import com.example.demo.questions.WorkAbilityNode;
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
public class AleAntrag extends RootNode {

    @Embedded
    private AleAntragMetadata metadaten;

    /**
     * Update the ale antrag node attributes if required.
     *
     * @param nodeEntity the nodeEntity to update
     * @return change log // TODO: change log return type
     */
    @Override
    protected String updateNode(NodeEntity<?> nodeEntity) {
        return "";
    }

    /**
     * All required children nodes are created in the initializeNode method
     */
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
        addChildNode(new IbanQuestion()); // question 2
        addChildNode(new WorkAbilityNode()); // question 3
        addChildNode(new ChildQuestion()); // question 4
        addChildNode(new DisabilityInsuranceQuestion()); // question 5c
    }

    /**
     * Removing child nodes is NOT allowed for AleAntrag
     */
    @Override
    protected boolean isRemoveChildNodesAllowed() {
        return false;
    }
}
