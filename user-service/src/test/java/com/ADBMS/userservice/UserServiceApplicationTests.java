package com.ADBMS.userservice;

import com.ADBMS.userservice.dto.UserCreateDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class UserServiceApplicationTests {

	@Container
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.2");
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry){
		dynamicPropertyRegistry.add("spring.data.mongodb.url", mongoDBContainer::getReplicaSetUrl);
	}

	/*@Test
	void shouldCreateUser() throws Exception {
		UserRequest userRequest = getUserRequest();
		String userRequestString = objectMapper.writeValueAsString(userRequest);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/user")
				.contentType(MediaType.APPLICATION_JSON)
				.content(userRequestString))
				.andExpect(status().isCreated());/*
	}*/

	private UserCreateDTO getUserRequest() {
		return UserCreateDTO.builder()
				.email("jhathnagoda@gmail.com")
				.name("Janith Hathnagoda")
				.contact("0772433249")
				.password("12345678")
				.build();
	}

}
