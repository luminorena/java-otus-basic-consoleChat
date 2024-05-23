package ru.otus.server;

import java.sql.SQLException;

public interface AuthenticationService {
    String getNicknameByLoginAndPassword(String login, String password) throws SQLException;
    boolean isLoginAlreadyExist(String login) throws SQLException;
    boolean isNicknameAlreadyExist(String nickname) throws SQLException;
    boolean isAdminOnline(String nickname) throws SQLException;
    int register(String login, String password, String nickname, int role) throws SQLException;
}
