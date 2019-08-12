package fr.smile.zipkindemo;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.CrossOrigin;

@EnableAutoConfiguration
@CrossOrigin
@EnableKafka
public class KafkaTemplateConsumer {
	private static final Logger logger = LoggerFactory.getLogger(KafkaTemplateConsumer.class);

	@Value("${spring.application.name}")
	private String applicationName;

	@Value("${demo.kafkaTopic}")
	private String topic;

	@KafkaListener(topics = "${demo.kafkaTopic}")
	public void onMessage(ConsumerRecord<?, ?> message) {
		logger.debug("{} - onMessage", applicationName);
		System.err.println("message value [" + message.value() + "] processed by [" + applicationName + "]");
	}

	public static void main(String[] args) {
		SpringApplication.run(KafkaTemplateConsumer.class);
	}
}
