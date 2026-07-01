package com.fortivus.attachment.attachment_service.messaging;

import java.util.UUID;

public record AttachmentUploadedEvent(
    UUID id,
    Long despachoId,
    String entityType,
    String fileName,
    String contentType,
    Long sizeBytes,
    String storagePath
) {}
