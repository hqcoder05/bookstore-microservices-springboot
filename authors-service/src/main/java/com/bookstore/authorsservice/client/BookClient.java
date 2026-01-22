package com.bookstore.authorsservice.client;

import java.util.List;
import java.util.UUID;

public interface BookClient {
    List<BookResponse> getBooksByAuthor(UUID authorId);
}
