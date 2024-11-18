package com.example.demo.base.documents;

import com.example.demo.base.Node;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
public class DocumentUploads {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Use can set this flag to true if the upload should be done later after the form is submitted.
     * This flag is used to calculate the validation state of the form when a required document is not uploaded.
     */
    private Boolean laterUpload;

    private String ownerUserId;

    @OneToMany(mappedBy = "documentUpload", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Attachment> attachments;

    @Transient
    private List<DocumentType> docTypes;

    @Transient
    private int maxDocsCount;

    @Transient
    private boolean required;

    public void addAttachment(Attachment attachment) {
        attachments.add(attachment);
    }

    public void removeAttachment(UUID attachmentId) {
        attachments.removeIf(attachment -> attachment.getId().equals(attachmentId));

        // TODO: delete attachment from document storage service by domain event listener
    }
}
