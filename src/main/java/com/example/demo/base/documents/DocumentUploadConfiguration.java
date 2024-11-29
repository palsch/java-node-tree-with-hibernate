package com.example.demo.base.documents;

import java.util.List;
import java.util.Objects;

public record DocumentUploadConfiguration(List<DocumentType> documentTypes, int maxDocumentCount, boolean required) {
    public DocumentUploadConfiguration {
        Objects.requireNonNull(documentTypes, "documentTypes must not be null");
    }
}
