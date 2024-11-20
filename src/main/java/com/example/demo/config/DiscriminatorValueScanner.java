package com.example.demo.config;

import jakarta.persistence.DiscriminatorValue;
import lombok.Getter;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.Resource;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Scans for classes annotated with {@link DiscriminatorValue} and stores them in a map.
 * It is used to register all valid node subclasses with ObjectMapperConfiguration.
 */
public class DiscriminatorValueScanner {

    @Getter
    private static final Map<String, Class<?>> discriminatorMap = new HashMap<>();

    static {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath*:com/example/demo/**/*.class");
            MetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory();

            TypeFilter filter = new AnnotationTypeFilter(DiscriminatorValue.class);
            for (Resource resource : resources) {
                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                if (filter.match(metadataReader, metadataReaderFactory)) {
                    Class<?> clazz = Class.forName(metadataReader.getClassMetadata().getClassName());
                    DiscriminatorValue annotation = clazz.getAnnotation(DiscriminatorValue.class);
                    discriminatorMap.put(annotation.value(), clazz);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to scan for classes with @DiscriminatorValue", e);
        }
    }

    public static Class<?> getClassByDiscriminatorValue(String value) {
        return discriminatorMap.get(value);
    }
}