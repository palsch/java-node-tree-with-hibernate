package com.example.demo.questions;

import com.example.demo.base.NodeEntity;
import com.example.demo.base.documents.DocumentType;
import com.example.demo.base.question.SimpleQuestionWithDocuments;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter(AccessLevel.PROTECTED)

@Entity
@DiscriminatorValue("work_ability_question")
public class WorkAbilityQuestion extends SimpleQuestionWithDocuments {

    private Boolean yesNo;

    private Integer workAbilityPercent;

    @Transient
    @Override
    protected List<DocumentType> getRequiredDocumentTypes() {
        return getYesNo() != null && getYesNo()
                ? List.of(DocumentType.MEDICAL_CERTIFICATE) // on yes - medical certificate required
                : List.of(); // else no documents required
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

    /**
     * @return change log // TODO: change log return type
     */
    @Override
    protected String updateNodeImpl(NodeEntity<?> node) {
        WorkAbilityQuestion question = assertIsWorkAbilityQuestion(node);

        this.setYesNo(question.getYesNo());
        this.setWorkAbilityPercent(question.getWorkAbilityPercent());

        return "";
    }

    private WorkAbilityQuestion assertIsWorkAbilityQuestion(NodeEntity<?> question) {
        if (question instanceof WorkAbilityQuestion) {
            return (WorkAbilityQuestion) question;
        } else {
            throw new IllegalArgumentException("Not a WorkAbilityQuestion");
        }
    }
}
