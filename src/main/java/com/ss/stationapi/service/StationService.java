package com.ss.stationapi.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import com.ss.stationapi.model.StationInfo;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;

@Component
public class StationService {
    public static final String BASE_URL = "https://api-v3.mbta.com/";
    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    private final ObjectMapper mapper = new ObjectMapper();

    // Caches (instance fields)
    private Map<String, StationInfo> stationById = new HashMap<>();
    private Map<String, Set<String>> stopIdToLines = new HashMap<>();
    private Set<String> discoveredLineIds = new HashSet<>();
    private volatile boolean initialized = false;

    @PostConstruct
    public void init() {
        if (!initialized) {
            System.out.println("Initializing StationService...");
            System.out.println("Fetching all routes and stops...");
            loadStationsAndRoutes(); // Populates all StationInfo attributes
            System.out.println("Loading adjacency for all lines...");
            loadAdjacency();         // Populates neighbors for each station
            initialized = true;
        }
    }

    // Loads stations and their lines, caches in stationById and stopIdToLines
    public void loadStationsAndRoutes() {
        try {
            stopIdToLines = fetchAllRoutes();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "stops?filter[route_type]=0,1"))
                    .timeout(Duration.ofSeconds(10))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode root = mapper.readTree(response.body()).get("data");
            stationById.clear();
            for (JsonNode node : root) {
                StationInfo station = new StationInfo();
                String stopId = node.get("id").asText();
                String stationId = node.path("relationships").path("parent_station").path("data").path("id").isMissingNode()
                        ? stopId
                        : node.path("relationships").path("parent_station").path("data").path("id").asText();
                station.setName(node.get("attributes").get("name").asText());
                station.setId(stopId);
                station.setLatitude(node.get("attributes").get("latitude").asDouble());
                station.setLongitude(node.get("attributes").get("longitude").asDouble());
                Set<String> linesForStop = stopIdToLines.getOrDefault(stopId, new HashSet<>());
                for (String line : linesForStop) {
                    station.addLine(line);
                }
                stationById.put(stopId, station);
            }
            System.out.println("Loaded stations: " + stationById.size());
        } catch (Exception e) {
            System.err.println("Error loading stations/routes: " + e.getMessage());
        }
    }

    // Loads adjacency info for all lines, populates neighbors in stationById
    public void loadAdjacency() {
        try {
            for (String lineId : discoveredLineIds) {
                Set<String> orderedStops = new LinkedHashSet<>();
                for (int direction = 0; direction <= 1; direction++) {
                    String url = BASE_URL + "schedules?filter[route]=" + lineId + "&filter[direction_id]=" + direction + "&sort=stop_sequence";
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(url))
                            .timeout(Duration.ofSeconds(10))
                            .build();
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    JsonNode root = mapper.readTree(response.body());
                    for (JsonNode node : root.path("data")) {
                        JsonNode stopIdNode = node.path("relationships").path("stop").path("data").path("id");
                        if (!stopIdNode.isMissingNode()) {
                            orderedStops.add(stopIdNode.asText());
                        }
                    }
                }
                populateNeighbors(orderedStops, lineId, stationById);
            }
            System.out.println("Adjacency loaded.");
        } catch (Exception e) {
            System.err.println("Error loading adjacency: " + e.getMessage());
        }
    }

    /**
     * Populates neighboring (adjacent) stations for each stop on a given line.
     * Neighbors are determined based on their order in the route's stop sequence.
     */
    public void populateNeighbors(Set<String> stopIds, String line, Map<String, StationInfo> stationById) {
        List<String> orderedStops = new ArrayList<>(stopIds);
        for (int i = 0; i < orderedStops.size(); i++) {
            String currentId = orderedStops.get(i);
            StationInfo currentStation = stationById.get(currentId);
            if (currentStation == null) continue;
            if (i > 0) {
                String prevId = orderedStops.get(i - 1);
                currentStation.addNeighbor(prevId, line);
            }
            if (i < orderedStops.size() - 1) {
                String nextId = orderedStops.get(i + 1);
                currentStation.addNeighbor(nextId, line);
            }
        }
    }

    // Only returns cached data, does not fetch from MBTA
    public Map<String, StationInfo> getStationInfo_v2() {
        return stationById;
    }

    // Only returns cached data, does not fetch from MBTA
    public Set<String> linesByStation_v2(String stopId) {
        return stopIdToLines.getOrDefault(stopId, Collections.emptySet());
    }

     // Returns a single StationInfo by stopId, or null if not found
    public StationInfo getStationInfoById(String stopId) {
        return stationById.get(stopId);
    }

    // Fetches all MBTA light and heavy rail routes and maps each stop/station ID to the set of line IDs
    private Map<String, Set<String>> fetchAllRoutes() throws IOException, InterruptedException {
        String url = BASE_URL + "routes?filter[type]=0,1";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode root = mapper.readTree(response.body()).get("data");
        Set<String> lineSet = new HashSet<>();
        Map<String, Set<String>> stopIdToLines = new HashMap<>();
        for (JsonNode node : root) {
            lineSet.add(node.get("id").asText());
        }
        this.discoveredLineIds = lineSet;
        for (String lineId : lineSet) {
            String stopsUrl = BASE_URL + "stops?filter[route]=" + lineId;
            HttpRequest requestStops = HttpRequest.newBuilder()
                    .uri(URI.create(stopsUrl))
                    .timeout(Duration.ofSeconds(10))
                    .build();
            HttpResponse<String> responseStops = client.send(requestStops, HttpResponse.BodyHandlers.ofString());
            JsonNode root2 = mapper.readTree(responseStops.body()).get("data");
            for (JsonNode node2 : root2) {
                String stationId = node2.get("id").asText();
                stopIdToLines.computeIfAbsent(stationId, k -> new HashSet<>()).add(lineId);
            }
        }
        return stopIdToLines;
    }
}

