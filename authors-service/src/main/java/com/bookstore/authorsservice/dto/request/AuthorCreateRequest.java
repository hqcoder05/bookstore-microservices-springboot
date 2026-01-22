package com.bookstore.authorsservice.dto.request;

import jdk.jshell.ImportSnippet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorCreateRequest {
    private String fullname;
    private String penName;
    private LocalDate dateOfBirth;
    private String nationality;
    private String biography;
    private String avatarUrl;

}
