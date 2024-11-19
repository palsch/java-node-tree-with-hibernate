package com.example.demo.questions.children;

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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor // for hibernate

@Entity
@DiscriminatorValue("child_personal_data_answer")
public class ChildPersonalDataAnswer extends AnswerNodeEntityWithDocs {

    private String vorname;
    private String nachname;
    private String ahvNummer;
    private LocalDate geburtsdatum;

    @Transient
    @Override
    protected List<DocumentType> getRequiredDocumentTypes() {
        return List.of(DocumentType.BIRTH_CERTIFICATE);
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
        assertIsChildPersonalDataAnswer(nodeEntity);

        ChildPersonalDataAnswer answer = (ChildPersonalDataAnswer) nodeEntity;

        setVorname(answer.getVorname());
        setNachname(answer.getNachname());
        setAhvNummer(answer.getAhvNummer());
        setGeburtsdatum(answer.getGeburtsdatum());

        return "updated child personal data answer";
    }

    private void assertIsChildPersonalDataAnswer(NodeEntity<?> nodeEntity) {
        if (!(nodeEntity instanceof ChildPersonalDataAnswer)) {
            throw new IllegalArgumentException("Answer must be of type ChildPersonalDataAnswer");
        }
    }

    @Override
    public Optional<NodeEntity<?>> createNewChildNode() {
        return Optional.empty();
    }
}
