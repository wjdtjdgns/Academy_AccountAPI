package com.nhnacademy.miniDooray.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class ErrorResponse {
    private String title;
    private int status;
    private LocalDateTime localDateTime;
}
