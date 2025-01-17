package com.kanezi.mailing_4_java;

import org.springframework.boot.SpringApplication;

public class TestMailing4JavaApplication {

	public static void main(String[] args) {
		SpringApplication.from(Mailing4JavaApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
