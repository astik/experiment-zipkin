package fr.smile.zipkindemo;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fr.smile.zipkindemo.data.Customer;
import fr.smile.zipkindemo.data.CustomerRepository;

@EnableAutoConfiguration
@RestController
@CrossOrigin
public class MysqlFrontend {
	private static final Logger logger = LoggerFactory.getLogger(MysqlFrontend.class);

	@Value("${spring.application.name}")
	private String applicationName;

	@Autowired
	private CustomerRepository repository;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public List<Customer> findAll() {
		logger.debug("{} - findAll", applicationName);
		return repository.findAll();
	}

	@RequestMapping(value = "/", method = RequestMethod.POST)
	public String init() {
		logger.debug("{} - init", applicationName);
		repository.save(new Customer("Jack", "Bauer"));
		repository.save(new Customer("Chloe", "O'Brian"));
		repository.save(new Customer("Kim", "Bauer"));
		repository.save(new Customer("David", "Palmer"));
		repository.save(new Customer("Michelle", "Dessler"));
		return "init OK";
	}

	public static void main(String[] args) {
		SpringApplication.run(MysqlFrontend.class);
	}
}
