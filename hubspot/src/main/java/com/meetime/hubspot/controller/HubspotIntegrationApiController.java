package com.meetime.hubspot.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.meetime.hubspot.model.Contact;
import com.meetime.hubspot.service.HubspotIntegrationApiService;

@RestController
@RequestMapping("/api")
public class HubspotIntegrationApiController {
	
	private final HubspotIntegrationApiService service;

    public HubspotIntegrationApiController(HubspotIntegrationApiService service) {
        this.service = service;
    }
	
	@GetMapping("/oauth/url")
	public ResponseEntity<String> getAuthorizationUrl() {
		return ResponseEntity.ok(service.getAuthorizationUrl());
	}
	
	@GetMapping("/oauth/callback")
	public ResponseEntity<String> getTokenByCallbackProcess(@RequestParam("code") String code) {
		return ResponseEntity.ok(service.getTokenByCallbackProcess(code));
	}
	
	@PostMapping("/contact")
	public ResponseEntity<String> createContact(@RequestHeader("Authorization") String accessToken,
	        @RequestBody Contact contact) {
	    return ResponseEntity.ok(service.createContact(accessToken, contact));
	}
	
	@PostMapping("/webhooks")
	public ResponseEntity<Void> receiveWebhooks(@RequestBody List<Map<String, Object>> webhookData) {
		service.processWebhooks(webhookData);
		return ResponseEntity.ok().build();
		
	}


}
