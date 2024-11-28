package com.example.demo.base.question;

import com.example.demo.base.NodeEntity;
import com.example.demo.base.documents.DocumentUpload;
import com.example.demo.base.documents.DocumentUploadConfiguration;
import com.example.demo.base.documents.DocumentUploadType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

    @OneToMany(mappedBy = "nodeId", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DocumentUpload> documentUploads = new HashSet<>();

    /**
     * Implement this method to update the node with the given data.
     * <p>
     * This method should update all the attributes of the node. But NOT the document uploads.
     * The method is called by {@link #updateNode(NodeEntity)}. The given data is of the same type as the node.
     */
    protected abstract String updateNodeImpl(NodeEntity<?> nodeEntity);

    protected abstract Map<DocumentUploadType, DocumentUploadConfiguration> getDocumentUploadConfigurations();

    /**
     * Either initialize the document uploads with default values or setup the node after loading from the database
     */
    @PostPersist
    @PostUpdate
    @Override
    final protected void initializeNode() {
        // setup of required and optional document upload
        setupDocumentUploads();
    }

    /**
     * Updates the node with the given data.
     * <p>
     * Calls {@link #updateNodeImpl(NodeEntity)} to update the node.
     * Then sets up the required and optional document uploads.
     *
     * @param nodeEntity the nodeEntity to update
     * @return
     */
    @Override
    final protected String updateNode(NodeEntity<?> nodeEntity) {
        // update node
        updateNodeImpl(nodeEntity);

        // setup of update required and optional document upload
        setupDocumentUploads();

        return "answer updated";
    }

    private void setupDocumentUploads() {
        log.debug("setupDocumentUploads for {} - id {}", this.getClass().getSimpleName(), this.getId());

        // setup document uploads by configuration
        getDocumentUploadConfigurations().forEach((type, configuration) -> {
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
                    .nodeId(getId())
                    .type(type)
                    .docTypes(configuration.documentTypes())
                    .maxDocsCount(configuration.maxDocumentCount())
                    .required(configuration.required())
                    .build();
            documentUploads.add(documentUpload);
        });

        // remove document uploads that are not in the configuration
        Map<DocumentUploadType, DocumentUploadConfiguration> configurations = getDocumentUploadConfigurations();
        documentUploads.removeIf(documentUpload -> !configurations.containsKey(documentUpload.getType()));
        // TODO: domain event listener to delete attachments from document storage service
    }

    private DocumentUpload findDocumentUploadByType(DocumentUploadType type) {
        return documentUploads.stream()
                .filter(documentUpload -> documentUpload.getType().name().equals(type.name()))
                .findFirst()
                .orElse(null);
    }
}

