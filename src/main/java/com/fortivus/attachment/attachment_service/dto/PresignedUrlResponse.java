package com.fortivus.attachment.attachment_service.dto;

import java.util.UUID;

public record PresignedUrlResponse(
    String url,
    String fileKey,
    String fileName,
    String contentType
) {}
