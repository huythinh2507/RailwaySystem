package com.railway.dao;

import com.railway.model.Train;
import com.railway.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TrainDAO {

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
        StringBuilder sql = new StringBuilder("SELECT * FROM Train WHERE 1=1");

        if (source != null && !source.isEmpty()) {
            sql.append(" AND Source = ?");
        }
        if (destination != null && !destination.isEmpty()) {
            sql.append(" AND Destination = ?");
        }
        if (date != null) {
            sql.append(" AND Date = ?");
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (source != null && !source.isEmpty()) {
                stmt.setString(paramIndex++, source);
            }
            if (destination != null && !destination.isEmpty()) {
                stmt.setString(paramIndex++, destination);
            }
            if (date != null) {
                stmt.setDate(paramIndex++, Date.valueOf(date));
            }

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

    public int getAvailableSeats(String trainNumber) throws SQLException {
        String sql = "SELECT COUNT(*) as booked FROM Ticket WHERE TrainNumber = ? AND " +
                "(SELECT Date FROM Train WHERE TrainNumber = ?) >= CAST(GETDATE() AS DATE)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, trainNumber);
            stmt.setString(2, trainNumber);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int bookedSeats = rs.getInt("booked");
                    return 100 - bookedSeats;
                }
            }
        }
        return 100;
    }

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
        return train;
    }
}
