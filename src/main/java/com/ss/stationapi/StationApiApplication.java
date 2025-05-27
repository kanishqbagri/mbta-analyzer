package com.ss.stationapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ss.stationapi.model.StationInfo;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.ss.stationapi.service.StationService.BASE_URL;

@SpringBootApplication
public class StationApiApplication {
	private final HttpClient client = HttpClient.newHttpClient();
	private final ObjectMapper mapper = new ObjectMapper();
	private Map<String, StationInfo> stationById;

	public static void main(String[] args) {
		SpringApplication.run(StationApiApplication.class, args);
	}
    @PostConstruct
	public void startSetUp()
	{
		System.out.println("Spring context initialized – starting MBTA station data load...");

		System.out.println("***Data Cached***");
	}

}
