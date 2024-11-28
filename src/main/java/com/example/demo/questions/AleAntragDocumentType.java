package com.example.demo.questions;

import com.example.demo.base.documents.DocumentType;
import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum AleAntragDocumentType implements DocumentType {
    /**
     * IV-Antrag
     */
    DISABILITY_INSURANCE_APPLICATION,
    /**
     * IV-Entscheid
     */
    DISABILITY_INSURANCE_DECISION,
    /**
     * IV-Taggeldabrechnung
     */
    DISABILITY_INSURANCE_DAILY_ALLOWANCE_STATEMENT,
    /**
     * Arztzeugnis
     */
    MEDICAL_CERTIFICATE,
    /**
     * Geburtsurkunde
     */
    BIRTH_CERTIFICATE,
    /**
     * Sonstiges
     */
    OTHER
}
