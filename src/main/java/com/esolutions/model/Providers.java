package com.esolutions.model;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect
public class Providers {

	@JsonProperty("provider")
	private List<Provider> providers;

	public Providers(List<Provider> providers) {
		super();
		this.providers = providers;
	}

	public Providers() {
		super();
	}

	/**
	 * @return the providers
	 */
	public List<Provider> getProviders() {
		return providers;
	}

	/**
	 * @param providers the providers to set
	 */
	public void setProviders(List<Provider> providers) {
		this.providers = providers;
	}

	@Override
	public String toString() {
		return "Providers [providers=" + providers + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(providers);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Providers))
			return false;
		Providers other = (Providers) obj;
		return Objects.equals(providers, other.providers);
	}

}
