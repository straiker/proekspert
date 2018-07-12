package org.rait.proekspert.data;

import javafx.scene.chart.XYChart;
import lombok.Getter;
import lombok.Setter;

public class SeriesData {

    @Getter @Setter
    private String word;

    @Getter @Setter
    private double occurrence;

    public SeriesData(){}

    public SeriesData(String word, double occurrence){
        this.word = word;
        this.occurrence = occurrence;
    }

    public XYChart.Series getSeries(){
        XYChart.Series series = new XYChart.Series();
        series.setName(getWord());
        series.getData().add(new XYChart.Data(getWord(), getOccurrence()));

        return series;
    }
}
