package fr.smile.zipkindemo;

import java.util.Date;

import javax.jms.JMSException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;

@EnableAutoConfiguration
@EnableJms
public class ActiveMqConsumer {
	private Logger logger = LoggerFactory.getLogger(ActiveMqConsumer.class);

	@Value("${spring.application.name}")
	private String applicationName;

	@JmsListener(destination = "${demo.messageQueueName}")
	public void onMessage(Date date) throws JMSException {
		logger.debug("{} - onMessage", applicationName);
		System.err.println("date from message [" + date + "] processed by [" + applicationName + "]");
	}

	public static void main(String[] args) {
		SpringApplication.run(ActiveMqConsumer.class);
	}
}
