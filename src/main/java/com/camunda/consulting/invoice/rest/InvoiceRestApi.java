package com.camunda.consulting.invoice.rest;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;

@RestController
public class InvoiceRestApi {
  
  private static final Logger LOG = LoggerFactory.getLogger(InvoiceRestApi.class);

  @Autowired
  ZeebeClient client;

  @PostMapping("/start-invoice")
  public ResponseEntity<String> startInvoiceProcess(@RequestBody InvoiceDTO invoiceDto) {
    LOG.info("starting process with businessKey: {}", invoiceDto);
    ProcessInstanceEvent processInstanceEvent = client
        .newCreateInstanceCommand()
        .bpmnProcessId("invoice")
        .latestVersion()
        .variables(invoiceDto)
        .send()
        .join();
    
    return ResponseEntity
        .status(HttpStatus.OK)
        .body("Started: " + processInstanceEvent.getProcessInstanceKey());
  }
  
  @PostMapping("/resume-invoice")
  public ResponseEntity<String> resumeInvoiceProcess(@RequestBody ResumeProcessDTO resumeProcessDto) {
    LOG.info("starting process at {} with invoice {}", resumeProcessDto.getActivityId(),
        resumeProcessDto.getInvoice());
    
    ProcessInstanceEvent processInstance = client
        .newCreateInstanceCommand()
        .bpmnProcessId("invoice")
        .latestVersion()
        .variables(resumeProcessDto.getInvoice())
        .startBeforeElement(resumeProcessDto.getActivityId())
        .send()
        .join();
    
    return ResponseEntity
        .status(HttpStatus.OK)
        .body("Resume: " + processInstance.getProcessInstanceKey());
  }
  
  public static class InvoiceDTO {
    private String creditor;
    private Double amount;
    private String invoiceCategory;
    private String invoiceNumber;
    private String invoiceDocument;
    private List<String> approverGroups;
    
    public InvoiceDTO() {
      super();
    }

    public InvoiceDTO(String creditor, Double amount, String invoiceCategory, String invoiceNumber, String invoiceDocument, List<String> approverGroups) {
      super();
      this.creditor = creditor;
      this.amount = amount;
      this.invoiceCategory = invoiceCategory;
      this.invoiceNumber = invoiceNumber;
      this.invoiceDocument = invoiceDocument;
      this.approverGroups = approverGroups;
    }

    public String getCreditor() {
      return creditor;
    }

    public void setCreditor(String creditor) {
      this.creditor = creditor;
    }

    public Double getAmount() {
      return amount;
    }

    public void setAmount(Double amount) {
      this.amount = amount;
    }

    public String getInvoiceCategory() {
      return invoiceCategory;
    }

    public void setInvoiceCategory(String invoiceCategory) {
      this.invoiceCategory = invoiceCategory;
    }

    public String getInvoiceNumber() {
      return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
      this.invoiceNumber = invoiceNumber;
    }

    public String getInvoiceDocument() {
      return invoiceDocument;
    }

    public void setInvoiceDocument(String invoiceDocument) {
      this.invoiceDocument = invoiceDocument;
    }

    public List<String> getApproverGroups() {
      return approverGroups;
    }

    public void setApproverGroups(List<String> approverGroups) {
      this.approverGroups = approverGroups;
    }

    @Override
    public String toString() {
      return "InvoiceDTO [creditor=" + creditor + ", amount=" + amount + ", invoiceCategory=" + invoiceCategory
          + ", invoiceNumber=" + invoiceNumber + ", invoiceDocument=" + invoiceDocument + ", approverGroups="
          + approverGroups + "]";
    }
  }
  
  public static class ResumeProcessDTO {
    private String activityId;
    private InvoiceDTO invoice;

    public ResumeProcessDTO() {
      super();
    }

    public ResumeProcessDTO(String activityId, InvoiceDTO invoice) {
      this.invoice = new InvoiceDTO(invoice.getCreditor(), 
            invoice.getAmount(), 
            invoice.getInvoiceCategory(), 
            invoice.getInvoiceNumber(), 
            invoice.getInvoiceDocument(), 
            invoice.getApproverGroups());
      this.activityId = activityId;
    }

    public String getActivityId() {
      return activityId;
    }

    public void setActivityId(String activityId) {
      this.activityId = activityId;
    }

    public InvoiceDTO getInvoice() {
      return invoice;
    }

    public void setInvoice(InvoiceDTO invoice) {
      this.invoice = invoice;
    }
    
  }
}
