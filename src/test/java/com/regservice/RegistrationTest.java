package com.regservice;

import com.regservice.application.dto.UserDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(classes = RegistrationServiceApplication.class)
@ExtendWith(SpringExtension.class)
public class RegistrationTest {

    @Autowired
    private ApplicationContext context;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void simpleFlowTest() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("john.doe@gmail.com");
        userDTO.setPassword("12345678");
        userDTO.setName("John Doe");
        String body = objectMapper.writeValueAsString(userDTO);

        WebTestClient webTestClient = WebTestClient.bindToApplicationContext(this.context)
                .build();
        webTestClient
                .post()
                .uri("/signup")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus()
                .isNoContent();

        webTestClient.post()
                .uri("/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus()
                .isNoContent()
                .expectCookie()
                .value("X-Auth", xAuth -> {
                    webTestClient
                            .get()
                            .uri("/user-details")
                            .cookie("X-Auth", xAuth)
                            .exchange()
                            .expectStatus()
                            .isOk()
                            .expectBody(UserDTO.class)
                            .equals(userDTO);
                });
    }
}
