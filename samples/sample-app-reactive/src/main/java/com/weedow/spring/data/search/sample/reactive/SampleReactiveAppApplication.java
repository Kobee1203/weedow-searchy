package com.weedow.spring.data.search.sample.reactive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("com.weedow.spring.data.search.common.model")
public class SampleReactiveAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(SampleReactiveAppApplication.class, args);
	}

}
