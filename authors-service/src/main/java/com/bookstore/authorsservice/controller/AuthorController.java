package com.bookstore.authorsservice.controller;

import com.bookstore.authorsservice.dto.request.AuthorCreateRequest;
import com.bookstore.authorsservice.dto.request.AuthorUpdateRequest;
import com.bookstore.authorsservice.dto.response.AuthorResponse;
import com.bookstore.authorsservice.service.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/authors")
@RequiredArgsConstructor
public class AuthorController {
    private final AuthorService authorService;

    @GetMapping
    public Page<AuthorResponse> getAll(Pageable pageable) {
        return authorService.getActiveAuthors(pageable);
    }

    @GetMapping("/{uuid}")
    public AuthorResponse getById(@PathVariable UUID uuid) {
        return authorService.getAuthorById(uuid);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AuthorResponse create(@RequestBody AuthorCreateRequest authorCreaterequest) {
        return authorService.createAuthor(authorCreaterequest);
    }

    @PutMapping("/{uuid}")
    public AuthorResponse update(
            @PathVariable UUID uuid,
            @RequestBody AuthorUpdateRequest authorUpdateRequest) {
        return authorService.updateAuthor(uuid, authorUpdateRequest);
    }

    @DeleteMapping("/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID uuid) {
        authorService.deleteAuthor(uuid);
    }
}
