package com.bookstore.authorsservice.repository;

import com.bookstore.authorsservice.entity.Author;
import com.bookstore.authorsservice.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthorRepository extends JpaRepository<Author, UUID> {

    List<Author> findByFullnameContainingIgnoreCase(String fullname);

    Page<Author> findByFullnameContainingIgnoreCase(
            String fullname,
            Pageable pageable
    );

    List<Author> findByStatus(Status status);

    Optional<Author> findByUuidAndStatus(UUID uuid, Status status);

    boolean existsByFullnameIgnoreCase(String fullname);

    List<Author> findByFullnameContainingIgnoreCaseAndStatus(
            String fullname,
            Status status
    );
}


