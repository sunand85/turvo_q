package com.example.turvo;

import com.example.turvo.bean.Location;
import com.example.turvo.service.DistanceService;
import com.example.turvo.service.LocationService;
import com.google.common.base.Stopwatch;
import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by Sunand on 22/12/2020.
 **/
public class KeyTest {
  public static void main(String[] args) {
    final File file = new File("/Users/spadmanabhan/sunand/study/turvo/src/main/resources/south.csv");
    BeanListProcessor<Location> rowProcessor = new BeanListProcessor<>(Location.class);
    CsvParserSettings settings = new CsvParserSettings();
    settings.setHeaderExtractionEnabled(true);
    settings.setDelimiterDetectionEnabled(true);
    settings.getFormat().setDelimiter(',');
    settings.getFormat().setLineSeparator("\n");
    settings.setProcessor(rowProcessor);
    CsvParser parser = new CsvParser(settings);
    parser.parse(file);
    final List<Location> locations = rowProcessor.getBeans();

    System.out.println(locations);
    final List<Location> collect = locations.stream()
            .sorted(Comparator.comparingDouble(value -> value.getLat()))
            .collect(Collectors.toList());

    System.out.println("==================================");
    System.out.println("==================================");
    System.out.println("==================================");

    System.out.println(collect.size());

    System.out.println("==================================");
    System.out.println("==================================");
    System.out.println("==================================");

    DistanceService distanceService = new DistanceService();
    LocationService locationService = new LocationService(distanceService);
    locationService.addAll(locations);

    System.out.println("==================================");
    System.out.println("==================================");
    System.out.println("==================================");

    Location refLocation = new Location("Coimbatore", 11, 76.9667);
    Stopwatch stopwatch = Stopwatch.createStarted();
    final List<Location> orderedLocation = locationService.getOrderedLocation(refLocation);
    final Stopwatch stop = stopwatch.stop();
    System.out.println("Elapsed Time 1 : " + stop.elapsed(TimeUnit.MILLISECONDS));
    System.out.println(orderedLocation);

    final Stopwatch started = Stopwatch.createStarted();
    locationService.getOrderedLocation(refLocation);
    System.out.println("Elapsed Time 2 : " + started.stop().elapsed(TimeUnit.MILLISECONDS));


  }
}
