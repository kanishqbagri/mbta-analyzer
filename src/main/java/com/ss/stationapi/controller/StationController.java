package com.ss.stationapi.controller;


import com.ss.stationapi.model.StationInfo;
import com.ss.stationapi.service.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/stations")
public class StationController {
@Autowired
    private StationService svc;

    /**
     * GET /api/stations
     * returns a map of all rail stops (id → StationInfo)
     */
    @GetMapping
    public ResponseEntity<Map<String, StationInfo>> getAllAllStationInfo() {
        Map<String, StationInfo> all = svc.getStationInfo();
        return ResponseEntity.ok(all);
    }

    /**
     * GET /API/stations/lines
     * returns a list of Lines for the station provided. Accepts RouteId of the Station
     * **/
    @GetMapping("/lines")
    public ResponseEntity<Set<String>> getLinesByStation(@RequestParam("routeID") String routeID){
    Set<String> listOfLines= svc.linesByStation(routeID);
    return ResponseEntity.ok(listOfLines);
    }

//    @GetMapping
//    public ResponseEntity<Map<String, StationInfo>> getAdjacentStops(){
//        Map<String, StationInfo> all = svc.getStationInfo();
//        return null;
//    }
}
