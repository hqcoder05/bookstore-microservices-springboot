package com.bookstore.authorsservice.controller;

import com.bookstore.authorsservice.dto.request.AuthorCreateRequest;
import com.bookstore.authorsservice.dto.request.AuthorUpdateRequest;
import com.bookstore.authorsservice.dto.response.AuthorResponse;
import com.bookstore.authorsservice.service.AuthorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
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
public class AuthorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthorService authorService;

    @Autowired
    private ObjectMapper objectMapper;

    //POST
    @Test
    @DisplayName("Create Author - Thanh Cong (201)")
    void createAuthorThanhCong() throws Exception {
        // 1. Chuẩn bị dữ liệu
        AuthorCreateRequest authorCreateRequest = new AuthorCreateRequest();
        authorCreateRequest.setFullname("Nam Cao");
        authorCreateRequest.setDateOfBirth(LocalDate.of(1915, 10, 29));

        AuthorResponse authorResponse = AuthorResponse.builder()
                .uuid(UUID.randomUUID())
                .fullname("Nam Cao")
                .build();

        when(authorService.createAuthor(any(AuthorCreateRequest.class))).thenReturn(authorResponse);

        // 2. Thực thi
        mockMvc.perform(
                        post("/authors")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(authorCreateRequest))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fullname").value("Nam Cao"));
    }

    //GET
    @Test
    @DisplayName("Get Author By ID - Thanh Cong (200)")
    void getAuthorById() throws Exception {
        UUID uuid = UUID.randomUUID();
        AuthorResponse authorResponse = AuthorResponse.builder()
                .uuid(uuid)
                .fullname("To Hoai")
                .build();

        when(authorService.getAuthorById(uuid)).thenReturn(authorResponse);

        mockMvc.perform(
                get("/authors/" + uuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullname").value("To Hoai"))
                .andExpect(jsonPath("$.uuid").value(uuid.toString()));
    }

    //PUT
    @Test
    @DisplayName("Update Author - Thanh Cong (200)")
    void updateAuthorThanhCong() throws Exception {
        UUID uuid = UUID.randomUUID();
        AuthorUpdateRequest authorUpdateRequest = new AuthorUpdateRequest();

        authorUpdateRequest.setFullname("Nam Cao (Ten Moi)");

        AuthorResponse authorResponse = AuthorResponse.builder()
                .uuid(uuid)
                .fullname("Nam Cao (Ten Moi")
                .build();

        when(authorService.updateAuthor(eq(uuid), any(AuthorUpdateRequest.class))).thenReturn(authorResponse);

        mockMvc.perform(
                put("/authors/" + uuid)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(authorUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullname").value("Nam Cao (Ten Moi"));
    }

    //DELETE
    @Test
    @DisplayName("Delete Author - Thanh Cong(204")
    void deleteAuthorThanhCong() throws Exception {
        UUID uuid = UUID.randomUUID();

        doNothing().when(authorService).deleteAuthor(eq(uuid));

        mockMvc.perform(
                delete("/authors/" + uuid))
                .andExpect(status().isNoContent());
    }

    //Get All voi Pagination
    @Test
    @DisplayName("Get All Authors - Success (200)")
    void getAll_Success() throws Exception {
        // GIVEN: Giả lập 1 trang kết quả
        AuthorResponse author1 = AuthorResponse.builder().fullname("Tac Gia 1").build();
        Page<AuthorResponse> page = new PageImpl<>(List.of(author1));

        when(authorService.getActiveAuthors(any(Pageable.class))).thenReturn(page);

        // WHEN & THEN
        mockMvc.perform(get("/authors")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].fullname").value("Tac Gia 1")) // Kiểm tra phần tử đầu tiên trong list
                .andExpect(jsonPath("$.totalElements").value(1));
    }
}