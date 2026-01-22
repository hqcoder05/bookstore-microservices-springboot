package com.bookstore.authorsservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
