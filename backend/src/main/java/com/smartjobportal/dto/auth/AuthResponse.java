package com.smartjobportal.dto.auth;

import com.smartjobportal.entity.Role;

public class AuthResponse {

    private String token;
    private String email;
    private String fullName;
    private Role role;
    private Long userId;

    public AuthResponse(String token, String email, String fullName, Role role, Long userId) {
        this.token = token;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.userId = userId;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
