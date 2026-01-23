package com.bookstore.authorsservice.integration;

import com.bookstore.authorsservice.dto.request.AuthorCreateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class AuthorDeleteIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deleteAuthor_shouldSoftDelete_andReturn404WhenGet() throws Exception {

        AuthorCreateRequest request = AuthorCreateRequest.builder()
                .fullname("Tô Hoài")
                .nationality("Vietnam")
                .build();

        String createResponse = mockMvc.perform(
                        post("/authors")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.uuid").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String uuid = objectMapper
                .readTree(createResponse)
                .get("data")
                .get("uuid")
                .asText();

        mockMvc.perform(delete("/authors/{uuid}", uuid))
                .andExpect(status().isOk());
        mockMvc.perform(get("/authors/{uuid}", uuid))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Không tìm thấy tác giả"));
    }
}
