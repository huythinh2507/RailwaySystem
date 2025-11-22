package com.railway.service;

import com.railway.dao.FeedbackDAO;
import com.railway.model.Feedback;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class FeedbackService {
    private final FeedbackDAO feedbackDAO;

    public FeedbackService() {
        this.feedbackDAO = new FeedbackDAO();
    }

    public boolean submitFeedback(String username, String trainNumber, int rating, String comments) throws SQLException {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5!");
        }

        Feedback feedback = new Feedback(username, trainNumber, rating, comments);
        return feedbackDAO.addFeedback(feedback);
    }

    public boolean respondToFeedback(int feedbackId, String adminResponse) throws SQLException {
        Feedback feedback = feedbackDAO.getFeedbackById(feedbackId);
        if (feedback == null) {
            throw new IllegalArgumentException("Feedback not found!");
        }

        return feedbackDAO.updateFeedbackResponse(feedbackId, adminResponse);
    }

    public List<Feedback> getUserFeedback(String username) throws SQLException {
        return feedbackDAO.getFeedbackByUsername(username);
    }

    public List<Feedback> getAllFeedback() throws SQLException {
        return feedbackDAO.getAllFeedback();
    }

    public List<Feedback> getPendingFeedback() throws SQLException {
        return feedbackDAO.getPendingFeedback();
    }

    public double getTrainAverageRating(String trainNumber) throws SQLException {
        return feedbackDAO.getAverageRatingForTrain(trainNumber);
    }

    public Feedback getFeedbackById(int feedbackId) throws SQLException {
        return feedbackDAO.getFeedbackById(feedbackId);
    }

    public List<Feedback> getTrainFeedback(String trainNumber) throws SQLException {
        return feedbackDAO.getFeedbackByTrainNumber(trainNumber);
    }
}
