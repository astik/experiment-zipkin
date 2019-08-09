package fr.smile.zipkindemo;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
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

import brave.http.HttpTracing;
import brave.httpclient.TracingHttpClientBuilder;

@EnableAutoConfiguration
@RestController
@CrossOrigin
public class HttpClientFrontend {
	private static final Logger logger = LoggerFactory.getLogger(HttpClientFrontend.class);

	@Autowired
	private HttpClient httpClient;

	@Value("${spring.application.name}")
	private String applicationName;

	@Value("${demo.backendBaseUrl}")
	private String backendBaseUrl;

	/**
	 * Apache HC clients aren't traced by default. This creates a traced instance.
	 */
	@Bean
	HttpClient httpClient(HttpTracing httpTracing) {
		return TracingHttpClientBuilder.create(httpTracing).build();
	}

	@RequestMapping("/")
	public String processRootCall() throws ClientProtocolException, IOException {
		logger.debug("{} - processRootCall", applicationName);
		HttpResponse response = httpClient.execute(new HttpGet(backendBaseUrl));
		return new BasicResponseHandler().handleResponse(response);
	}

	public static void main(String[] args) {
		SpringApplication.run(HttpClientFrontend.class);
	}
}
