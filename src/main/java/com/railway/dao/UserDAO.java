package com.railway.dao;

import com.railway.model.User;
import com.railway.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public boolean registerUser(User user, String password) throws SQLException {
        String userSql = "INSERT INTO [User] (Username, Name, Address, City, Age, Contact, Gender, Role) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String loginSql = "INSERT INTO Login (Username, Password) VALUES (?, ?)";

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement userStmt = conn.prepareStatement(userSql);
                 PreparedStatement loginStmt = conn.prepareStatement(loginSql)) {

                userStmt.setString(1, user.getUsername());
                userStmt.setString(2, user.getName());
                userStmt.setString(3, user.getAddress());
                userStmt.setString(4, user.getCity());
                userStmt.setObject(5, user.getAge());
                userStmt.setString(6, user.getContact());
                userStmt.setString(7, user.getGender());
                userStmt.setString(8, user.getRole() != null ? user.getRole() : "passenger");
                userStmt.executeUpdate();

                loginStmt.setString(1, user.getUsername());
                loginStmt.setString(2, password);
                loginStmt.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                DatabaseConnection.closeConnection(conn);
            }
        }
    }

    public User login(String username, String password) throws SQLException {
        String sql = "SELECT u.* FROM [User] u INNER JOIN Login l ON u.Username = l.Username " +
                "WHERE u.Username = ? AND l.Password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractUserFromResultSet(rs);
                }
            }
        }
        return null;
    }

    public User getUserByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM [User] WHERE Username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractUserFromResultSet(rs);
                }
            }
        }
        return null;
    }

    public boolean updateUser(User user) throws SQLException {
        String sql = "UPDATE [User] SET Name = ?, Address = ?, City = ?, Age = ?, " +
                "Contact = ?, Gender = ?, Role = ? WHERE Username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getAddress());
            stmt.setString(3, user.getCity());
            stmt.setObject(4, user.getAge());
            stmt.setString(5, user.getContact());
            stmt.setString(6, user.getGender());
            stmt.setString(7, user.getRole() != null ? user.getRole() : "passenger");
            stmt.setString(8, user.getUsername());

            return stmt.executeUpdate() > 0;
        }
    }

    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM [User]";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
        }
        return users;
    }

    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUsername(rs.getString("Username"));
        user.setName(rs.getString("Name"));
        user.setAddress(rs.getString("Address"));
        user.setCity(rs.getString("City"));
        user.setAge(rs.getObject("Age", Integer.class));
        user.setContact(rs.getString("Contact"));
        user.setGender(rs.getString("Gender"));
        user.setRole(rs.getString("Role"));
        return user;
    }
}
