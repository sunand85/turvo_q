package com.example.turvo.controller;

import com.example.turvo.bean.Location;
import com.example.turvo.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by Sunand on 22/12/2020.
 **/
@RestController
@RequestMapping("/locations")
public class SpatialController {

  private final LocationService locationService;

  public SpatialController(LocationService locationService) {
    this.locationService = locationService;
  }

  @GetMapping("/")
  public ResponseEntity<List<Location>> getLocations() {
    return ResponseEntity.ok(locationService.find());
  }

  @PostMapping("/sort")
  public ResponseEntity<Object> sortedLocations(@RequestBody Location referenceLocation) {
    final List<Location> orderedLocation = locationService.getOrderedLocation(referenceLocation);
    return ResponseEntity.accepted().contentType(MediaType.APPLICATION_JSON).body(orderedLocation);
  }

  @PostMapping("/add")
  public ResponseEntity<Object> addLocations(@RequestBody Location location) {
    locationService.add(location);
    return ResponseEntity.ok("Added Successfully");
  }
}
