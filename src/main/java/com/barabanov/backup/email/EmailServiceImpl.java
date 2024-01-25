package com.barabanov.backup.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class EmailServiceImpl implements EmailService
{
    private final JavaMailSender javaMailSender;

    @Override
    public void sendHtmlEmail(String receiverEmail, String subject, String htmlText) throws MessagingException
    {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        helper.setTo(receiverEmail);
        helper.setSubject(subject);
        helper.setText(htmlText, true);

        javaMailSender.send(mimeMessage);
    }
}
