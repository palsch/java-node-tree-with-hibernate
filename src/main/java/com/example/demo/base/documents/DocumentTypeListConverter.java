package com.example.demo.base.documents;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.IOException;
import java.util.List;

@Converter(autoApply = true)
public class DocumentTypeListConverter implements AttributeConverter<List<DocumentType>, String> {

    /**
     * Converts a list of DocumentType to a json array string
     *
     * @param attribute list of DocumentType
     * @return json array string
     */
    @Override
    public String convertToDatabaseColumn(List<DocumentType> attribute) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.valueToTree(attribute).toString();
    }

    /**
     * Converts a json array string to a list of DocumentType
     *
     * @param dbData json array string
     * @return list of DocumentType
     */
    @Override
    public List<DocumentType> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return List.of();
        }

        ObjectMapper objectMapper = new ObjectMapper();
        // register DocumentTypeDeserializer to handle deserialization of DocumentType
        objectMapper.registerModule(new com.fasterxml.jackson.databind.module.SimpleModule().addDeserializer(DocumentType.class, new DocumentTypeDeserializer()));


        try {
            return objectMapper.readValue(dbData, new TypeReference<List<DocumentType>>() {
            });
        } catch (Exception e) {
            throw new IllegalArgumentException("Error converting JSON to list of DocumentType", e);
        }
        //return objectMapper.convertValue(dbData, objectMapper.getTypeFactory().constructCollectionType(List.class, DocumentType.class));
    }

    // a deserializer class for DocumentType is needed
    public static class DocumentTypeDeserializer extends JsonDeserializer<DocumentType> {

        @Override
        public DocumentType deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
            return DocumentTypeConverter.convertStringToDocumentType(jsonParser.getText());
        }
    }
}
