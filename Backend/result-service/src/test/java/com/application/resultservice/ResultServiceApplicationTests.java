package com.application.resultservice;

import com.application.resultservice.client.AttemptServiceClient;
import com.application.resultservice.client.QuestionServiceClient;
import com.application.resultservice.client.QuizServiceClient;
import com.application.resultservice.client.UserProgressClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(properties = {
		"spring.cloud.config.enabled=false",
		"spring.cloud.discovery.enabled=false",
		"eureka.client.enabled=false",
		"spring.datasource.url=jdbc:h2:mem:resultservice;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
		"spring.datasource.driverClassName=org.h2.Driver",
		"spring.datasource.username=sa",
		"spring.datasource.password=",
		"spring.jpa.hibernate.ddl-auto=create-drop",
		"spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
})
class ResultServiceApplicationTests {

	@MockBean
	private AttemptServiceClient attemptServiceClient;

	@MockBean
	private QuestionServiceClient questionServiceClient;

	@MockBean
	private UserProgressClient userProgressClient;

	@MockBean
	private QuizServiceClient quizServiceClient;

	@Test
	void contextLoads() {
	}

}
