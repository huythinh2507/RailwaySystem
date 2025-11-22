package com.railway.service;

import com.railway.dao.BookingDAO;
import com.railway.dao.TrainDAO;
import com.railway.model.Passenger;
import com.railway.model.Ticket;
import com.railway.model.Train;
import com.railway.util.PNRGenerator;

import java.sql.SQLException;
import java.util.List;

public class BookingService {
    private final BookingDAO bookingDAO;
    private final TrainDAO trainDAO;

    public BookingService() {
        this.bookingDAO = new BookingDAO();
        this.trainDAO = new TrainDAO();
    }

    public String bookTicket(String username, String passengerName, int age, String gender,
                             String trainNumber, String source, String destination) throws SQLException {

        Train train = trainDAO.getTrainByNumber(trainNumber);
        if (train == null) {
            throw new IllegalArgumentException("Train not found!");
        }

        int availableSeats = trainDAO.getAvailableSeats(trainNumber);
        if (availableSeats <= 0) {
            throw new IllegalStateException("No seats available!");
        }

        String pnr = PNRGenerator.generatePNR();

        Passenger passenger = new Passenger(pnr, passengerName, age, gender,
                source, destination, username);

        Ticket ticket = new Ticket(pnr, trainNumber, train.getDate(),
                source, destination);
        ticket.setAmount(train.getCost());

        boolean success = bookingDAO.bookTicket(passenger, ticket);

        if (success) {
            return pnr;
        } else {
            throw new SQLException("Failed to book ticket");
        }
    }

    public boolean cancelTicket(String pnr, String username) throws SQLException {
        Passenger passenger = bookingDAO.getPassengerByPNR(pnr);

        if (passenger == null) {
            throw new IllegalArgumentException("Ticket not found!");
        }

        if (!passenger.getUsername().equals(username)) {
            throw new SecurityException("You can only cancel your own tickets!");
        }

        return bookingDAO.cancelTicket(pnr);
    }

    public Ticket getTicketDetails(String pnr) throws SQLException {
        return bookingDAO.getTicketByPNR(pnr);
    }

    public List<Ticket> getUserBookings(String username) throws SQLException {
        return bookingDAO.getTicketsByUsername(username);
    }

    public List<Ticket> getAllBookings() throws SQLException {
        return bookingDAO.getAllBookings();
    }

    public Passenger getPassengerDetails(String pnr) throws SQLException {
        return bookingDAO.getPassengerByPNR(pnr);
    }
}
