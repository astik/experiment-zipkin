package fr.smile.zipkindemo;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@EnableAutoConfiguration
@RestController
@CrossOrigin
public class SimpleFrontend {
	private static final Logger logger = LoggerFactory.getLogger(SimpleFrontend.class);
	
	@Value("${spring.application.name}")
	private String applicationName;

	@RequestMapping("/")
	public String processRootCall() {
		logger.debug("{} - processRootCall", applicationName);
		return new Date() + "";
	}

	public static void main(String[] args) {
		SpringApplication.run(SimpleFrontend.class);
	}
}
