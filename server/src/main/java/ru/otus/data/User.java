package ru.otus.data;

import ru.otus.server.PersonRole;

import java.util.ArrayList;
import java.util.List;

public class User {
    private int id;
    private String login;
    private String password;
    private String nickname;
    private Role role;
    private PersonRole personRole;

    private List<Role> roles = new ArrayList<>();


    public User(int id, String login, String password) {
        this.id = id;
        this.login = login;
        this.password = password;
    }

    public User(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public User(int id, String login, String password, String nickname, Role role) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.nickname = nickname;
        this.role = role;
    }

    public User(String login, String password, String nickname, PersonRole personRole) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.nickname = nickname;
        this.personRole = personRole;
    }


    public int getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getNickname() {
        return nickname;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public Role getRole() {
        return role;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "User{" +
                "login='" + login + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
