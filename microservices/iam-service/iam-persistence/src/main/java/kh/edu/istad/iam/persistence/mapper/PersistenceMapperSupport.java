package kh.edu.istad.iam.persistence.mapper;

import kh.edu.istad.iam.domain.entity.AuditableDomain;
import kh.edu.istad.iam.domain.entity.AuditedAssignment;
import kh.edu.istad.iam.persistence.entity.AssignmentEntity;
import kh.edu.istad.iam.persistence.entity.BaseEntity;

final class PersistenceMapperSupport {
    private PersistenceMapperSupport() { }

    static void copyBase(BaseEntity source, AuditableDomain target) {
        target.setId(source.getId());
        target.setCreatedAt(source.getCreatedAt());
        target.setCreatedBy(source.getCreatedBy());
        target.setUpdatedAt(source.getUpdatedAt());
        target.setUpdatedBy(source.getUpdatedBy());
        target.setDeleted(source.isDeleted());
        target.setDeletedAt(source.getDeletedAt());
        target.setDeletedBy(source.getDeletedBy());
        target.setVersion(source.getVersion());
    }

    static void copyBase(AuditableDomain source, BaseEntity target) {
        target.setId(source.getId());
        target.setCreatedAt(source.getCreatedAt());
        target.setCreatedBy(source.getCreatedBy());
        target.setUpdatedAt(source.getUpdatedAt());
        target.setUpdatedBy(source.getUpdatedBy());
        target.setDeleted(source.isDeleted());
        target.setDeletedAt(source.getDeletedAt());
        target.setDeletedBy(source.getDeletedBy());
        target.setVersion(source.getVersion());
    }

    static void copyAssignment(AssignmentEntity source, AuditedAssignment target) {
        target.setId(source.getId());
        target.setAssignedAt(source.getAssignedAt());
        target.setAssignedBy(source.getAssignedBy());
        target.setExpiresAt(source.getExpiresAt());
        target.setRevokedAt(source.getRevokedAt());
        target.setRevokedBy(source.getRevokedBy());
        target.setReason(source.getReason());
    }

    static void copyAssignment(AuditedAssignment source, AssignmentEntity target) {
        target.setId(source.getId());
        target.setAssignedAt(source.getAssignedAt());
        target.setAssignedBy(source.getAssignedBy());
        target.setExpiresAt(source.getExpiresAt());
        target.setRevokedAt(source.getRevokedAt());
        target.setRevokedBy(source.getRevokedBy());
        target.setReason(source.getReason());
    }
}
