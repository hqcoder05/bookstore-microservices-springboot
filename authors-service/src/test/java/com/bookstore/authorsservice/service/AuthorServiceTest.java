package com.bookstore.authorsservice.service;

import com.bookstore.authorsservice.client.BookClient;
import com.bookstore.authorsservice.dto.request.AuthorCreateRequest;
import com.bookstore.authorsservice.dto.request.AuthorUpdateRequest;
import com.bookstore.authorsservice.dto.response.AuthorResponse;
import com.bookstore.authorsservice.entity.Author;
import com.bookstore.authorsservice.enums.Status;
import com.bookstore.authorsservice.exception.DuplicateResourceException;
import com.bookstore.authorsservice.exception.ResourceNotFoundException;
import com.bookstore.authorsservice.repository.AuthorRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private BookClient bookClient;

    @InjectMocks
    private AuthorService authorService;

    /* ================= CREATE ================= */

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

        when(authorRepository.existsByFullnameIgnoreCase("Nam Cao"))
                .thenReturn(false);
        when(authorRepository.save(any(Author.class)))
                .thenReturn(savedAuthor);

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

        when(authorRepository.existsByFullnameIgnoreCase("To Hoai"))
                .thenReturn(true);

        DuplicateResourceException exception =
                assertThrows(DuplicateResourceException.class,
                        () -> authorService.createAuthor(request));

        assertEquals("Tác giả đã tồn tại: To Hoai", exception.getMessage());

        verify(authorRepository).existsByFullnameIgnoreCase("To Hoai");
        verify(authorRepository, never()).save(any());
    }

    /* ================= READ ================= */

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
        when(bookClient.getBooksByAuthor(uuid))
                .thenReturn(List.of());

        AuthorResponse response = authorService.getAuthorById(uuid);

        assertNotNull(response);
        assertEquals(uuid, response.getUuid());
        assertEquals("Kim Lan", response.getFullname());
        assertNotNull(response.getBooks());
        assertTrue(response.getBooks().isEmpty());

        verify(authorRepository).findByUuidAndStatus(uuid, Status.ACTIVE);
        verify(bookClient).getBooksByAuthor(uuid);
    }

    @Test
    @DisplayName("Get Author By ID | Không tìm thấy")
    void getAuthorById_notFound() {
        UUID uuid = UUID.randomUUID();

        when(authorRepository.findByUuidAndStatus(uuid, Status.ACTIVE))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(ResourceNotFoundException.class,
                        () -> authorService.getAuthorById(uuid));

        assertEquals("Không tìm thấy tác giả", exception.getMessage());

        verify(authorRepository).findByUuidAndStatus(uuid, Status.ACTIVE);
        verify(bookClient, never()).getBooksByAuthor(any());
    }

    /* ================= UPDATE ================= */

    @Test
    @DisplayName("Update Author | Thành công")
    void updateAuthor_success() {
        UUID uuid = UUID.randomUUID();

        Author author = Author.builder()
                .uuid(uuid)
                .fullname("Old Name")
                .status(Status.ACTIVE)
                .verified(false)
                .build();

        AuthorUpdateRequest request = new AuthorUpdateRequest();
        request.setFullname("New Name");
        request.setVerified(true);

        when(authorRepository.findByUuidAndStatus(uuid, Status.ACTIVE))
                .thenReturn(Optional.of(author));
        when(authorRepository.save(any(Author.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AuthorResponse response = authorService.updateAuthor(uuid, request);

        assertEquals(uuid, response.getUuid());
        assertEquals("New Name", response.getFullname());
        assertTrue(response.isVerified());

        verify(authorRepository).save(author);
    }

    @Test
    @DisplayName("Update Author | Không tìm thấy")
    void updateAuthor_notFound() {
        UUID uuid = UUID.randomUUID();

        when(authorRepository.findByUuidAndStatus(uuid, Status.ACTIVE))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> authorService.updateAuthor(uuid, new AuthorUpdateRequest()));
    }

    /* ================= DELETE ================= */

    @Test
    @DisplayName("Delete Author | Soft delete")
    void deleteAuthor_success() {
        UUID uuid = UUID.randomUUID();

        Author author = Author.builder()
                .uuid(uuid)
                .status(Status.ACTIVE)
                .build();

        when(authorRepository.findByUuidAndStatus(uuid, Status.ACTIVE))
                .thenReturn(Optional.of(author));

        authorService.deleteAuthor(uuid);

        assertEquals(Status.INACTIVE, author.getStatus());
        verify(authorRepository).save(author);
    }
}
