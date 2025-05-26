package com.ss.stationapi.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ss.stationapi.model.StationInfo;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
public class StationServiceV2 {
    private static final String BASE_URL = "https://api-v3.mbta.com/";
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final List<String> lines = Arrays.asList("Red", "Green", "Orange");
    private static final Map<String, Set<String>> adjacencyMap = new HashMap<>();



//    public static Map<String, Set<String>> getAdjacentStops() {
//        for (String line : lines) {
//            try {
//                String tripId = fetchFirstTripId(line);
//                if (tripId != null) {
//                    List<String> stopIds = fetchOrderedStopIds(tripId);
//                    addToAdjacencyMap(stopIds);
//                }
//            } catch (Exception e) {
//                System.err.println("Error processing line " + line + ": " + e.getMessage());
//            }
//        }
//        return adjacencyMap;
//    }

    private static String fetchFirstTripId(String route) throws Exception {
//        String url = BASE_URL + "/trips?filter[route]=" + route;
//        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
//        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//        JsonNode data = mapper.readTree(response.body()).get("data");
//        System.out.println("Fetching the Route for the Lines: " + route);
        String validTrip=null;
        // Fetch the valid Trip ID from the response:
        String url2 = BASE_URL + "/schedules?filter[route]=Red&include=stop&sort=stop_sequence";
        HttpRequest request2 = HttpRequest.newBuilder().uri(URI.create(url2)).build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        JsonNode data2 = mapper.readTree(response2.body()).get("data");

        HashSet<String> listStation = new HashSet<>();
        for (int i=0; i< data2.size();i++){
            listStation.add(data2.get(i).get("attributes").get("stop").get("data").get("id").asText());
        }
        System.out.println("Size of the listStations: " + listStation.size());
//        for (int i=0; i< data.size();i++){
////            String url2 = BASE_URL + "/schedules?filter[trip]=" + data.get(i).get("id").asText() + "&sort=stop_sequence&include=stop";
//            HttpRequest request2 = HttpRequest.newBuilder().uri(URI.create(url2)).build();
//            HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
//            JsonNode data2 = mapper.readTree(response2.body()).get("data");
//
//            if (data2.get(0).get("id").asText() != null)
//               validTrip =  data2.get(0).get("id").asText();
//        }
//        return (data != null && data.size() > 0) ? data.get(0).get("id").asText() : null;
        return validTrip;
    }
    private static List<String> fetchOrderedStopIds(String tripId) throws Exception {
        String url = BASE_URL + "/schedules?filter[trip]=" + tripId + "&sort=stop_sequence&include=stop";
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode data = mapper.readTree(response.body()).get("data");
        System.out.println("Fetching the Ordered Stop Ids for the Lines: " + tripId);

        List<String> stopIds = new ArrayList<>();
        for (JsonNode node : data) {
            JsonNode stopIdNode = node.path("relationships").path("stop").path("data").path("id");
            if (!stopIdNode.isMissingNode()) {
                stopIds.add(stopIdNode.asText());
            }
        }
        System.out.println("Ordered Stops Ids: " + stopIds.size());
        return stopIds;
    }
    private static void fetchListOfStopIds() throws Exception {
        try {
            String url2 = BASE_URL + "schedules?filter[route]=Red&include=stop&sort=stop_sequence";
            HttpRequest request2 = HttpRequest.newBuilder().uri(URI.create(url2)).build();
            HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
            JsonNode data2 = mapper.readTree(response2.body()).get("data");

            HashSet<String> listStation = new HashSet<>();
            for (int i = 0; i < data2.size(); i++) {
                listStation.add(data2.get(i).get("relationships").get("stop").get("data").get("id").asText());
            }
            System.out.println("Size of the listStations: " + listStation.size());
            System.out.println("List of stations: " + listStation);

            Map<String, String> stopIdToName = new HashMap<>();
            for (JsonNode stopNode : data2) {
                if (stopNode.get("type").asText().equals("stop")) {
                    String id = stopNode.get("id").asText();
                    String name = stopNode.get("attributes").get("name").asText();
                    stopIdToName.put(id, name);
                }
            }
            System.out.println("Stop Id and Name:");
            System.out.println("70079" + " Value: " + stopIdToName.get(70079));
        }
        catch(Exception e) {
            System.out.println("Error: " + e);
        }
    }

    public static void main(String[] args){
        System.out.println("Test ***");
//        getStationInfo(); // Fetch Station Info
//        linesByStation(70159); // Fetch Line info for the station
//        System.out.println("Stations on a Line: Red");
        try {
            fetchListOfStopIds();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
//        getAdjacentStops();
    }
}
