package com.esolutions.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.esolutions.configuration.YodleeDataFetchExecutor;
import com.esolutions.model.Provider;
import com.esolutions.model.Providers;
import com.esolutions.repository.YodleeAPIRepository;
import com.esolutions.responses.Response;

@Service
public class YodleeAPIServiceImpl implements YodleeAPIService {

	@Autowired
	private YodleeAPIRepository yodleeAPIRepository;

	@Value("${yodlee.records.batch.size}")
	private int skip;

	@Value("${yodlee.records.total.size}")
	private int totalSize;

	private static final Logger log = LoggerFactory.getLogger(YodleeAPIServiceImpl.class.toString());;

	@Override
	public Providers getProviders() {
		log.info("Start Getting Providers");
		Providers providers = this.yodleeAPIRepository.getAllProviders();
		log.info("End");
		return providers;
	}

	@Override
	public List<Document> getGroupByCountryISO() {
		return this.yodleeAPIRepository.getGroupByCountryISO();
	}

	@Override
	public Provider getProvidersByName(Object name) {
		return this.yodleeAPIRepository.getProviderByName(name);
	}

	@Override
	public Provider updateProviders(Provider provider) {
		return this.yodleeAPIRepository.updateProviders(provider);

	}

	@Override
	public Response fetchYodleeProviders() {
		this.yodleeAPIRepository.deleteIfExists();
		// Start the clock
		long start = System.currentTimeMillis();

		List<Integer> list = new ArrayList<>();
		for (int i = 0; i < totalSize; i += skip) {
			list.add(i);
		}

		CompletableFuture<Providers>[] listOfCompletableFuture = new CompletableFuture[list.size()];
		int j = 0;

		// Kick of multiple, asynchronous lookups
		for (int i = 0; i < listOfCompletableFuture.length; i++) {
			CompletableFuture<Providers> completableFuture = listOfCompletableFuture[i];
			completableFuture = yodleeAPIRepository.getProviders(list.get(j));
			listOfCompletableFuture[i] = completableFuture;
			j++;
		}

		// Wait until they are all done
		CompletableFuture.allOf(listOfCompletableFuture).join();
		// Print elapsed time
		log.info("Elapsed time: " + (System.currentTimeMillis() - start));
		List<Response> responses = new ArrayList<Response>(0);
		try {
			responses = this.yodleeAPIRepository.processDataToMongoDB(listOfCompletableFuture);
		} catch (InterruptedException | ExecutionException e) {
			log.debug(e.getMessage());
		}
		return processResponse(responses);

	}

	private Response processResponse(List<Response> response) {
		final long[] processingCount = new long[2];
		response.parallelStream().forEach((a) -> {
			if (a.getCode() == 500) {
				processingCount[0]++;
			} else {
				processingCount[1]++;
			}
		});

		return new Response("FETCH", "Operation Completed", 200, processingCount[0], processingCount[1]);
	}

}
