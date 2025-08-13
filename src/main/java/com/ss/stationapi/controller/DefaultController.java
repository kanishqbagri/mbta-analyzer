package com.ss.stationapi.controller;


import com.ss.stationapi.model.StationInfo;
import com.ss.stationapi.service.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class DefaultController {

    @GetMapping("/")
    public String home() {
        return "MBTA Analyzer API is running. Use /api/stations for data.";
    }
}
