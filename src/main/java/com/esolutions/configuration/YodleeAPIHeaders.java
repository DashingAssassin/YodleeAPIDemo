package com.esolutions.configuration;

import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;

public final class YodleeAPIHeaders {

	public MultiValueMap<String, String> getHeaders() {
		MultiValueMap<String, String> headers = new HttpHeaders();
		//removed headers from here for security purposes before checking in to git 
		return headers;
	}

}
