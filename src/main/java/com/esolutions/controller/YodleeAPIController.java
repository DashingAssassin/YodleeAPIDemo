package com.esolutions.controller;

import java.util.List;

import javax.ws.rs.core.MediaType;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.esolutions.model.Provider;
import com.esolutions.model.Providers;
import com.esolutions.responses.Response;
import com.esolutions.service.YodleeAPIService;

@RestController
public class YodleeAPIController {

	@Autowired
	private YodleeAPIService yodleeAPIService;

	/**
	 * Test Method to verify Providers exists in DB
	 * 
	 * @return Providers List
	 */
	@GetMapping(value = { "/providers" }, produces = { MediaType.APPLICATION_JSON }, consumes = {
			MediaType.APPLICATION_JSON })
	public Providers getProviders() {
		return this.yodleeAPIService.getProviders();
	}

	/**
	 * Test Method to verify Fetch Providers works as a rest call templates
	 * 
	 * @return Response Object stating failure as well as pass count
	 */
	@GetMapping(value = { "/fetchProvidersFromYodlee" }, produces = { MediaType.APPLICATION_JSON }, consumes = {
			MediaType.APPLICATION_JSON })
	public Response getYodleeProviders() {
		return this.yodleeAPIService.fetchYodleeProviders();
	}

	@GetMapping(value = { "/providers/{name}" }, produces = { MediaType.APPLICATION_JSON }, consumes = {
			MediaType.APPLICATION_JSON })
	public Provider getProvidersByName(@PathVariable("name") Object name) {
		return this.yodleeAPIService.getProvidersByName(name);
	}

	@PostMapping(value = { "/updateProvider" }, produces = { MediaType.APPLICATION_JSON }, consumes = {
			MediaType.APPLICATION_JSON })
	public Response updateProviders(@RequestBody Provider provider) {
		Provider result = this.yodleeAPIService.updateProviders(provider);
		return (result == null) ? new Response("update", "Unable to update due to technical issues", 500)
				: new Response("update", "Updated Successfully", 200);
	}

	@GetMapping(value = { "/groupByCountryCode" }, produces = { MediaType.APPLICATION_JSON }, consumes = {
			MediaType.APPLICATION_JSON })
	public List<Document> groupByCountryCode() {
		return this.yodleeAPIService.getGroupByCountryISO();
	}

}
