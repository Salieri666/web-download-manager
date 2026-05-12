package com.filedownloader.downloaderservice.model.entity;

import com.filedownloader.corelib.model.entity.Auditable;
import com.filedownloader.downloaderservice.model.enums.FileDescriptionStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "file_descriptions")
@Getter
@Setter
@SuperBuilder
@FieldNameConstants
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FileDescriptionEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "file_name", nullable = false, length = 512)
    private String filename;

    @Column(name = "storage_path", nullable = false)
    private String storagePath;

    @Column(name = "source_url", nullable = false)
    private String sourceUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private FileDescriptionStatus status = FileDescriptionStatus.PENDING;

    @Column(name = "total_size", nullable = false)
    private Long totalSize;

    @Column(name = "mime_type", nullable = false)
    private String mimeType;

    @Column(name = "checksum")
    private String checksum;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private Map<String, Object> metadata;

    @Column(name = "error_message")
    private String errorMessage;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "fileDescription", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<FileChunkEntity> chunks = new HashSet<>();

}
