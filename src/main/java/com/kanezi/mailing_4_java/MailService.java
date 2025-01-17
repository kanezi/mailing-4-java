package com.kanezi.mailing_4_java;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


@Service
@Value
public class MailService {

    private static final String NO_REPLY = "noreply@example.com";
    JavaMailSender mailSender;

    void sendTextMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(NO_REPLY);

        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
    }

    void inlineHtml(String to, String subject) throws MessagingException, FileNotFoundException {
        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(NO_REPLY);
        helper.setTo(to);
        helper.setSubject(subject);

        helper.addAttachment("junit-cheat-sheet.pdf", ResourceUtils.getFile("classpath:templates/mail/attachments/junit-cheat-sheet.pdf"));

        helper.setText("<html><body><p>Junit cheatsheet!</p><img src='cid:logo'></body></html>", true);

        // Ordering is important for inlining
        // we first need to add html text, then resources
        helper.addInline("logo", ResourceUtils.getFile("classpath:templates/mail/attachments/logo.png"));

        mailSender.send(message);
    }


    public record MailResource(String name, File file) {
    }


    void sendHtmlMessageWithResources(String to,
                                      String subject,
                                      String htmlBody,
                                      List<MailResource> inlines,
                                      List<MailResource> attachments) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(NO_REPLY);
        helper.setTo(to);
        helper.setSubject(subject);

        if (Objects.nonNull(attachments)) {
            for (MailResource attachment : attachments) {
                helper.addAttachment(attachment.name, attachment.file);
            }
        }

        helper.setText(htmlBody, true);

        if (Objects.nonNull(inlines)) {
            for (MailResource inline : inlines) {
                helper.addInline(inline.name, inline.file);
            }
        }

        mailSender.send(message);
    }

    void sendHtml(String to,
                  String subject,
                  String htmlBody) throws MessagingException {
        sendHtmlMessageWithResources(to, subject, htmlBody, Collections.emptyList(), Collections.emptyList());
    }


}
