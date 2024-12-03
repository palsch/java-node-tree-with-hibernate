package com.example.demo.base.controller;

import com.example.demo.base.controller.dto.AttachmentUploadDto;
import com.example.demo.base.documents.Attachment;
import com.example.demo.base.documents.DocumentUpload;
import com.example.demo.base.repository.DocumentUploadsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

import static com.example.demo.TestData.ownerUserId;

@Slf4j

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/ale_antrag/documents")
public class DocumentController {

    public static final String DUMMY_PDF = "dummy.pdf";

    @Autowired
    private DocumentUploadsRepository documentUploadsRepository;

    // WARNING: in the original code, get attachment will be done directly from document storage service
    @GetMapping(value = "/<documentUploadId>/<attachmentId>")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Resource> getById(UUID documentUploadId, UUID attachmentId) throws IOException {
        log.info("Get attachment with id {}", attachmentId);
        return ResponseEntity.ok(new ClassPathResource(DUMMY_PDF));
    }

    @PostMapping(path = "/<documentUploadId>", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public DocumentUpload upload(UUID documentUploadId, AttachmentUploadDto attachmentUploadDto, MultipartFile file) throws IOException {
        DocumentUpload docUpload = documentUploadsRepository.getReferenceById(documentUploadId);

        // TODO: upload file to document storage service
        UUID docStorageId = UUID.randomUUID();

        docUpload.addAttachment(Attachment.builder()
                .documentId(docStorageId)
                .name(file.getOriginalFilename())
                .documentType(attachmentUploadDto.getType())
                .mimeType(file.getContentType())
                .ownerUserId(ownerUserId)
                .build());
        documentUploadsRepository.save(docUpload);
        return docUpload;
    }

    @DeleteMapping(value = "/<documentUploadId>/<attachmentId>")
    @ResponseStatus(HttpStatus.OK)
    public void delete(UUID documentUploadId, UUID attachmentId) {
        DocumentUpload docUpload = documentUploadsRepository.getReferenceById(documentUploadId);
        docUpload.removeAttachment(attachmentId);
    }
}
