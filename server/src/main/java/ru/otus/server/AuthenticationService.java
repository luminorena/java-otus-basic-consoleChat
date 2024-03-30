package ru.otus.server;

public interface AuthenticationService {
    String getNicknameByLoginAndPassword(String login, String password);
    boolean register(String login, String password, String nickname);
    boolean isLoginAlreadyExist(String login);
    boolean isNicknameAlreadyExist(String nickname);
}
