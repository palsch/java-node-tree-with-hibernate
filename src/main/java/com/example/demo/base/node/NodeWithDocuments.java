package com.example.demo.base.node;

import com.example.demo.base.NodeEntity;
import com.example.demo.base.documents.DocumentUpload;
import com.example.demo.base.documents.DocumentUploadConfiguration;
import com.example.demo.base.documents.DocumentUploadType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Base class for questions with documents.
 * <p>
 * A question with documents is a question that contains document uploads.
 *
 * @param <TChildNode>
 */
@Slf4j
@Getter
@Setter

@MappedSuperclass
public abstract class NodeWithDocuments<TChildNode extends NodeEntity<?>> extends NodeEntity<TChildNode> {

    @OneToMany(mappedBy = "node", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DocumentUpload> documentUploads = new HashSet<>();

    /**
     * Implement this method to update the node with the given data.
     * <p>
     * This method should update all the attributes of the node. But NOT the document uploads.
     * The method is called by {@link #updateNode(NodeEntity)}. The given data is of the same type as the node.
     *
     * @return change log // TODO: change log return type
     */
    protected abstract String updateNodeImpl(NodeEntity<?> nodeEntity);

    protected abstract Map<DocumentUploadType, DocumentUploadConfiguration> getDocumentUploadConfigurations();

    /**
     * Either initialize the document uploads with default values or setup the node after loading from the database
     * <p>
     * <b>IMPORTANT</b>: If you need to setup the node after loading from the database, override this method and call super.initializeNode()
     */
    @Override
    protected void initializeNode() {
        // setup of required and optional document upload
        setupDocumentUploads();
    }

    /**
     * Remove all document uploads and delete all attachments by triggering the attachment deleted event
     */
    @Override
    protected void onDestroyNode() {
        log.debug("DESTROY_DOCUMENT_UPLOADS for {} - id {}", this.getClass().getSimpleName(), this.getId());
        removeAllDocumentUploads();
    }

    /**
     * Updates the node with the given data.
     * <p>
     * Calls {@link #updateNodeImpl(NodeEntity)} to update the node.
     * Then sets up the required and optional document uploads.
     *
     * @param nodeEntity the nodeEntity to update
     * @return // TODO: change log return type
     */
    @Override
    final protected String updateNode(NodeEntity<?> nodeEntity) {
        // update node
        String changeLog = updateNodeImpl(nodeEntity);

        // setup of update required and optional document upload
        changeLog += setupDocumentUploads();

        return changeLog;
    }

    /**
     * Setup the document uploads for the node.
     * <p>
     * This method is called after the node is persisted or updated.
     * It sets up the document uploads based on the configuration.
     */
    private String setupDocumentUploads() {
        log.debug("SETUP_DOCUMENT_UPLOADS for {} - id {}", this.getClass().getSimpleName(), this.getId());

        Map<DocumentUploadType, DocumentUploadConfiguration> documentUploadConfigurations = getDocumentUploadConfigurations();
        // remove the document uploads by type if no configuration is available for that type
        Set<DocumentUpload> toRemove = documentUploads.stream()
                // filter out document uploads that are not in the configuration or have no document types
                // IMPORTANT: this will also remove all doc uploads with attachments
                .filter(documentUpload -> !documentUploadConfigurations.containsKey(documentUpload.getType()) || documentUploadConfigurations.get(documentUpload.getType()).documentTypes().isEmpty())
                .collect(Collectors.toSet());
        toRemove.forEach(documentUpload -> {
            documentUploads.remove(documentUpload);
            documentUpload.onDestroy();
            // TODO: domain event listener to delete attachments from document storage service
        });
        documentUploads.removeAll(toRemove);

        // setup document uploads by configuration
        documentUploadConfigurations.forEach((type, configuration) -> {
            DocumentUpload documentUpload = findDocumentUploadByType(type);
            if (documentUpload != null) {
                // use modifiable list - so hibernate can update the list if required
                documentUpload.setDocTypes(new ArrayList<>(configuration.documentTypes()));
                documentUpload.setMaxDocsCount(configuration.maxDocumentCount());
                documentUpload.setRequired(configuration.required());
                return; // already setup - only update
            }

            if (configuration.documentTypes().isEmpty() || configuration.maxDocumentCount() == 0) {
                return; // no document upload possible without document types or zero max count
            }

            // create new
            documentUpload = DocumentUpload.builder()
                    .node(this)
                    .type(type)
                    .docTypes(configuration.documentTypes())
                    .maxDocsCount(configuration.maxDocumentCount())
                    .required(configuration.required())
                    .build();
            documentUploads.add(documentUpload);
        });

        // remove document uploads that are not in the configuration
        Map<DocumentUploadType, DocumentUploadConfiguration> configurations = documentUploadConfigurations;
        documentUploads.removeIf(documentUpload -> !configurations.containsKey(documentUpload.getType()));
        // TODO: domain event listener to delete attachments from document storage service
        return "";
    }

    private DocumentUpload findDocumentUploadByType(DocumentUploadType type) {
        return documentUploads.stream()
                .filter(documentUpload -> documentUpload.getType().name().equals(type.name()))
                .findFirst()
                .orElse(null);
    }

    private void removeDocumentUpload(UUID documentUploadId) {
        Optional<DocumentUpload> documentUploadOptional = documentUploads.stream()
                .filter(documentUpload -> documentUpload.getId().equals(documentUploadId))
                .findFirst();
        documentUploadOptional.ifPresent(documentUpload -> {
            documentUpload.onDestroy();
            documentUploads.remove(documentUpload);
        });
    }

    /**
     * Remove all document uploads from the node.
     */
    private void removeAllDocumentUploads() {
        List<UUID> documentUploadIdListToRemove = documentUploads.stream().map(DocumentUpload::getId).toList();
        documentUploadIdListToRemove.forEach(this::removeDocumentUpload);
    }
}

