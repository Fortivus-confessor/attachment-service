package com.fortivus.attachment.attachment_service.repository;

import com.fortivus.attachment.attachment_service.domain.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, UUID> {
    List<Attachment> findByEntityId(UUID entityId);
    List<Attachment> findByDespachoId(Long despachoId);
}
