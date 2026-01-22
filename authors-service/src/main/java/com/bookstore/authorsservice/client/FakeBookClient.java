package com.bookstore.authorsservice.client;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Profile({"dev", "test"})
public class FakeBookClient {
    @Override
    public List<BookResponse> getBooksByAuthor(UUID authorId) {
        return List.of(
                new BookResponse(UUID.randomUUID(), "Fake Book 1"),
                new BookResponse(UUID.randomUUID(), "Fake Book 2")
        );
    }
}
