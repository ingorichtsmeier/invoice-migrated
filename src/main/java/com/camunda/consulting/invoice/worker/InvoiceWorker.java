package com.camunda.consulting.invoice.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;

@Component
public class InvoiceWorker {
  
  private static final Logger LOG = LoggerFactory.getLogger(InvoiceWorker.class);
  
  @JobWorker
  public void logger(JobClient client, ActivatedJob job) {
    LOG.info("passing job {} on activity {}", job.getKey(), job.getElementId());
  }


}
