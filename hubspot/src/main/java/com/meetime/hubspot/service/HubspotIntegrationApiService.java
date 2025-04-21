package com.meetime.hubspot.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.retry.Retry;
import io.vavr.CheckedFunction0;
import io.vavr.control.Try;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.meetime.hubspot.config.HubspotIntegrationApiConfig;
import com.meetime.hubspot.exception.HubspotAuthorizationException;
import com.meetime.hubspot.exception.HubspotCallbackProcessException;
import com.meetime.hubspot.exception.HubspotContactException;
import com.meetime.hubspot.exception.HubspotWebhookException;
import com.meetime.hubspot.model.Contact;

@Service
public class HubspotIntegrationApiService {

    private final RateLimiter rateLimiter;

    private final Retry retry;
	
	private final RestTemplate restTemplate = new RestTemplate();
	
	private final HubspotIntegrationApiConfig config;

	public HubspotIntegrationApiService(HubspotIntegrationApiConfig config, Retry retry, RateLimiter rateLimiter) {
	    this.config = config;
	    this.retry = retry;
	    this.rateLimiter = rateLimiter;
	}
	
	
	public String getAuthorizationUrl() {
		
		try {
	 
		    String mainUrl = "https://app.hubspot.com/oauth/authorize";
		    
		    String clientId = config.getClientId();
	        String redirectUri = config.getRedirectUri();
	        String scopes = config.getScopes();
		
		    StringBuilder completeUrl = new StringBuilder(mainUrl);
		    completeUrl.append("?");
		    completeUrl.append("client_id=").append(clientId);
		    completeUrl.append("&redirect_uri=").append(URLEncoder.encode(redirectUri, StandardCharsets.UTF_8));
		    completeUrl.append("&scope=").append(URLEncoder.encode(scopes, StandardCharsets.UTF_8));
		    completeUrl.append("&response_type=code");
	
		    return completeUrl.toString();
		}
	    catch (Exception e) {
	    	 throw new HubspotAuthorizationException("Error when generating authorization URL");
		}
	}

	
	public String getTokenByCallbackProcess(String authorizationCode) {
		
		try {
			String tokenUrl = "https://api.hubapi.com/oauth/v1/token";
	
		    HttpHeaders headers = new HttpHeaders();
		    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		    
		    String clientId = config.getClientId();
		    String clientSecret = config.getClientSecret();
		    String redirectUri = config.getRedirectUri();
	
		    StringBuilder body = new StringBuilder();
		    body.append("grant_type=authorization_code");
		    body.append("&client_id=").append(clientId);
		    body.append("&client_secret=").append(clientSecret);
		    body.append("&redirect_uri=").append(URLEncoder.encode(redirectUri, StandardCharsets.UTF_8));
		    body.append("&code=").append(authorizationCode);
	
		    HttpEntity<String> request = new HttpEntity<>(body.toString(), headers);
	
		    RestTemplate restTemplate = new RestTemplate();
		    ResponseEntity<String> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, request, String.class);
	
		    return response.getBody();
		}
		catch (Exception e) {
	    	 throw new HubspotCallbackProcessException("Error when getting the token by callback process");
		}
		
	}
	
	public String createContact(String accessToken, Contact contact) {
			
			Supplier<String> createContactSupplier = Retry.decorateSupplier(retry,
					RateLimiter.decorateSupplier(rateLimiter, () -> {
						String createContactUrl = "https://api.hubapi.com/crm/v3/objects/contacts";
						
						HttpHeaders headers = new HttpHeaders();
						headers.setBearerAuth(accessToken);
						headers.setContentType(MediaType.APPLICATION_JSON);
						
						Map<String, Object> body = new HashMap<>();
						Map<String, String> properties = new HashMap<>();
				
				        properties.put("email", contact.getEmail());
				        properties.put("firstname", contact.getFirstname());
				        properties.put("lastname", contact.getLastname());
				        properties.put("phone", contact.getPhone());
				        
				        body.put("properties", properties);
				
				        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
				        
				        ResponseEntity<String> response = restTemplate.postForEntity(createContactUrl, entity, String.class);
				        return response.getBody();
						
					})
			);
			
			return Try.ofSupplier(createContactSupplier)
			    .recover(throwable -> {
			        throw new HubspotAuthorizationException("Error when creating contact on the HubSpot");
			    })
			    .get();
		
		
	}
	
	public void processWebhooks(List<Map<String, Object>> webhookData) {
		try {
	        System.out.println("Webhooks received from HubSpot:");
	        
	        for (Map<String, Object> event : webhookData) {
	            System.out.println(event);
	        }
		}
		catch (Exception e) {
			throw new HubspotWebhookException("Error when processing Webhook events");
		}
    }

}
