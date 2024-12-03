package com.example.demo.questions.children;

import com.example.demo.base.NodeEntity;
import com.example.demo.base.documents.DocumentUploadConfiguration;
import com.example.demo.base.documents.DocumentUploadType;
import com.example.demo.base.node.LeafNodeWithDocuments;
import com.example.demo.questions.AleAntragDocumentType;
import com.example.demo.questions.AleAntragDocumentUploadTypes;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor // for hibernate

@Entity
@DiscriminatorValue("child_personal_data_question")
public class ChildPersonalDataQuestion extends LeafNodeWithDocuments {

    private String name;
    private String surname;
    private String ahv;
    private LocalDate birthday;

    @Override
    protected String updateNodeImpl(NodeEntity<?> nodeEntity) {
        ChildPersonalDataQuestion question = assertIsChildPersonalDataQuestion(nodeEntity);

        setName(question.getName());
        setSurname(question.getSurname());
        setAhv(question.getAhv());
        setBirthday(question.getBirthday());

        return "updated child personal data question";
    }

    @Transient
    @Override
    protected Map<DocumentUploadType, DocumentUploadConfiguration> getDocumentUploadConfigurations() {
        return Map.of(AleAntragDocumentUploadTypes.REQUIRED, new DocumentUploadConfiguration(List.of(AleAntragDocumentType.BIRTH_CERTIFICATE), 3, true));
    }

    private ChildPersonalDataQuestion assertIsChildPersonalDataQuestion(NodeEntity<?> node) {
        if (node instanceof ChildPersonalDataQuestion) {
            return (ChildPersonalDataQuestion) node;
        } else {
            throw new IllegalArgumentException("Not an ChildPersonalDataQuestion");
        }
    }
}
