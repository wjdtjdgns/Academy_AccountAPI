package com.nhnacademy.miniDooray.dto;

import com.nhnacademy.miniDooray.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class MemberDto {
    private String id;
    private String password;
    private String email;
    private String name;
    private Status status;
}
