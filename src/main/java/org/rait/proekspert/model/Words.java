package org.rait.proekspert.model;

import lombok.Getter;
import lombok.Setter;

public class Words {

    @Getter @Setter
    private String word;

    @Getter @Setter
    private long count;

    @Getter @Setter
    private double percentage;

    public Words(String word, long count, double percentage){
        this.word = word;
        this.count = count;
        this.percentage = percentage;
    }

}
