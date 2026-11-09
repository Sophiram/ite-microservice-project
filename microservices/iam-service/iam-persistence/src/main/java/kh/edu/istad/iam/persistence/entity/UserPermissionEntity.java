package kh.edu.istad.iam.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import kh.edu.istad.iam.domain.entity.Effect;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "user_permissions")
@SQLRestriction("is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
public class UserPermissionEntity extends AssignmentEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "permission_id", nullable = false)
    private PermissionEntity permission;

    @Enumerated(EnumType.STRING)
    @jakarta.persistence.Column(name = "effect", nullable = false, length = 5)
    private Effect effect;
}
