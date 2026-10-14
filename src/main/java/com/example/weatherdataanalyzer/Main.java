
package com.example.weatherdataanalyzer;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Objects;

/// Nimbus Weather Data Analyzer.
///
/// Uses Java records, interfaces, streams, lambdas,
/// enhanced switch expressions, text blocks, and
/// Java 21 pattern matching for switch.
///
/// The dashboard includes an animated monthly summary
/// panel that expands from the right.
public interface Main {

    /// Reads weather observations from a CSV file.
    ///
    /// ```java
    /// List<WeatherData> data =
    ///         Main.loadWeatherData("weatherdata.csv");
    /// ```
    ///
    /// @param filename path to the CSV file
    /// @return parsed weather observations
    /// @throws IOException if the file cannot be read
    static List<WeatherData> loadWeatherData(String filename)
            throws IOException {

        try (var lines = Files.lines(Path.of(filename))) {
            return lines.map(String::trim)
                    .filter(line -> !line.isBlank())
                    .filter(line ->
                            !line.toLowerCase().startsWith("date,"))
                    .map(line -> {
                        String[] values = line.split(",");

                        return new WeatherData(
                                LocalDate.parse(values[0].trim()),
                                Double.parseDouble(values[1].trim()),
                                Integer.parseInt(values[2].trim()),
                                Double.parseDouble(values[3].trim())
                        );
                    })
                    .toList();
        }
    }

    /// Launches the JavaFX dashboard.
    ///
    /// ```java
    /// Main.main(new String[0]);
    /// ```
    ///
    /// @param args command-line arguments
    static void main(String[] args) {
        Platform.startup(Main::showWindow);
    }

    /// Converts a temperature to an icon category.
    ///
    /// ```java
    /// String icon = Main.temperatureIconType(75);
    /// // "warm"
    /// ```
    ///
    /// @param temperature Fahrenheit temperature
    /// @return cold, cool, warm, or hot
    static String temperatureIconType(double temperature) {
        return switch (WeatherAnalyzer.weatherCategory(temperature)) {
            case "Cold" -> "cold";
            case "Cool" -> "cool";
            case "Warm" -> "warm";
            default -> "hot";
        };
    }

    /// Returns the display color for a weather category.
    ///
    /// ```java
    /// String color = Main.categoryColor("Cold");
    /// // "#A9E3F5"
    /// ```
    ///
    /// @param category weather category
    /// @return hexadecimal display color
    static String categoryColor(String category) {
        return switch (category) {
            case "Cold" -> "#A9E3F5";
            case "Cool" -> "#79B9F2";
            case "Warm" -> "#F5C778";
            case "Hot" -> "#F28D78";
            default -> "#F4F8FF";
        };
    }

    /// Loads a PNG weather icon from the resources folder.
    ///
    /// ```java
    /// ImageView icon = Main.loadIcon("rain.png");
    /// ```
    ///
    /// @param filename PNG filename
    /// @return configured image view
    static ImageView loadIcon(String filename) {
        String resource = "/icons/" + filename;

        Image image = new Image(
                Objects.requireNonNull(
                        Main.class.getResource(resource),
                        "Missing icon: " + resource
                ).toExternalForm()
        );

        ImageView view = new ImageView(image);
        view.setFitWidth(64);
        view.setFitHeight(64);
        view.setPreserveRatio(true);
        view.setSmooth(true);

        return view;
    }

    /// Returns a thermometer colored by temperature category.
    ///
    /// ```java
    /// Node icon = Main.createThermometer(33.4);
    /// ```
    ///
    /// @param temperature Fahrenheit temperature
    /// @return thermometer icon
    static Node createThermometer(double temperature) {
        return loadIcon(
                "thermometer_" +
                        temperatureIconType(temperature) + ".png"
        );
    }

    /// Returns a weather icon for a temperature.
    ///
    /// ```java
    /// Node icon = Main.createTemperatureIcon(75);
    /// ```
    ///
    /// @param temperature Fahrenheit temperature
    /// @return matching weather icon
    static Node createTemperatureIcon(double temperature) {
        return loadIcon(
                temperatureIconType(temperature) + ".png"
        );
    }

