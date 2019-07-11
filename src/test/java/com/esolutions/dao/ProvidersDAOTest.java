package com.esolutions.dao;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.esolutions.configuration.MongoDBConfiguration;
import com.esolutions.exception.IncorrectMongoDAOException;
import com.esolutions.model.Provider;
import com.esolutions.model.Providers;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

@SpringBootTest(classes = { MongoDBConfiguration.class })
@EnableConfigurationProperties
@EnableAutoConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class ProvidersDAOTest {

	private ProvidersDAO providerDao;

	@Mock
	MongoClient mongoClient;

	@Value("${spring.mongodb.database}")
	String databaseName;

	@Mock
	private MongoCollection<Provider> providers;

	@Mock
	private MongoDatabase providerDB;

	@Mock
	private FindIterable<Provider> providerIterableMock;

	@Mock
	private AggregateIterable<Document> aggregateMock;

	@Mock
	private MongoCursor<Document> iteratorMock;

	@Mock
	private MongoCollection<Document> mongoDocumentCollection;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		Mockito.when(this.mongoClient.getDatabase(databaseName)).thenReturn(providerDB);
		Mockito.when(this.providerDB.withWriteConcern(WriteConcern.MAJORITY)).thenReturn(this.providerDB);
		Mockito.when(this.providerDB.getCollection(ProvidersDAO.PROVIDERS_COLLECTION, Provider.class))
				.thenReturn(providers);
		Mockito.when(this.providers.withCodecRegistry(Mockito.any())).thenReturn(providers);
		Mockito.when(this.providers.withWriteConcern(WriteConcern.MAJORITY)).thenReturn(this.providers);
		this.providerDao = new ProvidersDAO(mongoClient, databaseName);
	}

	@Test
	public void test_addProviders_whenListHasData_AddsListToDB() {
		List<Provider> providers = getProviders();
		boolean result = this.providerDao.addProviders(providers);
		Assert.assertTrue(result);
	}

	@Test
	public void test_addProviders_whenListHasInCorrectData_ExceptionIsThrown() {
		List<Provider> providers = getIncorrectData();
		String exceptedErrorMessage = "Bulk write operation error on server localhost:27017. Write errors: [BulkWriteError{index=1, code=11000, message='E11000 duplicate key error collection: test.providers index: _id_ dup key: { : ObjectId('"
				+ providers.get(0).getOid().toHexString() + "') }', details={ }}]. ";
		try {
			Mockito.when(this.providers.withWriteConcern(WriteConcern.MAJORITY))
					.thenThrow(new MongoException(exceptedErrorMessage));
			this.providerDao.addProviders(providers);
		} catch (IncorrectMongoDAOException exception) {
			Assert.assertEquals(exceptedErrorMessage, exception.getMessage());
		}
	}

	@Test
	public void test_fetchCount_hasDocuments() {
		Mockito.when(this.providers.countDocuments()).thenReturn(5L);
		long count = this.providerDao.fetchCount();
		Assert.assertEquals(5L, count);
	}

	@Test
	public void test_fetchCount_hasZeroDocuments_Returns_0() {
		Mockito.when(this.providers.countDocuments()).thenReturn(0L);
		long count = this.providerDao.fetchCount();
		Assert.assertEquals(0L, count);
	}

	@Test
	public void test_fetchAll_NotEmpty() {
		Mockito.when(this.providers.find()).thenReturn(providerIterableMock);
		Providers result = this.providerDao.fetchAll();
		Assert.assertNotNull(result);
	}

	@Test
	public void test_getProviderByName_providernameexists_returnsproviderObject() {
		Provider provider = getProvider(1);
		Mockito.when(this.providers.find(Mockito.any(Bson.class))).thenReturn(providerIterableMock);
		Mockito.when(this.providerIterableMock.first()).thenReturn(provider);
		Provider result = this.providerDao.getProviderByName("temp");
		Assert.assertEquals(provider, result);
	}

	@Test
	public void test_getProviderByName_providernamedoesnotexists_returnsnull() {
		Provider provider = null;
		Mockito.when(this.providers.find(Mockito.any(Bson.class))).thenReturn(providerIterableMock);
		Mockito.when(this.providerIterableMock.first()).thenReturn(provider);
		Provider result = this.providerDao.getProviderByName("temp");
		Assert.assertEquals(provider, result);
	}

	@Test
	public void test_updateProvider_ifNameExists_returnOldProvider() {
		Provider provider = getProvider(1);
		Provider providerObj = new Provider(provider.getOid(), "2", "testUrl", "tempCode");
		providerObj.setName("2");
		Mockito.when(this.providers.find(Mockito.any(Bson.class))).thenReturn(providerIterableMock);
		Mockito.when(this.providerIterableMock.first()).thenReturn(provider);
		Mockito.when(this.providers.findOneAndUpdate(Mockito.any(Bson.class), Mockito.any(Bson.class)))
				.thenReturn(provider);
		Provider result = this.providerDao.updateProviders(providerObj);
		Assert.assertEquals(provider, result);
	}

	@Test
	public void test_groupByCountryISOCode_hasContent_returnGroupByObjects() {
		Mockito.when(this.providerDB.getCollection(ProvidersDAO.PROVIDERS_COLLECTION))
				.thenReturn(mongoDocumentCollection);
		Mockito.when(this.mongoDocumentCollection.aggregate(Mockito.any(List.class))).thenReturn(aggregateMock);
		Mockito.when(this.aggregateMock.iterator()).thenReturn(iteratorMock);
		Mockito.when(this.iteratorMock.hasNext()).thenReturn(true).thenReturn(true).thenReturn(false);
		Mockito.when(this.iteratorMock.next()).thenReturn(getDocumentObject(5, "US"))
				.thenReturn(getDocumentObject(3, "IN"));
		List<Document> document = this.providerDao.getCountByISOCode();
		Assert.assertEquals(2,document.size());
		Assert.assertEquals(document.get(0).get("count"), 5);
		Assert.assertEquals(document.get(0).get("_id"), "US");
	}

	private Document getDocumentObject(int i, String string) {
		Document document = new Document();
		document.append("count", i).append("_id", string);
		return document;
	}

	@Test
	public void test_updateProvider_ifNameDoesNotExists_returnsNull() {
		Provider provider = getProvider(1);
		Provider providerObj = new Provider(provider.getOid(), "2", "testUrl", "tempCode");
		providerObj.setName("2");
		Mockito.when(this.providers.find(Mockito.any(Bson.class))).thenReturn(providerIterableMock);
		Mockito.when(this.providerIterableMock.first()).thenReturn(null);
		Mockito.when(this.providers.findOneAndUpdate(Mockito.any(Bson.class), Mockito.any(Bson.class)))
				.thenReturn(provider);
		Provider result = this.providerDao.updateProviders(providerObj);
		Assert.assertEquals(null, result);
	}

	private List<Provider> getIncorrectData() {
		List<Provider> providers = getProviders();
		getDuplicateProvidersId(providers);
		return providers;
	}

	private List<Provider> getProviders() {
		List<Provider> providers = new ArrayList<Provider>();
		for (int i = 0; i < 5; i++) {
			providers.add(getProvider(i));
		}
		return providers;
	}

	private List<Provider> getDuplicateProvidersId(List<Provider> providers) {
		final ObjectId obj = new ObjectId();
		for (int i = 0; i < 5; i++) {
			providers.add(getProvider(i));
			Provider provider = providers.get(i);
			provider.setOid(obj);
			provider.setId(null);
		}
		return providers;
	}

	private Provider getProvider(int i) {
		Provider provider = new Provider();
		provider.setBaseUrl(String.valueOf(i));
		provider.setCountryISOCode(String.valueOf(i));
		provider.setOid(this.providerDao.generateObjectId());
		provider.setId(provider.getOid().toHexString());
		provider.setName(String.valueOf(i));
		return provider;
	}

}
