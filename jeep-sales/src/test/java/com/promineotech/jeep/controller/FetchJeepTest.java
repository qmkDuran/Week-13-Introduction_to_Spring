package com.promineotech.jeep.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.math.BigDecimal;
import java.util.LinkedList;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import com.promineotech.jeep.entity.Jeep;
import com.promineotech.jeep.entity.JeepModel;

/*
 * SpringBootTest annotation does many things, but at our level, the most important thing for us to know
 * is that it extends the JUnit Test Framework so that Spring Boot is in control of the test. 
 * 
 * The webEnvironment tells Spring to run in a web environment and to use random ports so tests don't try
 * to run on the same port every time.
 * 
 * ActiveProfiles annotation tells Spring which active bean definition profile to use when loading
 * an Application Context for test classes.
 * 
 * Sql annotation is used to denote a test class or test method to configure SQL scripts and statements
 * to be executed against a given database during integration tests.
 * 
 * SqlConfig annotation tells Spring how to parse sql scripts.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
// @formatter:off
@Sql(scripts = { 
	"classpath:flyway/migrations/V1.0__Jeep_Schema.sql",
	"Classpath:flyway/migrations/V1.1__Jeep_Data.sql" }, 
	config = @SqlConfig(encoding = "utf-8"))
// @formatter:on
class FetchJeepTest {

	/*
	 * Autowired annotation tells Spring Boot to inject the TestRestTemplate object it has
	 * instantiated during ComponentScan into the variable following. An instance
	 * of dependency injection.
	 * 
	 * TestRestTemplate tells Spring Boot that this class is a REST Server, which allows us to
	 * send HTTP to the running application during tests.
	 */
	@Autowired
	private TestRestTemplate restTemplate;

	/*
	 * LocalServerPort annotation tells Spring Boot to fill in, or inject, the port
	 * it has instatiated during ComponentScan into the variable following. An
	 * instance of dependency injection.
	 */
	@LocalServerPort
	private int serverPort;

	protected List<Jeep> buildExpected() {
		List<Jeep> list = new LinkedList<>();

		// @formatter:off
	    list.add(Jeep.builder()
	        .modelId(JeepModel.WRANGLER)
	        .trimLevel("Sport")
	        .numDoors(2)
	        .wheelSize(17)
	        .basePrice(new BigDecimal("28475.00"))
	        .build());
	    
	    list.add(Jeep.builder()
	    	.modelId(JeepModel.WRANGLER)
	        .trimLevel("Sport")
	        .numDoors(4)
	        .wheelSize(17)
	        .basePrice(new BigDecimal("31975.00"))
	        .build());
	    // @formatter:on

		return list;
	} // end buildExpected

	/*
	 * Tests that we can send an HTTP GET request and receive the proper response.
	 * 
	 * ResponseEntity adds an HTTP status code to the response generated after
	 * exchange sends the GET request to the restTemplate through the uri. The null
	 * means no HTTP entity is sent, and the ParameterizedTypeReference is used to
	 * pass generic type information.
	 * 
	 * This method tests that the Test Rest Server returns a status of 200,
	 * signifying that the test was OK/successful.
	 * Also that the response list of jeeps matches the expected list of jeeps given the parameters requested.
	 */
	@Test
	void assertThatJeepsAreReturnedWhenAValidModelAndTrimAreSupplied() {
		// Given: a valid model, trim, and URI
		JeepModel model = JeepModel.WRANGLER;
		String trim = "Sport";
		String uri = String.format("http://localhost:%d/jeeps?model=%s&trim=%s", serverPort, model, trim);

		// When : a connection is made to the URI
		ResponseEntity<List<Jeep>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<>() {
				});

		// Then: a success (OK - 200) status code is returned
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		// And: the actual list returned is the same as the expected List
		List<Jeep> expected = buildExpected();
		assertThat(response.getBody()).isEqualTo(expected);

	} // end assertThatJeepsAreReturnedWhenAValidModelAndTrimAreSupplied

} // end CLASS