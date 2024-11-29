package com.example.demo.questions;

import com.example.demo.base.NodeEntity;
import com.example.demo.base.documents.DocumentUploadConfiguration;
import com.example.demo.base.documents.DocumentUploadType;
import com.example.demo.base.question.SimpleNodeWithDocuments;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter(AccessLevel.PROTECTED)

@Entity
@DiscriminatorValue("work_ability_question")
public class WorkAbilityNode extends SimpleNodeWithDocuments {

    private Boolean yesNo;

    private Integer workAbilityPercent;

    @Transient
    @Override
    protected Map<DocumentUploadType, DocumentUploadConfiguration> getDocumentUploadConfigurations() {
        Map<DocumentUploadType, DocumentUploadConfiguration> result = new HashMap<>();
        if (getYesNo() != null && getYesNo()) {
            // on yes - medical certificate required
            result.put(AleAntragDocumentUploadTypes.REQUIRED, new DocumentUploadConfiguration(List.of(AleAntragDocumentType.MEDICAL_CERTIFICATE), 3, true));
        }
        return result;
    }

    /**
     * @return change log // TODO: change log return type
     */
    @Override
    protected String updateNodeImpl(NodeEntity<?> node) {
        WorkAbilityNode question = assertIsWorkAbilityQuestion(node);

        // update yesNo
        this.setYesNo(question.getYesNo());

        if (question.getYesNo()) {
            // work ability percent is only relevant if "yes" is selected
            this.setWorkAbilityPercent(question.getWorkAbilityPercent());
        } else {
            this.setWorkAbilityPercent(null);
        }

        return "";
    }

    private WorkAbilityNode assertIsWorkAbilityQuestion(NodeEntity<?> question) {
        if (question instanceof WorkAbilityNode) {
            return (WorkAbilityNode) question;
        } else {
            throw new IllegalArgumentException("Not a WorkAbilityNode");
        }
    }
}
