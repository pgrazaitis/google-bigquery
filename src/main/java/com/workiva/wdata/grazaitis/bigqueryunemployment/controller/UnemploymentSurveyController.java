package com.workiva.wdata.grazaitis.bigqueryunemployment.controller;

import com.workiva.wdata.grazaitis.bigqueryunemployment.service.UnemploymentSurveyService;
import com.workiva.wdata.grazaitis.bigqueryunemployment.domain.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class UnemploymentSurveyController {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("unemploymentSurveyService")
    private UnemploymentSurveyService service;

    @RequestMapping(value = "/unemployment-survey", method = POST, produces = "text/plain")
    @ResponseStatus(HttpStatus.OK)
    public CompletableFuture<String> queryUnemployment(@RequestBody Query query)  {
        log.info("Entered queryUnemployment");
        long start = System.currentTimeMillis();

        CompletableFuture<String> completableFuture =
                CompletableFuture.supplyAsync(() -> service.query(query)) //Query BigQuery and get results
                                .thenApply( c -> service.persist(c, query)); //Once results are retrieved persist

        long executionTime = System.currentTimeMillis() - start;
        log.info("Exiting queryUnemployent. Completed in {} ms.",executionTime);
        return completableFuture;
    }

}
