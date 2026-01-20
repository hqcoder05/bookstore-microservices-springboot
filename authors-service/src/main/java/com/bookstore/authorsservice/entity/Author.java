package com.bookstore.authorsservice.entity;

import com.bookstore.authorsservice.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "authors",
        indexes = {
                @Index(name = "idx_author_fullname", columnList = "fullname"),
                @Index(name = "idx_author_status", columnList = "status")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class) // Tự động điền ngày tạo/sửa
public class Author {
    @Id
    @GeneratedValue
    @Column(nullable = false, updatable = false)
    private UUID uuid;

    @Column(nullable = false)
    private String fullname;

    private String penName;

    private LocalDate dateOfBirth;

    private LocalDate dateOfDeath;

    private String nationality;

    @Column(length = 2000)
    private String biography;

    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(nullable = false)
    private boolean verified = false;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}