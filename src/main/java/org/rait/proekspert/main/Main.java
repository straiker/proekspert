package org.rait.proekspert.main;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rait.proekspert.data.SeriesData;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

public class Main extends Application {
    private static final Logger logger = LogManager.getLogger("HelloWorld");
    static List<String> list = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) throws Exception{

        list.stream().forEach(System.out::println);

        Map<String, Long> counters = list.stream()
                .collect(Collectors.groupingBy(s -> s,
                        Collectors.counting()));

        double listSize = list.size();

        primaryStage.setTitle("Top 10 words");
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final BarChart<String,Number> bc =
                new BarChart<String,Number>(xAxis,yAxis);
        bc.setTitle("Words Summary");
        xAxis.setLabel("Word");
        yAxis.setLabel("%");

        XYChart.Series series = new XYChart.Series();
        series.setName("Word occurrence");
        Map<String, Long> sortedCounters = counters.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .collect(
                    Collectors
                        .toMap(
                                Map.Entry::getKey, Map.Entry::getValue,(oldValue, newValue) -> oldValue, LinkedHashMap::new)
        );

        sortedCounters.forEach((k,v) -> {
                    series.getData().add(
                            new XYChart.Data(k+"("+getPercentage(listSize, v)+")",getPercentage(listSize, v))
                    );
                });


        Scene scene  = new Scene(bc,800,600);
        bc.getData().addAll(series);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {

        renderFileData();
        launch(args);

    }

    private double getPercentage(double listSize, long count){
        return new BigDecimal((count*100)/listSize).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private static List<String> renderFileData(){
        String content = null;
        try {
            content = new Scanner(new File("words.txt")).useDelimiter("\\Z").next();
        } catch (FileNotFoundException e) {
            logger.warn(e.getMessage());
        }

        String[] words = content.split("[^\\w']+");

        for (String word : words){
            int wordSize = word.length();
            if (wordSize > 3){
                int desiredSize = 4;
                for (int i = 0; i <= wordSize-4; i++){
                    list.add(word.substring(i, desiredSize));
                    if (desiredSize <= wordSize){
                        desiredSize++;
                    }
                }
            }
        }

        return list;
    }

}
