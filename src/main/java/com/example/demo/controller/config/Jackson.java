package com.example.demo.controller.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class Jackson {

    @Autowired
    private ObjectMapper existingObjectMapper;

    @PostConstruct
    public void registerAllValidNodeSubclasses() {
        Map<String, Class<?>> nodeClasses = DiscriminatorValueScanner.getDiscriminatorMap();
        existingObjectMapper.registerSubtypes(
                nodeClasses.keySet().stream().map(key -> new NamedType(nodeClasses.get(key), key)).toArray(NamedType[]::new)
        );
    }
}
