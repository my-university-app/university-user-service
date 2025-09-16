package com.university.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UniversityUserServiceApplication {

	public static void main(String[] args) {
        System.setProperty("spring.mvc.throw-exception-if-no-handler-found", "true");
        SpringApplication.run(UniversityUserServiceApplication.class, args);

	}

}
