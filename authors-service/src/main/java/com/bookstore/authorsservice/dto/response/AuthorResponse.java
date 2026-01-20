package com.bookstore.authorsservice.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class AuthorResponse {
    private UUID uuid;
    private String fullname;
    private String penName;
    private String nationality;
    private String biography;
    private String avatarUrl;
    private boolean verified;
}
