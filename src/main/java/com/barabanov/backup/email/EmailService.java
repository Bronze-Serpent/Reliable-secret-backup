package com.barabanov.backup.email;

import jakarta.mail.MessagingException;

public interface EmailService
{
    void sendHtmlEmail(String receiverEmail, String subject, String htmlText) throws MessagingException;
}
