package com.ss.stationapi.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StationInfo {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    private String id;
    private String name;
    private double latitude;
    private double longitude;

    private Set<String> lines = new HashSet<>();

    // Getters and setters...

    public void addLine(String lineId) {
        lines.add(lineId);
    }

    public Set<String> getLines() {
        return lines;
    }

    // List to store neighbor stations of the current station.
// Each neighbor is represented by a NeighborInfo object which contains the station ID and lines connecting to it.
    private List<NeighborInfo> neighbors = new ArrayList<>();
    
        /**
     * Adds a neighbor to the current station.
     * 
     * If the neighbor already exists in the list, the line is added to the existing NeighborInfo.
     * If the neighbor does not exist, a new NeighborInfo is created and added to the list.
     * 
     * @param neighborId The station ID of the neighboring station.
     * @param line The transit line that connects the current station to the neighbor.
     */
    public void addNeighbor(String neighborId, String line) {
        for (NeighborInfo neighbor : neighbors) {
            if (neighbor.getStationId().equals(neighborId)) {
                neighbor.addLine(line);
                return;
            }
        }
        // If not found, add new neighbor
        NeighborInfo newNeighbor = new NeighborInfo(neighborId);
        newNeighbor.addLine(line);
        neighbors.add(newNeighbor);
    }
}
