package ru.otus.server;

import java.sql.SQLException;

public interface AuthenticationService {
    String getNicknameByLoginAndPassword(String login, String password) throws SQLException;
    boolean register2(String login, String password, String nickname, PersonRole personRole) throws SQLException;
    boolean isLoginAlreadyExist(String login) throws SQLException;
    boolean isNicknameAlreadyExist(String nickname) throws SQLException;
    boolean isAdminOnline(String nickname) throws SQLException;
    boolean register(String login, String password, String nickname, int role_id, int id) throws SQLException;
}
