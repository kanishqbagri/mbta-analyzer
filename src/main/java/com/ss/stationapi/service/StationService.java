package com.ss.stationapi.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ss.stationapi.model.StationInfo;

import javax.sound.sampled.Line;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;


@Component
public class StationService {
    public static final String BASE_URL = "https://api-v3.mbta.com/";
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final List<String> lines = Arrays.asList("Red", "Blue", "Orange", "Green-B", "Green-C");
    private static final Map<String, Set<String>> adjacencyMap = new HashMap<>();
    private static Map<String, StationInfo> stationById = null;

    @PostConstruct
    public void init() {
        System.out.println("Initializing StationService...");

        getStationInfo();     // Step 1: Load all station info
//        getAdjacentStops1();    // Step 2: Now that data is loaded, process adjacent stops or other logic
    }
    public StationService() {

    }


    //API to fetch the Name, Latitude and Longitude of the Stations maps to the default API
    public static Map<String, StationInfo> getStationInfo() {

        try {
             // Build the HTTP request to fetch stops of type 0 (light rail) and 1 (heavy rail)
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "stops?filter[route_type]=0,1"))
                    .build();

        // Send the HTTP request and parse the JSON response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode root = mapper.readTree(response.body()).get("data");
            int size = root.size();
            if (root == null || !root.isArray()) {
                System.err.println("No 'data' array found in response.");
                return null;
            }
            //Parsing the Station Response to build a list of Objects
            stationById = new HashMap<>();
            Map<String, Set<String>> stopIdToLines = getAllRoutes();
            // Iterate through each node (i.e., stop) in the response, populate the values of the Station Info Object
            for (JsonNode node : root) {
                StationInfo station = new StationInfo();
                String stopId = node.get("id").asText();
                JsonNode parentNode = node.get("relationships").get("parent_station").get("data");
                String stationId = node.path("relationships").path("parent_station").path("data").path("id").isMissingNode()
                        ? stopId
                        : node.path("relationships").path("parent_station").path("data").path("id").asText();

                station.setName(node.get("attributes").get("name").asText());
                station.setId(node.get("id").asText());
                station.setLatitude(node.get("attributes").get("latitude").asDouble());
                station.setLongitude(node.get("attributes").get("longitude").asDouble());
            
                // Assign lines that pass through this station (if any)
                Set<String> lines = stopIdToLines.getOrDefault(stationId, new HashSet<>());
                for (String line : lines) {
                    station.addLine(line);
                }
//                System.out.println("Station Name: " + station.getId());
                stationById.put(station.getId(), station);
            }
             // Store station in the map using its stop ID
            System.out.println("Size of the Map: " + stationById.size());

        } catch (Exception e) {
            System.err.println("Error fetching info for stop " + ": " + e.getMessage());
        }
        return stationById;
    }


    // API call to fetch the Lines by a StopId:
    public static Set<String> linesByStation(String stopId){
        Set<String>  listOfLines = new HashSet();
//        routeId=70159;
        try {
            String url = BASE_URL + "routes?filter[stop]=" +stopId;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode root = mapper.readTree(response.body()).get("data");
            if (root != null && root.isArray())
                for (JsonNode item : root) {
                String lineId = item
                        .path("relationships")
                        .path("line")
                        .path("data")
                        .path("id")
                        .asText();
                    if (lineId != null && !lineId.isEmpty()) {
                        listOfLines.add(lineId);
                    }
              }
            System.out.println("List of Lines for : " + stopId + " " + listOfLines);

        }
        catch (Exception e) {
            System.err.println("Error fetching stops for route " + stopId + ": " + e.getMessage());
        }
        return listOfLines;
    }

    /**
 * Fetches all the MBTA light and heavy rail routes (route_type 0 and 1),
 * and maps each stop/station ID to the set of line IDs (routes) that pass through it.
 *
 * @return A map where each key is a station/stop ID and the value is a set of line IDs that serve it.
 */
    private static Map<String, Set<String>> getAllRoutes(){
        // Construct the URL to fetch only light rail and heavy rail routes
        String url = BASE_URL + "routes?filter[type]=0,1";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();
        HttpResponse<String> response = null;
        Set<String> lineSet = new HashSet<>();
        // Map from stop/station ID to all lines that pass through it
        Map<String, Set<String>> stopIdToLines = new HashMap<>();
        try {
            
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode root = mapper.readTree(response.body()).get("data");
            for(JsonNode node : root){
                // Populate the lineSet with all route IDs
                lineSet.add(node.get("id").asText());
            }
            System.out.println("Size of lineSet: " + lineSet.size());
            System.out.println(lineSet);

            // For each Line in LineSet, fetch all the Stops on each line

            for(String lineId: lineSet){
                String stopsUrl = BASE_URL + "stops?filter[route]=" + lineId;
                HttpRequest request_stops = HttpRequest.newBuilder()
                        .uri(URI.create(stopsUrl))
                        .build();
                HttpResponse<String> response_stops = null;


                response_stops = client.send(request_stops, HttpResponse.BodyHandlers.ofString());
                JsonNode root2 = mapper.readTree(response_stops.body()).get("data");
                for (JsonNode node : root2) {
                    String stationId = node.get("id").asText();
                    // Use computeIfAbsent to initialize the set if needed, then add the line
                    stopIdToLines.computeIfAbsent(stationId, k -> new HashSet<>()).add(lineId); // Add current route/line to this station
                }
            }
            System.out.println("place-boyls -> " + stopIdToLines.get("place-boyls"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return stopIdToLines;
    }


/**
 * Fetches the list of stops for each transit line in order of stop sequence,
 * then maps each stop to its adjacent stops (neighbors) in the `stationById` map.
 * Uses the MBTA schedules API to determine stop ordering.
 */
    private static void getAdjacentStops1() {
        // Logs how many stations we will process for neighbor information
        System.out.println("Processing adjacent stops for " + stationById.size() + " stations.");
        https://api-v3.mbta.com/schedules?include=stop&sort=stop_sequence&filter[route]=
        // Calling the Route API, with filters and order

        try {
             // Iterate over each transit line (e.g., Red, Green, Orange, etc.)
            for (String lineId : lines) {
                String url = BASE_URL + "schedules?filter[route]=" + lineId + "&filter[direction_id]=0&sort=stop_sequence";
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                JsonNode root = mapper.readTree(response.body());
                // Preserve insertion order to maintain the stop sequence
                Set<String> stopIds = new LinkedHashSet<>();
         // Extract stop IDs from the schedule response
                for (JsonNode node : root.path("data")) {
                    JsonNode stopIdNode = node.path("relationships").path("stop").path("data").path("id");
                    if (!stopIdNode.isMissingNode()) {
                        stopIds.add(stopIdNode.asText());
                    }
                }
                 // Optional: Map of stopId to human-readable station name for debugging
                Map<String, String> StopIdToNameMap = new HashMap<>();
                System.out.println("Size of Stops Ids Map on the Line: " + stopIds.size());
                // Build the name map from station info
                for (String stops : stopIds) {
                    StationInfo s1 = stationById.get(stops);
                    StopIdToNameMap.put(stops, s1.getName());
                    System.out.println(stops + " -- " + s1.getName());
                }

// Populate the Adjacent Map, with info on the Neighbor stations
            populateNeighbors(stopIds, lineId, stationById);
            }
        }
        catch (Exception e){
            System.err.println("Error fetching stops for route " + ": " + e.getMessage());
        }
    }

    /**
 * Populates neighboring (adjacent) stations for each stop on a given line.
 * Neighbors are determined based on their order in the route's stop sequence.
 *
 * @param stopIds      Set of stop IDs in the order of appearance on the line.
 * @param line         The line (route) identifier, e.g., "Red", "Green".
 * @param stationById  Map of station ID to StationInfo object.
 */
    public static void populateNeighbors(Set<String> stopIds, String line, Map<String, StationInfo> stationById) {
        // Convert the ordered set of stop IDs into a list to allow index-based traversal
        List<String> orderedStops = new ArrayList<>(stopIds);
        // Loop through each stop to determine and assign its adjacent neighbors
        for (int i = 0; i < orderedStops.size(); i++) {
            String currentId = orderedStops.get(i);
            StationInfo currentStation = stationById.get(currentId);
            // Skip processing if station info is not available for the current stop ID
            if (currentStation == null) continue;
            // If not the first stop, add the previous stop as a neighbor
            if (i > 0) {
                String prevId = orderedStops.get(i - 1);
                currentStation.addNeighbor(prevId, line);
            }
            // If not the last stop, add the next stop as a neighbor
            if (i < orderedStops.size() - 1) {
                String nextId = orderedStops.get(i + 1);
                currentStation.addNeighbor(nextId, line);
            }
        }
    }
    public static HttpClient getClient() {
        return client;
    }

    public static void main(String[] args){
        StationService service = new StationService();
        service.init();
        System.out.println("Test ***");
//        getAllRoutes();
//        getStationInfo(); // Fetch Station Info
//        linesByStation(70159); // Fetch Line info for the station
        System.out.println("Stations on a Line: Red");
        getAdjacentStops1();
        //        getAdjacentStops();
    }
}
