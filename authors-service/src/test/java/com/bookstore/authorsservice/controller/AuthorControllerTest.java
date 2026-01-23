package com.bookstore.authorsservice.controller;

import com.bookstore.authorsservice.dto.request.AuthorCreateRequest;
import com.bookstore.authorsservice.dto.request.AuthorUpdateRequest;
import com.bookstore.authorsservice.dto.response.AuthorResponse;
import com.bookstore.authorsservice.service.AuthorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthorController.class)
class AuthorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthorService authorService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID uuid;
    private AuthorResponse authorResponse;

    @BeforeEach
    void setup() {
        uuid = UUID.randomUUID();

        authorResponse = AuthorResponse.builder()
                .uuid(uuid)
                .fullname("Nguyễn Nhật Ánh")
                .penName("NNA")
                .nationality("Vietnam")
                .biography("Nhà văn Việt Nam")
                .verified(true)
                .build();
    }

    @Test
    void getAll_shouldReturnPage() throws Exception {
        Page<AuthorResponse> page =
                new PageImpl<>(List.of(authorResponse));

        when(authorService.getActiveAuthors(any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/authors")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].fullname")
                        .value("Nguyễn Nhật Ánh"));
    }

    @Test
    void getById_shouldReturnAuthor() throws Exception {
        when(authorService.getAuthorById(uuid))
                .thenReturn(authorResponse);

        mockMvc.perform(get("/authors/{uuid}", uuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.penName").value("NNA"));
    }

    @Test
    void create_shouldReturnCreatedAuthor() throws Exception {
        AuthorCreateRequest request = AuthorCreateRequest.builder()
                .fullname("Nguyễn Nhật Ánh")
                .penName("NNA")
                .nationality("Vietnam")
                .build();

        when(authorService.createAuthor(any()))
                .thenReturn(authorResponse);

        mockMvc.perform(post("/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.fullname")
                        .value("Nguyễn Nhật Ánh"));
    }

    @Test
    void update_shouldReturnUpdatedAuthor() throws Exception {
        AuthorUpdateRequest request = AuthorUpdateRequest.builder()
                .fullname("Updated Name")
                .build();

        when(authorService.updateAuthor(eq(uuid), any()))
                .thenReturn(authorResponse);

        mockMvc.perform(put("/authors/{uuid}", uuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void delete_shouldReturnNoContent() throws Exception {
        doNothing().when(authorService).deleteAuthor(uuid);

        mockMvc.perform(delete("/authors/{uuid}", uuid))
                .andExpect(status().isOk());
    }
}
