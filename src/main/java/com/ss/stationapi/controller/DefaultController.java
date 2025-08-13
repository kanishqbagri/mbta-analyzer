package com.ss.stationapi.controller;


import org.springframework.web.bind.annotation.*;

@RestController
public class DefaultController {

    @GetMapping("/")
    public String home() {
        return "MBTA Analyzer API is running. Use /api/v1/stations for data.";
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
