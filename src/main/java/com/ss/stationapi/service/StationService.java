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
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "stops?filter[route_type]=0,1"))
                    .build();
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

                Set<String> lines = stopIdToLines.getOrDefault(stationId, new HashSet<>());
                for (String line : lines) {
                    station.addLine(line);
                }
//                System.out.println("Station Name: " + station.getId());
                stationById.put(station.getId(), station);
            }
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

    // Fetch All Routes and create a Set of Routes to be used
    private static Map<String, Set<String>> getAllRoutes(){
        String url = BASE_URL + "routes?filter[type]=0,1";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();
        HttpResponse<String> response = null;
        Set<String> lineSet = new HashSet<>();
        Map<String, Set<String>> stopIdToLines = new HashMap<>();
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode root = mapper.readTree(response.body()).get("data");
            for(JsonNode node : root){
                lineSet.add(node.get("id").asText());
            }
            System.out.println("Size of lineSet: " + lineSet.size());
            System.out.println(lineSet);

            // For each Line in LineSet, capture all the Stops on each line

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

    private static void fetchAllStopsOnAllRoutes(){

    }

    private static void addToAdjacencyMap(List<String> stopIds) {
        for (int i = 0; i < stopIds.size(); i++) {
            String current = stopIds.get(i);
            adjacencyMap.putIfAbsent(current, new HashSet<>());
            System.out.println("Building the adjacency list for : " + stopIds);
            if (i > 0) {
                String previous = stopIds.get(i - 1);
                adjacencyMap.get(current).add(previous);
                adjacencyMap.get(previous).add(current);
            }
        }
    }
//    private static Map<String, StationInfo> getAdjacentStops1(String route) {
    private static void getAdjacentStops1() {
        System.out.println("Processing adjacent stops for " + stationById.size() + " stations.");
        https://api-v3.mbta.com/schedules?include=stop&sort=stop_sequence&filter[route]=
        // Calling the Route API, with filters and order
        try {
            String lineId = "Red";
            String url = BASE_URL + "schedules?filter[route]=" + lineId + "&filter[direction_id]=0&sort=stop_sequence";
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonNode root = mapper.readTree(response.body());
            Set<String> stopIds = new LinkedHashSet<>();

            for (JsonNode node : root.path("data")) {
                JsonNode stopIdNode = node.path("relationships").path("stop").path("data").path("id");
                if (!stopIdNode.isMissingNode()) {
                    stopIds.add(stopIdNode.asText());
                }
            }
          Map<String, String> StopIdToNameMap= new HashMap<>();
          System.out.println("Size of Stops Ids Map on the Line: " + stopIds.size());
            for (String stops: stopIds){
                StationInfo s1=stationById.get(stops);
                StopIdToNameMap.put(stops, s1.getName());
                System.out.println(stops + " -- " +s1.getName());
            }
//            System.out.println(StopIdToNameMap);

        }
        catch (Exception e){
            System.err.println("Error fetching stops for route " + ": " + e.getMessage());
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

//class StationInfo {
//    public String id;
//    public String name;
//    public double latitude;
//    public double longitude;
//    public String description;
//}