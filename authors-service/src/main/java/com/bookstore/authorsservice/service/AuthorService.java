package com.bookstore.authorsservice.service;

import com.bookstore.authorsservice.dto.request.AuthorCreateRequest;
import com.bookstore.authorsservice.dto.request.AuthorUpdateRequest;
import com.bookstore.authorsservice.dto.response.AuthorResponse;
import com.bookstore.authorsservice.entity.Author;
import com.bookstore.authorsservice.enums.Status;
import com.bookstore.authorsservice.mapper.AuthorMapper;
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

    public Page<AuthorResponse> getActiveAuthors(Pageable pageable) {
        return repository.findByStatus(Status.ACTIVE, pageable)
                .map(AuthorMapper::authorToAuthorResponse);
    }

    private Author getActiveAuthorEntity(UUID uuid) {
        return repository.findByUuidAndStatus(uuid, Status.ACTIVE)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tác giả"));
    }

    public AuthorResponse getAuthorById(UUID uuid) {
        Author author = repository.findByUuidAndStatus(uuid, Status.ACTIVE)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tác giả"));
        return AuthorMapper.authorToAuthorResponse(author);
    }

    /* ================= CREATE ================= */

    @Transactional
    public AuthorResponse createAuthor(AuthorCreateRequest authorCreateRequest) {
        if (repository.existsByFullnameIgnoreCase(authorCreateRequest.getFullname())) {
            throw new RuntimeException("Tác giả đã tồn tại: " + authorCreateRequest.getFullname());
        }

        Author author = Author.builder()
                .fullname(authorCreateRequest.getFullname())
                .penName(authorCreateRequest.getPenName())
                .dateOfBirth(authorCreateRequest.getDateOfBirth())
                .nationality(authorCreateRequest.getNationality())
                .biography(authorCreateRequest.getBiography())
                .avatarUrl(authorCreateRequest.getAvatarUrl())
                .status(Status.ACTIVE)
                .verified(false)
                .build();

        return AuthorMapper.authorToAuthorResponse(repository.save(author));
    }

    /* ================= UPDATE ================= */

    @Transactional
    public AuthorResponse updateAuthor(UUID uuid, AuthorUpdateRequest request) {

        Author author = getActiveAuthorEntity(uuid);

        author.setFullname(request.getFullname());
        author.setPenName(request.getPenName());
        author.setBiography(request.getBiography());
        author.setDateOfBirth(request.getDateOfBirth());
        author.setDateOfDeath(request.getDateOfDeath());
        author.setNationality(request.getNationality());
        author.setAvatarUrl(request.getAvatarUrl());

        if (request.getVerified() != null) {
            author.setVerified(request.getVerified());
        }

        log.info("Cập nhật tác giả: {}", uuid);
        return AuthorMapper.authorToAuthorResponse(repository.save(author));
    }


    /* ================= DELETE (SOFT) ================= */

    @Transactional
    public void deleteAuthor(UUID uuid) {
        Author author = getActiveAuthorEntity(uuid);
        author.setStatus(Status.INACTIVE);

        log.info("Soft delete tác giả: {}", uuid);
        repository.save(author);
    }
}
