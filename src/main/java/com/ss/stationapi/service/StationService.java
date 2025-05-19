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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/stations")
public class StationService {
    private static final String BASE_URL = "https://api-v3.mbta.com/";
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    public static Map<String, StationInfo> getStationInfo() {
        Map<String, StationInfo> stationById = null;
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "stops?filter[route_type]=0,1"))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode root = mapper.readTree(response.body()).get("data");
            int size = root.size();

            //Parsing the Station Response to build a list of Objects
            stationById = new HashMap<>();
            for (int i = 0; i < size; i++) {
//                JsonNode attributes = node.get("attributes");
                StationInfo station = new StationInfo();
                station.setName(root.get(i).get("attributes").get("name").asText());
                station.setId(root.get(i).get("id").asText());
                station.setLatitude(root.get(i).get("attributes").get("latitude").asDouble());
                station.setLatitude(root.get(i).get("attributes").get("longitude").asDouble());
                System.out.println("Station Name: " + station.getId() + station.getLongitude() + station.getLongitude());
                stationById.put(station.getId(), station);  // Lookup by ID
            }

            System.out.println("Size of the Map: " + stationById.size());

        } catch (Exception e) {
            System.err.println("Error fetching info for stop " + ": " + e.getMessage());
        }
        return stationById;
    }


    // API call to fetch the Lines:
    public static Set<String> linesByStation(int routeId){
        Set listOfLines = new HashSet();
//        routeId=70159;
        try {
            String url = BASE_URL + "routes?filter[stop]=" +routeId;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                        JsonNode root = mapper.readTree(response.body()).get("data");
            for (JsonNode item : root) {
                String lineId = item
                        .path("relationships")
                        .path("line")
                        .path("data")
                        .path("id")
                        .asText();
                listOfLines.add(lineId);
              }
            System.out.println("List of Lines for : " + routeId + " " + listOfLines);

        }
        catch (Exception e) {
            System.err.println("Error fetching stops for route " + routeId + ": " + e.getMessage());
        }
        return listOfLines;
    }

    public static void main(String[] args){
        System.out.println("Test ***");
        getStationInfo(); // Fetch Station Info
        linesByStation(70159); // Fetch Line info for the station
    }
}

//class StationInfo {
//    public String id;
//    public String name;
//    public double latitude;
//    public double longitude;
//    public String description;
//}