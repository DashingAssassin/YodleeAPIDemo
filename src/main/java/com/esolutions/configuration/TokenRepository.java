package com.esolutions.configuration;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.esolutions.exception.TokenException;

@Component
public class TokenRepository {

	private String URL = "https://8244c0d7-2407-48b0-ad3e-6279b9cebcaa.mock.pstmn.io/do-nothing";

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private YodleeAPIHeaders headers;

	public String getToken() {
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers.getHeaders());
		ResponseEntity<String> responseToken = this.restTemplate.exchange(URL, HttpMethod.GET, requestEntity,
				String.class);
		String response = "";
		try {
			response = responseToken.getBody();
			if (isEmpty(response)) {
				throw new TokenException("Error In Retireing Token. Token was Empty");
			}
		} catch (TokenException e) {
			// Log Excpeption Here;
		}
		return response;
	}

	private boolean isEmpty(String content) {
		return (null == content || "".equals(content.trim()));
	}
}
