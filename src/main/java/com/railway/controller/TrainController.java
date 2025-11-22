package com.railway.controller;

import com.railway.model.Train;
import com.railway.service.TrainService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trains")
@CrossOrigin(origins = "*")
public class TrainController {

    private final TrainService trainService;

    public TrainController() {
        this.trainService = new TrainService();
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllTrains() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Train> trains = trainService.getAllTrains();
            response.put("success", true);
            response.put("data", trains);
            response.put("count", trains.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchTrains(
            @RequestParam String source,
            @RequestParam String destination,
            @RequestParam(required = false) String date) {
        Map<String, Object> response = new HashMap<>();
        try {
            LocalDate travelDate = date != null ? LocalDate.parse(date) : LocalDate.now();
            List<Train> trains = trainService.searchTrains(source, destination, travelDate);
            response.put("success", true);
            response.put("data", trains);
            response.put("count", trains.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{trainNumber}")
    public ResponseEntity<Map<String, Object>> getTrainByNumber(@PathVariable String trainNumber) {
        Map<String, Object> response = new HashMap<>();
        try {
            Train train = trainService.getTrainByNumber(trainNumber);
            if (train != null) {
                response.put("success", true);
                response.put("data", train);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("error", "Train not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{trainNumber}/availability")
    public ResponseEntity<Map<String, Object>> checkAvailability(@PathVariable String trainNumber) {
        Map<String, Object> response = new HashMap<>();
        try {
            int availableSeats = trainService.checkSeatAvailability(trainNumber);
            response.put("success", true);
            response.put("trainNumber", trainNumber);
            response.put("availableSeats", availableSeats);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> addTrain(@RequestBody Train train) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean success = trainService.addTrain(train);
            if (success) {
                response.put("success", true);
                response.put("message", "Train added successfully");
                response.put("data", train);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                response.put("success", false);
                response.put("error", "Failed to add train");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{trainNumber}")
    public ResponseEntity<Map<String, Object>> deleteTrain(@PathVariable String trainNumber) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean success = trainService.deleteTrain(trainNumber);
            if (success) {
                response.put("success", true);
                response.put("message", "Train deleted successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("error", "Failed to delete train");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
