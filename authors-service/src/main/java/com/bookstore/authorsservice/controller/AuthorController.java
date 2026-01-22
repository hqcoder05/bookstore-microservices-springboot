package com.bookstore.authorsservice.controller;

import com.bookstore.authorsservice.dto.request.AuthorCreateRequest;
import com.bookstore.authorsservice.dto.request.AuthorUpdateRequest;
import com.bookstore.authorsservice.dto.response.ApiResponse;
import com.bookstore.authorsservice.dto.response.AuthorResponse;
import com.bookstore.authorsservice.service.AuthorService;
import jakarta.validation.Valid;
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

    /* ================= READ ================= */

    @GetMapping
    public ApiResponse<Page<AuthorResponse>> getAll(Pageable pageable) {
        return ApiResponse.success(
                200,
                "Lấy danh sách tác giả thành công",
                authorService.getActiveAuthors(pageable)
        );
    }

    @GetMapping("/{uuid}")
    public ApiResponse<AuthorResponse> getById(@PathVariable UUID uuid) {
        return ApiResponse.success(
                200,
                "Lấy tác giả thành công",
                authorService.getAuthorById(uuid)
        );
    }

    /* ================= CREATE ================= */

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AuthorResponse> create(
            @Valid @RequestBody AuthorCreateRequest request
    ) {
        return ApiResponse.success(
                201,
                "Tạo tác giả thành công",
                authorService.createAuthor(request)
        );
    }

    /* ================= UPDATE ================= */

    @PutMapping("/{uuid}")
    public ApiResponse<AuthorResponse> update(
            @PathVariable UUID uuid,
            @RequestBody AuthorUpdateRequest request
    ) {
        return ApiResponse.success(
                200,
                "Cập nhật tác giả thành công",
                authorService.updateAuthor(uuid, request)
        );
    }

    /* ================= DELETE ================= */

    @DeleteMapping("/{uuid}")
    public ApiResponse<Void> delete(@PathVariable UUID uuid) {
        authorService.deleteAuthor(uuid);
        return ApiResponse.success(
                200,
                "Xoá tác giả thành công",
                null
        );
    }
}

