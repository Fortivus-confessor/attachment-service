package com.fortivus.attachment.attachment_service.controller;

import com.fortivus.attachment.attachment_service.dto.AttachmentConfirmRequest;
import com.fortivus.attachment.attachment_service.dto.AttachmentDTO;
import com.fortivus.attachment.attachment_service.dto.PresignedUrlResponse;
import com.fortivus.attachment.attachment_service.service.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    public ResponseEntity<AttachmentDTO> confirmUpload(
            @RequestBody AttachmentConfirmRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        return ResponseEntity.ok(attachmentService.confirmUpload(request, userId));
    }

    @PostMapping(value = "/upload", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AttachmentDTO> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("despachoId") Long despachoId,
            @RequestParam("entityType") String entityType,
            @AuthenticationPrincipal Jwt jwt) throws IOException {
        UUID userId = UUID.fromString(jwt.getSubject());
        AttachmentDTO dto = attachmentService.uploadAndSave(file, despachoId, entityType, userId);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/despacho/{despachoId}")
    public ResponseEntity<List<AttachmentDTO>> getByDespacho(@PathVariable Long despachoId) {
        return ResponseEntity.ok(attachmentService.getAttachmentsByDespachoId(despachoId));
    }

    @GetMapping("/entity/{entityId}")
    public ResponseEntity<List<AttachmentDTO>> getEntityAttachments(@PathVariable UUID entityId) {
        return ResponseEntity.ok(attachmentService.getAttachmentsForEntity(entityId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttachment(@PathVariable UUID id) {
        attachmentService.deleteAttachment(id);
        return ResponseEntity.noContent().build();
    }
}
