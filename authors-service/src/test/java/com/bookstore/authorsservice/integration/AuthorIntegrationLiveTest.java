package com.bookstore.authorsservice.integration;

import com.bookstore.authorsservice.client.BookClient;
import com.bookstore.authorsservice.dto.request.AuthorCreateRequest;
import com.bookstore.authorsservice.repository.AuthorRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.cloud.loadbalancer.enabled=false", // Tắt load balancer để đỡ log warn
        "spring.cloud.discovery.enabled=false"     // Tắt consul discovery
})
class AuthorIntegrationLiveTest {

    @Container
    @ServiceConnection
    static MariaDBContainer<?> mariadb = new MariaDBContainer<>("mariadb:10.11");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BookClient bookClient;

    @BeforeEach
    void setUp() {
        authorRepository.deleteAll();
    }

    @Test
    @DisplayName("E2E: Gọi API tạo tác giả -> Lưu thành công vào MariaDB thật")
    void createAuthor_ShouldPersistToMariaDB() throws Exception {
        AuthorCreateRequest request = AuthorCreateRequest.builder()
                .fullname("Integration Test Author")
                .nationality("Vietnam")
                .build();

        mockMvc.perform(post("/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.uuid").exists());

        // Assert: Kiểm tra trực tiếp trong DB thật xem data có chưa
        assertThat(authorRepository.existsByFullnameIgnoreCase("Integration Test Author")).isTrue();
    }
}