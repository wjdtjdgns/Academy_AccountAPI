package com.nhnacademy.miniDooray.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class Member {
    @Id
    private Long id;

    @NotNull
    @Length(min = 1, max = 255)
    @Setter
    private String password;

    @NotNull
    @Email
    @Length(max = 50)
    @Setter
    private String email;

    @NotNull
    @Length(min = 2, max = 20)
    @Setter
    private String name;

    @NotNull
    @Length(max = 20)
    @Setter
    private Status status;
}
