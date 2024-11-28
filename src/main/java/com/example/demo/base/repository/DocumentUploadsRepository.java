package com.example.demo.base.repository;

import com.example.demo.base.documents.DocumentUpload;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DocumentUploadsRepository extends JpaRepository<DocumentUpload, UUID> {
}