    /// Creates a dashboard statistic card.
    ///
    /// ```java
    /// VBox card = Main.createStatCard(
    ///         "Rainy Days", "9", "This month",
    ///         Main.loadIcon("rain.png"));
    /// ```
    ///
    /// @param title statistic heading
    /// @param value statistic value
    /// @param detail supporting description
    /// @param icon weather illustration
    /// @return styled statistic card
    static VBox createStatCard(
            String title, String value, String detail, Node icon) {

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("stat-label");

        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("stat-value");

        Label detailLabel = new Label(detail);
        detailLabel.getStyleClass().add("stat-detail");

        VBox labels = new VBox(
                8, titleLabel, valueLabel, detailLabel
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        StackPane iconBox = new StackPane(icon);
        iconBox.setMinSize(68, 68);
        iconBox.setPrefSize(68, 68);
        iconBox.setMaxSize(68, 68);

        HBox contents = new HBox(
                10, labels, spacer, iconBox
        );
        contents.setAlignment(Pos.CENTER_LEFT);

        VBox card = new VBox(contents);
        card.getStyleClass().add("dashboard-card");
        card.setMinHeight(135);
        card.setMaxWidth(Double.MAX_VALUE);

        HBox.setHgrow(card, Priority.ALWAYS);

        return card;
    }

    /// Creates the daily temperature chart.
    ///
    /// ```java
    /// LineChart<Number, Number> chart =
    ///         Main.createTemperatureChart(weatherData);
    /// ```
    ///
    /// @param data observations for the selected month
    /// @return temperature line chart
    static LineChart<Number, Number> createTemperatureChart(
            List<WeatherData> data) {

        NumberAxis xAxis = new NumberAxis();
        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(1);
        xAxis.setUpperBound(
                data.stream()
                        .mapToInt(day -> day.date().lengthOfMonth())
                        .max()
                        .orElse(31)
        );
        xAxis.setTickUnit(5);
        xAxis.setMinorTickVisible(false);
        xAxis.setLabel("Day of Month");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Temperature (°F)");

        LineChart<Number, Number> chart =
                new LineChart<>(xAxis, yAxis);

        chart.setLegendVisible(false);
        chart.setAnimated(false);
        chart.setCreateSymbols(true);
        chart.setPrefHeight(300);
        chart.setMinHeight(300);

        XYChart.Series<Number, Number> series =
                new XYChart.Series<>();

        data.stream()
                .sorted((a, b) ->
                        a.date().compareTo(b.date()))
                .forEach(day -> series.getData().add(
                        new XYChart.Data<>(
                                day.date().getDayOfMonth(),
                                day.temperature()
                        )
                ));

        chart.getData().add(series);

        return chart;
    }

    /// Builds the daily weather table.
    ///
    /// ```java
    /// TableView<WeatherData> table =
    ///         Main.createWeatherTable(weatherData);
    /// ```
    ///
    /// @param data monthly observations
    /// @return weather data table
    static TableView<WeatherData> createWeatherTable(
            List<WeatherData> data) {

        TableView<WeatherData> table = new TableView<>();

        TableColumn<WeatherData, LocalDate> date =
                new TableColumn<>("Date");

        date.setCellValueFactory(c ->
                new ReadOnlyObjectWrapper<>(
                        c.getValue().date()
                )
        );

        TableColumn<WeatherData, Double> temperature =
                new TableColumn<>("Temperature (°F)");

        temperature.setCellValueFactory(c ->
                new ReadOnlyObjectWrapper<>(
                        c.getValue().temperature()
                )
        );

        TableColumn<WeatherData, Integer> humidity =
                new TableColumn<>("Humidity (%)");

        humidity.setCellValueFactory(c ->
                new ReadOnlyObjectWrapper<>(
                        c.getValue().humidity()
                )
        );

        TableColumn<WeatherData, Double> precipitation =
                new TableColumn<>("Precipitation (in)");

        precipitation.setCellValueFactory(c ->
                new ReadOnlyObjectWrapper<>(
                        c.getValue().precipitation()
                )
        );

        TableColumn<WeatherData, String> category =
                new TableColumn<>("Category");

        category.setCellValueFactory(c ->
                new ReadOnlyObjectWrapper<>(
                        WeatherAnalyzer.weatherCategory(
                                c.getValue().temperature()
                        )
                )
        );

        table.getColumns().addAll(
                date, temperature, humidity,
                precipitation, category
        );

        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS
        );

        table.setItems(
                FXCollections.observableArrayList(data)
        );

        table.setPrefHeight(265);

        return table;
    }

