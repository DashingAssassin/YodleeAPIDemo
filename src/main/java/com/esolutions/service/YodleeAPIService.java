package com.esolutions.service;

import java.util.List;

import org.bson.Document;
import org.springframework.stereotype.Service;

import com.esolutions.model.Provider;
import com.esolutions.model.Providers;
import com.esolutions.responses.Response;

@Service
public interface YodleeAPIService {
	// A test Method to get all Providers
	public Providers getProviders();

	public List<Document> getGroupByCountryISO();

	public Provider getProvidersByName(Object name);

	public Provider updateProviders(Provider provider);
	
	public Response fetchYodleeProviders();

}
