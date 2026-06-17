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
}
