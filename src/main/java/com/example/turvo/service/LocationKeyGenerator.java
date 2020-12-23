package com.example.turvo.service;

import com.example.turvo.bean.Location;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Created by Sunand on 22/12/2020.
 **/
@Component
public class LocationKeyGenerator implements KeyGenerator {

  @Override
  public Object generate(Object target, Method method, Object... params) {

    Location location1 = Location.class.cast(params[0]);
    Location location2 = Location.class.cast(params[1]);
    /**
     * Just to get the key lookup to remain same when the distance is looked up from
     * either CBE to HYD or HYD to CBE.
     */
    final char[] sorted = location1.getName().concat(location2.getName()).toCharArray();
    Arrays.sort(sorted);
    final String key = new String(sorted);
    return key.intern();
    //Simple form of key but this will generate duplicate
//    return location1.getName() + "_" + location2.getName();
  }
}
