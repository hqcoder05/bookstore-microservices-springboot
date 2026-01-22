package com.bookstore.authorsservice.client;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class BookResponse {
    private UUID uuid;
    private String title;
}
