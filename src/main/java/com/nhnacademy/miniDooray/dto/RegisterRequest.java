package com.nhnacademy.miniDooray.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegisterRequest {

    @NotNull
    @Length(min = 1, max = 20)
    private String id;

    @NotNull
    @Length(min = 1, max = 255)
    private String password;

    @NotNull
    @Email
    @Length(max = 50)
    private String email;

    @NotNull
    @Length(min = 2, max = 20)
    private String name;
}
