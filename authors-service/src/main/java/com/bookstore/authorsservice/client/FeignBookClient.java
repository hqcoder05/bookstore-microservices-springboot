package com.bookstore.authorsservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@Profile("prod")
@FeignClient(
        name = "book-service",
        url = "${book-service.url}"
)
public interface FeignBookClient extends BookClient {

    @Override
    @GetMapping("/books/author/{authorId}")
    List<BookResponse> getBooksByAuthor(@PathVariable UUID authorId);
}
