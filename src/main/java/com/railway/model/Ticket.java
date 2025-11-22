package com.railway.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class Ticket {
    private String pnr;
    private String trainNumber;
    private LocalDate date;
    private String source;
    private String destination;
    private String status;
    private BigDecimal amount;
    private LocalDateTime bookingDate;

    public Ticket() {
    }

    public Ticket(String pnr, String trainNumber, LocalDate date,
                  String source, String destination) {
        this.pnr = pnr;
        this.trainNumber = trainNumber;
        this.date = date;
        this.source = source;
        this.destination = destination;
        this.status = "CONFIRMED";
        this.bookingDate = LocalDateTime.now();
    }

    public String getPnr() {
        return pnr;
    }

    public void setPnr(String pnr) {
        this.pnr = pnr;
    }

    public String getTrainNumber() {
        return trainNumber;
    }

    public void setTrainNumber(String trainNumber) {
        this.trainNumber = trainNumber;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ticket ticket = (Ticket) o;
        return Objects.equals(pnr, ticket.pnr) &&
                Objects.equals(trainNumber, ticket.trainNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pnr, trainNumber);
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "pnr='" + pnr + '\'' +
                ", trainNumber='" + trainNumber + '\'' +
                ", date=" + date +
                ", source='" + source + '\'' +
                ", destination='" + destination + '\'' +
                ", status='" + status + '\'' +
                ", amount=" + amount +
                '}';
    }
}
