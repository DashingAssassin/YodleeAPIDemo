package com.esolutions.dao;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.esolutions.exception.IncorrectMongoDAOException;
import com.esolutions.model.Provider;
import com.esolutions.model.Providers;
import com.mongodb.MongoBulkWriteException;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.BsonField;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;

@Component
public class ProvidersDAO extends AbstractDAO {
	public static String PROVIDERS_COLLECTION = "providers";

	private MongoCollection<Provider> providersCollection;

	private final Logger log;

	@Autowired
	public ProvidersDAO(MongoClient mongoClient, @Value("${spring.mongodb.database}") String databaseName) {
		super(mongoClient, databaseName);
		CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
				fromProviders(PojoCodecProvider.builder().automatic(true).build()));

		log = LoggerFactory.getLogger(this.getClass());
		providersCollection = db.getCollection(PROVIDERS_COLLECTION, Provider.class)
				.withCodecRegistry(pojoCodecRegistry);
	}

	/**
	 * Add Providers list to MongoDB
	 * 
	 * @param providers The providers List
	 */
	public boolean addProviders(List<Provider> providers) {
		log.info("Start Adding Provider to Collection ");
		try {
			if (!CollectionUtils.isEmpty(providers)) {
				this.providersCollection.withWriteConcern(WriteConcern.MAJORITY).insertMany(providers);
				return true;
			}
		} catch (MongoException exception) {
			log.info(exception.getMessage());
			throw new IncorrectMongoDAOException(exception.getMessage());
		}
		log.info("End");
		return false;
	}

	/**
	 * Fetch count to verify how many providers are available
	 * 
	 * @return The Count of Document
	 */
	public long fetchCount() {
		log.info("Start Fetching count of Collection ");
		try {
			return this.providersCollection.countDocuments();
		} catch (IncorrectMongoDAOException exception) {
			log.trace(exception.getMessage());
		}
		log.info("End");
		return 0;
	}

	/**
	 * Delete the collection on startup if it exists to calculate the time required
	 * for the Requets to run and push data to mongo db
	 */
	public void deleteAllIfExists() {
		if (fetchCount() != 0) {
			this.providersCollection.deleteMany(new Document());
		}
	}

	public List<Document> getCountByISOCode() {
		List<Document> groupByCountryCode = new LinkedList<Document>();
		AggregateIterable<Document> groupByISOCode = db.getCollection(PROVIDERS_COLLECTION)
				.aggregate(getAggregates("$countryISOCode"));
		pushToList(groupByISOCode, groupByCountryCode);
		return groupByCountryCode;
	}

	private void pushToList(AggregateIterable<Document> year, List<Document> groupByCountryCode) {
		Iterator<Document> iterator = year.iterator();
		while (iterator.hasNext()) {
			groupByCountryCode.add(iterator.next());
		}

	}

	private Bson getProjections() {
		Document document = new Document();
		document.append("countryCode", "$_id").append("total", "$total");
		document.append("_id", 0);
		return document;
	}

	private BsonField getFields() {
		return new BsonField("total", new Document("$sum", 1));
	}

	private List<? extends Bson> getAggregates(String key) {
		return Arrays.asList(Aggregates.group(key, getFields()),
				Aggregates.project(Projections.fields(getProjections())));
	}

	public Providers fetchAll() {
		Providers providers = new Providers();
		providers.setProviders(new ArrayList<Provider>());
		this.providersCollection.find().into(providers.getProviders());
		return providers;
	}

	public Provider getProviderByName(Object name) {
		return this.providersCollection.find(createNameFilter(name)).first();

	}

	private Bson createNameFilter(Object name) {
		return Filters.eq("name", name);
	}

	public Provider updateProviders(Provider provider) {
		Provider oldProvider = this.providersCollection.find(getBson(provider)).first();
		if (checkIfProvidersAreEquals(provider, oldProvider)) {
			getUpdatedProvider(oldProvider, provider);
			Provider result = this.providersCollection.findOneAndUpdate(Filters.eq("id", provider.getId()),
					bsonUpdate(oldProvider));
			return result;
		} else {
			return null;
		}
	}

	private Bson bsonUpdate(Provider oldProvider) {
		Bson bson = new Document("$set", oldProvider);
		return bson;
	}

	private boolean checkIfProvidersAreEquals(Provider provider, Provider oldProvider) {
		return (Objects.nonNull(provider) && Objects.nonNull(oldProvider)) && (Objects.nonNull(provider.getId())
				&& Objects.nonNull(oldProvider.getId()) && Objects.equals(oldProvider.getId(), provider.getId()));

	}

	private void getUpdatedProvider(Provider oldProvider, Provider provider) {
		if (!Objects.equals(oldProvider.getName(), provider.getName())) {
			oldProvider.setName(provider.getName());
		}
		if (!Objects.equals(oldProvider.getBaseUrl(), provider.getBaseUrl())) {
			oldProvider.setBaseUrl(provider.getBaseUrl());
		}
		if (!Objects.equals(oldProvider.getCountryISOCode(), provider.getCountryISOCode())) {
			oldProvider.setCountryISOCode(provider.getCountryISOCode());
		}
	}

	private Bson getBson(Provider provider) {
		return new Document("id", provider.getId());
	}
}
