package com.camunda.consulting.invoice.resume.process_test;

import static io.camunda.zeebe.process.test.assertions.BpmnAssert.*;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.process.test.api.ZeebeTestEngine;
import io.camunda.zeebe.process.test.extension.ZeebeProcessTest;
import io.camunda.zeebe.process.test.filters.RecordStream;
import io.camunda.zeebe.process.test.filters.StreamFilter;
import io.camunda.zeebe.process.test.inspections.InspectionUtility;
import io.camunda.zeebe.process.test.inspections.model.InspectedProcessInstance;
import io.camunda.zeebe.protocol.record.Record;
import io.camunda.zeebe.protocol.record.value.ProcessInstanceRecordValue;

@ZeebeProcessTest
public class ResumeInCallActivityTest {
  
  ZeebeClient client;
  ZeebeTestEngine engine;  
  
  @BeforeEach
  public void deployProcessDefinitions() {
    client
        .newDeployResourceCommand()
        .addResourceFromClasspath("superProcess.bpmn")
        .addResourceFromClasspath("subProcess.bpmn")
        .send()
        .join();
  }
  
  @Test
  public void testStartAtSecondActivityOfSubprocess() throws InterruptedException, TimeoutException {
    ProcessInstanceEvent processInstance = client
        .newCreateInstanceCommand()
        .bpmnProcessId("ResumeBeforeCallActivityProcess")
        .latestVersion()
        .variables(Map.of(
            "result", true, 
            "varForSubprocess", "just a value"))
        .startBeforeElement("CallingSmallProcessCallActivity")
        .send()
        .join();

    engine.waitForIdleState(Duration.ofSeconds(2));
    
    assertThat(processInstance).isWaitingAtElements("CallingSmallProcessCallActivity");
    
    InspectedProcessInstance subProcessInstance = InspectionUtility
        .findProcessInstances()
        .withParentProcessInstanceKey(processInstance.getProcessInstanceKey())
        .findFirstProcessInstance()
        .get();
    assertThat(subProcessInstance).isWaitingAtElements("ServiceTask_1");
    Record<ProcessInstanceRecordValue> serviceTask1 = StreamFilter
        .processInstance(RecordStream.of(engine.getRecordStreamSource()))
        .withElementId("ServiceTask_1")
        .stream().findFirst().get();
    client
        .newModifyProcessInstanceCommand(subProcessInstance.getProcessInstanceKey())
        .activateElement("ServiceTask_2")
        .and()
        .terminateElement(serviceTask1.getKey())
        .send()
        .join();
    engine.waitForIdleState(Duration.ofSeconds(2));
    
    assertThat(subProcessInstance).isWaitingAtElements("ServiceTask_2");
  }

}
