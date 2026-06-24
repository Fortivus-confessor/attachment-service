package com.fortivus.attachment.attachment_service.controller;

import com.fortivus.attachment.attachment_service.dto.AttachmentConfirmRequest;
import com.fortivus.attachment.attachment_service.dto.AttachmentDTO;
import com.fortivus.attachment.attachment_service.dto.PresignedUrlResponse;
import com.fortivus.attachment.attachment_service.service.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1/attachments")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService;

    @GetMapping("/upload-url")
    public ResponseEntity<PresignedUrlResponse> requestUploadUrl(
            @RequestParam String fileName,
            @RequestParam String contentType) {
        return ResponseEntity.ok(attachmentService.requestUploadUrl(fileName, contentType));
    }

    @PostMapping("/confirm")
    public ResponseEntity<AttachmentDTO> confirmUpload(@RequestBody AttachmentConfirmRequest request) {
        // In a real scenario, extract userId from JWT token
        UUID userId = UUID.randomUUID(); 
        AttachmentDTO dto = attachmentService.confirmUpload(request, userId);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/entity/{entityId}")
    public ResponseEntity<List<AttachmentDTO>> getEntityAttachments(@PathVariable UUID entityId) {
        return ResponseEntity.ok(attachmentService.getAttachmentsForEntity(entityId));
    }

    @PostMapping(value = "/upload", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AttachmentDTO> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("entityId") UUID entityId,
            @RequestParam("entityType") String entityType) throws IOException {
        UUID userId = UUID.randomUUID(); // Mock JWT User
        AttachmentDTO dto = attachmentService.uploadAndSave(file, entityId, entityType, userId);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttachment(@PathVariable UUID id) {
        attachmentService.deleteAttachment(id);
        return ResponseEntity.noContent().build();
    }
}
