package com.pospick.pospick.dto.request.auth;

import lombok.Getter;

@Getter
public class SignupRequest {
    private String loginId;
    private String password;
    private String name;
    private String email;
    private String role; // ORGANIZER, SELLER
}
