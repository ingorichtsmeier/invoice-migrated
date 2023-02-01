package com.camunda.consulting.invoice.resume.spring_test;

import static io.camunda.zeebe.process.test.assertions.BpmnAssert.*;
import static io.camunda.zeebe.spring.test.ZeebeTestThreadSupport.*;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.spring.test.ZeebeSpringTest;

@SpringBootTest
@ZeebeSpringTest
public class ResumeBeforeCallActivityTest {
  
  @Autowired
  ZeebeClient client;
  
  @Test
  public void testStartProcessBeforeCallActivity() {
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
    
    waitForProcessInstanceCompleted(processInstance);
    assertThat(processInstance).isCompleted().hasPassedElement("trueEndEvent");
  }

}
