package com.weedow.spring.data.search.sample.reactive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.weedow.spring.data.search.common.model")
@EnableJpaRepositories("com.weedow.spring.data.search.common.repository")
public class SampleReactiveAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(SampleReactiveAppApplication.class, args);
	}

}
