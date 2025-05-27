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

    public Set<String> getLines() {
        return lines;
    }

    public void setLines(Set<String> lines) {
        this.lines = lines;
    }

    private String stationId;
    private Set<String> lines = new HashSet<>();

    public NeighborInfo(String stationId) {
        this.stationId = stationId;
    }

    public void addLine(String line) {
        lines.add(line);
    }
    // getters and setters
}