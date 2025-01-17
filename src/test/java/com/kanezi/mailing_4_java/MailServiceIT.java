package com.kanezi.mailing_4_java;

import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;

// tag::greenmail-testcontainers-test[]
@SpringBootTest
@Testcontainers
public class MailServiceIT {

    @Autowired
    MailService mailService;
    static final String MAIL_USERNAME = "username";
    static final String MAIL_PASSWORD = "user_password";

    @Container
    static GenericContainer<?> greenMailContainer = new GenericContainer(DockerImageName.parse("greenmail/standalone:2.1.2"))
            .waitingFor(Wait.forLogMessage(".*Starting GreenMail standalone.*", 1))
            .withEnv("GREENMAIL_OPTS", "-Dgreenmail.setup.test.smtp -Dgreenmail.hostname=0.0.0.0 -Dgreenmail.users=" + MAIL_USERNAME + ":" + MAIL_PASSWORD)
            .withExposedPorts(ServerSetupTest.SMTP.getPort());

    @DynamicPropertySource
    static void configureMailHost(DynamicPropertyRegistry registry) {
        registry.add("spring.mail.host", greenMailContainer::getHost);
        registry.add("spring.mail.port", greenMailContainer::getFirstMappedPort);
        registry.add("spring.mail.username", () -> MAIL_USERNAME);
        registry.add("spring.mail.password", () -> MAIL_PASSWORD);
    }


    @Test
    void sendingMailWithTextMessageWorks() throws MessagingException, IOException {
        mailService.sendTextMessage("user@mail.com", "text message test", "plain text in mail body");
    }
}
// end::greenmail-testcontainers-test[]
