package org.motometer.bot.telegram.api;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.URI;
import java.nio.charset.Charset;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = {UpdateControllerTest.Initializer.class})
class UpdateControllerTest {

    @Container
    public static KafkaContainer kafka = new KafkaContainer();

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void onUpdate() throws Exception {
        String update = IOUtils.resourceToString("/org/motometer/bot/telegram/api/UpdateControllerTest/update.json", Charset.defaultCharset());

        RequestEntity<String> request = RequestEntity.post(new URI(restTemplate.getRootUri() + "/bot/update/fake-token"))
            .contentType(MediaType.APPLICATION_JSON)
            .body(update);

        ResponseEntity<Void> response = restTemplate.exchange(request, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                "kafka.bootstrapAddress=localhost:" + kafka.getMappedPort(9093)
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}