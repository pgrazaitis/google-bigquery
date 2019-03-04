package com.workiva.wdata.grazaitis.bigqueryunemployment.domain;

import com.workiva.wdata.grazaitis.bigqueryunemployment.domain.AbstractEntity;

import java.io.Serializable;

public class Query extends AbstractEntity implements Serializable {

    private int year;

    public int getYear() {
        return year;
    }
    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public String toString() {
        return String.format("year=%d",getYear());
    }
}
