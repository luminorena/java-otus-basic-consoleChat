package ru.otus.server;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InMemoryAuthenticationService implements AuthenticationService {
    private class User {
        private String login;
        private String password;
        private String nickname;
        private PersonRole personRole;

        public User(String login, String password, String nickname, PersonRole personRole) {
            this.login = login;
            this.password = password;
            this.nickname = nickname;
            this.personRole = personRole;
        }
    }

    private List<User> users;

    public InMemoryAuthenticationService() {
        this.users = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            this.users.add(new User("login" + i, "pass" + i, "nick" + i, PersonRole.USER));
        }
        this.users.add(new User("admin", "admin", "admin", PersonRole.ADMIN));
    }

    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {
        for (User u : users) {
            if (u.login.equals(login) && u.password.equals(password)) {
                return u.nickname;
            }
        }
        return null;
    }

    @Override
    public boolean register2(String login, String password, String nickname, PersonRole personRole) {
        if (isLoginAlreadyExist(login)) {
            return false;
        }
        if (isNicknameAlreadyExist(nickname)) {
            return false;
        }
        users.add(new User(login, password, nickname, personRole));
        return true;
    }

    @Override
    public boolean isLoginAlreadyExist(String login) {
        for (User u : users) {
            if (u.login.equals(login)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isNicknameAlreadyExist(String nickname) {
        for (User u : users) {
            if (u.nickname.equals(nickname)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isAdminOnline(String nickname) {
        for (User u : users) {
            if (u.nickname.equals(nickname)) {
                if (u.personRole.equals(PersonRole.ADMIN)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean register(String login, String password, String nickname, int role_id, int id) throws SQLException {
        return false;
    }
}

