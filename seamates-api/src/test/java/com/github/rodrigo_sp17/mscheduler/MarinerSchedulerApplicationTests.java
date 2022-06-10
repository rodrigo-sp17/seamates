package com.github.rodrigo_sp17.mscheduler;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

@SpringBootTest
class MarinerSchedulerApplicationTests {

	@MockBean
	private JavaMailSender mailSender;
	@MockBean
	private ClientRegistrationRepository clientRegistrationRepository;

	@Test
	void contextLoads() {
	}
}
