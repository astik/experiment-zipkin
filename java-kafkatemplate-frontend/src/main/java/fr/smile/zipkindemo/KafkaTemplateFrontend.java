package fr.smile.zipkindemo;

import java.util.Date;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@EnableAutoConfiguration
@RestController
@CrossOrigin
public class KafkaTemplateFrontend {
	private static final Logger logger = LoggerFactory.getLogger(KafkaTemplateFrontend.class);

	@Value("${spring.application.name}")
	private String applicationName;

	@Value("${demo.kafkaTopic}")
	private String topic;

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	@RequestMapping("/")
	public String processRootCall() throws InterruptedException, ExecutionException {
		logger.debug("{} - processRootCall", applicationName);
		return kafkaTemplate.send(topic, "Hello World " + new Date()).get().toString();
	}

	public static void main(String[] args) {
		SpringApplication.run(KafkaTemplateFrontend.class);
	}
}
