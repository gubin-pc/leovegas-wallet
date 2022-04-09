package org.leovegas.wallet;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.*;


@Testcontainers
@SpringBootTest(webEnvironment = RANDOM_PORT)
public abstract class AbstractIntegrationTest  {

    public static PostgreSQLContainer<?> postgreDBContainer =
            new PostgreSQLContainer<>("postgres:11")
                    .withUrlParam("stringtype", "unspecified")
                    .waitingFor(Wait.forListeningPort());

    static {
        postgreDBContainer.start();
    }

    @DynamicPropertySource
    static void mySqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreDBContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreDBContainer::getUsername);
        registry.add("spring.datasource.password", postgreDBContainer::getPassword);
    }

}

