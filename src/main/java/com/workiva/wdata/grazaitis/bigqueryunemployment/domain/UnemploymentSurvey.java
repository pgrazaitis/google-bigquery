package com.workiva.wdata.grazaitis.bigqueryunemployment.domain;

import com.opencsv.bean.CsvBindByPosition;
import com.workiva.wdata.grazaitis.bigqueryunemployment.domain.helper.BigQueryTable;

@BigQueryTable("bigquery-public-data.bls.unemployment_cps")
public class UnemploymentSurvey extends AbstractEntity {

    @CsvBindByPosition(position = 0)
    private String series_id;
    @CsvBindByPosition(position = 1)
    private Integer year;
    @CsvBindByPosition(position = 2)
    private String period;
    @CsvBindByPosition(position = 3)
    private Float value;
    @CsvBindByPosition(position = 4)
    private String footnote_codes;
    @CsvBindByPosition(position = 5)
    private String date;
    @CsvBindByPosition(position = 6)
    private String series_title;


    public String getSeries_id() {
        return series_id;
    }

    public void setSeries_id(String series_id) {
        this.series_id = series_id;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
        this.value = value;
    }

    public String getFootnote_codes() {
        return footnote_codes;
    }

    public void setFootnote_codes(String footnote_codes) {
        this.footnote_codes = footnote_codes;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSeries_title() {
        return series_title;
    }

    public void setSeries_title(String series_title) {
        this.series_title = series_title;
    }

}
