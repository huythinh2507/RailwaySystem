package com.railway.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Feedback {
    private Integer feedbackId;
    private String username;
    private String trainNumber;
    private Integer rating;
    private String comments;
    private LocalDateTime submittedDate;
    private String status;
    private String adminResponse;

    public Feedback() {
    }

    public Feedback(String username, String trainNumber, Integer rating, String comments) {
        this.username = username;
        this.trainNumber = trainNumber;
        this.rating = rating;
        this.comments = comments;
        this.submittedDate = LocalDateTime.now();
        this.status = "PENDING";
    }

    public Integer getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(Integer feedbackId) {
        this.feedbackId = feedbackId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTrainNumber() {
        return trainNumber;
    }

    public void setTrainNumber(String trainNumber) {
        this.trainNumber = trainNumber;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public LocalDateTime getSubmittedDate() {
        return submittedDate;
    }

    public void setSubmittedDate(LocalDateTime submittedDate) {
        this.submittedDate = submittedDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAdminResponse() {
        return adminResponse;
    }

    public void setAdminResponse(String adminResponse) {
        this.adminResponse = adminResponse;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Feedback feedback = (Feedback) o;
        return Objects.equals(feedbackId, feedback.feedbackId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(feedbackId);
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "feedbackId=" + feedbackId +
                ", username='" + username + '\'' +
                ", trainNumber='" + trainNumber + '\'' +
                ", rating=" + rating +
                ", comments='" + comments + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
