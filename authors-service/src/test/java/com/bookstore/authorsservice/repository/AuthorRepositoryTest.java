package com.bookstore.authorsservice.repository;

import com.bookstore.authorsservice.config.JpaConfiguration;
import com.bookstore.authorsservice.entity.Author;
import com.bookstore.authorsservice.enums.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaConfiguration.class)
class AuthorRepositoryTest {

    @Autowired
    private AuthorRepository authorRepository;

    @Test
    @DisplayName("Test check trùng tên (không phân biệt HOA - thường)")
    void existsByFullnameIgnoreCase() {
        Author author = Author.builder()
                .fullname("Nam Cao")
                .status(Status.ACTIVE)
                .build();

        authorRepository.save(author);

        assertThat(authorRepository.existsByFullnameIgnoreCase("Nam Cao")).isTrue();
        assertThat(authorRepository.existsByFullnameIgnoreCase("nam cao")).isTrue();
        assertThat(authorRepository.existsByFullnameIgnoreCase("NAM CAO")).isTrue();
        assertThat(authorRepository.existsByFullnameIgnoreCase("Ngo Tat To")).isFalse();
    }

    @Test
    @DisplayName("Tìm tác giả theo trạng thái ACTIVE")
    void findByStatus() {
        authorRepository.save(
                Author.builder().fullname("Active Guy").status(Status.ACTIVE).build()
        );
        authorRepository.save(
                Author.builder().fullname("Inactive Guy").status(Status.INACTIVE).build()
        );

        List<Author> result = authorRepository.findByStatus(Status.ACTIVE);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(Status.ACTIVE);
    }

    @Test
    @DisplayName("Tìm theo UUID và Status")
    void findByUuidAndStatus() {
        Author saved = authorRepository.save(
                Author.builder()
                        .fullname("Xuân Quỳnh")
                        .status(Status.ACTIVE)
                        .build()
        );

        UUID uuid = saved.getUuid();

        Optional<Author> found =
                authorRepository.findByUuidAndStatus(uuid, Status.ACTIVE);

        Optional<Author> notFound =
                authorRepository.findByUuidAndStatus(uuid, Status.INACTIVE);

        assertThat(found).isPresent();
        assertThat(found.get().getFullname()).isEqualTo("Xuân Quỳnh");
        assertThat(notFound).isEmpty();
    }

    @Test
    @DisplayName("Search tên gần đúng (Containing + IgnoreCase + Status)")
    void searchByNameContainingIgnoreCaseAndStatus() {
        authorRepository.saveAll(List.of(
                Author.builder().fullname("Nguyen Nhat Anh").status(Status.ACTIVE).build(),
                Author.builder().fullname("Nguyen Huy Thiep").status(Status.ACTIVE).build(),
                Author.builder().fullname("To Hoai").status(Status.ACTIVE).build()
        ));

        List<Author> result =
                authorRepository.findByFullnameContainingIgnoreCaseAndStatus(
                        "nguyen", Status.ACTIVE
                );

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(Author::getFullname)
                .containsExactlyInAnyOrder(
                        "Nguyen Nhat Anh",
                        "Nguyen Huy Thiep"
                );
    }

    @Test
    @DisplayName("Test phân trang theo Status")
    void findByStatusWithPagination() {
        for (int i = 1; i <= 5; i++) {
            authorRepository.save(
                    Author.builder()
                            .fullname("Author " + i)
                            .status(Status.ACTIVE)
                            .build()
            );
        }

        Page<Author> page =
                authorRepository.findByStatus(
                        Status.ACTIVE,
                        PageRequest.of(0, 3)
                );

        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.getContent()).hasSize(3);
    }
}
