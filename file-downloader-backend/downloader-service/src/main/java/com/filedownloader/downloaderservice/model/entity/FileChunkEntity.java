package com.filedownloader.downloaderservice.model.entity;

import com.filedownloader.downloaderservice.model.enums.FileChunkStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "file_chunks",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_file_chunks_file_id_chunk_index",
                columnNames = {"file_id", "chunk_index"}
        )
)
@Getter
@Setter
@SuperBuilder
@FieldNameConstants
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FileChunkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "file_id", nullable = false)
    private FileDescriptionEntity fileDescription;

    @Column(name = "source_url", nullable = false)
    private String sourceUrl;

    @Column(name = "chunk_index", nullable = false)
    private Integer chunkIndex;

    @Column(name = "start_byte", nullable = false)
    private Long startByte;

    @Column(name = "end_byte", nullable = false)
    private Long endByte;

    @Column(name = "current_size", nullable = false)
    @Builder.Default
    private Long currentSize = 0L;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private FileChunkStatus status = FileChunkStatus.PENDING;

    @Column(name = "worker_id")
    private String workerId;

    @Column(name = "storage_path")
    private String storagePath;

    @Column(name = "retry_count", nullable = false)
    @Builder.Default
    private Integer retryCount = 0;

    @Column(name = "last_heartbeat")
    private Instant lastHeartbeat;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "error_message", columnDefinition = "text")
    private String errorMessage;
}
