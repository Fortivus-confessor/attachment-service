package com.fortivus.attachment.attachment_service.service;

import com.fortivus.attachment.attachment_service.domain.Attachment;
import com.fortivus.attachment.attachment_service.dto.AttachmentConfirmRequest;
import com.fortivus.attachment.attachment_service.dto.AttachmentDTO;
import com.fortivus.attachment.attachment_service.dto.PresignedUrlResponse;
import com.fortivus.attachment.attachment_service.repository.AttachmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final AttachmentRepository repository;
    private final S3StorageService s3StorageService;
    private final RabbitTemplate rabbitTemplate;

    public PresignedUrlResponse requestUploadUrl(String fileName, String contentType) {
        String fileKey = UUID.randomUUID().toString() + "-" + fileName;
        String url = s3StorageService.generatePresignedUploadUrl(fileKey, contentType);
        return new PresignedUrlResponse(url, fileKey, fileName, contentType);
    }

    @Transactional
    public AttachmentDTO confirmUpload(AttachmentConfirmRequest request, UUID userId) {
        Attachment attachment = new Attachment();
        attachment.setFileName(request.fileName());
        attachment.setContentType(request.contentType());
        attachment.setSizeBytes(request.sizeBytes());
        attachment.setStoragePath(request.fileKey());
        attachment.setEntityId(request.entityId());
        attachment.setEntityType(request.entityType());
        attachment.setGpsLat(request.gpsLat());
        attachment.setGpsLng(request.gpsLng());
        attachment.setUploadedBy(userId);

        Attachment saved = repository.save(attachment);

        // Notify other services (e.g., Foco Service, Report Service)
        rabbitTemplate.convertAndSend("attachment.exchange", "attachment.uploaded", saved.getId().toString());

        return toDTO(saved);
    }

    public List<AttachmentDTO> getAttachmentsForEntity(UUID entityId) {
        return repository.findByEntityId(entityId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private AttachmentDTO toDTO(Attachment a) {
        String downloadUrl = s3StorageService.generatePresignedDownloadUrl(a.getStoragePath());
        return new AttachmentDTO(
                a.getId(),
                a.getFileName(),
                a.getContentType(),
                a.getSizeBytes(),
                downloadUrl,
                a.getEntityId(),
                a.getEntityType(),
                a.getGpsLat(),
                a.getGpsLng(),
                a.getCreatedAt()
        );
    }
}
