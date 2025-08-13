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
import com.ss.stationapi.service.StationService;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Entry point for the Spring Boot application that loads and caches MBTA station data.
 */
@SpringBootApplication
public class StationApiApplication {
	private final HttpClient client = HttpClient.newHttpClient();
	private final ObjectMapper mapper = new ObjectMapper();
	private Map<String, StationInfo> stationById;

	@Autowired
	private StationService stationService;

	public static void main(String[] args) {
		SpringApplication.run(StationApiApplication.class, args);
	}
	

 /**
     * Method annotated with @PostConstruct is executed after the Spring context is initialized.
     * This is a good place to trigger setup logic such as loading and caching data.
     */
    @PostConstruct
	public void startSetUp()
	{
		System.out.println("Spring context initialized – starting MBTA station data load...");
  		System.out.println("Initializing StationService...");
            System.out.println("Fetching all routes and stops...");
            stationService.loadStationsAndRoutes(); // Populates all StationInfo attributes
            System.out.println("Loading adjacency for all lines...");
            stationService.loadAdjacency();         // Populates neighbors for each station
		System.out.println("***Data Cached***");
	}

}
