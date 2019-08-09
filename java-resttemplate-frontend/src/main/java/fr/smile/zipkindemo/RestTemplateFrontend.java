package fr.smile.zipkindemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@EnableAutoConfiguration
@RestController
@CrossOrigin
public class RestTemplateFrontend {
	private static final Logger logger = LoggerFactory.getLogger(RestTemplateFrontend.class);

	@Autowired
	private RestTemplate restTemplate;

	@Value("${spring.application.name}")
	private String applicationName;

	@Value("${demo.backendBaseUrl}")
	private String backendBaseUrl;

	@Bean
	RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@RequestMapping("/")
	public String processRootCall() {
		logger.debug("{} - processRootCall", applicationName);
	    return restTemplate.getForObject(backendBaseUrl, String.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(RestTemplateFrontend.class);
	}
}
