package com.esolutions.repository;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.bson.Document;
import org.springframework.stereotype.Repository;

import com.esolutions.model.Provider;
import com.esolutions.model.Providers;
import com.esolutions.responses.Response;

@Repository
public interface YodleeAPIRepository {

	public CompletableFuture<Providers> getProviders(int skip);

	List<Response> processDataToMongoDB(CompletableFuture<Providers>[] listOfCompletableFuture)
			throws InterruptedException, ExecutionException;

	List<Document> getGroupByCountryISO();

	public Providers getAllProviders();

	public Provider getProviderByName(Object name);

	public Provider updateProviders(Provider provider);

	public void deleteIfExists();

}
