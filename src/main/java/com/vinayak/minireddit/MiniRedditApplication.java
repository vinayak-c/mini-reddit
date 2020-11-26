package com.vinayak.minireddit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MiniRedditApplication {

	public static void main(String[] args) {
		SpringApplication.run(MiniRedditApplication.class, args);
	}

}
