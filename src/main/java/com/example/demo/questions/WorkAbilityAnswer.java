package com.example.demo.questions;

import com.example.demo.base.AnswerNodeEntityWithDocs;
import com.example.demo.base.NodeEntity;
import com.example.demo.base.documents.DocumentType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Optional;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@DiscriminatorValue("work_ability_answer")
public class WorkAbilityAnswer extends AnswerNodeEntityWithDocs {
    private Integer workAbilityPercent;

    @Transient
    @Override
    protected List<DocumentType> getRequiredDocumentTypes() {
        return List.of(DocumentType.MEDICAL_CERTIFICATE);
    }

    @Transient
    @Override
    protected List<DocumentType> getOptionalDocumentTypes() {
        return List.of();
    }

    @Transient
    @Override
    protected int getRequiredDocumentMaxCount() {
        return 3;
    }

    @Transient
    @Override
    protected int getOptionalDocumentMaxCount() {
        return 0;
    }

    @Override
    protected String updateAnswer(NodeEntity<?> nodeEntity) {
        assertIsWorkAbilityAnswer(nodeEntity);

        WorkAbilityAnswer answer = (WorkAbilityAnswer) nodeEntity;
        this.setWorkAbilityPercent(answer.getWorkAbilityPercent());

        return "";
    }

    private void assertIsWorkAbilityAnswer(NodeEntity<?> nodeEntity) {
        if (!(nodeEntity instanceof WorkAbilityAnswer)) {
            throw new IllegalArgumentException("Not a work ability answer");
        }
    }

    @Override
    public Optional<NodeEntity<?>> createNewChildNode() {
        return Optional.empty();
    }
}
