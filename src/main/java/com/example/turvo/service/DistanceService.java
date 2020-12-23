package com.example.turvo.service;

import com.example.turvo.bean.Location;
import com.google.common.collect.Maps;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;

/**
 * Created by Sunand on 22/12/2020.
 **/
@Service
public class DistanceService {

  private static final double R = 6372.8; // In kilometers
  private static final Map<String, Double> DISTANCE_CACHE = Maps.newHashMap();

  /**
   * https://rosettacode.org/wiki/Haversine_formula
   *
   * @param loc1
   * @param loc2
   * @return
   */
  public Double calcDistance(Location loc1, Location loc2) {
    double dLat = Math.toRadians(loc2.getLat() - loc1.getLat());
    double dLon = Math.toRadians(loc2.getLon() - loc1.getLon());
    double lat1 = Math.toRadians(loc1.getLat());
    double lat2 = Math.toRadians(loc2.getLat());

    double a = Math.pow(Math.sin(dLat / 2), 2) + Math.pow(Math.sin(dLon / 2), 2) * Math.cos(lat1) * Math.cos(lat2);
    double c = 2 * Math.asin(Math.sqrt(a));
    return R * c;
  }

  @Cacheable(value = "distances", keyGenerator = "locationKeyGenerator")
  public Double calcDistanceWithCacheSupport(Location loc1, Location loc2) {
    return calcDistance(loc1, loc2);
  }

  public Double calcDistanceWithMapCache(Location loc1, Location loc2) {
    final String key = keyGen(loc1, loc2);
    final Double value = DISTANCE_CACHE.get(key);
    if(value == null) {
      final Double distance = calcDistance(loc1, loc2);
      DISTANCE_CACHE.put(key, distance);
      return distance;
    } else {
      return value;
    }
  }

  private String keyGen(Location loc1, Location loc2) {
    final char[] sorted = loc1.getName().concat(loc2.getName()).toCharArray();
    Arrays.sort(sorted);
    final String key = new String(sorted);
    return key.intern();
  }
}
