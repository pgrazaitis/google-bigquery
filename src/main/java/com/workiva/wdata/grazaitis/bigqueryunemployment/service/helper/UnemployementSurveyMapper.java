package com.workiva.wdata.grazaitis.bigqueryunemployment.service.helper;

import com.google.cloud.bigquery.FieldValueList;
import com.workiva.wdata.grazaitis.bigqueryunemployment.domain.UnemploymentSurvey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("unemploymentSurveyMapper")
public class UnemployementSurveyMapper {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public UnemploymentSurvey map(FieldValueList row) {

        UnemploymentSurvey obj = new UnemploymentSurvey();
        obj.setSeries_id((String)row.get(0).getValue());
        obj.setYear((int)row.get(1).getLongValue());
        obj.setPeriod((String)row.get(2).getValue());
        obj.setValue((float)row.get(3).getDoubleValue());
        obj.setFootnote_codes((String)row.get(4).getValue());
        obj.setDate((String)(row.get(5).getValue()));
        obj.setSeries_title((String)row.get(6).getValue());

        return obj;
    }
}
