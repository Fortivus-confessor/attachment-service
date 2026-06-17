package com.fortivus.attachment.attachment_service.dto;

import java.util.UUID;

public record AttachmentConfirmRequest(
    String fileKey,
    String fileName,
    String contentType,
    Long sizeBytes,
    UUID entityId,
    String entityType,
    Double gpsLat,
    Double gpsLng
) {}
