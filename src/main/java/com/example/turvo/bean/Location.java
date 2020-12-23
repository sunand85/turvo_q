package com.example.turvo.bean;

import com.univocity.parsers.annotations.Parsed;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Sunand on 22/12/2020.
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Location {
  @Parsed
  private String name;
  @Parsed
  private double lat;
  @Parsed
  private double lon;
}
