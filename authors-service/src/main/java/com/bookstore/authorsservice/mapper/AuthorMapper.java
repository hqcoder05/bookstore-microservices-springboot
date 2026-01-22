package com.bookstore.authorsservice.mapper;

import com.bookstore.authorsservice.dto.response.AuthorResponse;
import com.bookstore.authorsservice.entity.Author;

public class AuthorMapper {

    public static AuthorResponse authorToAuthorResponse(Author author) {
        return AuthorResponse.builder()
                .uuid(author.getUuid())
                .fullname(author.getFullname())
                .penName(author.getPenName())
                .nationality(author.getNationality())
                .avatarUrl(author.getAvatarUrl())
                .biography(author.getBiography())
                .verified(author.isVerified())
                .books(null)
                .build();
    }
}

