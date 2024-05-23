package ru.otus.server;

public enum PersonRole {
    USER("user"),
    ADMIN ("admin");

    private String user;

    PersonRole(String user) {
        this.user = user;
    }

    public String getUser() {
        return this.user;
    }
}
