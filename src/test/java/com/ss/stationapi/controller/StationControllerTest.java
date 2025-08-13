package com.ss.stationapi.controller;

import com.ss.stationapi.model.StationInfo;
import com.ss.stationapi.service.StationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StationController.class)
class StationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StationService stationService;

    private StationInfo buildStation(String id, String name, double lat, double lon, String... lines) {
        StationInfo s = new StationInfo();
        s.setId(id);
        s.setName(name);
        s.setLatitude(lat);
        s.setLongitude(lon);
        for (String line : lines) {
            s.addLine(line);
        }
        return s;
    }

    @Test
    void getAllStations_returnsMap() throws Exception {
        Map<String, StationInfo> map = new LinkedHashMap<>();
        map.put("stop-1", buildStation("stop-1", "Station One", 1.0, 2.0, "Red"));
        map.put("stop-2", buildStation("stop-2", "Station Two", 3.0, 4.0, "Blue"));
        given(stationService.getStationInfo_v2()).willReturn(map);

        mockMvc.perform(get("/api/v1/stations"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.['stop-1'].id").value("stop-1"))
                .andExpect(jsonPath("$.['stop-2'].name").value("Station Two"));
    }

    @Test
    void getStationById_found() throws Exception {
        StationInfo s = buildStation("stop-1", "Station One", 1.0, 2.0, "Red", "Blue");
        given(stationService.getStationInfoById("stop-1")).willReturn(s);

        mockMvc.perform(get("/api/v1/stations/stop-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("stop-1"))
                .andExpect(jsonPath("$.lines.length()").value(2));
    }

    @Test
    void getStationById_notFound() throws Exception {
        given(stationService.getStationInfoById("missing")).willReturn(null);

        mockMvc.perform(get("/api/v1/stations/missing"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getLinesByStation_found() throws Exception {
        Set<String> lines = new LinkedHashSet<>(Arrays.asList("Red", "Blue"));
        given(stationService.linesByStation_v2("stop-1")).willReturn(lines);

        mockMvc.perform(get("/api/v1/stations/stop-1/lines"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("Red"))
                .andExpect(jsonPath("$[1]").value("Blue"));
    }

    @Test
    void getLinesByStation_notFoundOrEmpty() throws Exception {
        given(stationService.linesByStation_v2("stop-2")).willReturn(Collections.emptySet());

        mockMvc.perform(get("/api/v1/stations/stop-2/lines"))
                .andExpect(status().isNotFound());
    }
}