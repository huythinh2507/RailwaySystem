package com.railway.controller;

import com.railway.model.Ticket;
import com.railway.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    private final BookingService bookingService;

    public BookingController() {
        this.bookingService = new BookingService();
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> bookTicket(@RequestBody BookingRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String pnr = bookingService.bookTicket(
                request.getUsername(),
                request.getPassengerName(),
                request.getAge(),
                request.getGender(),
                request.getTrainNumber(),
                request.getSource(),
                request.getDestination()
            );

            if (pnr != null) {
                response.put("success", true);
                response.put("message", "Ticket booked successfully");
                response.put("pnr", pnr);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                response.put("success", false);
                response.put("error", "Failed to book ticket");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{pnr}")
    public ResponseEntity<Map<String, Object>> cancelTicket(
            @PathVariable String pnr,
            @RequestParam String username) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean success = bookingService.cancelTicket(pnr, username);
            if (success) {
                response.put("success", true);
                response.put("message", "Ticket cancelled successfully");
                response.put("pnr", pnr);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("error", "Failed to cancel ticket. PNR not found or unauthorized.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<Map<String, Object>> getUserBookings(@PathVariable String username) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Ticket> bookings = bookingService.getUserBookings(username);
            response.put("success", true);
            response.put("data", bookings);
            response.put("count", bookings.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllBookings() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Ticket> bookings = bookingService.getAllBookings();
            response.put("success", true);
            response.put("data", bookings);
            response.put("count", bookings.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Inner class for booking request
    public static class BookingRequest {
        private String username;
        private String passengerName;
        private int age;
        private String gender;
        private String trainNumber;
        private String source;
        private String destination;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getPassengerName() { return passengerName; }
        public void setPassengerName(String passengerName) { this.passengerName = passengerName; }

        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }

        public String getGender() { return gender; }
        public void setGender(String gender) { this.gender = gender; }

        public String getTrainNumber() { return trainNumber; }
        public void setTrainNumber(String trainNumber) { this.trainNumber = trainNumber; }

        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }

        public String getDestination() { return destination; }
        public void setDestination(String destination) { this.destination = destination; }
    }
}
