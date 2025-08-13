package com.ss.stationapi;

import com.ss.stationapi.model.StationInfo;
import com.ss.stationapi.service.StationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MbtaApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private StationService stationService;

    private StationInfo station(String id, String name, String... lines) {
        StationInfo s = new StationInfo();
        s.setId(id);
        s.setName(name);
        for (String line : lines) {
            s.addLine(line);
        }
        return s;
    }

    @BeforeEach
    void setupMocks() {
        Map<String, StationInfo> all = new LinkedHashMap<>();
        all.put("stop-1", station("stop-1", "Station One", "Red", "Blue"));
        all.put("stop-2", station("stop-2", "Station Two", "Green"));

        given(stationService.getStationInfo_v2()).willReturn(all);
        given(stationService.getStationInfoById("stop-1")).willReturn(all.get("stop-1"));
        given(stationService.getStationInfoById("stop-2")).willReturn(all.get("stop-2"));
        given(stationService.getStationInfoById("nonexistent-stop")).willReturn(null);

        given(stationService.linesByStation_v2("stop-1")).willReturn(new LinkedHashSet<>(Arrays.asList("Red", "Blue")));
        given(stationService.linesByStation_v2("stop-2")).willReturn(Collections.emptySet());
    }

    @Test
    void contextLoads() {
    }

    @Test
    void getAllStations_returnsExpectedData() {
        ResponseEntity<Map> response = restTemplate.getForEntity("/api/v1/stations", Map.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).containsKeys("stop-1", "stop-2");
    }

    @Test
    void getStationById_found() {
        ResponseEntity<StationInfo> response = restTemplate.getForEntity("/api/v1/stations/stop-1", StationInfo.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo("stop-1");
    }

    @Test
    void getStationById_notFound() {
        ResponseEntity<StationInfo> response = restTemplate.getForEntity("/api/v1/stations/nonexistent-stop", StationInfo.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getLinesByStation_found() {
        ResponseEntity<List> response = restTemplate.getForEntity("/api/v1/stations/stop-1/lines", List.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactly("Red", "Blue");
    }

    @Test
    void getLinesByStation_notFound() {
        ResponseEntity<List> response = restTemplate.getForEntity("/api/v1/stations/stop-2/lines", List.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void rootAndHealthEndpoints() {
        ResponseEntity<String> root = restTemplate.getForEntity("/", String.class);
        assertThat(root.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(root.getBody()).contains("MBTA Analyzer API is running");

        ResponseEntity<String> health = restTemplate.getForEntity("/health", String.class);
        assertThat(health.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(health.getBody()).isEqualTo("OK");
    }
}