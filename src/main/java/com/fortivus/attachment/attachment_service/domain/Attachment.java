package com.fortivus.attachment.attachment_service.domain;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "attachments")
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String contentType;

    private Long sizeBytes;

    @Column(nullable = false)
    private String storagePath;

    private UUID entityId;

    @Column(nullable = false)
    private String entityType; // TERRESTRE | AEREO | MAQUINARIO

    private Long despachoId;

    @Column(nullable = false)
    private UUID uploadedBy;

    private Double gpsLat;
    private Double gpsLng;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
