package com.socialnetwork.domain;

public class Credentials extends Entity<Long>{
    private String username;
    private String hashedPassword;
    private Long id;

    public Credentials(String username, String hashedPassword, Long id) {
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
