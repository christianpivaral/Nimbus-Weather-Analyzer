
package com.example.weatherdataanalyzer;

import java.time.Month;
import java.util.List;

/// Weather calculations for the Nimbus dashboard.
///
/// Uses Java streams, lambdas, and enhanced switch expressions
/// to analyze daily weather observations.
public interface WeatherAnalyzer {

    /// Calculates the average temperature for a selected month.
    ///
    /// ```java
    /// double average = WeatherAnalyzer.averageTemperature(
    ///         weatherData, Month.JANUARY);
    /// ```
    ///
    /// @param data all weather observations
    /// @param month the month to analyze
    /// @return average temperature in Fahrenheit, or 0 if no data exists
    static double averageTemperature(List<WeatherData> data, Month month) {
        return data.stream()
                .filter(day -> day.date().getMonth() == month)
                .mapToDouble(WeatherData::temperature)
                .average()
                .orElse(0.0);
    }

    /// Finds days with temperatures strictly above a threshold.
    ///
    /// ```java
    /// List<WeatherData> hotDays =
    ///         WeatherAnalyzer.daysAboveTemperature(weatherData, 75);
    /// ```
    ///
    /// @param data weather observations
    /// @param threshold temperature threshold in Fahrenheit
    /// @return a list of matching observations
    static List<WeatherData> daysAboveTemperature(
            List<WeatherData> data, double threshold) {

        return data.stream()
                .filter(day -> day.temperature() > threshold)
                .toList();
    }

    /// Counts days with precipitation greater than zero.
    ///
    /// ```java
    /// long rainyDays = WeatherAnalyzer.countRainyDays(weatherData);
    /// ```
    ///
    /// @param data weather observations
    /// @return number of rainy days
    static long countRainyDays(List<WeatherData> data) {
        return data.stream()
                .filter(day -> day.precipitation() > 0)
                .count();
    }

    /// Determines the weather category using an enhanced switch.
    ///
    /// Temperature ranges:
    /// - Cold: below 45°F
    /// - Cool: 45°F to below 65°F
    /// - Warm: 65°F to below 85°F
    /// - Hot: 85°F and above
    ///
    /// ```java
    /// String category = WeatherAnalyzer.weatherCategory(75);
    /// // Returns "Warm"
    /// ```
    ///
    /// @param temperature temperature in Fahrenheit
    /// @return Cold, Cool, Warm, or Hot
    static String weatherCategory(double temperature) {
        int band = temperature < 45 ? 0
                : temperature < 65 ? 1
                : temperature < 85 ? 2 : 3;

        return switch (band) {
            case 0 -> "Cold";
            case 1 -> "Cool";
            case 2 -> "Warm";
            default -> "Hot";
        };
    }

    /// Filters weather observations by month.
    ///
    /// ```java
    /// List<WeatherData> january =
    ///         WeatherAnalyzer.filterByMonth(weatherData, Month.JANUARY);
    /// ```
    ///
    /// @param data all weather observations
    /// @param month selected month
    /// @return observations from that month
    static List<WeatherData> filterByMonth(
            List<WeatherData> data, Month month) {

        return data.stream()
                .filter(day -> day.date().getMonth() == month)
                .toList();
    }
}
