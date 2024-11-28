package com.example.demo.base.documents;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class DocumentTypeConverter implements AttributeConverter<DocumentType, String> {

    @Override
    public String convertToDatabaseColumn(DocumentType attribute) {
        return attribute != null ? attribute.name() : null;
    }

    @Override
    public DocumentType convertToEntityAttribute(String dbData) {
        return convertStringToDocumentType(dbData);
    }

    public static DocumentType convertStringToDocumentType(String dbData) {
        if (dbData == null) {
            return null;
        }

        for (Class<?> typeClass : DocumentTypeScanner.getDocumentTypeImplementations()) {
            if (typeClass.isEnum()) {
                for (Object enumConstant : typeClass.getEnumConstants()) {
                    DocumentType type = (DocumentType) enumConstant;
                    if (type.name().equals(dbData)) {
                        return type;
                    }
                }
            }
        }
        throw new IllegalArgumentException("Unknown value: " + dbData);
    }
}
