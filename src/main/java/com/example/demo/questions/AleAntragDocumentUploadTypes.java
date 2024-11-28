package com.example.demo.questions;

import com.example.demo.base.documents.DocumentUploadType;
import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum AleAntragDocumentUploadTypes implements DocumentUploadType {
    OPTIONAL,
    REQUIRED;
}
