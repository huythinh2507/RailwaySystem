package com.railway.dao;

import com.railway.model.Passenger;
import com.railway.model.Ticket;
import com.railway.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    public boolean bookTicket(Passenger passenger, Ticket ticket) throws SQLException {
        String passengerSql = "INSERT INTO Passenger (PNR, PassengerName, Age, Gender, Source, Destination, Username) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        String ticketSql = "INSERT INTO Ticket (PNR, TrainNumber, Date, Source, Destination) " +
                "VALUES (?, ?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement passengerStmt = conn.prepareStatement(passengerSql);
                 PreparedStatement ticketStmt = conn.prepareStatement(ticketSql)) {

                passengerStmt.setString(1, passenger.getPnr());
                passengerStmt.setString(2, passenger.getPassengerName());
                passengerStmt.setObject(3, passenger.getAge());
                passengerStmt.setString(4, passenger.getGender());
                passengerStmt.setString(5, passenger.getSource());
                passengerStmt.setString(6, passenger.getDestination());
                passengerStmt.setString(7, passenger.getUsername());
                passengerStmt.executeUpdate();

                ticketStmt.setString(1, ticket.getPnr());
                ticketStmt.setString(2, ticket.getTrainNumber());
                ticketStmt.setDate(3, Date.valueOf(ticket.getDate()));
                ticketStmt.setString(4, ticket.getSource());
                ticketStmt.setString(5, ticket.getDestination());
                ticketStmt.executeUpdate();

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

    public boolean cancelTicket(String pnr) throws SQLException {
        String deleteTicketSql = "DELETE FROM Ticket WHERE PNR = ?";
        String deletePassengerSql = "DELETE FROM Passenger WHERE PNR = ?";

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement ticketStmt = conn.prepareStatement(deleteTicketSql);
                 PreparedStatement passengerStmt = conn.prepareStatement(deletePassengerSql)) {

                ticketStmt.setString(1, pnr);
                ticketStmt.executeUpdate();

                passengerStmt.setString(1, pnr);
                passengerStmt.executeUpdate();

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

    public Ticket getTicketByPNR(String pnr) throws SQLException {
        String sql = "SELECT t.*, tr.TrainName, tr.Cost FROM Ticket t " +
                "INNER JOIN Train tr ON t.TrainNumber = tr.TrainNumber WHERE t.PNR = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, pnr);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Ticket ticket = new Ticket();
                    ticket.setPnr(rs.getString("PNR"));
                    ticket.setTrainNumber(rs.getString("TrainNumber"));

                    Date date = rs.getDate("Date");
                    if (date != null) {
                        ticket.setDate(date.toLocalDate());
                    }

                    ticket.setSource(rs.getString("Source"));
                    ticket.setDestination(rs.getString("Destination"));
                    ticket.setAmount(rs.getBigDecimal("Cost"));
                    ticket.setStatus("CONFIRMED");
                    return ticket;
                }
            }
        }
        return null;
    }

    public List<Ticket> getTicketsByUsername(String username) throws SQLException {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT t.*, tr.TrainName, tr.Cost FROM Ticket t " +
                "INNER JOIN Train tr ON t.TrainNumber = tr.TrainNumber " +
                "INNER JOIN Passenger p ON t.PNR = p.PNR " +
                "WHERE p.Username = ? ORDER BY t.Date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Ticket ticket = new Ticket();
                    ticket.setPnr(rs.getString("PNR"));
                    ticket.setTrainNumber(rs.getString("TrainNumber"));

                    Date date = rs.getDate("Date");
                    if (date != null) {
                        ticket.setDate(date.toLocalDate());
                    }

                    ticket.setSource(rs.getString("Source"));
                    ticket.setDestination(rs.getString("Destination"));
                    ticket.setAmount(rs.getBigDecimal("Cost"));
                    ticket.setStatus("CONFIRMED");
                    tickets.add(ticket);
                }
            }
        }
        return tickets;
    }

    public Passenger getPassengerByPNR(String pnr) throws SQLException {
        String sql = "SELECT * FROM Passenger WHERE PNR = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, pnr);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Passenger passenger = new Passenger();
                    passenger.setPnr(rs.getString("PNR"));
                    passenger.setPassengerName(rs.getString("PassengerName"));
                    passenger.setAge(rs.getObject("Age", Integer.class));
                    passenger.setGender(rs.getString("Gender"));
                    passenger.setSource(rs.getString("Source"));
                    passenger.setDestination(rs.getString("Destination"));
                    passenger.setUsername(rs.getString("Username"));
                    return passenger;
                }
            }
        }
        return null;
    }

    public List<Ticket> getAllBookings() throws SQLException {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT t.*, tr.TrainName, tr.Cost, p.PassengerName FROM Ticket t " +
                "INNER JOIN Train tr ON t.TrainNumber = tr.TrainNumber " +
                "INNER JOIN Passenger p ON t.PNR = p.PNR " +
                "ORDER BY t.Date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Ticket ticket = new Ticket();
                ticket.setPnr(rs.getString("PNR"));
                ticket.setTrainNumber(rs.getString("TrainNumber"));

                Date date = rs.getDate("Date");
                if (date != null) {
                    ticket.setDate(date.toLocalDate());
                }

                ticket.setSource(rs.getString("Source"));
                ticket.setDestination(rs.getString("Destination"));
                ticket.setAmount(rs.getBigDecimal("Cost"));
                ticket.setStatus("CONFIRMED");
                tickets.add(ticket);
            }
        }
        return tickets;
    }
}
