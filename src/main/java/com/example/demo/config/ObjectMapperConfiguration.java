package com.example.demo.config;

import com.example.demo.base.documents.DocumentType;
import com.example.demo.base.documents.DocumentTypeListConverter;
import com.example.demo.base.documents.DocumentUploadType;
import com.example.demo.base.documents.DocumentUploadTypeConverter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@RequiredArgsConstructor
@Configuration
public class ObjectMapperConfiguration {

    private final ObjectMapper existingObjectMapper;

    @PostConstruct
    public void setupObjectMapper() {
        // Ignore unknown enum properties (like empty strings)
        existingObjectMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);

        // Register all classes annotated with @DiscriminatorValue
        Map<String, Class<?>> nodeClasses = DiscriminatorValueScanner.getDiscriminatorMap();
        existingObjectMapper.registerSubtypes(
                nodeClasses.keySet().stream().map(key -> new NamedType(nodeClasses.get(key), key)).toArray(NamedType[]::new)
        );

        // register DocumentTypeDeserializer to handle deserialization of DocumentType
        existingObjectMapper.registerModule(new com.fasterxml.jackson.databind.module.SimpleModule().addDeserializer(DocumentType.class, new DocumentTypeListConverter.DocumentTypeDeserializer()));

        // register DocumentTypeDeserializer to handle deserialization of DocumentType
        existingObjectMapper.registerModule(new com.fasterxml.jackson.databind.module.SimpleModule().addDeserializer(DocumentUploadType.class, new DocumentUploadTypeConverter.DocumentUploadTypeDeserializer()));
    }
}
