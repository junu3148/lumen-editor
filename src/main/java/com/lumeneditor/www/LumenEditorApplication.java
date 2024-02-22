package com.lumeneditor.www;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
public class LumenEditorApplication {

	public static void main(String[] args) {
		SpringApplication.run(LumenEditorApplication.class, args);
	}

}
