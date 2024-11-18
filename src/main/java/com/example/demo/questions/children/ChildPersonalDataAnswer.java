package com.example.demo.questions.children;

import com.example.demo.base.AnswerNodeWithDocs;
import com.example.demo.base.Node;
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
public class ChildPersonalDataAnswer extends AnswerNodeWithDocs {

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
    protected String updateAnswer(Node<?> node) {
        assertIsChildPersonalDataAnswer(node);

        ChildPersonalDataAnswer answer = (ChildPersonalDataAnswer) node;

        setVorname(answer.getVorname());
        setNachname(answer.getNachname());
        setAhvNummer(answer.getAhvNummer());
        setGeburtsdatum(answer.getGeburtsdatum());

        return "updated child personal data answer";
    }

    private void assertIsChildPersonalDataAnswer(Node<?> node) {
        if (!(node instanceof ChildPersonalDataAnswer)) {
            throw new IllegalArgumentException("Answer must be of type ChildPersonalDataAnswer");
        }
    }

    @Override
    public Optional<Node<?>> createNewChildNode() {
        return Optional.empty();
    }
}
