package com.bookstore.authorsservice.integration;

import com.bookstore.authorsservice.client.BookClient;
import com.bookstore.authorsservice.dto.request.AuthorCreateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class AuthorIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BookClient  bookClient;

    /* ================= CREATE + GET ================= */

    @Test
    void createAuthor_success() throws Exception {
        AuthorCreateRequest authorCreateRequest = AuthorCreateRequest.builder()
                .fullname("Nguyễn Nhật Ánh")
                .nationality("Vietnam")
                .build();

        String response = mockMvc.perform(
                        post("/authors")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(authorCreateRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String uuid = objectMapper
                .readTree(response)
                .get("data")
                .get("uuid")
                .asText();

        mockMvc.perform(get("/authors/{uuid}", uuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.fullname")
                        .value("Nguyễn Nhật Ánh"))
                .andExpect(jsonPath("$.data.nationality")
                        .value("Vietnam"));
    }

    /* ================= DUPLICATE ================= */

    @Test
    void createAuthor_shouldReturn409_whenDuplicate() throws Exception {
        AuthorCreateRequest request = AuthorCreateRequest.builder()
                .fullname("Nam Cao")
                .build();

        // First create
        mockMvc.perform(post("/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Duplicate
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
    void createAuthor_shouldReturn400_whenValidationFail() throws Exception {
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
