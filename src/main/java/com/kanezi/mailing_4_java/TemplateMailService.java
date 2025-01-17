package com.kanezi.mailing_4_java;

import jakarta.mail.MessagingException;
import lombok.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
@Value
public class TemplateMailService {

    SpringTemplateEngine templateEngine;
    MailService mailService;

    void sendPasswordResetMail(String lang, String to, String subject) throws MessagingException {
        Context context = new Context();
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("username", "John Doe");

        context.setVariables(templateModel);
        context.setLocale(Locale.forLanguageTag(lang));

        String html = templateEngine.process("mail/password-reset/password-reset.html", context);

        mailService.sendHtml(to, subject, html);
    }
}
