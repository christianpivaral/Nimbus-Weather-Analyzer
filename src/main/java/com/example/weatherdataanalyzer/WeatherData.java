
package com.example.weatherdataanalyzer;

import java.time.LocalDate;

/// Represents one daily weather observation.
///
/// Java records automatically provide a constructor,
/// accessor methods, equals, hashCode, and toString.
///
/// ```java
/// WeatherData sample = new WeatherData(
///         LocalDate.of(2026, 1, 1),
///         32.5,
///         65,
///         0.0
/// );
///
/// System.out.println(sample.temperature());
/// // Prints 32.5
/// ```
///
/// @param date date of the observation
/// @param temperature temperature in Fahrenheit
/// @param humidity relative humidity percentage
/// @param precipitation precipitation in inches
public record WeatherData(
        LocalDate date,
        double temperature,
        int humidity,
        double precipitation
) {
}
