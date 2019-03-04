package com.workiva.wdata.grazaitis.bigqueryunemployment.service;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.workiva.wdata.grazaitis.bigqueryunemployment.domain.UnemploymentSurvey;
import com.workiva.wdata.grazaitis.bigqueryunemployment.domain.helper.BigQueryTable;
import com.workiva.wdata.grazaitis.bigqueryunemployment.domain.Query;
import com.workiva.wdata.grazaitis.bigqueryunemployment.service.helper.UnemployementSurveyMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service("unemploymentSurveyService")
public class UnemploymentSurveyService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${gsa.json}")
    private String gsa_cred_path; //Leverage properties file to control BigQuery API key aspects

    @Value("${gsa.project.id}")
    private String gs_project_id; //Leverage properties file to control BigQuery API key aspects

    @Autowired
    private UnemployementSurveyMapper mapper;

    private BigQuery bigQuery = null;

    /***
     * Decorated getter to control the basic builder of BigQuery.
     * @param query
     * @return
     */
    private String queryBuilder(Query query) {
        log.info("Entered queryBuilder");
        long start = System.currentTimeMillis();

        BigQueryTable bigQueryTableAnnotation = UnemploymentSurvey.class.getAnnotation(BigQueryTable.class);
        String fieldCsv = Arrays.stream(UnemploymentSurvey.class.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(CsvBindByPosition.class))
                .sorted(Comparator.comparingInt(o -> o.getAnnotation(CsvBindByPosition.class).position()))
                .map(field -> field.getName() )
                .collect(Collectors.joining(","));


        String sqlStatement = String.format("SELECT %s FROM `%s` WHERE %s;", fieldCsv, bigQueryTableAnnotation.value(),query);
        log.info("queryBuilder.sqlStatement={}",sqlStatement);

        long executionTime = System.currentTimeMillis() - start;
        log.info("Exiting queryBuilder. Completed in {} ms.",executionTime);
        return sqlStatement;
    }
    public BigQuery getBigQuery() {
        log.info("Entered getBigQuery");
        long start = System.currentTimeMillis();

        if (this.bigQuery != null) {
            long executionTime = System.currentTimeMillis() - start;
            log.info("Exiting getBigQuery. Completed in {} ms.",executionTime);
            return bigQuery;
        }
        ClassPathResource gsa_cred_json = new ClassPathResource(gsa_cred_path);
        try {
            this.bigQuery =
                    BigQueryOptions.newBuilder()
                            .setProjectId(gs_project_id)
                            .setCredentials(ServiceAccountCredentials.fromStream(new FileInputStream(gsa_cred_json.getFile())))
                            .build().getService();
        } catch (IOException e) {
            log.error("getBigQuery failed to initialize BigQuery.");
        }

        long executionTime = System.currentTimeMillis() - start;
        log.info("Exiting getBigQuery. Completed in {} ms.",executionTime);
        return this.bigQuery;
    }

    /***
     * Query is a simple facade to the underlying Google service.
     *
     * Note: Could further add logic to look at locally saved instances for the given query parameter.
     * Additional refactoring possible to complete abstraction of logic and provide flexibility for other objects.
     *
     * @param query
     * @return
     */
    public List<UnemploymentSurvey> query(Query query) {
        log.info("Entered query");
        long start = System.currentTimeMillis();

        List<UnemploymentSurvey> content = null;
        try {

            String bd_query = queryBuilder(query);

            QueryJobConfiguration queryConfig =
                    QueryJobConfiguration.newBuilder(bd_query)
                            .setUseQueryCache(true)
                            .build();

            content = StreamSupport.stream(getBigQuery().query(queryConfig).iterateAll().spliterator(), false)
                    .map(row -> mapper.map(row))
                    .collect(Collectors.toList());

        } catch (InterruptedException e) {
            log.error("query thread was interrupted.");
            throw new IllegalStateException(e);
        }

        if(content != null)
            log.info("query count={}",content.size());
        else
            log.warn("query failed to execute, no records available.");

        long executionTime = System.currentTimeMillis() - start;
        log.info("Exiting query. Completed in {} ms.",executionTime);
        return content;
    }


    /***
     * Persist provides a basic interface to save content as a CSV. Decision to use a CSV was based on the idea that it
     * was just a write, no additional manipulation/retrieval (outside assumed bulk) was necessary. File write was most
     * performant.
     *
     * Note: Could refactor to further abstract and improve naming and even abstract to allow Repository injection.
     * @param content
     * @param query
     * @return
     */
    public String persist(List<UnemploymentSurvey> content, Query query) {
        log.info("Entered persist");
        long start = System.currentTimeMillis();

        Writer writer = null;
        try {
            String fileName = String.format("%s.csv",query.getYear());
            String currDir = System.getProperty("user.dir");
            File output = new File(currDir, fileName);
            writer = new FileWriter(output);
            StatefulBeanToCsv sbc = new StatefulBeanToCsvBuilder(writer)
                    .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                    .build();

            sbc.write(content);
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvRequiredFieldEmptyException e) {
            e.printStackTrace();
        } catch (CsvDataTypeMismatchException e) {
            e.printStackTrace();
        }

        String response = String.format("Successfully saved: %d records.",content.size());
        log.info("persist.response={}",response);

        long executionTime = System.currentTimeMillis() - start;
        log.info("Exiting persist. Completed in {} ms.",executionTime);
        return response;
    }
}
