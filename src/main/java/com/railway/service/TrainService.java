package com.railway.service;

import com.railway.dao.TrainDAO;
import com.railway.model.Train;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class TrainService {
    private final TrainDAO trainDAO;

    public TrainService() {
        this.trainDAO = new TrainDAO();
    }

    public boolean addTrain(Train train) throws SQLException {
        if (train.getTrainNumber() == null || train.getTrainNumber().isEmpty()) {
            throw new IllegalArgumentException("Train number is required!");
        }

        Train existing = trainDAO.getTrainByNumber(train.getTrainNumber());
        if (existing != null) {
            throw new IllegalArgumentException("Train number already exists!");
        }

        return trainDAO.addTrain(train);
    }

    public boolean updateTrain(Train train) throws SQLException {
        Train existing = trainDAO.getTrainByNumber(train.getTrainNumber());
        if (existing == null) {
            throw new IllegalArgumentException("Train not found!");
        }

        return trainDAO.updateTrain(train);
    }

    public boolean deleteTrain(String trainNumber) throws SQLException {
        Train existing = trainDAO.getTrainByNumber(trainNumber);
        if (existing == null) {
            throw new IllegalArgumentException("Train not found!");
        }

        return trainDAO.deleteTrain(trainNumber);
    }

    public Train getTrainDetails(String trainNumber) throws SQLException {
        return trainDAO.getTrainByNumber(trainNumber);
    }

    public List<Train> searchTrains(String source, String destination, LocalDate date) throws SQLException {
        return trainDAO.searchTrains(source, destination, date);
    }

    public List<Train> getAllTrains() throws SQLException {
        return trainDAO.getAllTrains();
    }

    public int checkSeatAvailability(String trainNumber) throws SQLException {
        Train train = trainDAO.getTrainByNumber(trainNumber);
        if (train == null) {
            throw new IllegalArgumentException("Train not found!");
        }

        return trainDAO.getAvailableSeats(trainNumber);
    }

    public Train getTrainByNumber(String trainNumber) throws SQLException {
        return trainDAO.getTrainByNumber(trainNumber);
    }
}
