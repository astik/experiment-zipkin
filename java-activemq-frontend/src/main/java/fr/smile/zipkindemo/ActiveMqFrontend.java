package fr.smile.zipkindemo;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@EnableAutoConfiguration
@RestController
@CrossOrigin
public class ActiveMqFrontend {
	private Logger logger = LoggerFactory.getLogger(ActiveMqFrontend.class);

	@Autowired
	private JmsTemplate jmsTemplate;

	@Value("${spring.application.name}")
	private String applicationName;

	@Value("${demo.messageQueueName}")
	private String messageQueueName;

	@RequestMapping("/")
	public String processRootCall() {
		logger.debug("{} - processRootCall", applicationName);
		jmsTemplate.convertAndSend(messageQueueName, new Date());
		return "OK";
	}

	public static void main(String[] args) {
		SpringApplication.run(ActiveMqFrontend.class);
	}
}
