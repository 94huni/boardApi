package com.example.erdstudy.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomExceptionResponse {
    private int status;
    private String message;
}
