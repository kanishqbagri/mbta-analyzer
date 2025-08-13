package com.ss.stationapi.controller;


import com.ss.stationapi.model.StationInfo;
import com.ss.stationapi.service.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class StationController {

@Autowired
    private StationService svc;

    /**
     * GET /api/stations
     * returns a map of all rail stops (id → StationInfo)
     */
 
    @GetMapping("/stations")
    public ResponseEntity<Map<String, StationInfo>> getAllAllStationInfo() {
        Map<String, StationInfo> all = svc.getStationInfo_v2();
        return ResponseEntity.ok(all);
    }



 /**
     * GET /api/stations/{stopId}
     * Returns the StationInfo for the given stopId, or 404 if not found.
     */
 
    @GetMapping("/stations/{stopId}")
    public ResponseEntity<StationInfo> getStationById(@PathVariable String stopId) {
        StationInfo info = svc.getStationInfoById(stopId);
        if (info == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(info);
    }

    /**
     * GET /API/stations/lines
     * returns a list of Lines for the station provided. Accepts RouteId of the Station
     * **/
    // @GetMapping("/lines")
    // public ResponseEntity<Set<String>> getLinesByStation(@RequestParam("routeID") String routeID){
    // Set<String> listOfLines= svc.linesByStation(routeID);
    // return ResponseEntity.ok(listOfLines);
    // }

//    @GetMapping
//    public ResponseEntity<Map<String, StationInfo>> getAdjacentStops(){
//        Map<String, StationInfo> all = svc.getStationInfo();
//        return null;
//    }
}
