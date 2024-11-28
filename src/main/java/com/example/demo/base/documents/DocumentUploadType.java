package com.example.demo.base.documents;

import com.fasterxml.jackson.annotation.JsonInclude;


public interface DocumentUploadType {
    @JsonInclude
    String name();
}
