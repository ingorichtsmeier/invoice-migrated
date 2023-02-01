# Invoice Example migrated to Camunda Platform 8

This repository contains the process application to run the Invoice Example from the Camunda Platform 7 in Camunda Platform 8.

The process diagrams were converted with the 
[diagram converter](https://github.com/camunda-community-hub/camunda-7-to-8-migration/tree/main/backend-diagram-converter).

The DMN diagram was handcrafted. I copied the dmn file and added this to the definition element in the 2nd line:

```
xmlns:modeler="http://camunda.org/schema/modeler/1.0" modeler:executionPlatform="Camunda Cloud" modeler:executionPlatformVersion="8.1.0"
```

With a Rest API at `/resume-invoice`, process instances can be started at a given activity 
(usually the one where they wait in Camunda 7).

The delegate class is copied to the new project and served by the [camunda-7-adapter](https://github.com/camunda-community-hub/camunda-7-to-8-migration/tree/main/camunda-7-adapter)

### Simplifications

The Camunda 7 process used a file variable to save the Invoice as a PDF. 
This is not recommended (maybe not possible) in Zeebe, as all variables are JSON. 

The best way would be to migrate the PDFs from process variables into a content management system beforehand and replace the 
value of the variable with the URL to access the document directly in the CMS.

Currently the archive invoice delegate assumes that the type of `invoiceDocument` is String.