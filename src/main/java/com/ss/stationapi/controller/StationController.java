package com.ss.stationapi.controller;


import com.ss.stationapi.model.StationInfo;
import com.ss.stationapi.service.StationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/stations")
public class StationController {
    private final StationService svc;

    public StationController(StationService svc) {
        this.svc = svc;
    }

    /**
     * GET /api/stations
     * returns a map of all rail stops (id → StationInfo)
     */
    @GetMapping
    public ResponseEntity<Map<String, StationInfo>> getAllRailStops() {
        Map<String, StationInfo> all = svc.getStationInfo();
        return ResponseEntity.ok(all);
    }

    @GetMapping("/lines")
    public ResponseEntity<Set<String>> getLinesByStation(@RequestParam("routeID") int routeID){
    Set<String> listOfLines= svc.linesByStation(routeID);
    return ResponseEntity.ok(listOfLines);
    }
}
