# WaCoDiS Data Access API

This projects implements a REST API that persists and manages metadata from all relevant datasets and datasources within the WaCoDiS monitoring system.

## Table of Content  

1. [WaCoDiS Project Information](#wacodis-project-information)
2. [Overview](#overview)
  * [Architecture Overview](#architecture-overview)
3. [Installation / Building Information](#installation--building-information)
  * [Build from Source](#build-from-source)
  * [Build using Docker](#build-using-docker)
  * [Deployment](#deployment)
    * [Preconditions](#preconditions)
4. [User Guide](#user-guide)
  * [Run Data Access](#run-data-access)
    * [Using Docker](#using-docker)
  * [Elasticsearch Index Initialization](#elasticsearch-index-initialization)
  * [Configuration](#configuration)
    * [Parameters](#parameters)
5. [Contribution - Developer Information](#contribution---developer-information)
  * [How to Contribute](#how-to-contribute)
    * [Extending Data Access](#extending-data-access)
      * [New Types of DataEnvelope and SubsetDefinition](#new-types-of-dataenvelope-and-subsetdefinition)
  * [Branching](#branching) 
  * [License and Third Party Lib POM Plugins](#license-and-third-party-lib-pom-plugins)
6. [Contact](#contact)
7. [Credits and Contributing Organizations](#credits-and-contributing-organizations)
    
## WaCoDiS Project Information
<p align="center">
  <img src="https://raw.githubusercontent.com/WaCoDiS/apis-and-workflows/master/misc/logos/wacodis.png" width="200">
</p>
Climate changes and the ongoing intensification of agriculture effect in increased material inputs in watercourses and dams. Thus, water industry associations, suppliers and municipalities face new challenges. To ensure an efficient and environmentally friendly water supply for the future, adjustments on changing conditions are necessary. Hence, the research project WaCoDiS aims to geo-locate and quantify material outputs from agricultural areas and to optimize models for sediment and material inputs (nutrient contamination) into watercourses and dams. Therefore, approaches for combining heterogeneous data sources, existing interoperable web based information systems and innovative domain oriented models will be explored.

### Architecture Overview

The WaCoDiS project aims to exploit the great potential of Earth Observation (EO) data (e.g. as provided by the Copernicus Programme) for the development of innovative water management analytics service and the improvement of hydrological models in order to optimize monitoring processes. Existing SDI based geodata and in-situ data from the sensors that monitor water bodies will be combined with Sentinel-1 and Sentinel-2 data. Therefore, the WaCoDiS monitoring system is designed as a modular and extensible software architecture that is based on interoperable interfaces such as the Open Geospatial Consortium (OGC) Web Processing Service. This allows a sustainable and ﬂexible way of integrating different EO processing algorithms. In addition, we consider architectural aspects like publish/subscribe patterns and messaging protocols that increase the effectiveness of processing big EO data sets. Up to now, the WaCoDiS monitoring system comprises the following components:  

**[Job Manager](https://github.com/WaCoDiS/job-definition-api)**  
A REST API enables users to define job descriptions for planning the execution of analysis processes. 

**[Core Engine](https://github.com/WaCoDiS/core-engine)**  
The _Core Engine_ schedules jobs for planned process executions based on the job descriptions. In addition, it is responsible for triggering WPS processes as soon as all required process input data is available.

**[Datasource Observer](https://github.com/WaCoDiS/datasource-observer)**  
Several observing routins requests certain datastores for new available data, such as in-situ measurements, Copernicus satellite data, sdi based geodata and services or meteorological data that are required for process executions.

**[Data Wrapper](https://github.com/WaCoDiS/data-access-api)**  
Information  about  all  incoming  required datasets are bundled by a are stored in a Metadata Storage. For the purpose of defining process inputs, the _Data Wrapper_ generates references to the required datasets from the metadata and provides these references to the Core Engine via a REST API. To provide an asynchronous Pub/Sub pattern, a [Metadata Connector](https://github.com/WaCoDiS/metadata-connector) will listen for new datasets and then interacts with the REST API.

**[Web Processing Service](https://github.com/WaCoDiS/javaps-wacodis-backend)**  
The  execution  of  analysis processes  provided  by  EO  Tools  is  encapsulated  by  a  OGC 
Web Processing Service (WPS), which provides a standardized interface for this purpose. Therefore a custom backend for the [52°North javaPS implementation](https://github.com/WaCoDiS/javaPS) provides certain preprocessing and execution features. 

**[Product Listener](https://github.com/WaCoDiS/product-listener)**  
A _Product Listener_ will be notified as soon as any analyis process has finished and a new earth observation product is available. The component will fetch the product from the WPS and routes it to one or more specific backends (e.g. GeoServer, ArcGIS Image Server) that provides a certain service for the user to access the product.

**Product Importer**  
For each product service backend a certain helper component will import the earth observation product into the specific backend's datastore and may set up a service on top of it. The product importer can be provided as part of the _Product Listener_ or can be provided as an external component (e.g. a [python script](https://github.com/WaCoDiS/Tools/tree/imageServicePublisherTest/imageServicePublisher) for importing porduct into the ArcGIS Image Server).

<p align="center">
  <img src="https://raw.githubusercontent.com/WaCoDiS/apis-and-workflows/master/architecture/wacodis_high_level_architecture.png" width="600" alt="Diagram of WaCoDiS high level architecture">
</p>

The WaCoDiS monitoring system architecture is designed in a modular fashion and follows a  publish/subscribe pattern. The different components are loosely connected to each other via messages that are passed through a message broker. Each module subscribes to messages of interest at the message broker. This approach enables an independent and asynchronous handling of specific events.  

The messages exchanged via message broker follow a domain model that has been defined by the OpenAPI specification. You can find these definitions and other documentation in the [apis-and-workflows repo](https://github.com/WaCoDiS/apis-and-workflows).

## Overview  
TODO
* Brief component description
* OpenAPI Specification
* Utilized technologies

## Installation / Building Information
### Build from Source
WaCoDiS Data Access uses (as most of the WaCoDiS components) the java programming language. In order to build Data Access from source _Java Development Kit_ (JDK) must be available. Data Access is tested with Oracle JDK 8 and OpenJDK 8. Unless stated otherwise later JDK versions can be used.  
Data Access is a [maven project](https://maven.apache.org/install.html). To build this project from source maven has to be installed.  
  
1. Build Data Models
  * change directory to _Data Access Models_ subfolder (_data-access-api/data-access-models_)
  * run `mvn clean install -p download-generate-models`
  
  Two profiles are applicable for building data models module. The profile _download-generate-models_ fetches the latest version of [WaCoDiS Schema Definitions](https://github.com/WaCoDiS/apis-and-workflows/blob/master/openapi/src/main/definitions/wacodis-schemas.yml) (OpenAPI) from Github and generates corresponding java classes. The profile _generate-models_ gets the schema definitions from a local file and generates corresponding java classes. By default the schema definitions are expected in the modules resource folder (_resources/definitions/wacodis-schemas.yml_). Alternatively, the file path can be configured in _pom.xml_.  
    
2. Build _Data Wrapper_ and _Data Access API_
  * change directory to the projects root directory (_data_access_api_)
  * * run `mvn clean install`  
    
The Data Wrapper module must be built before the API module if both modules are built separately.

### Build using Docker
See [run section](#using-docker) for docker instructions.

### Deployment
This section describes deployment scenarios, options and preconditions.
#### Preconditions
* In order to run Data Access Java Runtime Environment (JRE) (version >= 8) must be available. In order to [build Data Access from source](#installation--building-information) Java Development Kit (JDK) version >= 8) must be abailable. Data Access is tested with Oracle JDK 8.
* A (running) instance of [elasticsearch](https://www.elastic.co/downloads/elasticsearch) must be available.  
* When running data access as part of the WaCoDiS system, a running instance of [RabbitMQ message broker](https://www.rabbitmq.com/) must be available. Otherwise communication with other WaCoDis components fails.  
  
The server addresses are [configurable](#configuration).  
  
 * If configuration should be fetched from Configuration Server a running instance of [WaCoDiS Config Server](https://github.com/WaCoDiS/config-server) must be available.
 
## User Guide
### Run Data Access
Currently there are no pre-compiled binaries available for WaCoDiS Data Access. Data Access must be [built from source](#installation--building-information). Alternatively Docker can be used to (build and) run WaCoDiS Data Access.

Data Access is a Spring Boot application. Execute the compiled jar (`java -jar  data-access-api.jar`) or run *org.openapitools.OpenAPI2SpringBoot.java* in IDE (Module: _WaCoDiS Data Access API_) to start the data access service. By default the service is available on port 8080. [Multiple ways to change the default port exist](https://www.baeldung.com/spring-boot-change-port). If the service started successfully (see [Preconditions](#preconditions)) the API description for WaCoDiS Data Access is available on _localhost:8080_ (provided default port was not changed). 

#### Using Docker
1. Build Docker Image from [Dockerfile](https://github.com/WaCoDiS/data-access-api/blob/master/Dockerfile) that resides in the projects root folder.
2. Run created Docker Image. A port binding for container port 8080 is necessary to make the service available.  
(`docker run -p 8080:8080 wacodis_data_access:latest`)

### Elasticsearch Index Initialization
During the start up process, data access automatically initializes a (Elasticsearch) search index that indexes metadata (DataEnvelopes) for available data sets. The index settings are defined in a json file which is by default */main/resources/elasticsearch_indexsettings.json*. The mappings section of this file should not be altered because data access needs an index that matches those specifications. The location of the index settings file is [configurable](#configuration).  
If index intitialization fails because of a connection error it is retried after a timeout. The max. number of retries and the timeout is [configurable](#configuration). This configuration parameters can be used in deployment scenarios (for example docker compose) if elasticsearch is not available before data access is started.

### Configuration
Configuration is fetched from [WaCoDiS Config Server](https://github.com/WaCoDiS/config-server). If config server is not available configuration values located at *main/resources/bootstrap.yml* are applied instead.  

#### Parameters
The following section contain descriptions for configuration parameters ordered by configuration section.

##### spring/resources-api
parameters related to the Resource API (*/resources/...*)  

| value     | description       | note  |
| ------------- |-------------| -----|
| elasticsearch/uri     | elasticsearch server address | uri scheme must match *http://host:port* |
| elasticsearch/index      | name of the index (containing DataEnvelopes) that should be queried   |  |
| elasticsearch/requestTimeout_Millis  | request timeout (milliseconds) |  |

##### spring/dataenvelopes-api
parameters related to the DataEnvelope API (*/dataenvelopes/...*)  

| value     | description     | note  |
| ------------- |-------------| -----|
| elasticsearch/uri     | elasticsearch server address | uri scheme must match *http://host:port* |
| elasticsearch/index      | name of the index (containing DataEnvelopes) that should be queried   |  |
| elasticsearch/requestTimeout_Millis  | request timeout (milliseconds) |  |
| elasticsearch/indexInitialization_RetryMaxAttempts  | max attempts for index intitialization during start-up | [see Elasticsearch Index Initialization](#elasticsearch-index-initialization) |
| elasticsearch/indexInitialization_RetryDelay_Millis  | delay between atempts for index intitialization during start-up (milliseconds) |  |
| elasticsearch/indexInitialization_SettingsFile | location of settings file that is applied for index intitialization during start-up|  |

##### spring/cloud/stream/bindings/acknowledgeDataEnvelope
parameters related to DataEnvelope acknowledgement messages

| value     | description       | note  |
| ------------- |-------------| -----|
| destination     | topic used for DataEnvelope acknowledgement messages | e.g. *wacodis.dataenvelope.acknowledgment* |
| binder      | defines the binder (message broker)   | see [binders](#springcloudstreambinderswacodis_rabbit), does not have to be changed from *wacodis_rabbit* |
| content-type      | content type of  DataEnvelope acknowledgement messages (mime type)   | see [binders](#springcloudstreambinderswacodis_rabbit), does not have to be changed from *application/json* |

##### spring/cloud/stream/binders/wacodis_rabbit
parameters related to WaCoDis message broker

| value     | description       | note  |
| ------------- |-------------| -----|
| type     | type of message broker  | WaCoDiS uses [RabbitMQ message broker](https://www.rabbitmq.com/)|
| environment/spring/rabbitmq/host | RabbitMQ host (WaCoDiS message broker) | e.g. *localhost* |
| environment/spring/rabbitmq/host | RabbitMQ port (WaCoDiS message broker)   | e.g. *5672*|
| environment/spring/rabbitmq/username | RabbitMQ username (WaCoDiS message broker)   | |
| environment/spring/rabbitmq/password | RabbitMQ password (WaCoDiS message broker)   | |

## Contribution - Developer Information
This section contains information for developers.

### How to Contribute
#### Extending Data Access
##### New Types of DataEnvelope and SubsetDefinition
Data Access must be modified if new types of DataEnvelope or SubsetDefintion are added to [Wacodis schemas](https://github.com/WaCoDiS/apis-and-workflows/blob/master/openapi/src/main/definitions/wacodis-schemas.yml) in order to support the newly introduced data types. See the [Wiki](https://github.com/WaCoDiS/data-access-api/wiki/Extending-Data-Access) for further information.

### Branching
The master branch provides sources for stable builds. The develop branch represents the latest (maybe unstable) state of development.

### License and Third Party Lib POM Plugins
[optional]

## Contact
|    Name   |   Organization    |    Mail    |
| :-------------: |:-------------:| :-----:|
| Sebastian Drost | Bochum University of Applied Sciences | sebastian.drost@hs-bochum.de |
| Arne Vogt | Bochum University of Applied Sciences | arne.vogt@hs-bochum.de |
| Andreas Wytzisk  | Bochum University of Applied Sciences | andreas.wytzisk@hs-bochum.de |
| Matthes Rieke | 52° North GmbH | m.rieke@52north.org |

## Credits and Contributing Organizations
- Department of Geodesy, Bochum University of Applied Sciences, Bochum
- 52° North Initiative for Geospatial Open Source Software GmbH, Münster
- Wupperverband, Wuppertal
- EFTAS Fernerkundung Technologietransfer GmbH, Münster

The research project WaCoDiS is funded by the BMVI as part of the [mFund programme](https://www.bmvi.de/DE/Themen/Digitales/mFund/Ueberblick/ueberblick.html)  
<p align="center">
  <img src="https://raw.githubusercontent.com/WaCoDiS/apis-and-workflows/master/misc/logos/mfund.jpg" height="100">
  <img src="https://raw.githubusercontent.com/WaCoDiS/apis-and-workflows/master/misc/logos/bmvi.jpg" height="100">
</p>
