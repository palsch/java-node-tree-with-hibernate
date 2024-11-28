package com.example.demo.config;

import com.example.demo.base.documents.DocumentType;
import com.example.demo.base.documents.DocumentTypeListConverter;
import com.example.demo.base.documents.DocumentUploadType;
import com.example.demo.base.documents.DocumentUploadTypeConverter;
import com.example.demo.base.documents.DocumentUploadTypeScanner;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class ObjectMapperConfiguration {

    @Autowired
    private ObjectMapper existingObjectMapper;

    @PostConstruct
    public void setupObjectMapper() {
        // Register all classes annotated with @DiscriminatorValue
        Map<String, Class<?>> nodeClasses = DiscriminatorValueScanner.getDiscriminatorMap();
        existingObjectMapper.registerSubtypes(
                nodeClasses.keySet().stream().map(key -> new NamedType(nodeClasses.get(key), key)).toArray(NamedType[]::new)
        );

        // Register all classes implementing DocumentType
        /*existingObjectMapper.registerSubtypes(
                DocumentTypeScanner.getDocumentTypeImplementations().stream().map(clazz -> new NamedType(clazz, clazz.getSimpleName())).toArray(NamedType[]::new)
        );*/
        // register DocumentTypeDeserializer to handle deserialization of DocumentType
        existingObjectMapper.registerModule(new com.fasterxml.jackson.databind.module.SimpleModule().addDeserializer(DocumentType.class, new DocumentTypeListConverter.DocumentTypeDeserializer()));


        // register DocumentTypeDeserializer to handle deserialization of DocumentType
        existingObjectMapper.registerModule(new com.fasterxml.jackson.databind.module.SimpleModule().addDeserializer(DocumentUploadType.class, new DocumentUploadTypeConverter.DocumentUploadTypeDeserializer()));


        // Register all classes implementing DocumentUpdateType
        /*existingObjectMapper.registerSubtypes(
                DocumentUploadTypeScanner.getDocumentUploadTypeImplementations().stream().map(clazz -> new NamedType(clazz, clazz.getSimpleName())).toArray(NamedType[]::new)
        );*/
    }
}
