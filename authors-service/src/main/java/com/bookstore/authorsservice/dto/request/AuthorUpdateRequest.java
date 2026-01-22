package com.bookstore.authorsservice.dto.request;

import jakarta.validation.constraints.Size;
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

    @Size(max = 255, message = "Tên tác giả tối đa 255 ký tự")
    private String fullname;

    @Size(max = 255, message = "Bút danh tối đa 255 ký tự")
    private String penName;

    private LocalDate dateOfBirth;

    private LocalDate dateOfDeath;

    @Size(max = 100, message = "Quốc tịch tối đa 100 ký tự")
    private String nationality;

    @Size(max = 2000, message = "Tiểu sử tối đa 2000 ký tự")
    private String biography;

    private String avatarUrl;

    private Boolean verified;
}
