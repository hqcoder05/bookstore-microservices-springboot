package com.bookstore.authorsservice.service;

import com.bookstore.authorsservice.dto.request.AuthorCreateRequest;
import com.bookstore.authorsservice.dto.response.AuthorResponse;
import com.bookstore.authorsservice.entity.Author;
import com.bookstore.authorsservice.enums.Status;
import com.bookstore.authorsservice.repository.AuthorRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private AuthorService authorService;

    @Test
    @DisplayName("Create Author | Thành công")
    void createAuthor_success() {
        AuthorCreateRequest request = new AuthorCreateRequest();
        request.setFullname("Nam Cao");
        request.setNationality("Vietnam");

        Author savedAuthor = Author.builder()
                .uuid(UUID.randomUUID())
                .fullname("Nam Cao")
                .nationality("Vietnam")
                .status(Status.ACTIVE)
                .build();

        when(authorRepository.existsByFullnameIgnoreCase("Nam Cao")).thenReturn(false);
        when(authorRepository.save(any(Author.class))).thenReturn(savedAuthor);

        AuthorResponse response = authorService.createAuthor(request);

        assertNotNull(response);
        assertEquals("Nam Cao", response.getFullname());
        assertEquals("Vietnam", response.getNationality());

        verify(authorRepository).existsByFullnameIgnoreCase("Nam Cao");
        verify(authorRepository).save(any(Author.class));
    }

    @Test
    @DisplayName("Create Author | Thất bại do trùng tên")
    void createAuthor_fail_duplicateName() {
        AuthorCreateRequest request = new AuthorCreateRequest();
        request.setFullname("To Hoai");

        when(authorRepository.existsByFullnameIgnoreCase("To Hoai")).thenReturn(true);

        RuntimeException exception =
                assertThrows(RuntimeException.class,
                        () -> authorService.createAuthor(request));

        assertEquals("Tác giả đã tồn tại: To Hoai", exception.getMessage());

        verify(authorRepository).existsByFullnameIgnoreCase("To Hoai");
        verify(authorRepository, never()).save(any());
    }

    @Test
    @DisplayName("Get Author By ID | Thành công")
    void getAuthorById_success() {
        UUID uuid = UUID.randomUUID();

        Author author = Author.builder()
                .uuid(uuid)
                .fullname("Kim Lan")
                .status(Status.ACTIVE)
                .build();

        when(authorRepository.findByUuidAndStatus(uuid, Status.ACTIVE))
                .thenReturn(Optional.of(author));

        AuthorResponse response = authorService.getAuthorById(uuid);

        assertNotNull(response);
        assertEquals(uuid, response.getUuid());
        assertEquals("Kim Lan", response.getFullname());

        verify(authorRepository).findByUuidAndStatus(uuid, Status.ACTIVE);
    }

    @Test
    @DisplayName("Get Author By ID | Không tìm thấy")
    void getAuthorById_notFound() {
        UUID uuid = UUID.randomUUID();

        when(authorRepository.findByUuidAndStatus(uuid, Status.ACTIVE))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(RuntimeException.class,
                        () -> authorService.getAuthorById(uuid));

        assertEquals("Không tìm thấy tác giả", exception.getMessage());

        verify(authorRepository).findByUuidAndStatus(uuid, Status.ACTIVE);
    }
}