    /// Demonstrates Java 21 pattern matching for switch.
    ///
    /// ```java
    /// String category = Main.describeWeatherValue(75.0);
    /// // "Warm"
    /// ```
    ///
    /// @param value a temperature or weather observation
    /// @return weather category or fallback text
    static String describeWeatherValue(Object value) {
        return switch (value) {
            case Double temperature ->
                    WeatherAnalyzer.weatherCategory(temperature);

            case WeatherData observation ->
                    WeatherAnalyzer.weatherCategory(
                            observation.temperature()
                    );

            case null -> "No Data";

            default -> "Unknown";
        };
    }

    /// Creates a monthly report using a Java 15 text block.
    ///
    /// ```java
    /// String report = Main.monthlySummary(
    ///         Month.JUNE, 75.8, 75, 18, 9, 30);
    /// ```
    ///
    /// @param month selected month
    /// @param average average temperature
    /// @param threshold temperature threshold
    /// @param above days above threshold
    /// @param rainy rainy-day count
    /// @param observations observation count
    /// @return formatted multiline report
    static String monthlySummary(
            Month month,
            double average,
            double threshold,
            int above,
            long rainy,
            int observations) {

        if (observations == 0) {
            return "No weather observations are available for "
                    + month + ".";
        }

        return """
                Month: %s
                Observations: %d
                Average temperature: %.1f°F
                Weather category: %s
                Days above %.1f°F: %d
                Rainy days: %d
                """.formatted(
                month,
                observations,
                average,
                describeWeatherValue(average),
                threshold,
                above,
                rainy
        );
    }

    /// Creates a labeled metric for the summary panel.
    ///
    /// ```java
    /// VBox metric = Main.createSummaryMetric(
    ///         "Rainy Days", "9");
    /// ```
    ///
    /// @param title metric heading
    /// @param value metric value
    /// @return styled metric container
    static VBox createSummaryMetric(
            String title, String value) {

        Label heading = new Label(title);
        heading.getStyleClass().add("summary-metric-label");

        Label number = new Label(value);
        number.getStyleClass().add("summary-metric-value");

        VBox metric = new VBox(7, heading, number);
        metric.getStyleClass().add("summary-metric");
        metric.setMaxWidth(Double.MAX_VALUE);

        return metric;
    }

    /// Updates the right-side summary using the text block report.
    ///
    /// The category color changes automatically for
    /// Cold, Cool, Warm, and Hot.
    ///
    /// ```java
    /// Main.updateSummaryPanel(
    ///         panel, Month.JUNE, 75.8, 75, 18, 9, 30);
    /// ```
    ///
    /// @param panel summary content container
    /// @param month selected month
    /// @param average average temperature
    /// @param threshold selected threshold
    /// @param above days above threshold
    /// @param rainy rainy-day count
    /// @param observations observation count
    static void updateSummaryPanel(
            VBox panel,
            Month month,
            double average,
            double threshold,
            int above,
            long rainy,
            int observations) {

        String report = monthlySummary(
                month, average, threshold,
                above, rainy, observations
        );

        Label heading = new Label("MONTHLY SUMMARY");
        heading.getStyleClass().add("summary-heading");

        Label monthLabel = new Label(
                month + "  •  " + observations + " DAYS"
        );
        monthLabel.getStyleClass().add("summary-month");

        if (observations == 0) {
            Label empty = new Label(report);
            empty.getStyleClass().add("summary-empty");
            empty.setWrapText(true);

            panel.getChildren().setAll(
                    heading, monthLabel, empty
            );
            return;
        }

        String[] lines =
                report.stripIndent().strip().split("\\R");

        String averageValue =
                lines[2].split(": ", 2)[1];

        String categoryValue =
                lines[3].split(": ", 2)[1];

        String aboveValue =
                lines[4].split(": ", 2)[1];

        String rainyValue =
                lines[5].split(": ", 2)[1];

        VBox averageMetric = createSummaryMetric(
                "AVERAGE TEMPERATURE", averageValue
        );

        VBox categoryMetric = createSummaryMetric(
                "WEATHER CATEGORY", categoryValue
        );

        VBox aboveMetric = createSummaryMetric(
                "DAYS ABOVE %.1f°F".formatted(threshold),
                aboveValue
        );

        VBox rainyMetric = createSummaryMetric(
                "RAINY DAYS", rainyValue
        );

        categoryMetric.getStyleClass().add(
                "summary-category"
        );

        Label categoryValueLabel =
                (Label) categoryMetric.getChildren().get(1);

        categoryValueLabel.setStyle(
                "-fx-text-fill: " +
                        categoryColor(categoryValue) + ";"
        );

        panel.getChildren().setAll(
                heading,
                monthLabel,
                new Separator(),
                averageMetric,
                categoryMetric,
                aboveMetric,
                rainyMetric
        );
    }

