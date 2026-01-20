package com.bookstore.authorsservice.service;

import com.bookstore.authorsservice.entity.Author;
import com.bookstore.authorsservice.enums.Status;
import com.bookstore.authorsservice.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorService {

    private final AuthorRepository repository;

    /* ================= READ ================= */

    public Page<Author> getActiveAuthors(Pageable pageable) {
        return repository.findByStatus(Status.ACTIVE, pageable);
    }


    public Author getAuthorById(UUID uuid) {
        return repository.findByUuidAndStatus(uuid, Status.ACTIVE)
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy tác giả với id: " + uuid));
    }

    /* ================= CREATE ================= */

    @Transactional
    public Author createAuthor(Author author) {
        if (repository.existsByFullnameIgnoreCase(author.getFullname())) {
            throw new RuntimeException("Tác giả đã tồn tại: " + author.getFullname());
        }

        author.setStatus(Status.ACTIVE);
        log.info("Tạo tác giả mới: {}", author.getFullname());

        return repository.save(author);
    }

    /* ================= UPDATE ================= */

    @Transactional
    public Author updateAuthor(UUID id, Author request) {
        Author author = getAuthorById(id);

        author.setFullname(request.getFullname());
        author.setPenName(request.getPenName());
        author.setBiography(request.getBiography());
        author.setDateOfBirth(request.getDateOfBirth());
        author.setDateOfDeath(request.getDateOfDeath());
        author.setNationality(request.getNationality());
        author.setAvatarUrl(request.getAvatarUrl());
        author.setVerified(request.isVerified());

        log.info("Cập nhật tác giả: {}", id);
        return repository.save(author);
    }

    /* ================= DELETE (SOFT) ================= */

    @Transactional
    public void deleteAuthor(UUID id) {
        Author author = getAuthorById(id);
        author.setStatus(Status.INACTIVE);

        log.info("Soft delete tác giả: {}", id);
        repository.save(author);
    }
}
