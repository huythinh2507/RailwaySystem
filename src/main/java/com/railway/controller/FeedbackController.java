package com.railway.controller;

import com.railway.model.Feedback;
import com.railway.service.FeedbackService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/feedback")
@CrossOrigin(origins = "*")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController() {
        this.feedbackService = new FeedbackService();
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> submitFeedback(@RequestBody FeedbackRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean success = feedbackService.submitFeedback(
                request.getUsername(),
                request.getTrainNumber(),
                request.getRating(),
                request.getComments()
            );

            if (success) {
                response.put("success", true);
                response.put("message", "Feedback submitted successfully");
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                response.put("success", false);
                response.put("error", "Failed to submit feedback");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllFeedback() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Feedback> feedbacks = feedbackService.getAllFeedback();
            response.put("success", true);
            response.put("data", feedbacks);
            response.put("count", feedbacks.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/train/{trainNumber}")
    public ResponseEntity<Map<String, Object>> getTrainFeedback(@PathVariable String trainNumber) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Feedback> feedbacks = feedbackService.getTrainFeedback(trainNumber);
            double avgRating = feedbackService.getTrainAverageRating(trainNumber);

            response.put("success", true);
            response.put("data", feedbacks);
            response.put("count", feedbacks.size());
            response.put("averageRating", avgRating);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<Map<String, Object>> getPendingFeedback() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Feedback> feedbacks = feedbackService.getPendingFeedback();
            response.put("success", true);
            response.put("data", feedbacks);
            response.put("count", feedbacks.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{feedbackId}/respond")
    public ResponseEntity<Map<String, Object>> respondToFeedback(
            @PathVariable int feedbackId,
            @RequestBody RespondRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean success = feedbackService.respondToFeedback(feedbackId, request.getAdminResponse());
            if (success) {
                response.put("success", true);
                response.put("message", "Response added successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("error", "Failed to add response");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Inner classes for request bodies
    public static class FeedbackRequest {
        private String username;
        private String trainNumber;
        private int rating;
        private String comments;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getTrainNumber() { return trainNumber; }
        public void setTrainNumber(String trainNumber) { this.trainNumber = trainNumber; }

        public int getRating() { return rating; }
        public void setRating(int rating) { this.rating = rating; }

        public String getComments() { return comments; }
        public void setComments(String comments) { this.comments = comments; }
    }

    public static class RespondRequest {
        private String adminResponse;

        public String getAdminResponse() { return adminResponse; }
        public void setAdminResponse(String adminResponse) { this.adminResponse = adminResponse; }
    }
}
