package com.fortivus.attachment.attachment_service.dto;

import java.util.UUID;
import java.time.LocalDateTime;

public record AttachmentDTO(
    UUID id,
    String fileName,
    String contentType,
    Long sizeBytes,
    String url,
    UUID entityId,
    String entityType,
    Double gpsLat,
    Double gpsLng,
    LocalDateTime createdAt
) {}
