package com.ss.stationapi.service;

import com.ss.stationapi.model.StationInfo;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class StationServiceTest {

    @Test
    void populateNeighbors_buildsBidirectionalAdjacencyWithNamesAndLines() {
        StationService service = new StationService();
        Map<String, StationInfo> stationById = new HashMap<>();

        StationInfo a = new StationInfo();
        a.setId("A");
        a.setName("Alpha");
        StationInfo b = new StationInfo();
        b.setId("B");
        b.setName("Bravo");
        StationInfo c = new StationInfo();
        c.setId("C");
        c.setName("Charlie");

        stationById.put("A", a);
        stationById.put("B", b);
        stationById.put("C", c);

        LinkedHashSet<String> ordered = new LinkedHashSet<>(Arrays.asList("A", "B", "C"));

        service.populateNeighbors(ordered, "Red", stationById);

        assertThat(a.getNeighbors()).extracting(n -> n.getStationId()).containsExactly("B");
        assertThat(b.getNeighbors()).extracting(n -> n.getStationId()).containsExactly("A", "C");
        assertThat(c.getNeighbors()).extracting(n -> n.getStationId()).containsExactly("B");

        assertThat(b.getNeighbors().get(0).getName()).isEqualTo("Alpha");
        assertThat(b.getNeighbors().get(1).getName()).isEqualTo("Charlie");

        assertThat(a.getNeighbors().get(0).getLines()).contains("Red");
        assertThat(b.getNeighbors().get(0).getLines()).contains("Red");
        assertThat(b.getNeighbors().get(1).getLines()).contains("Red");
        assertThat(c.getNeighbors().get(0).getLines()).contains("Red");
    }
}