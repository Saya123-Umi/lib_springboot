package com.southwind.form;

import lombok.Data;

@Data
public class UserRegisterForm {
    private String username;
    private String password;
    private String confirmPassword;
}
