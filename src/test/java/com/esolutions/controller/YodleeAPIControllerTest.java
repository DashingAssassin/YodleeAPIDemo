package com.esolutions.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.esolutions.model.Provider;
import com.esolutions.responses.Response;
import com.esolutions.service.YodleeAPIService;

@EnableConfigurationProperties
@EnableAutoConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class YodleeAPIControllerTest {

	@InjectMocks
	private YodleeAPIController yodleeApiController;

	@MockBean
	private YodleeAPIService yodleeApiService;

	@Before
	public void inti() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void test_groupByCountryCode_hasDocuments_returnsGroupedDocuments() {
		Mockito.when(this.yodleeApiService.getGroupByCountryISO()).thenReturn(getDocuments());
		List<Document> result = this.yodleeApiController.groupByCountryCode();
		Assert.assertEquals(10, result.size());
		for (int i = 0; i < result.size(); i++) {
			Assert.assertEquals(getCountList().get(i), result.get(i).get("count"));
			Assert.assertEquals(getISOList().get(i), result.get(i).get("_id"));
		}
	}

	@Test
	public void test_groupByCountryCode_hasNoDocuments_returnsEmptyList() {
		Mockito.when(this.yodleeApiService.getGroupByCountryISO()).thenReturn(Collections.emptyList());
		List<Document> result = this.yodleeApiController.groupByCountryCode();
		Assert.assertEquals(0, result.size());
	}

	@Test
	public void test_UpdateProviders_ProviderExists_AndNameIsUpdated() {
		Provider oldProvider = getProviders();
		Provider newProvider = new Provider();
		newProvider = newProvider.copyProvder(oldProvider);
		newProvider.setName("Name1");
		Mockito.when(this.yodleeApiService.updateProviders(newProvider)).thenReturn(newProvider);
		Response response = this.yodleeApiController.updateProviders(newProvider);
		Assert.assertNotNull(response);
		Assert.assertEquals(200, response.getCode());
		Assert.assertEquals("Updated Successfully", response.getMessage());
		Assert.assertEquals("update", response.getOperation());

	}

	@Test
	public void test_UpdateProviders_ProviderDoesNotExists_ReturnResponseWithUnSuccessFulMessage() {
		Provider oldProvider = getProviders();
		Provider newProvider = new Provider();
		newProvider = newProvider.copyProvder(oldProvider);
		newProvider.setName("Name1");
		Mockito.when(this.yodleeApiService.updateProviders(newProvider)).thenReturn(null);
		Response response = this.yodleeApiController.updateProviders(newProvider);
		Assert.assertNotNull(response);
		Assert.assertEquals(500, response.getCode());
		Assert.assertEquals("Unable to update due to technical issues", response.getMessage());
		Assert.assertEquals("update", response.getOperation());
	}

	@Test
	public void test_getProvidersByName_ProviderExists_ReturnsProvider() {
		Provider provider = getProviders();
		Mockito.when(this.yodleeApiService.getProvidersByName("tempName")).thenReturn(provider);
		Provider result = this.yodleeApiController.getProvidersByName(provider.getName());
		Assert.assertNotNull(result);
		Assert.assertEquals(result.getBaseUrl(), provider.getBaseUrl());
		Assert.assertEquals(result.getName(), provider.getName());
		Assert.assertEquals(result.getCountryISOCode(), provider.getCountryISOCode());
	}

	@Test
	public void test_getProvidersByName_ProviderDoesNotExists_ReturnsNull() {
		Mockito.when(this.yodleeApiService.getProvidersByName("tempName")).thenReturn(null);
		Provider result = this.yodleeApiController.getProvidersByName("tempName");
		Assert.assertNull(result);
	}

	private Provider getProviders() {
		Provider provider = new Provider();
		provider.setBaseUrl("tempUrl");
		provider.setCountryISOCode("IN");
		provider.setId("ID1");
		provider.setName("tempName");
		provider.setOid(new ObjectId());
		return provider;
	}

	private List<Integer> getCountList() {
		return Arrays.asList(5, 3, 6, 7, 1, 6, 4, 9, 10, 11);
	}

	private List<String> getISOList() {
		return Arrays.asList("IN", "US", "AB", "CD", "EF", "GH", "IJ", "KL", "MN", "OP");
	}

	private List<Document> getDocuments() {
		List<Integer> countList = getCountList();
		List<String> isoList = getISOList();
		List<Document> documentList = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			Document document = new Document();
			document.append("count", countList.get(i)).append("_id", isoList.get(i));
			documentList.add(document);
		}
		return documentList;

	}

}
