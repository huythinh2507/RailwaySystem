package com.railway.ui;

import com.railway.model.Feedback;
import com.railway.model.Ticket;
import com.railway.model.Train;
import com.railway.service.BookingService;
import com.railway.service.FeedbackService;
import com.railway.service.TrainService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class AdminMenu {
    private final Scanner scanner;
    private final TrainService trainService;
    private final BookingService bookingService;
    private final FeedbackService feedbackService;

    public AdminMenu(Scanner scanner) {
        this.scanner = scanner;
        this.trainService = new TrainService();
        this.bookingService = new BookingService();
        this.feedbackService = new FeedbackService();
    }

    public void display() {
        while (true) {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║      ADMIN MENU                        ║");
            System.out.println("╠════════════════════════════════════════╣");
            System.out.println("║ 1. Add Train                           ║");
            System.out.println("║ 2. Update Train                        ║");
            System.out.println("║ 3. Delete Train                        ║");
            System.out.println("║ 4. View All Trains                     ║");
            System.out.println("║ 5. View All Bookings                   ║");
            System.out.println("║ 6. View Pending Feedback               ║");
            System.out.println("║ 7. View All Feedback                   ║");
            System.out.println("║ 8. Respond to Feedback                 ║");
            System.out.println("║ 9. View Train Ratings                  ║");
            System.out.println("║ 10. Logout                             ║");
            System.out.println("╚════════════════════════════════════════╝");
            System.out.print("Enter your choice: ");

            int choice = getIntInput();
            scanner.nextLine();

            try {
                switch (choice) {
                    case 1:
                        addTrain();
                        break;
                    case 2:
                        updateTrain();
                        break;
                    case 3:
                        deleteTrain();
                        break;
                    case 4:
                        viewAllTrains();
                        break;
                    case 5:
                        viewAllBookings();
                        break;
                    case 6:
                        viewPendingFeedback();
                        break;
                    case 7:
                        viewAllFeedback();
                        break;
                    case 8:
                        respondToFeedback();
                        break;
                    case 9:
                        viewTrainRatings();
                        break;
                    case 10:
                        System.out.println("Logging out...");
                        return;
                    default:
                        System.out.println("Invalid choice! Please try again.");
                }
            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

    private void addTrain() throws SQLException {
        System.out.println("\n=== ADD NEW TRAIN ===");
        System.out.print("Enter Train Number: ");
        String trainNumber = scanner.nextLine().trim();

        System.out.print("Enter Train Name: ");
        String trainName = scanner.nextLine().trim();

        System.out.print("Enter Source: ");
        String source = scanner.nextLine().trim();

        System.out.print("Enter Destination: ");
        String destination = scanner.nextLine().trim();

        LocalDate date = null;
        while (date == null) {
            System.out.print("Enter Date (YYYY-MM-DD): ");
            String dateStr = scanner.nextLine().trim();
            try {
                date = LocalDate.parse(dateStr);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format! Please use YYYY-MM-DD format.");
            }
        }

        System.out.print("Enter Cost: ");
        BigDecimal cost = new BigDecimal(scanner.nextLine().trim());

        Train train = new Train(trainNumber, trainName, source, destination, date, cost);

        try {
            boolean success = trainService.addTrain(train);
            if (success) {
                System.out.println("\n✓ Train added successfully!");
            }
        } catch (Exception e) {
            System.err.println("Failed to add train: " + e.getMessage());
        }
    }

    private void updateTrain() throws SQLException {
        System.out.println("\n=== UPDATE TRAIN ===");
        System.out.print("Enter Train Number to update: ");
        String trainNumber = scanner.nextLine().trim();

        Train existingTrain = trainService.getTrainDetails(trainNumber);
        if (existingTrain == null) {
            System.out.println("Train not found!");
            return;
        }

        System.out.println("\nCurrent Details:");
        System.out.println("Name: " + existingTrain.getTrainName());
        System.out.println("Source: " + existingTrain.getSource());
        System.out.println("Destination: " + existingTrain.getDestination());
        System.out.println("Date: " + existingTrain.getDate());
        System.out.println("Cost: ₹" + existingTrain.getCost());

        System.out.print("\nEnter New Train Name (or press Enter to keep current): ");
        String trainName = scanner.nextLine().trim();
        if (trainName.isEmpty()) trainName = existingTrain.getTrainName();

        System.out.print("Enter New Source (or press Enter to keep current): ");
        String source = scanner.nextLine().trim();
        if (source.isEmpty()) source = existingTrain.getSource();

        System.out.print("Enter New Destination (or press Enter to keep current): ");
        String destination = scanner.nextLine().trim();
        if (destination.isEmpty()) destination = existingTrain.getDestination();

        LocalDate date = existingTrain.getDate();
        System.out.print("Enter New Date (YYYY-MM-DD) (or press Enter to keep current): ");
        String dateStr = scanner.nextLine().trim();
        if (!dateStr.isEmpty()) {
            try {
                date = LocalDate.parse(dateStr);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format! Keeping current date.");
            }
        }

        BigDecimal cost = existingTrain.getCost();
        System.out.print("Enter New Cost (or press Enter to keep current): ");
        String costStr = scanner.nextLine().trim();
        if (!costStr.isEmpty()) {
            cost = new BigDecimal(costStr);
        }

        Train updatedTrain = new Train(trainNumber, trainName, source, destination, date, cost);

        try {
            boolean success = trainService.updateTrain(updatedTrain);
            if (success) {
                System.out.println("\n✓ Train updated successfully!");
            }
        } catch (Exception e) {
            System.err.println("Failed to update train: " + e.getMessage());
        }
    }

    private void deleteTrain() throws SQLException {
        System.out.println("\n=== DELETE TRAIN ===");
        System.out.print("Enter Train Number: ");
        String trainNumber = scanner.nextLine().trim();

        Train train = trainService.getTrainDetails(trainNumber);
        if (train == null) {
            System.out.println("Train not found!");
            return;
        }

        System.out.println("\nTrain Details:");
        System.out.println("Train Number: " + train.getTrainNumber());
        System.out.println("Train Name: " + train.getTrainName());
        System.out.println("Route: " + train.getSource() + " → " + train.getDestination());

        System.out.print("\nAre you sure you want to delete this train? (yes/no): ");
        String confirm = scanner.nextLine().trim().toLowerCase();

        if (confirm.equals("yes")) {
            try {
                boolean success = trainService.deleteTrain(trainNumber);
                if (success) {
                    System.out.println("\n✓ Train deleted successfully!");
                }
            } catch (Exception e) {
                System.err.println("Failed to delete train: " + e.getMessage());
            }
        } else {
            System.out.println("Deletion aborted.");
        }
    }

    private void viewAllTrains() throws SQLException {
        System.out.println("\n=== ALL TRAINS ===");
        List<Train> trains = trainService.getAllTrains();

        if (trains.isEmpty()) {
            System.out.println("No trains found.");
        } else {
            System.out.println("\n" + "=".repeat(120));
            System.out.printf("%-12s %-25s %-20s %-20s %-12s %-10s %-10s%n",
                    "Train No", "Train Name", "Source", "Destination", "Date", "Cost", "Seats");
            System.out.println("=".repeat(120));

            for (Train train : trains) {
                int availableSeats = trainService.checkSeatAvailability(train.getTrainNumber());
                System.out.printf("%-12s %-25s %-20s %-20s %-12s ₹%-9.2f %-10d%n",
                        train.getTrainNumber(),
                        train.getTrainName(),
                        train.getSource(),
                        train.getDestination(),
                        train.getDate(),
                        train.getCost(),
                        availableSeats);
            }
            System.out.println("=".repeat(120));
        }
    }

    private void viewAllBookings() throws SQLException {
        System.out.println("\n=== ALL BOOKINGS ===");
        List<Ticket> tickets = bookingService.getAllBookings();

        if (tickets.isEmpty()) {
            System.out.println("No bookings found.");
        } else {
            System.out.println("\n" + "=".repeat(110));
            System.out.printf("%-15s %-12s %-12s %-20s %-20s %-10s%n",
                    "PNR", "Train No", "Date", "Source", "Destination", "Amount");
            System.out.println("=".repeat(110));

            for (Ticket ticket : tickets) {
                System.out.printf("%-15s %-12s %-12s %-20s %-20s ₹%-9.2f%n",
                        ticket.getPnr(),
                        ticket.getTrainNumber(),
                        ticket.getDate(),
                        ticket.getSource(),
                        ticket.getDestination(),
                        ticket.getAmount());
            }
            System.out.println("=".repeat(110));
        }
    }

    private void viewPendingFeedback() throws SQLException {
        System.out.println("\n=== PENDING FEEDBACK ===");
        List<Feedback> feedbackList = feedbackService.getPendingFeedback();

        if (feedbackList.isEmpty()) {
            System.out.println("No pending feedback.");
        } else {
            displayFeedbackList(feedbackList);
        }
    }

    private void viewAllFeedback() throws SQLException {
        System.out.println("\n=== ALL FEEDBACK ===");
        List<Feedback> feedbackList = feedbackService.getAllFeedback();

        if (feedbackList.isEmpty()) {
            System.out.println("No feedback found.");
        } else {
            displayFeedbackList(feedbackList);
        }
    }

    private void displayFeedbackList(List<Feedback> feedbackList) {
        for (Feedback feedback : feedbackList) {
            System.out.println("\n" + "=".repeat(80));
            System.out.println("Feedback ID: " + feedback.getFeedbackId());
            System.out.println("User: " + feedback.getUsername());
            System.out.println("Train Number: " + (feedback.getTrainNumber() != null ? feedback.getTrainNumber() : "General"));
            System.out.println("Rating: " + "★".repeat(feedback.getRating()) + "☆".repeat(5 - feedback.getRating()) +
                    " (" + feedback.getRating() + "/5)");
            System.out.println("Comments: " + feedback.getComments());
            System.out.println("Submitted: " + feedback.getSubmittedDate());
            System.out.println("Status: " + feedback.getStatus());
            if (feedback.getAdminResponse() != null) {
                System.out.println("Admin Response: " + feedback.getAdminResponse());
            }
            System.out.println("=".repeat(80));
        }
    }

    private void respondToFeedback() throws SQLException {
        System.out.println("\n=== RESPOND TO FEEDBACK ===");
        System.out.print("Enter Feedback ID: ");
        int feedbackId = getIntInput();
        scanner.nextLine();

        Feedback feedback = feedbackService.getFeedbackById(feedbackId);
        if (feedback == null) {
            System.out.println("Feedback not found!");
            return;
        }

        System.out.println("\nFeedback Details:");
        System.out.println("User: " + feedback.getUsername());
        System.out.println("Train: " + (feedback.getTrainNumber() != null ? feedback.getTrainNumber() : "General"));
        System.out.println("Rating: " + feedback.getRating() + "/5");
        System.out.println("Comments: " + feedback.getComments());
        System.out.println("Submitted: " + feedback.getSubmittedDate());

        System.out.print("\nEnter your response: ");
        String response = scanner.nextLine().trim();

        try {
            boolean success = feedbackService.respondToFeedback(feedbackId, response);
            if (success) {
                System.out.println("\n✓ Response submitted successfully!");
            }
        } catch (Exception e) {
            System.err.println("Failed to submit response: " + e.getMessage());
        }
    }

    private void viewTrainRatings() throws SQLException {
        System.out.println("\n=== TRAIN RATINGS ===");
        List<Train> trains = trainService.getAllTrains();

        if (trains.isEmpty()) {
            System.out.println("No trains found.");
        } else {
            System.out.println("\n" + "=".repeat(90));
            System.out.printf("%-12s %-30s %-20s %-15s%n",
                    "Train No", "Train Name", "Route", "Avg Rating");
            System.out.println("=".repeat(90));

            for (Train train : trains) {
                double avgRating = feedbackService.getTrainAverageRating(train.getTrainNumber());
                String route = train.getSource() + " → " + train.getDestination();
                String rating = avgRating > 0 ? String.format("%.2f/5.0", avgRating) : "No ratings";

                System.out.printf("%-12s %-30s %-20s %-15s%n",
                        train.getTrainNumber(),
                        train.getTrainName(),
                        route,
                        rating);
            }
            System.out.println("=".repeat(90));
        }
    }

    private int getIntInput() {
        while (!scanner.hasNextInt()) {
            System.out.print("Invalid input! Please enter a number: ");
            scanner.next();
        }
        return scanner.nextInt();
    }
}
