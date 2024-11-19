package com.example.demo.base;

import com.example.demo.base.documents.DocumentType;
import com.example.demo.base.documents.DocumentUploads;
import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static com.example.demo.TestData.ownerUserId;

@Slf4j
@Getter
@Setter

@MappedSuperclass
// reset json deserializer to prevent endless loop during deserialization
//@JsonDeserialize(using = JsonDeserializer.None.class)
public abstract class AnswerNodeEntityWithDocs extends NodeEntity<NodeEntity<?>> {

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "required_document_upload_id")
    private DocumentUploads requiredDocumentUpload;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "optional_document_upload_id")
    private DocumentUploads optionalDocumentUpload;

    /**
     * Either initialize the document uploads with default values or setup the node after loading from the database
     */
    @PostPersist
    @PostLoad
    @Override
    protected void initializeNode() {
        // setup of required and optional document upload
        setupRequiredDocumentUpload();
        setupOptionalDocumentUpload();
    }

    @Override
    protected String updateNode(NodeEntity<?> nodeEntity) {
        // update answer
        updateAnswer(nodeEntity);

        // setup of update required and optional document upload
        setupRequiredDocumentUpload();
        setupOptionalDocumentUpload();

        return "answer updated";
    }

    /**
     * Get the required document types that must be uploaded
     * <p>
     * If no required documents must be uploaded, return an empty list
     */
    @Transient
    protected abstract List<DocumentType> getRequiredDocumentTypes();

    /**
     * Get the optional document types that can be uploaded
     * <p>
     * If no optional documents can be uploaded, return an empty list
     */
    @Transient
    protected abstract List<DocumentType> getOptionalDocumentTypes();

    /**
     * Get the maximum number of required documents that can be uploaded
     * <p>
     * if 0, no required documents can be uploaded
     */
    @Transient
    protected abstract int getRequiredDocumentMaxCount();

    /**
     * Get the maximum number of optional documents that can be uploaded
     * <p>
     * if 0, no optional documents can be uploaded
     */
    @Transient
    protected abstract int getOptionalDocumentMaxCount();

    /**
     * Update the answer from the given nodeEntity
     *
     * @param nodeEntity the nodeEntity to update the answer from - must be of the same type
     * @return change log
     */
    protected abstract String updateAnswer(NodeEntity<?> nodeEntity);

    private void setupRequiredDocumentUpload() {
        log.debug("setupRequiredDocumentUpload for {} - id {}", this.getClass().getSimpleName(), this.getId());
        if (requiredDocumentUpload != null) {
            requiredDocumentUpload.setDocTypes(getRequiredDocumentTypes());
            requiredDocumentUpload.setMaxDocsCount(getRequiredDocumentMaxCount());
            requiredDocumentUpload.setRequired(true);
            return; // already setup - only update
        }

        if (getRequiredDocumentTypes().isEmpty() || getRequiredDocumentMaxCount() == 0) {
            return; // no required documents
        }

        // create new
        requiredDocumentUpload = DocumentUploads.builder()
                .ownerUserId(ownerUserId)
                .laterUpload(false)
                .docTypes(getRequiredDocumentTypes())
                .maxDocsCount(getRequiredDocumentMaxCount())
                .required(true)
                .build();
    }

    private void setupOptionalDocumentUpload() {
        if (optionalDocumentUpload != null) {
            optionalDocumentUpload.setDocTypes(getOptionalDocumentTypes());
            optionalDocumentUpload.setMaxDocsCount(getOptionalDocumentMaxCount());
            requiredDocumentUpload.setRequired(false);
            return; // already setup - only update
        }

        if (getOptionalDocumentTypes().isEmpty() || getOptionalDocumentMaxCount() == 0) {
            return; // no optional documents
        }

        // create new
        optionalDocumentUpload = DocumentUploads.builder()
                .ownerUserId(ownerUserId)
                .laterUpload(false)
                .docTypes(getOptionalDocumentTypes())
                .maxDocsCount(getOptionalDocumentMaxCount())
                .required(false)
                .build();
    }
}

