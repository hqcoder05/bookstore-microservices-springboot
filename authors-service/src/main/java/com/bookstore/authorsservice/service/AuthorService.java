package com.bookstore.authorsservice.service;

import com.bookstore.authorsservice.entity.Author;
import com.bookstore.authorsservice.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorService {

    private final AuthorRepository repository;

    public List<Author> getAllAuthors() {
        return repository.findAll();
    }

    public Author getAuthorById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm ra tác giả với ID: " + id));
    }

    @Transactional
    public Author createAuthor(Author author) {
        // Kiểm tra email trùng
        if (author.getEmail() != null && repository.findByEmail(author.getEmail()).isPresent()) {
            throw new RuntimeException("Email đã tồn tại");
        }
        log.info("Khởi tạo tác giả: {}", author.getName());
        return repository.save(author);
    }

    @Transactional
    public Author updateAuthor(Long id, Author request) {
        Author existingAuthor = getAuthorById(id);

        existingAuthor.setName(request.getName());
        existingAuthor.setBio(request.getBio());
        existingAuthor.setBirthDate(request.getBirthDate());
        existingAuthor.setCountry(request.getCountry());
        existingAuthor.setWebsite(request.getWebsite());
        existingAuthor.setImageUrl(request.getImageUrl());

        log.info("Cập nhật tác giả: {}", id);
        return repository.save(existingAuthor);
    }

    public void deleteAuthor(Long id) {
        repository.deleteById(id);
        log.info("Xóa tác giả: {}", id);
    }
}