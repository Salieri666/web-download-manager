package com.filedownloader.corelib.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@SuperBuilder
@Getter
@MappedSuperclass
@FieldNameConstants(innerTypeName = "BaseAuditable")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
public class Auditable {

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private Instant createdDate;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant modifiedDate;

}
