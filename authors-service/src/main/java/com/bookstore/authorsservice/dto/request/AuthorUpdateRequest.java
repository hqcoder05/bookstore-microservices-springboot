package com.bookstore.authorsservice.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AuthorUpdateRequest {
    private String fullname;
    private String penName;
    private String biography;
    private LocalDate dateOfBirth;
    private LocalDate dateOfDeath;
    private String nationality;
    private String avatarUrl;
    private Boolean verified;
}