    /// Animates the summary drawer without changing scroll position.
    ///
    /// Saves the ScrollPane vertical position before the
    /// animation and restores it throughout the transition.
    ///
    /// ```java
    /// Main.animateSummary(drawer, button, scroll, true);
    /// ```
    ///
    /// @param drawer right-side summary panel
    /// @param button summary toggle button
    /// @param scroll dashboard scroll container
    /// @param opening true to open, false to close
    static void animateSummary(
            VBox drawer,
            Button button,
            ScrollPane scroll,
            boolean opening) {

        double savedScrollPosition = scroll.getVvalue();

        double targetWidth = opening ? 290 : 0;
        double targetOpacity = opening ? 1 : 0;

        if (opening) {
            drawer.setManaged(true);
            drawer.setVisible(true);
            button.setText("Hide Summary");
        } else {
            button.setText("View Summary");
        }

        Timeline animation = new Timeline(
                new KeyFrame(
                        Duration.millis(300),
                        new KeyValue(
                                drawer.prefWidthProperty(),
                                targetWidth,
                                Interpolator.EASE_BOTH
                        ),
                        new KeyValue(
                                drawer.minWidthProperty(),
                                targetWidth,
                                Interpolator.EASE_BOTH
                        ),
                        new KeyValue(
                                drawer.maxWidthProperty(),
                                targetWidth,
                                Interpolator.EASE_BOTH
                        ),
                        new KeyValue(
                                drawer.opacityProperty(),
                                targetOpacity,
                                Interpolator.EASE_BOTH
                        )
                )
        );

        button.setDisable(true);

        animation.currentTimeProperty().addListener(
                (observable, oldTime, newTime) ->
                        scroll.setVvalue(savedScrollPosition)
        );

        animation.setOnFinished(e -> {
            if (!opening) {
                drawer.setVisible(false);
                drawer.setManaged(false);
            }

            scroll.setVvalue(savedScrollPosition);
            button.setDisable(false);

            Platform.runLater(
                    () -> scroll.setVvalue(savedScrollPosition)
            );
        });

        animation.play();
    }

