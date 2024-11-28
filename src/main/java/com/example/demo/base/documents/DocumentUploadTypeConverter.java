package com.example.demo.base.documents;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.IOException;

@Converter(autoApply = true)
public class DocumentUploadTypeConverter implements AttributeConverter<DocumentUploadType, String> {

    @Override
    public String convertToDatabaseColumn(DocumentUploadType attribute) {
        return attribute != null ? attribute.name() : null;
    }

    @Override
    public DocumentUploadType convertToEntityAttribute(String dbData) {
        return convertStringToDocumentUploadType(dbData);
    }

    public static DocumentUploadType convertStringToDocumentUploadType(String dbData) {
        if (dbData == null) {
            return null;
        }

        for (Class<?> typeClass : DocumentUploadTypeScanner.getDocumentUploadTypeImplementations()) {
            if (typeClass.isEnum()) {
                for (Object enumConstant : typeClass.getEnumConstants()) {
                    DocumentUploadType type = (DocumentUploadType) enumConstant;
                    if (type.name().equals(dbData)) {
                        return type;
                    }
                }
            }
        }
        throw new IllegalArgumentException("Unknown value: " + dbData);
    }

    // a deserializer class for DocumentUploadType is needed
    public static class DocumentUploadTypeDeserializer extends JsonDeserializer<DocumentUploadType> {

        @Override
        public DocumentUploadType deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
            return DocumentUploadTypeConverter.convertStringToDocumentUploadType(jsonParser.getText());
        }
    }
}
