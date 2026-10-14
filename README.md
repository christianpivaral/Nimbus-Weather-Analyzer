
# nimbus.
### Weather Data Analyzer

**CSC 311 – Advanced Programming**

Nimbus is a JavaFX desktop application that reads weather data from a CSV file and displays monthly weather statistics through an interactive dashboard.

The project combines modern Java programming concepts with a graphical interface. Users can select a month, enter a temperature threshold, and view weather statistics, temperature trends, and daily weather records.

## Features

- **Monthly Weather Analysis:** Select any month to analyze its weather observations.
- **Average Temperature:** Calculate the average temperature for the selected month in Fahrenheit.
- **Temperature Threshold:** Count the number of days above a user-defined temperature.
- **Rainy Days:** Count days with precipitation greater than zero.
- **Weather Categories:** Classify temperatures as Cold, Cool, Warm, or Hot.
- **Temperature Trend Chart:** Display daily temperatures using a JavaFX line chart.
- **Daily Weather Records:** View dates, temperatures, humidity, precipitation, and weather categories in a table.
- **Dynamic Weather Icons:** Display weather icons and thermometers based on temperature.
- **Animated Monthly Summary:** Open and close a sliding panel on the right side of the dashboard.
- **Category-Based Colors:** Display each weather category using its own color.
- **Scroll Position Preservation:** Keep the current scroll position when opening or closing the summary panel.
- **Input Validation:** Display an error message when the temperature threshold is invalid.

## Technologies Used

- Java 27
- JavaFX 21.0.6
- Maven
- CSS
- CSV data processing
- IntelliJ IDEA

## Modern Java Concepts

This project demonstrates several modern Java features covered in CSC 311.

| Concept | Implementation |
|---|---|
| Records | `WeatherData` stores daily weather observations. |
| Interfaces | `Main` and `WeatherAnalyzer` organize the application without explicitly declaring classes. |
| Streams | Used for reading CSV data, filtering observations, and performing calculations. |
| Lambda Expressions | Used in stream operations and JavaFX event handlers. |
| Enhanced Switch Expressions | Used to determine weather categories and icon types. |
| Pattern Matching for Switch | Used to process different weather values. |
| Text Blocks | Used to generate the monthly weather report. |
| Markdown Javadoc | Used to document methods with descriptions and examples. |

## Weather Analysis

The application reads daily weather observations from `weatherdata.csv`.

Each observation contains:

- Date
- Temperature (°F)
- Humidity (%)
- Precipitation (inches)

The program filters observations by the selected month and calculates:

1. Average monthly temperature
2. Number of days above the selected temperature threshold
3. Number of rainy days
4. Weather category based on the average temperature

### Weather Categories

| Category | Temperature |
|---|---|
| Cold | Below 45°F |
| Cool | 45°F to below 65°F |
| Warm | 65°F to below 85°F |
| Hot | 85°F and above |

Weather categories are displayed using matching icons and colors.

## Dashboard

Nimbus uses a dark blue-gray interface with rounded statistic cards, weather icons, a temperature chart, and a daily records table.

### Dashboard Statistics

The four main statistic cards display:

- Average Temperature
- Days Above Threshold
- Rainy Days
- Weather Category

### Temperature Trend

A JavaFX line chart displays daily temperatures for the selected month.

The horizontal axis represents the day of the month, and the vertical axis represents temperature in Fahrenheit.

### Daily Weather Records

A JavaFX table displays individual weather observations with the following columns:

- Date
- Temperature (°F)
- Humidity (%)
- Precipitation (in)
- Category

### Animated Monthly Summary

The **View Summary** button opens a sliding panel on the right side of the dashboard.

The panel displays:

- Average temperature
- Weather category
- Days above the selected threshold
- Rainy days

The panel uses a JavaFX `Timeline` animation to open and close smoothly.

The **Hide Summary** button closes the panel, and the application preserves the dashboard's scroll position.

The weather category text also changes color automatically:

- Cold: Icy blue
- Cool: Blue
- Warm: Yellow
- Hot: Coral

## CSV Data Format

The application loads weather observations from `weatherdata.csv` in the project root directory.

Example format:

```csv
Date,Temperature,Humidity,Precipitation
2026-01-01,32.5,65,0.0
2026-01-02,38.2,71,0.2
2026-01-03,29.8,60,0.0
```

Java streams are used to read and process the CSV file.

Each observation is stored as a `WeatherData` record.

## How to Run

### Requirements

- JDK 27
- Maven or Maven Wrapper
- JavaFX-compatible desktop environment

### Instructions

1. Open the project in IntelliJ IDEA.
2. Make sure the project SDK is JDK 27.
3. Make sure `weatherdata.csv` is in the project root.
4. Open the terminal in the project directory.
5. Run:

```powershell
.\mvnw.cmd clean javafx:run
```

6. Select a month.
7. Enter a temperature threshold.
8. Click **Analyze Weather**.
9. Review the dashboard statistics, chart, and daily records.
10. Click **View Summary** to open the monthly report.
11. Click **Hide Summary** to close the report.

## Project Structure

```text
WeatherDataAnalyzer/
├── pom.xml
├── mvnw
├── mvnw.cmd
├── weatherdata.csv
├── README.md
└── src/
    └── main/
        ├── java/
        │   ├── module-info.java
        │   └── com/example/weatherdataanalyzer/
        │       ├── Main.java
        │       ├── WeatherAnalyzer.java
        │       └── WeatherData.java
        └── resources/
            ├── weather.css
            └── icons/
                ├── cold.png
                ├── cool.png
                ├── warm.png
                ├── hot.png
                ├── rain.png
                ├── thermometer_cold.png
                ├── thermometer_cool.png
                ├── thermometer_warm.png
                └── thermometer_hot.png
```

## Error Handling

Nimbus includes error handling for:

- Missing or unreadable CSV files
- Invalid temperature threshold input
- Non-finite numeric thresholds

When an error occurs, the application displays a JavaFX alert explaining the problem.

## Project Purpose

This project was developed for **CSC 311 – Advanced Programming**.

Its purpose is to demonstrate modern Java programming concepts through a practical desktop application.

Nimbus combines records, interfaces, streams, lambda expressions, switch expressions, pattern matching, text blocks, Markdown Javadoc, and JavaFX into an interactive weather data analyzer.
