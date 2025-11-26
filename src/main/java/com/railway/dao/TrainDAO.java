package com.railway.dao;

import com.railway.model.Train;
import com.railway.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository; 

@Repository // FIX: ADDED @Repository
public class TrainDAO {

    // --- Helper to convert database result into a Train object ---
    private Train extractTrainFromResultSet(ResultSet rs) throws SQLException {
        Train train = new Train();
        train.setTrainNumber(rs.getString("TrainNumber"));
        train.setTrainName(rs.getString("TrainName"));
        train.setSource(rs.getString("Source"));
        train.setDestination(rs.getString("Destination"));

        Date date = rs.getDate("Date");
        if (date != null) {
            train.setDate(date.toLocalDate());
        }

        train.setCost(rs.getBigDecimal("Cost"));
        // AvailableSeats and TotalSeats are not stored in the DB, only calculated (in Service)
        return train;
    }

    // --- CRUD Operations ---

    public boolean addTrain(Train train) throws SQLException {
        String sql = "INSERT INTO Train (TrainNumber, TrainName, Source, Destination, Date, Cost) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, train.getTrainNumber());
            stmt.setString(2, train.getTrainName());
            stmt.setString(3, train.getSource());
            stmt.setString(4, train.getDestination());
            stmt.setDate(5, Date.valueOf(train.getDate()));
            stmt.setBigDecimal(6, train.getCost());

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updateTrain(Train train) throws SQLException {
        String sql = "UPDATE Train SET TrainName = ?, Source = ?, Destination = ?, " +
                "Date = ?, Cost = ? WHERE TrainNumber = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, train.getTrainName());
            stmt.setString(2, train.getSource());
            stmt.setString(3, train.getDestination());
            stmt.setDate(4, Date.valueOf(train.getDate()));
            stmt.setBigDecimal(5, train.getCost());
            stmt.setString(6, train.getTrainNumber());

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deleteTrain(String trainNumber) throws SQLException {
        String sql = "DELETE FROM Train WHERE TrainNumber = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, trainNumber);
            return stmt.executeUpdate() > 0;
        }
    }

    // --- Retrieval Operations ---

    public Train getTrainByNumber(String trainNumber) throws SQLException {
        String sql = "SELECT * FROM Train WHERE TrainNumber = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, trainNumber);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractTrainFromResultSet(rs);
                }
            }
        }
        return null;
    }

    public List<Train> searchTrains(String source, String destination, LocalDate date) throws SQLException {
        List<Train> trains = new ArrayList<>();
        // Basic query is simple since Source/Destination/Date are stored directly on the Train table
        String sql = "SELECT * FROM Train WHERE Source = ? AND Destination = ? AND Date = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, source);
            stmt.setString(2, destination);
            stmt.setDate(3, Date.valueOf(date));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    trains.add(extractTrainFromResultSet(rs));
                }
            }
        }
        return trains;
    }

    public List<Train> getAllTrains() throws SQLException {
        List<Train> trains = new ArrayList<>();
        String sql = "SELECT * FROM Train ORDER BY Date, TrainNumber";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                trains.add(extractTrainFromResultSet(rs));
            }
        }
        return trains;
    }

    // --- Availability Calculation ---

    public int getAvailableSeats(String trainNumber) throws SQLException {
        // Assumes a fixed total capacity (e.g., 100) and counts currently CONFIRMED tickets
        // NOTE: The previous version was correct, ensuring the logic is robust.
        String sql = "SELECT COUNT(t.PNR) AS bookedCount FROM Ticket t " +
                     "WHERE t.TrainNumber = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, trainNumber);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int bookedSeats = rs.getInt("bookedCount");
                    // Assuming a fixed total of 100 seats per train service for simplicity
                    final int TOTAL_SEATS = 100;
                    return TOTAL_SEATS - bookedSeats;
                }
            }
        }
        // Default to 100 if train/data isn't found
        return 100;
    }
}
