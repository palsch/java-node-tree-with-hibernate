package com.example.demo;

import com.example.demo.base.NodeEntity;
import com.example.demo.base.Question;
import com.example.demo.questions.ChildQuestion;
import com.example.demo.questions.IbanQuestion;
import com.example.demo.questions.WorkAbilityQuestion;
import com.example.demo.questions.insurance.DisabilityInsuranceQuestion;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.example.demo.TestData.ownerUserId;

@Getter
@Setter

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue("ale_antrag")
// reset json deserializer to prevent endless loop during deserialization
//@JsonDeserialize(using = JsonDeserializer.None.class)
public class AleAntrag extends NodeEntity<Question<?>> {

    @Embedded
    private AleAntragMetadata metadaten;

    @Override
    protected String updateNode(NodeEntity<?> nodeEntity) {
        return "";
    }

    @Override
    public Optional<Question<?>> createNewChildNode() {
        return Optional.empty();
    }

    @PrePersist
    public void prePersist() {
        System.out.println("PrePersist");

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

    @Override
    protected boolean isRemoveLastChildNodeAllowed() {
        return false;
    }
}
