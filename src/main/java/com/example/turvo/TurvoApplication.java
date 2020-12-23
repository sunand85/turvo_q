package com.example.turvo;

import com.example.turvo.bean.Location;
import com.example.turvo.service.LocationService;
import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.event.EventListener;

import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.List;
import java.util.ResourceBundle;

@EnableCaching
@SpringBootApplication
public class TurvoApplication {

  public static void main(String[] args) {
    SpringApplication.run(TurvoApplication.class, args);
  }

  @Autowired
  private LocationService locationService;

  @EventListener(ApplicationReadyEvent.class)
  public void init() throws URISyntaxException {
/*
    Location coimbatore = new Location("CBE", 11, 76.9667);
    Location bangalore = new Location("BLR", 12.9699, 77.598);
    Location hyderabad = new Location("HYD", 17.3667, 78.4667);

    locationService.add(coimbatore);
    locationService.add(hyderabad);
    locationService.add(bangalore);*/

    final InputStream southCitiesStream = TurvoApplication.class.getResourceAsStream("world.csv");

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
    locationService.addAll(locations);
  }
}
