package com.mba.saasapp.entities;

import com.mba.saasapp.config.TenantContext;
import jakarta.persistence.*;
import lombok.*;

import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
@SuperBuilder
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)


@FilterDef(
        name = "tenantFilter",
        parameters = @ParamDef(name = "tenantId", type = String.class),
        defaultCondition = "tenant_id = :tenantId"
)
@Filter(name = "tenantFilter")
public abstract class AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @Column(name = "tenant_id",  nullable = false)
    private String tenantId;



    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", insertable = false)
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(name = "created_by", updatable = false, nullable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "updated_by", insertable = false)
    private String updatedBy;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    @PrePersist
    protected void onCreate() {
        if (this.deleted == null) {
            this.deleted = Boolean.FALSE;
        }
        // TO DO / THIS HAS TO BE DELETED ONCE SECURITY IS IMPLEMENTED
        if (this.createdBy == null ){
            this.createdBy = "SYSTEM" ;
        }

        if (this.tenantId == null) {
            this.tenantId = TenantContext.getCurrentTenant();
        }
    }
}

