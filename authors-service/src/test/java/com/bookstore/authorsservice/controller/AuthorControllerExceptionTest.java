package com.bookstore.authorsservice.controller;

import com.bookstore.authorsservice.dto.request.AuthorCreateRequest;
import com.bookstore.authorsservice.exception.DuplicateResourceException;
import com.bookstore.authorsservice.exception.GlobalExceptionHandler;
import com.bookstore.authorsservice.exception.ResourceNotFoundException;
import com.bookstore.authorsservice.service.AuthorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthorController.class)
@Import(GlobalExceptionHandler.class)
class AuthorControllerExceptionTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthorService authorService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID uuid;

    @BeforeEach
    void setup() {
        uuid = UUID.randomUUID();
    }

    /* ================= NOT FOUND ================= */

    @Test
    void getById_shouldReturn404_whenAuthorNotFound() throws Exception {

        when(authorService.getAuthorById(uuid))
                .thenThrow(new ResourceNotFoundException("Không tìm thấy tác giả"));

        mockMvc.perform(get("/authors/{uuid}", uuid))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Không tìm thấy tác giả"));
    }

    /* ================= DUPLICATE ================= */

    @Test
    void create_shouldReturn409_whenDuplicateAuthor() throws Exception {

        AuthorCreateRequest request = AuthorCreateRequest.builder()
                .fullname("Nam Cao")
                .build();

        when(authorService.createAuthor(any()))
                .thenThrow(new DuplicateResourceException("Tác giả đã tồn tại: Nam Cao"));

        mockMvc.perform(post("/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message")
                        .value("Tác giả đã tồn tại: Nam Cao"));
    }

    /* ================= VALIDATION ================= */

    @Test
    void create_shouldReturn400_whenValidationFail() throws Exception {

        AuthorCreateRequest request = AuthorCreateRequest.builder()
                .fullname("") // @NotBlank
                .build();

        mockMvc.perform(post("/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors.fullname").exists());
    }
}