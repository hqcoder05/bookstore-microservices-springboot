package com.bookstore.authorsservice.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AuthorCreateRequest {
    private String fullname;
    private String penName;
    private LocalDate dateOfBirth;
    private String nationality;
    private String biography;
    private String avatarUrl;
}
