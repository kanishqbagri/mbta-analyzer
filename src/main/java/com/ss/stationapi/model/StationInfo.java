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
    private List<NeighborInfo> neighbors = new ArrayList<>();
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
