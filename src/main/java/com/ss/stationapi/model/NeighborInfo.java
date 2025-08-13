package com.ss.stationapi.model;

import java.util.HashSet;
import java.util.Set;

public class NeighborInfo {
    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getLines() {
        return lines;
    }

    public void setLines(Set<String> lines) {
        this.lines = lines;
    }

    private String stationId;
    private String name;
    private Set<String> lines = new HashSet<>();

    public NeighborInfo(String stationId) {
        this.stationId = stationId;
    }

    public NeighborInfo(String stationId, String name) {
        this.stationId = stationId;
        this.name = name;
    }

    public void addLine(String line) {
        lines.add(line);
    }
    // getters and setters
}