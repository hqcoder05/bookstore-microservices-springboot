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

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaConfiguration.class)
public class AuthorRepositoryTest {

    @Autowired
    private AuthorRepository authorRepository;

    @Test
    @DisplayName("Test check trung ten (khong phan biet HOA - thuong")
    void exitsByFullNameIgnoreCase() {
        Author author = Author.builder()
                .fullname("Nam Cao")
                .status(Status.ACTIVE)
                .build();

        authorRepository.save(author);
        boolean existsExact = authorRepository.existsByFullnameIgnoreCase("Nam Cao");
        boolean existsLower = authorRepository.existsByFullnameIgnoreCase("nam cao");
        boolean existsUpper = authorRepository.existsByFullnameIgnoreCase("NAM CAO");
        boolean notExists = authorRepository.existsByFullnameIgnoreCase("Ngo Tat To");

        assertThat(existsExact).isTrue();
        assertThat(existsLower).isTrue();
        assertThat(existsUpper).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Tim theo trang thai ACTIVE")
    void findByStatus() {
        Author activeAuthor = Author.builder().fullname("Active Guy").status(Status.ACTIVE).build();
        Author inactiveAuthor = Author.builder().fullname("Inactive Guy").status(Status.INACTIVE).build();
        authorRepository.save(activeAuthor);
        authorRepository.save(inactiveAuthor);

        List<Author> result = authorRepository.findByStatus(Status.ACTIVE);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFullname()).isEqualTo("Active Guy");
    }

    @Test
    @DisplayName("Test tim bang UUID va Status")
    void findByUuidAndStatus() {
        Author author = Author.builder()
                .fullname("Xuan Quynh")
                .status(Status.ACTIVE)
                .build();
        Author savedAuthor = authorRepository.save(author);

        Optional<Author> found = authorRepository.findByUuidAndStatus(savedAuthor.getUuid(), Status.ACTIVE);
        Optional<Author> notFoundStatus = authorRepository.findByUuidAndStatus(savedAuthor.getUuid(), Status.INACTIVE);

        assertThat(found).isPresent();
        assertThat(found.get().getFullname()).isEqualTo("Xuan Quynh");

        assertThat(notFoundStatus).isEmpty();
    }

    @Test
    @DisplayName("Test search ten gan dung (Containing)")
    void searchByName() {
        Author a1 = Author.builder().fullname("Nguyen Nhat Anh").status(Status.ACTIVE).build();
        Author a2 = Author.builder().fullname("Nguyen Huy Thiep").status(Status.ACTIVE).build();
        Author a3 = Author.builder().fullname("To Hoai").status(Status.ACTIVE).build();

        authorRepository.saveAll(List.of(a1, a2, a3));
        List<Author> result = authorRepository.findByFullnameContainingIgnoreCaseAndStatus("nguyen", Status.ACTIVE);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Author::getFullname)
                .containsExactlyInAnyOrder("Nguyen Nhat Anh", "Nguyen Huy Thiep");
    }

    @Test
    @DisplayName("Test phan trang")
    void testPagination() {
        for (int i = 1; i <= 5; i++) {
            authorRepository.save(Author.builder().fullname("Author " + i).status(Status.ACTIVE).build());
        }

        //Lay trang 0, size 3
        Page<Author> page = authorRepository.findByStatus(Status.ACTIVE, PageRequest.of(0, 3));

        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.getContent()).hasSize(3);
    }
}
