package com.consignment.consignmentapplication.authentication;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonAlias;

public class UserDto {
    private String username;
    private String password;

    @JsonAlias("role")
    private Set<String> roles;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }
}
