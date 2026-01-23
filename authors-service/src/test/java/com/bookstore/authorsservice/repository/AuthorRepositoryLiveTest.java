package com.bookstore.authorsservice.repository;

import com.bookstore.authorsservice.config.JpaConfiguration;
import com.bookstore.authorsservice.entity.Author;
import com.bookstore.authorsservice.enums.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JpaConfiguration.class)
@ActiveProfiles("test")
class AuthorRepositoryLiveTest {
    @Container
    @ServiceConnection
    static MariaDBContainer<?> mariadb = new MariaDBContainer<>("mariadb:10.11");

    @Autowired
    private AuthorRepository authorRepository;

    @Test
    @DisplayName("Kết nối thành công tới MariaDB thật và thực hiện lưu/tìm kiếm")
    void testSaveAndFind_RealDB() {
        System.out.println("JDBC URL đang chạy: " + mariadb.getJdbcUrl());
        System.out.println("Username: " + mariadb.getUsername());

        // 1. Tạo Data
        Author author = Author.builder()
                .fullname("Real MariaDB Author")
                .status(Status.ACTIVE)
                .verified(true)
                .build();

        Author saved = authorRepository.save(author);

        // 2. Kiểm tra ID được sinh ra (UUID)
        assertThat(saved.getUuid()).isNotNull();

        // 3. Tìm kiếm lại từ DB thật
        Optional<Author> found = authorRepository.findById(saved.getUuid());
        assertThat(found).isPresent();
        assertThat(found.get().getFullname()).isEqualTo("Real MariaDB Author");
    }

    @Test
    @DisplayName("Kiểm tra tính năng Auditing (CreatedAt/UpdatedAt) trên DB thật")
    void testAuditing_RealDB() {
        Author author = Author.builder()
                .fullname("Auditing Check")
                .status(Status.ACTIVE)
                .build();

        Author saved = authorRepository.save(author);

        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }
}