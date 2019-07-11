package com.esolutions.dao;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

@Configuration
public abstract class AbstractDAO {

	@Value("${spring.mongodb.uri}")
	private String connectionString;

	protected final String MFLIX_DATABASE;

	protected MongoDatabase db;

	protected MongoClient mongoClient;

	protected AbstractDAO(MongoClient mongoClient, String databaseName) {
		this.mongoClient = mongoClient;
		MFLIX_DATABASE = databaseName;
		this.db = this.mongoClient.getDatabase(MFLIX_DATABASE);
	}

	public ObjectId generateObjectId() {
		return new ObjectId();
	}

}
