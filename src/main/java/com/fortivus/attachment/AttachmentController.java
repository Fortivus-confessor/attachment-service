package com.fortivus.attachment;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.UUID;
import java.util.Map;

@RestController
@RequestMapping("/api/attachments")
public class AttachmentController {

    // Simulação do serviço que salva no Postgres e faz upload pro SeaweedFS
    // @Autowired
    // private AttachmentService attachmentService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("entityId") UUID entityId,
            @RequestParam("entityType") String entityType) {
        
        // Exemplo:
        // 1. Validar tamanho e tipo do arquivo
        // 2. Fazer upload para o SeaweedFS via S3Client (putObject)
        // 3. Salvar metadados no PostgreSQL (nome, caminho do bucket, etc)
        // 4. Disparar Evento via RabbitMQ para os outros serviços
        
        System.out.println("Recebido arquivo: " + file.getOriginalFilename() + " para entidade " + entityType);

        return ResponseEntity.ok(Map.of(
            "message", "Upload realizado com sucesso no SeaweedFS",
            "fileName", file.getOriginalFilename()
        ));
    }

    @GetMapping("/presigned-url/{attachmentId}")
    public ResponseEntity<?> getPresignedUrl(@PathVariable UUID attachmentId) {
        // Exemplo:
        // 1. Buscar metadados do attachmentId no Postgres
        // 2. Pedir pro S3Client (SeaweedFS) gerar uma URL assinada válida por 60min
        // 3. Retornar a URL pro frontend renderizar a imagem ou iniciar o download
        
        return ResponseEntity.ok(Map.of(
            "url", "http://localhost:8333/fortivus-bucket/caminho/para/imagem.jpg?X-Amz-Signature=..."
        ));
    }
}