    /// Creates the Nimbus dashboard.
    ///
    /// The summary drawer expands to the right of the chart
    /// and table while preserving the scroll position.
    ///
    /// ```java
    /// Main.showWindow();
    /// ```
    static void showWindow() {
        try {
            List<WeatherData> allData =
                    loadWeatherData("weatherdata.csv");

            Label appTitle = new Label("nimbus.");
            appTitle.getStyleClass().add("app-title");

            Label subtitle =
                    new Label("WEATHER DATA ANALYZER");
            subtitle.getStyleClass().add("app-subtitle");

            VBox branding = new VBox(
                    2, appTitle, subtitle
            );

            ComboBox<Month> monthBox = new ComboBox<>();
            monthBox.getItems().addAll(Month.values());
            monthBox.setValue(Month.JANUARY);
            monthBox.setPrefWidth(155);

            TextField thresholdField =
                    new TextField("75");
            thresholdField.setPrefWidth(105);

            Button analyzeButton =
                    new Button("Analyze Weather");
            analyzeButton.getStyleClass().add(
                    "analyze-button"
            );

            Button summaryButton =
                    new Button("View Summary");
            summaryButton.getStyleClass().add(
                    "summary-toggle-button"
            );
            summaryButton.setFocusTraversable(false);

            Label monthLabel = new Label("MONTH");
            monthLabel.getStyleClass().add(
                    "control-label"
            );

            Label thresholdLabel =
                    new Label("THRESHOLD (°F)");
            thresholdLabel.getStyleClass().add(
                    "control-label"
            );

            HBox controls = new HBox(
                    12,
                    new VBox(6, monthLabel, monthBox),
                    new VBox(
                            6, thresholdLabel, thresholdField
                    ),
                    analyzeButton,
                    summaryButton
            );
            controls.setAlignment(Pos.BOTTOM_RIGHT);

            Region headerSpacer = new Region();
            HBox.setHgrow(
                    headerSpacer, Priority.ALWAYS
            );

            HBox header = new HBox(
                    20, branding, headerSpacer, controls
            );
            header.setAlignment(Pos.CENTER_LEFT);

            HBox stats = new HBox(14);

            VBox chartBox = new VBox(12);
            chartBox.getStyleClass().add(
                    "dashboard-card"
            );

            VBox tableBox = new VBox(12);
            tableBox.getStyleClass().add(
                    "dashboard-card"
            );

            VBox mainContent = new VBox(
                    22, chartBox, tableBox
            );
            mainContent.setMinWidth(0);
            mainContent.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(
                    mainContent, Priority.ALWAYS
            );

            VBox summaryContent = new VBox(16);
            summaryContent.getStyleClass().add(
                    "summary-content"
            );

            VBox summaryDrawer = new VBox(
                    summaryContent
            );
            summaryDrawer.getStyleClass().add(
                    "summary-drawer"
            );

            summaryDrawer.setPrefWidth(0);
            summaryDrawer.setMinWidth(0);
            summaryDrawer.setMaxWidth(0);
            summaryDrawer.setOpacity(0);
            summaryDrawer.setVisible(false);
            summaryDrawer.setManaged(false);

            Rectangle drawerClip = new Rectangle();
            drawerClip.widthProperty().bind(
                    summaryDrawer.widthProperty()
            );
            drawerClip.heightProperty().bind(
                    summaryDrawer.heightProperty()
            );
            summaryDrawer.setClip(drawerClip);

            HBox workspace = new HBox(
                    16, mainContent, summaryDrawer
            );
            workspace.setAlignment(Pos.TOP_LEFT);

            // Create the dashboard and ScrollPane BEFORE the
            // button handlers so 'scroll' is in scope.
            VBox dashboard = new VBox(
                    22, header, stats, workspace
            );
            dashboard.setPadding(new Insets(28));
            dashboard.getStyleClass().add(
                    "dashboard-root"
            );

            ScrollPane scroll =
                    new ScrollPane(dashboard);
            scroll.getStyleClass().add(
                    "dashboard-scroll"
            );
            scroll.setFitToWidth(true);
            scroll.setHbarPolicy(
                    ScrollPane.ScrollBarPolicy.NEVER
            );

            Runnable update = () -> {
                try {
                    Month month = monthBox.getValue();

                    double threshold =
                            Double.parseDouble(
                                    thresholdField.getText().trim()
                            );

                    if (!Double.isFinite(threshold)) {
                        throw new NumberFormatException();
                    }

                    List<WeatherData> monthly =
                            WeatherAnalyzer.filterByMonth(
                                    allData, month
                            );

                    double average =
                            WeatherAnalyzer.averageTemperature(
                                    monthly, month
                            );

                    long rainy =
                            WeatherAnalyzer.countRainyDays(
                                    monthly
                            );

                    int above =
                            WeatherAnalyzer.daysAboveTemperature(
                                    monthly, threshold
                            ).size();

                    String category =
                            monthly.isEmpty()
                                    ? "No Data"
                                    : WeatherAnalyzer.weatherCategory(
                                    average
                            );

                    stats.getChildren().setAll(
                            createStatCard(
                                    "Average Temperature",
                                    monthly.isEmpty()
                                            ? "—"
                                            : "%.1f°F".formatted(
                                            average
                                    ),
                                    month.toString(),
                                    createThermometer(average)
                            ),

                            createStatCard(
                                    "Days Above Threshold",
                                    String.valueOf(above),
                                    "Above %.0f°F".formatted(
                                            threshold
                                    ),
                                    createTemperatureIcon(
                                            threshold
                                    )
                            ),

                            createStatCard(
                                    "Rainy Days",
                                    String.valueOf(rainy),
                                    "Precipitation > 0 in",
                                    loadIcon("rain.png")
                            ),

                            createStatCard(
                                    "Weather Category",
                                    category,
                                    "Based on monthly average",
                                    createTemperatureIcon(
                                            average
                                    )
                            )
                    );

                    Label chartTitle =
                            new Label("Temperature Trend");
                    chartTitle.getStyleClass().add(
                            "section-title"
                    );

                    Label chartSub = new Label(
                            "Daily temperatures · " + month
                    );
                    chartSub.getStyleClass().add(
                            "stat-detail"
                    );

                    chartBox.getChildren().setAll(
                            chartTitle,
                            chartSub,
                            createTemperatureChart(monthly)
                    );

                    Label tableTitle =
                            new Label("Daily Weather Records");
                    tableTitle.getStyleClass().add(
                            "section-title"
                    );

                    Label tableSub = new Label(
                            monthly.size()
                                    + " observations · " + month
                    );
                    tableSub.getStyleClass().add(
                            "stat-detail"
                    );

                    tableBox.getChildren().setAll(
                            tableTitle,
                            tableSub,
                            createWeatherTable(monthly)
                    );

                    updateSummaryPanel(
                            summaryContent,
                            month,
                            average,
                            threshold,
                            above,
                            rainy,
                            monthly.size()
                    );

                } catch (NumberFormatException e) {
                    Alert alert = new Alert(
                            Alert.AlertType.ERROR,
                            "Enter a valid numeric temperature threshold."
                    );
                    alert.setHeaderText(
                            "Invalid Threshold"
                    );
                    alert.showAndWait();
                }
            };

            analyzeButton.setOnAction(e ->
                    update.run()
            );

            summaryButton.setOnAction(e -> {
                boolean opening =
                        !summaryDrawer.isVisible();

                if (opening) {
                    try {
                        double threshold =
                                Double.parseDouble(
                                        thresholdField.getText().trim()
                                );

                        if (!Double.isFinite(threshold)) {
                            throw new NumberFormatException();
                        }

                        update.run();

                    } catch (NumberFormatException exception) {
                        Alert alert = new Alert(
                                Alert.AlertType.ERROR,
                                "Enter a valid numeric temperature threshold."
                        );
                        alert.setHeaderText(
                                "Invalid Threshold"
                        );
                        alert.showAndWait();
                        return;
                    }
                }

                animateSummary(
                        summaryDrawer,
                        summaryButton,
                        scroll,
                        opening
                );
            });

            Scene scene = new Scene(
                    scroll, 1200, 850
            );

            scene.getStylesheets().add(
                    Objects.requireNonNull(
                            Main.class.getResource(
                                    "/weather.css"
                            )
                    ).toExternalForm()
            );

            Stage stage = new Stage();
            stage.setTitle(
                    "Nimbus - Weather Data Analyzer"
            );
            stage.setScene(scene);
            stage.setMinWidth(1100);
            stage.setMinHeight(650);
            stage.show();

            update.run();

        } catch (IOException | IllegalArgumentException e) {
            Alert alert = new Alert(
                    Alert.AlertType.ERROR,
                    "Unable to load weather data: "
                            + e.getMessage()
            );

            alert.setHeaderText(
                    "Weather Data Error"
            );
            alert.showAndWait();

            Platform.exit();
        }
    }
}
