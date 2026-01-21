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
public class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;
    @InjectMocks
    private AuthorService authorService;

    @Test
    @DisplayName("Create Author - Thanh Cong")
    void createAuthorThanhCong() {
        AuthorCreateRequest authorCreateRequest = new AuthorCreateRequest();
        authorCreateRequest.setFullname("Nam Cao");
        authorCreateRequest.setNationality("Vietnam");

        Author saveAuthor = Author.builder()
                .uuid(UUID.randomUUID())
                .fullname("Nam Cao")
                .nationality("Vietnam")
                .status(Status.ACTIVE)
                .build();

        when(authorRepository.existsByFullnameIgnoreCase("Nam Cao")).thenReturn(false);
        when(authorRepository.save(any(Author.class))).thenReturn(saveAuthor);

        AuthorResponse authorResponse = authorService.createAuthor(authorCreateRequest);

        assertNotNull(authorResponse);
        assertEquals("Nam Cao", authorResponse.getFullname());
        assertEquals("Vietnam", authorResponse.getNationality());

        verify(authorRepository, times(1)).save(any(Author.class));
    }

    @Test
    @DisplayName("Create Author - That bai(Trung lap ten")
    void createAuthorThatBaiTrungLapTen() {
        AuthorCreateRequest authorCreateRequest = new AuthorCreateRequest();
        authorCreateRequest.setFullname("To Hoai");
        when(authorRepository.existsByFullnameIgnoreCase("To Hoai")).thenReturn(true);

        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
            authorService.createAuthor(authorCreateRequest);
        });

        assertEquals("Tác giả đã tồn tại: To Hoai", runtimeException.getMessage());
        verify(authorRepository, never()).save(any(Author.class));
    }

    @Test
    @DisplayName("Get Author By ID - Thanh Cong")
    void getAuthorByIdThanhCong() {
        UUID uuid = UUID.randomUUID();
        Author author = Author.builder()
                .uuid(uuid)
                .fullname("Kim Lan")
                .status(Status.ACTIVE)
                .build();

        when(authorRepository.findByUuidAndStatus(uuid, Status.ACTIVE)).thenReturn(Optional.of(author));

        AuthorResponse authorResponse = authorService.getAuthorById(uuid);

        assertEquals(uuid, authorResponse.getUuid());
        assertEquals("Kim Lan", authorResponse.getFullname());
    }
}
