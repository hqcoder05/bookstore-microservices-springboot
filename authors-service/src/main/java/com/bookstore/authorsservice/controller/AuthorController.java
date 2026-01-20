package com.bookstore.authorsservice.controller;

import com.bookstore.authorsservice.entity.Author;
import com.bookstore.authorsservice.service.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/authors")
@RequiredArgsConstructor
public class AuthorController {
    private final AuthorService authorService;

    @GetMapping
    public Page<Author> getAll(Pageable pageable) {
        return authorService.getActiveAuthors(pageable);
    }

    @GetMapping("/{uuid}")
    public Author getById(@PathVariable UUID uuid) {
        return authorService.getAuthorById(uuid);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Author create(@RequestBody Author author) {
        return authorService.createAuthor(author);
    }

    @PutMapping("/{uuid}")
    public Author update(@PathVariable UUID uuid, @RequestBody Author author) {
        return authorService.updateAuthor(uuid, author);
    }

    @DeleteMapping("/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID uuid) {
        authorService.deleteAuthor(uuid);
    }
}
