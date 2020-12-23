package com.example.turvo.service;

import com.example.turvo.bean.Location;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.checkerframework.checker.units.qual.min;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.StreamSupport;

/**
 * Created by Sunand on 22/12/2020.
 **/
@Service
public class LocationService {
  private final List<Location> locations = new ArrayList<>();
  private final List<Location> orderedLocation = new ArrayList<>();
  private final Map<Location, Location> minDistanceLocationMap = Maps.newHashMap();

  private final DistanceService distanceService;

  public LocationService(DistanceService distanceService) {
    this.distanceService = distanceService;
  }

  public void add(Location location) {
    locations.add(location);
  }

  public void addAll(List<Location> locationList) {
    locations.addAll(locationList);
  }

  public void remove(Location location) {
    locations.remove(location);
  }

  public List<Location> find() {
    return locations;
  }

  public List<Location> getOrderedLocation(Location referenceLocation) {
    orderedLocation.clear();
    //This should be coming from DB //Taking additional Space Complexity
    final List<Location> copyLocations = Lists.newArrayList(this.locations);//As good as calling stream and doing collection.toList()
//    return sortByLocationProximity(copyLocations, referenceLocation);
    return sortByLocationProximityOptimizedWithMapCache(copyLocations, referenceLocation);
//    return sortByLocationProximityOptimized(copyLocations, referenceLocation);
//    return sortByLocationProximityOptimizedForLoop(copyLocations, referenceLocation);
  }

  private List<Location> sortByLocationProximity(List<Location> locations, Location rloc) {
    orderedLocation.add(rloc);
    locations.remove(rloc);

    if (locations.isEmpty()) {
      return orderedLocation;
    } else {
      double min = 0;
      Location closest = null;
      for (Location location : locations) {
        final Double distance = distanceService.calcDistance(rloc, location); //seems to perform well
//        final Double distance = calcDistanceService.calcDistanceWithCacheSupport(rloc, location); //seems to perform the least
//        final Double distance = calcDistanceService.calcDistanceWithMapCache(rloc, location); //seems to perform average
        if (min == 0) {
          min = distance;
          closest = location;
        } else {
          if (distance < min) {
            min = distance;
            closest = location;
          }
        }
      }
      return sortByLocationProximity(locations, closest);
    }
  }

  private List<Location> sortByLocationProximityOptimized(List<Location> locations, Location rloc) {
    orderedLocation.add(rloc);
    locations.remove(rloc);

    if (locations.isEmpty()) {
      return orderedLocation;
    } else {
      AtomicReference<Double> min = new AtomicReference<>((double) 0);
      AtomicReference<Location> closest = new AtomicReference<>();

      // Caching has to be applied here to know the next closest location the given RLoc if it is already calculated
      // If new location is added then cache has to be reset

      StreamSupport.stream(() -> locations.spliterator(), 3, true)
              .forEach(location -> {
                final Double distance = distanceService.calcDistance(rloc, location); //seems to perform well
                if (min.get() == 0) {
                  min.set(distance);
                  closest.set(location);
                } else {
                  if (distance < min.get()) {
                    min.set(distance);
                    closest.set(location);
                  }
                }
              });

      return sortByLocationProximityOptimized(locations, closest.get());
    }
  }

  private List<Location> sortByLocationProximityOptimizedWithMapCache(List<Location> locations, Location rloc) {
    orderedLocation.add(rloc);
    locations.remove(rloc);

    if (locations.isEmpty()) {
      return orderedLocation;
    } else {
      AtomicReference<Double> min = new AtomicReference<>((double) 0);
      AtomicReference<Location> closest = new AtomicReference<>();

      // Caching has to be applied here to know the next closest location the given RLoc if it is already calculated
      // If new location is added then cache has to be reset
      final Location nearestLocation = minDistanceLocationMap.get(rloc);

      if (nearestLocation == null) {
        StreamSupport.stream(() -> locations.spliterator(), 3, true)
                .forEach(location -> {
                  final Double distance = distanceService.calcDistance(rloc, location); //seems to perform well
                  if (min.get() == 0) {
                    min.set(distance);
                    closest.set(location);
                  } else {
                    if (distance < min.get()) {
                      min.set(distance);
                      closest.set(location);
                    }
                  }
                });

        minDistanceLocationMap.put(rloc, closest.get());
        return sortByLocationProximityOptimizedWithMapCache(locations, closest.get());
      } else {
        return sortByLocationProximityOptimizedWithMapCache(locations, nearestLocation);
      }
    }
  }

  private List<Location> sortByLocationProximityOptimizedForLoop(List<Location> locations, Location rloc) {
    final int size = locations.size();
    AtomicReference<Location> reference = new AtomicReference<>();
    reference.set(rloc);
    while (orderedLocation.size() != size) {
      orderedLocation.add(reference.get());
      locations.remove(reference.get());

      if (locations.isEmpty()) {
        return orderedLocation;
      } else {

        final Location nearestLocation = minDistanceLocationMap.get(reference.get());
        if(nearestLocation == null) {
          AtomicReference<Double> min = new AtomicReference<>((double) 0);
          AtomicReference<Location> closest = new AtomicReference<>();
          final List<List<Location>> partition = Lists.partition(locations, 500);

          final Map<Double, Location> minLocationMap = Maps.newHashMap();

          partition.stream().parallel()
                  .forEach(l -> {
                    l.stream().parallel()
                            .forEach(loc -> {
                              final Double distance = distanceService.calcDistance(rloc, loc); //seems to perform well
                              if (min.get() == 0) {
                                min.set(distance);
                                closest.set(loc);
                              } else {
                                if (distance < min.get()) {
                                  min.set(distance);
                                  closest.set(loc);
                                }
                              }
                            });
                    minLocationMap.put(min.get(), closest.get());
                  });

          final Optional<Double> final_distance = minLocationMap.keySet().stream().min(Comparator.naturalOrder());

          minDistanceLocationMap.put(reference.get(), minLocationMap.get(final_distance.get()));
          reference.set(minLocationMap.get(final_distance.get()));
        } else {
//          System.out.println("Nearest Location : " + nearestLocation);
          reference.set(nearestLocation);
        }
      }
    }

    return orderedLocation;
  }
}

