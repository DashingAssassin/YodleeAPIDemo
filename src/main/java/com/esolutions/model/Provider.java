package com.esolutions.model;

import java.util.Objects;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
@Document("providers")
public class Provider {

	@BsonId
	private ObjectId oid;

	@JsonProperty("id")
	private String id;

	@Field("name")
	private String name;

	@Field("baseUrl")
	private String baseUrl;

	@Field("countryISOCode")
	private String countryISOCode;

	public Provider(ObjectId oid, String name, String baseUrl, String countryISOCode) {
		super();
		this.oid = oid;
		this.id = String.valueOf(oid);
		this.name = name;
		this.baseUrl = baseUrl;
		this.countryISOCode = countryISOCode;
	}

	public Provider copyProvder(Provider provider) {
		return new Provider(provider.oid, provider.name, provider.baseUrl, provider.countryISOCode);
	}

	public Provider() {
		super();
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the baseUrl
	 */
	public String getBaseUrl() {
		return baseUrl;
	}

	/**
	 * @param baseUrl the baseUrl to set
	 */
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	/**
	 * @return the countryISOCode
	 */
	public String getCountryISOCode() {
		return countryISOCode;
	}

	/**
	 * @param countryISOCode the countryISOCode to set
	 */
	public void setCountryISOCode(String countryISOCode) {
		this.countryISOCode = countryISOCode;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, baseUrl, countryISOCode, name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Provider))
			return false;
		Provider other = (Provider) obj;
		return Objects.equals(id, other.id) && Objects.equals(baseUrl, other.baseUrl)
				&& Objects.equals(countryISOCode, other.countryISOCode) && Objects.equals(name, other.name);
	}

	@Override
	public String toString() {
		return "Provider [_id=" + id + ", name=" + name + ", baseUrl=" + baseUrl + ", countryISOCode=" + countryISOCode
				+ "]";
	}

	/**
	 * @return the oid
	 */
	public ObjectId getOid() {
		return oid;
	}

	/**
	 * @param oid the oid to set
	 */
	public void setOid(ObjectId oid) {
		this.oid = oid;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

}
