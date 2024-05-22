package ru.otus.data;

import ru.otus.server.AuthenticationService;
import ru.otus.server.PersonRole;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class DataConnection implements AuthenticationService {
    private final Connection connection;

    private static final String GET_USER_CREDENTIALS = "select login, password, nickname from public.users where login = ? and password = ?";
    private static final String GET_LOGIN = "select login from public.users";
    private static final String GET_NICKNAME = "select nickname from public.users";
    private static final String GET_NICKNAME_AND_ROLE = "select role_name, nickname\n" +
            "from user_to_role ur\n" +
            "left join roles r ON r.id=ur.role_id\n" +
            "join users u on u.id = ur.user_id where nickname = ?";
    private static final String INSERT_USER_DATA = "insert into public.users (login, password, nickname) values(?, ?, ?);";
    private static final String ADD_USER_ROLE = "insert into user_to_role (user_id, role_id) VALUES (?, ?)";


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
        //    resultSet = preparedStatement.executeQuery();
            preparedStatement.setString(1, loginValue);
            preparedStatement.setString(2, passwordValue);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String loginParam = resultSet.getString("login");
                String passwordParam = resultSet.getString("password");
                return resultSet.getString("nickname");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(null, preparedStatement, resultSet);
        }

        return null;
    }

    @Override
    public boolean register2(String login, String password, String nickname, PersonRole personRole) throws SQLException {
        return false;
    }

    @Override
    public int register(String login, String password, String nickname, int role) throws SQLException {
        int userId = Integer.parseInt(getUserId(login, password, nickname));
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
                preparedStatement = connection.prepareStatement(ADD_USER_ROLE);
                preparedStatement.setInt(1, userId);
                preparedStatement.setInt(2, role);
                preparedStatement.executeUpdate();
            while (resultSet.next()) {
                return resultSet.getInt("role_id");
            }

        } finally {
            close(connection, preparedStatement,resultSet);
        }
        return 0;
    }

    public String getUserId(String login, String password, String nickname) throws SQLException {
            if (isLoginAlreadyExist(login)) {
                return null;
            }
            if (isNicknameAlreadyExist(nickname)) {
                return null;
            }
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            try {
                preparedStatement = connection.prepareStatement(INSERT_USER_DATA);
                preparedStatement.setString(1, login);
                preparedStatement.setString(2, password);
                preparedStatement.setString(3, nickname);
                preparedStatement.executeUpdate();
                while (resultSet.next()) {
                    System.out.println(resultSet.getInt("id"));
                    return String.valueOf(resultSet.getInt("id"));
                }

            } finally {
                close(connection, preparedStatement,resultSet);
            }
         return null;
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

