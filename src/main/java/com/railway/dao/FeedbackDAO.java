package com.railway.dao;

import com.railway.model.Feedback;
import com.railway.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FeedbackDAO {

    public FeedbackDAO() {
        createFeedbackTableIfNotExists();
    }

    private void createFeedbackTableIfNotExists() {
        String sql = "IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'Feedback') " +
                "CREATE TABLE Feedback (" +
                "FeedbackID INT IDENTITY(1,1) PRIMARY KEY, " +
                "Username VARCHAR(50) NOT NULL, " +
                "TrainNumber VARCHAR(10), " +
                "Rating INT CHECK (Rating >= 1 AND Rating <= 5), " +
                "Comments VARCHAR(500), " +
                "SubmittedDate DATETIME DEFAULT GETDATE(), " +
                "Status VARCHAR(20) DEFAULT 'PENDING', " +
                "AdminResponse VARCHAR(500), " +
                "FOREIGN KEY (Username) REFERENCES [User](Username) ON DELETE CASCADE)";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Error creating Feedback table: " + e.getMessage());
        }
    }

    public boolean addFeedback(Feedback feedback) throws SQLException {
        String sql = "INSERT INTO Feedback (Username, TrainNumber, Rating, Comments, SubmittedDate, Status) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, feedback.getUsername());
            stmt.setString(2, feedback.getTrainNumber());
            stmt.setInt(3, feedback.getRating());
            stmt.setString(4, feedback.getComments());
            stmt.setTimestamp(5, Timestamp.valueOf(feedback.getSubmittedDate()));
            stmt.setString(6, feedback.getStatus());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        feedback.setFeedbackId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    public boolean updateFeedbackResponse(int feedbackId, String adminResponse) throws SQLException {
        String sql = "UPDATE Feedback SET AdminResponse = ?, Status = 'RESPONDED' WHERE FeedbackID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, adminResponse);
            stmt.setInt(2, feedbackId);

            return stmt.executeUpdate() > 0;
        }
    }

    public List<Feedback> getFeedbackByUsername(String username) throws SQLException {
        List<Feedback> feedbackList = new ArrayList<>();
        String sql = "SELECT * FROM Feedback WHERE Username = ? ORDER BY SubmittedDate DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    feedbackList.add(extractFeedbackFromResultSet(rs));
                }
            }
        }
        return feedbackList;
    }

    public List<Feedback> getAllFeedback() throws SQLException {
        List<Feedback> feedbackList = new ArrayList<>();
        String sql = "SELECT * FROM Feedback ORDER BY SubmittedDate DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                feedbackList.add(extractFeedbackFromResultSet(rs));
            }
        }
        return feedbackList;
    }

    public List<Feedback> getPendingFeedback() throws SQLException {
        List<Feedback> feedbackList = new ArrayList<>();
        String sql = "SELECT * FROM Feedback WHERE Status = 'PENDING' ORDER BY SubmittedDate ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                feedbackList.add(extractFeedbackFromResultSet(rs));
            }
        }
        return feedbackList;
    }

    public Feedback getFeedbackById(int feedbackId) throws SQLException {
        String sql = "SELECT * FROM Feedback WHERE FeedbackID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, feedbackId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractFeedbackFromResultSet(rs);
                }
            }
        }
        return null;
    }

    public double getAverageRatingForTrain(String trainNumber) throws SQLException {
        String sql = "SELECT AVG(CAST(Rating AS FLOAT)) as AvgRating FROM Feedback WHERE TrainNumber = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, trainNumber);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("AvgRating");
                }
            }
        }
        return 0.0;
    }

    public List<Feedback> getFeedbackByTrainNumber(String trainNumber) throws SQLException {
        List<Feedback> feedbackList = new ArrayList<>();
        String sql = "SELECT * FROM Feedback WHERE TrainNumber = ? ORDER BY SubmittedDate DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, trainNumber);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    feedbackList.add(extractFeedbackFromResultSet(rs));
                }
            }
        }
        return feedbackList;
    }

    private Feedback extractFeedbackFromResultSet(ResultSet rs) throws SQLException {
        Feedback feedback = new Feedback();
        feedback.setFeedbackId(rs.getInt("FeedbackID"));
        feedback.setUsername(rs.getString("Username"));
        feedback.setTrainNumber(rs.getString("TrainNumber"));
        feedback.setRating(rs.getInt("Rating"));
        feedback.setComments(rs.getString("Comments"));

        Timestamp timestamp = rs.getTimestamp("SubmittedDate");
        if (timestamp != null) {
            feedback.setSubmittedDate(timestamp.toLocalDateTime());
        }

        feedback.setStatus(rs.getString("Status"));
        feedback.setAdminResponse(rs.getString("AdminResponse"));
        return feedback;
    }
}
