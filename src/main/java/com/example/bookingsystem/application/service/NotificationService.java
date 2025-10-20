package com.example.bookingsystem.application.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
public class NotificationService {

    private final JavaMailSender mailSender;

    public NotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    @TransactionalEventListener
    public void handleBookingCreated(BookingCreatedEvent event) {
        sendEmail(
                event.getUserEmail(),
                "Booking Confirmed",
                "Your booking for " + event.getResourceName() +
                        " from " + event.getStartTime() + " to " + event.getEndTime() +
                        " is confirmed."
        );
    }

    @Async
    @TransactionalEventListener
    public void handleBookingUpdated(BookingUpdatedEvent event) {
        sendEmail(
                event.getUserEmail(),
                "Booking Updated",
                "Your booking for " + event.getResourceName() +
                        " has been updated to " + event.getStartTime() + " - " + event.getEndTime() + "."
        );
    }

    @Async
    @TransactionalEventListener
    public void handleBookingCancelled(BookingCancelledEvent event) {
        sendEmail(
                event.getUserEmail(),
                "Booking Cancelled",
                "Your booking for " + event.getResourceName() +
                        " from " + event.getStartTime() + " to " + event.getEndTime() +
                        " has been cancelled."
        );
    }

    private void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom("no-reply@bookingsystem.com");
        mailSender.send(message);
    }
}
