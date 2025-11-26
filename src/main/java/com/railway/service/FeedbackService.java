package com.railway.service;

import com.railway.dao.FeedbackDAO;
import com.railway.dao.TrainDAO;
import com.railway.model.Feedback;
import com.railway.model.Train;

import java.sql.SQLException;
import java.util.List;

public class FeedbackService {
    private final FeedbackDAO feedbackDAO;
    private final TrainDAO trainDAO;

    public FeedbackService() {
        this.feedbackDAO = new FeedbackDAO();
        this.trainDAO = new TrainDAO();
    }

    /**
     * Handles the business logic for a passenger submitting new feedback.
     */
    public boolean submitFeedback(Feedback feedback) throws SQLException {
        // Basic validation
        if (feedback.getRating() < 1 || feedback.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }
        if (feedback.getComment() == null || feedback.getComment().trim().isEmpty()) {
            throw new IllegalArgumentException("Comment cannot be empty.");
        }

        // Check if the train exists before recording feedback
        Train train = trainDAO.getTrainByNumber(feedback.getTrainNumber());
        if (train == null) {
            throw new IllegalArgumentException("Train not found for this feedback.");
        }

        return feedbackDAO.submitFeedback(feedback);
    }

    /**
     * Retrieves all submitted feedback for the administrator view.
     */
    public List<Feedback> getAllFeedback() throws SQLException {
        return feedbackDAO.getAllFeedback();
    }
    
    /**
     * NEW FIX: Retrieves only feedback where AdminResponse is NULL or empty.
     */
    public List<Feedback> getPendingFeedback() throws SQLException {
        // NOTE: Assumes FeedbackDAO has a method to retrieve pending feedback.
        return feedbackDAO.getPendingFeedback();
    }

    /**
     * Allows an administrator to respond to a specific feedback entry.
     */
    public boolean updateAdminResponse(int feedbackId, String response) throws SQLException {
        if (response == null || response.trim().isEmpty()) {
            throw new IllegalArgumentException("Admin response cannot be empty.");
        }
        return feedbackDAO.updateAdminResponse(feedbackId, response);
    }
}
