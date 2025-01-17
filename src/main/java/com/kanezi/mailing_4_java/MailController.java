package com.kanezi.mailing_4_java;

import jakarta.mail.MessagingException;
import lombok.Value;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;

@RestController
@RequestMapping("/api/v1/mail")
@Value
public class MailController {
    MailService mailService;
    TemplateMailService templateMailService;

    @GetMapping("/text")
    void sendTextMessage(String to, String subject, String text) {
        mailService.sendTextMessage(to, subject, text);
    }


    record TextMailForm(String to, String subject, String text){}
    @PostMapping("/text")
    void submitTextMessage(@RequestBody TextMailForm textMailForm) {
        mailService.sendTextMessage(textMailForm.to, textMailForm.subject, textMailForm.text);
    }

    @GetMapping("/inline")
    void sendInlineHtmlMessage(String to, String subject) throws MessagingException, FileNotFoundException {
        mailService.inlineHtml(to, subject);
    }

    @GetMapping("/password-reset")
    void sendPasswordResetHtmlMessage(String lang, String to, String subject) throws MessagingException {
        templateMailService.sendPasswordResetMail(lang, to, subject);
    }
}
