package org.rait.proekspert.main;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rait.proekspert.model.Words;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

public class Main extends Application {
    private static final Logger logger = LogManager.getLogger("HelloWorld");

    @Getter @Setter
    private List<String> list = new ArrayList<>();

    private ObservableList<Words> data = FXCollections.observableArrayList();

    @Getter @Setter
    private double listSize;

    @Override
    public void start(Stage primaryStage) throws Exception{

        renderFileData();
        setListSize(list.size());

        primaryStage.setTitle("Top 10 words");
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final BarChart<String,Number> bc = new BarChart<>(xAxis, yAxis);
        bc.setTitle("Words Summary");
        xAxis.setLabel("Word");
        yAxis.setLabel("%");

        TabPane layout = new TabPane();
        Tab chart = new Tab("Chart");
        Tab table = new Tab("Table");

        layout.getTabs().add(chart);
        layout.getTabs().add(table);

        chart.setContent(bc);
        table.setContent(setTable());

        Scene scene  = new Scene(layout,800,600);
        bc.getData().addAll(series());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public double getPercentage(double listSize, long count){
        return BigDecimal.valueOf((count*100)/listSize).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public List<String> renderFileData(){
        String content = null;
        try {
            content = new Scanner(new File("words.txt")).useDelimiter("\\Z").next();
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }

        String[] words =    content.split("[^\\w']+");

        for (String word : words){
            int desiredSize = 4;

            for (int i = word.length(); i >= desiredSize; i--) {
                for (int k = 0; k <= word.length() - desiredSize; k++) {
                    if (i - k >= desiredSize) {
                        list.add(word.substring(k, i));
                    }
                }
            }
        }

        return list;
    }

    public Map<String, Long> countWords(){
        return list.stream()
                .collect(Collectors.groupingBy(s -> s,
                        Collectors.counting()));
    }

    public XYChart.Series series(){
        XYChart.Series series = new XYChart.Series();
        series.setName("Word occurrence");

        Map<String, Long> sortedCounters = countWords().entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .collect(
                        Collectors
                                .toMap(
                                        Map.Entry::getKey, Map.Entry::getValue,(oldValue, newValue) -> oldValue, LinkedHashMap::new)
                );


        sortedCounters.forEach((k,v) ->
            series.getData().add(
                    new XYChart.Data(k+"("+getPercentage(listSize, v)+")",getPercentage(listSize, v))
            )
        );

        return series;
    }

    public TableView setTable(){
        TableView table = new TableView();

        table.setEditable(true);

        TableColumn word = new TableColumn("Word");
        TableColumn count = new TableColumn("Count");
        TableColumn percentage = new TableColumn("Percentage");

        Map<String, Long> sortedCounters = countWords().entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .collect(
                        Collectors
                                .toMap(
                                        Map.Entry::getKey, Map.Entry::getValue,(oldValue, newValue) -> oldValue, LinkedHashMap::new)
                );

        sortedCounters.forEach((k,v) ->
            data.add(new Words(k,v,getPercentage(listSize,v)))
        );

        word.setCellValueFactory(
                new PropertyValueFactory<Words,String>("word")
        );
        count.setCellValueFactory(
                new PropertyValueFactory<Words,Long>("count")
        );
        percentage.setCellValueFactory(
                new PropertyValueFactory<Words,Double>("percentage")
        );

        table.setItems(data);
        table.getColumns().addAll(word, count, percentage);

        return table;
    }

}
