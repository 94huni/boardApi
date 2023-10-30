package com.example.erdstudy.dto;

import lombok.Data;

@Data
public class SignUpForm {
    private String name;
    private String email;
    private String password;
    private String validPassword;
}
