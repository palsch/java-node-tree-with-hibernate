package com.example.demo.base.documents;

import com.fasterxml.jackson.annotation.JsonInclude;

public interface DocumentType {
    @JsonInclude
    String name();
}
