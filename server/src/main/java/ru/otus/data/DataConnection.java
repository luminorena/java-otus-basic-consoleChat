package ru.otus.data;

import ru.otus.server.AuthenticationService;
import ru.otus.server.PersonRole;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class DataConnection implements AuthenticationService {
    private final Connection connection;

    private static final String GET_USER_CREDENTIALS = "select login, password, nickname from users where login = ? and password = ?";
    private static final String GET_LOGIN = "select login from users";
    private static final String GET_NICKNAME = "select nickname from users";
    private static final String GET_NICKNAME_AND_ROLE = "select role_name, nickname\n" +
            "from user_to_role ur\n" +
            "left join roles r ON r.id=ur.role_id\n" +
            "join users u on u.id = ur.user_id where nickname = ?";
    private static final String INSERT_USER_DATA = "DO $$\n" +
            "DECLARE\n" +
            "  param int;\n" +
            "BEGIN\n" +
            "  insert into users (id, login, password, nickname) values(?, ?, ?, ?);\n" +
            "  SELECT MAX(user_id) INTO param FROM user_to_role;\n" +
            "  INSERT INTO user_to_role (user_id, role_id) VALUES (param+1, ?);\n" +
            "END $$";

    public static void main(String[] args) throws SQLException, IOException {
        DataConnection dataConnection = new DataConnection();
        System.out.println(dataConnection.isAdminOnline("admin"));
        System.out.println(dataConnection.isNicknameAlreadyExist("admin1"));
        System.out.println(dataConnection.isLoginAlreadyExist("admin99@mail.ru"));
        System.out.println(dataConnection.getNicknameByLoginAndPassword("login1@mail.ru", "pass1"));
        dataConnection.register("login6@mail.ru", "pass6", "login6", 2, 8);
    }

    public DataConnection() throws SQLException, IOException {
        Properties props = new Properties();
        props.load(new FileInputStream("credentials.properties"));
        String user = props.getProperty("user");
        String password = props.getProperty("password");
        String databaseUrl = props.getProperty("databaseUrl");
        connection = DriverManager.getConnection(databaseUrl, user, password);
    }

    private static void close(Connection connection, Statement statement, ResultSet resultSet)
            throws SQLException {
        if (resultSet != null) {
            resultSet.close();
        }
        if (statement != null) {
            statement.close();
        }
        if (connection != null) {
            connection.close();
        }
    }


    @Override
    public String getNicknameByLoginAndPassword(String loginValue, String passwordValue) throws SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        User user = null;
        try {
            preparedStatement = connection.prepareStatement(GET_USER_CREDENTIALS);
            resultSet = preparedStatement.executeQuery(GET_USER_CREDENTIALS);
            preparedStatement.setString(1, loginValue);
            preparedStatement.setString(2, passwordValue);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String loginParam = resultSet.getString("login");
                String passwordParam = resultSet.getString("password");
                String nicknameParam = resultSet.getString("nickname");
                user = new User(loginParam, passwordParam);}
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(null, preparedStatement, resultSet);
        }

        return user.toString();
    }

    @Override
    public boolean register2(String login, String password, String nickname, PersonRole personRole) throws SQLException {
        return false;
    }

    @Override
    public boolean register(String login, String password, String nickname, int role_id, int id) throws SQLException {
        if (isLoginAlreadyExist(login)) {
            return false;
        }
        if (isNicknameAlreadyExist(nickname)) {
            return false;
        }
        PreparedStatement preparedStatement = null;
        try {
                preparedStatement = connection.prepareStatement(INSERT_USER_DATA);
                preparedStatement.setInt(1, id);
                preparedStatement.setString(2, login);
                preparedStatement.setString(3, password);
                preparedStatement.setString(4, nickname);
                preparedStatement.setInt(5, role_id);
                preparedStatement.executeUpdate();
        } finally {
            close(connection, preparedStatement,null);
        }
        return true;
    }

    @Override
    public boolean isLoginAlreadyExist(String login) throws SQLException {
        List<String> logins = new ArrayList<>();
        Statement statement = null;
        ResultSet resultSet = null;
        String loginResult;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(GET_LOGIN);
            while (resultSet.next()) {
                loginResult = resultSet.getString("login");
                logins.add(loginResult);
            }
            return logins.contains(login);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(null, statement, resultSet);
        }

        return false;
    }

    @Override
    public boolean isNicknameAlreadyExist(String nickname) throws SQLException {
        List<String> nicknames = new ArrayList<>();
        Statement statement = null;
        ResultSet resultSet = null;
        String nickResult;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(GET_NICKNAME);
            while (resultSet.next()) {
                nickResult = resultSet.getString("nickname");
                nicknames.add(nickResult);
            }
            return nicknames.contains(nickname);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(null, statement, resultSet);
        }

        return false;

    }

    @Override
    public boolean isAdminOnline(String nickname) throws SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String nickResult = null;
        String role = null;
        try {
            preparedStatement = connection.prepareStatement(GET_NICKNAME_AND_ROLE);
            resultSet = preparedStatement.executeQuery(GET_NICKNAME_AND_ROLE);
            preparedStatement.setString(1, nickname);
            while (resultSet.next()) {
                nickResult = resultSet.getString("nickname");
                role = resultSet.getString("role_name");
            }
            if (Objects.equals(nickResult, nickname)) {
                return role.equals(PersonRole.ADMIN.getUser());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(null, preparedStatement, resultSet);
        }

        return false;
    }


}

