package kh.edu.istad.platform.customer.persistence.document;

import kh.edu.istad.platform.customer.domain.valueobject.CustomerSegment;
import kh.edu.istad.platform.customer.domain.valueobject.CustomerStatus;
import lombok.*;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "customers")
public class CustomerDocument {

    @Id
    private String id;
    private String username;
    private String familyName;
    private String givenName;
    private String email;
    private String phoneNumber;
    private CustomerStatus status;

    /**
     * CustomerSegment - Manual Reference approach.
     * The segment is stored directly as an enum value in the same document.
     */
    private CustomerSegment segment;

    @CreatedBy
    private String createBy;

    @CreatedDate
    private Instant createAt;

    @LastModifiedBy
    private String modifiedBy;

    @LastModifiedDate
    private String modifiedAt;
}
