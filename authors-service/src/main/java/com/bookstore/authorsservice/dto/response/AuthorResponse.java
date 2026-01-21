package com.bookstore.authorsservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorResponse {
    private UUID uuid;
    private String fullname;
    private String penName;
    private String nationality;
    private String biography;
    private String avatarUrl;
    private boolean verified;
}
