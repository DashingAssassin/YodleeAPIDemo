package com.esolutions.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.esolutions.dao.ProvidersDAO;
import com.esolutions.model.Providers;
import com.esolutions.repository.YodleeAPIRepository;
import com.esolutions.service.YodleeAPIServiceImpl;

/**
 * This class on startup will delete all data present in MongoDB So that
 * duplicate records should not come.
 * 
 * @author Nikhil
 *
 */
@Component
public class YodleeDataFetchExecutor implements CommandLineRunner {
	private static final Logger logger = LoggerFactory.getLogger(YodleeDataFetchExecutor.class);

	@Value("${yodlee.records.batch.size}")
	private int skip;

	@Value("${yodlee.records.total.size}")
	private int totalSize;
	
	private final YodleeAPIRepository yodleeAPIRepository;

	private final ProvidersDAO providersDAO;

	public YodleeDataFetchExecutor(YodleeAPIRepository yodleeAPIRepository, ProvidersDAO providersDAO) {
		this.yodleeAPIRepository = yodleeAPIRepository;
		this.providersDAO = providersDAO;
	}

	@Override
	public void run(String... args) throws Exception {
		this.providersDAO.deleteAllIfExists();
		// Start the clock
		long start = System.currentTimeMillis();

		List<Integer> list = new ArrayList<>();
		for (int i = 0; i < 10000; i += 500) {
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
		logger.info("Elapsed time: " + (System.currentTimeMillis() - start));
		this.yodleeAPIRepository.processDataToMongoDB(listOfCompletableFuture);
	}

}
