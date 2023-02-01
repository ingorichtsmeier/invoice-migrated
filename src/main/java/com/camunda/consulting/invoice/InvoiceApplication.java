package com.camunda.consulting.invoice;

import org.camunda.community.migration.adapter.EnableCamunda7Adapter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.camunda.zeebe.spring.client.EnableZeebeClient;
import io.camunda.zeebe.spring.client.annotation.Deployment;

@SpringBootApplication
@EnableZeebeClient
@EnableCamunda7Adapter
@Deployment(resources = {"classpath*:*.bpmn", "classpath*:*.dmn"})
public class InvoiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(InvoiceApplication.class, args);
  }

}
