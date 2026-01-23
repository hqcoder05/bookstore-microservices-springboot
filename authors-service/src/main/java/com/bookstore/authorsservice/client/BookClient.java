package com.bookstore.authorsservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "books-service")
public interface BookClient {

    @GetMapping("/books/author/{authorId}")
    List<BookResponse> getBooksByAuthor(UUID authorId);
}
