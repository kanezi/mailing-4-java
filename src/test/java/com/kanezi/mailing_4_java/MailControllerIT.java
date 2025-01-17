package com.kanezi.mailing_4_java;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.HtmlUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

// tag::greenmail-integration-test[]
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MailControllerIT {

    @RegisterExtension
    static GreenMailExtension greenMail =
            new GreenMailExtension(ServerSetupTest.SMTP)
                    .withConfiguration(GreenMailConfiguration
                            .aConfig()
                            // same as in test application.properties
                            .withUser("user", "pass"))
                    .withPerMethodLifecycle(false);

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void shouldSendEmailWithCorrectPayloadToUser() {
        String payload = """
                {
                  "to": "example@mail.com",
                  "subject": "text mail",
                  "text": "example of plain text mail"
                }
                """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(payload, headers);

        ResponseEntity<Void> response =
                this.testRestTemplate.postForEntity("/api/v1/mail/text", request, Void.class);

        assertEquals(200, response.getStatusCode().value());

        await()
                .atMost(2, SECONDS)
                .untilAsserted(
                        () -> {
                            MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
                            assertEquals(1, receivedMessages.length);

                            MimeMessage receivedMessage = receivedMessages[0];
                            assertEquals(1, receivedMessage.getAllRecipients().length);
                            assertEquals("example@mail.com", receivedMessage.getAllRecipients()[0].toString());
                            assertEquals("example of plain text mail", GreenMailUtil.getBody(receivedMessage));
                            assertEquals("text mail", receivedMessage.getSubject());
                        });

    }

}
// end::greenmail-integration-test[]