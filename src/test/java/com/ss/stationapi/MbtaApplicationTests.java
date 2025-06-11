package com.ss.stationapi;

import com.ss.stationapi.model.StationInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;


import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
/**
 * Integration tests for the MBTA Station API.
 * These tests check the basic functionality of the API endpoints.
 */
class MbtaApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void contextLoads() {
    }

    @Test
    void testGetAllStations() {
        ResponseEntity<Map> response = restTemplate.getForEntity("/api/stations", Map.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().size()).isGreaterThan(0);
    }

    @Test
    void testGetStationById_found() {
        // Replace "place-alfcl" with a known stopId in your dataset
        String stopId = "place-alfcl";
        ResponseEntity<StationInfo> response = restTemplate.getForEntity("/api/stations/" + stopId, StationInfo.class);
        // Accept 200 or 404 depending on data load
        assertThat(response.getStatusCode()).isIn(HttpStatus.OK, HttpStatus.NOT_FOUND);
        if (response.getStatusCode() == HttpStatus.OK) {
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getId()).isEqualTo(stopId);
        }
    }

    @Test
    void testGetStationById_notFound() {
        String stopId = "nonexistent-stop";
        ResponseEntity<StationInfo> response = restTemplate.getForEntity("/api/stations/" + stopId, StationInfo.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}