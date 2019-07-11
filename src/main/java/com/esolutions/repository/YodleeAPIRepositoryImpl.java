package com.esolutions.repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.esolutions.configuration.YodleeAPIHeaders;
import com.esolutions.dao.ProvidersDAO;
import com.esolutions.model.Provider;
import com.esolutions.model.Providers;
import com.esolutions.responses.Response;
import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
public class YodleeAPIRepositoryImpl implements YodleeAPIRepository {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private YodleeAPIHeaders headers;

	@Autowired
	private ProvidersDAO providersDAO;

	@Value("${spring.rest.yodlee.providers.url}")
	private String PROVIDER_URL;

	private static final Logger log = LoggerFactory.getLogger(YodleeAPIRepositoryImpl.class.toString());;

	@Override
	@Async
	public CompletableFuture<Providers> getProviders(int skip) {
		HttpEntity<String> entity = new HttpEntity<String>(getHeader());
		ResponseEntity<String> providers = this.restTemplate.exchange(PROVIDER_URL, HttpMethod.GET, entity,
				String.class, skip);
		Providers provide = convertToProviders(providers.getBody());
		try {
			Thread.sleep(100L);
		} catch (InterruptedException e) {
			log.debug("Error occurred" + e.getMessage());
		}
		return CompletableFuture.completedFuture(provide);
	}

	@Override
	public Providers getAllProviders() {
		return this.providersDAO.fetchAll();
	}

	private Providers convertToProviders(String content) {
		if (isEmpty(content)) {
			System.out.println("Error Empty Response Received. Sending back empty response");
			return null;
		}
		Providers provider = null;
		try {
			provider = mapper.readValue(content, Providers.class);
		} catch (IOException e) {
			log.debug("Exception Occurred" + e.getMessage());
		}
		return provider;
	}

	private MultiValueMap<String, String> getHeader() {
		MultiValueMap<String, String> requestHeaders = headers.getHeaders();
		String token = "replace value with the token got from Yodlee";
		requestHeaders.add("token", token);
		requestHeaders.add("Authorization", "Bearer " + token);
		return requestHeaders;
	}

	private boolean isEmpty(String content) {
		return (null == content || "".equals(content.trim()));
	}

	@Override
	public List<Response> processDataToMongoDB(CompletableFuture<Providers>[] listOfCompletableFuture)
			throws InterruptedException, ExecutionException {
		List<Response> responses = new ArrayList<>();
		for (CompletableFuture<Providers> completableFuture : listOfCompletableFuture) {
			Response response = null;
			try {
				Providers providers = completableFuture.get();
				providersDAO.addProviders(providers.getProviders());
				response = new Response("insert", "Inserted SuccessFully", 200);
			} catch (Exception exception) {
				response = new Response("insert", exception.getMessage(), 500);
				log.debug("Exception occurred" + exception.getMessage());
			}
			responses.add(response);
		}
		return responses;

	}

	@Override
	public List<Document> getGroupByCountryISO() {
		return this.providersDAO.getCountByISOCode();
	}

	@Override
	public Provider getProviderByName(Object name) {
		return this.providersDAO.getProviderByName(name);

	}

	@Override
	public Provider updateProviders(Provider provider) {
		return this.providersDAO.updateProviders(provider);
	}

	@Override
	public void deleteIfExists() {
		this.providersDAO.deleteAllIfExists();

	}

}
