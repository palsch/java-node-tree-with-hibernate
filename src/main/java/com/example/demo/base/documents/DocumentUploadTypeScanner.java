package com.example.demo.base.documents;

import lombok.Getter;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Scans for classes implementing the {@link DocumentUploadType} interface and stores them in a list.
 */
public class DocumentUploadTypeScanner {

    @Getter
    private static final List<Class<?>> documentUploadTypeImplementations = new ArrayList<>();

    static {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath*:com/example/demo/**/*.class");
            MetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory();

            // find all implementations of the DocumentUploadType interface
            TypeFilter filter = new AssignableTypeFilter(DocumentUploadType.class);
            for (Resource resource : resources) {
                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                if (filter.match(metadataReader, metadataReaderFactory)) {
                    Class<?> clazz = Class.forName(metadataReader.getClassMetadata().getClassName());
                    if (DocumentUploadType.class.isAssignableFrom(clazz) && clazz.isEnum()) {
                        documentUploadTypeImplementations.add(clazz);
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to scan for classes implementing DocumentUploadType", e);
        }
    }
}