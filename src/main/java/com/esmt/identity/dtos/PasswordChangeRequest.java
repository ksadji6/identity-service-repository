package com.esmt.identity.dtos;

import lombok.Data;

@Data
public class PasswordChangeRequest {
    private String email;
    private String newPassword;

}
