
# ESPM

The ESPM (Enterprise Sales & Procurement Model) application is a reference application which demonstrates how to build applications on SAP Cloud Platform with the Java runtime. This version of the ESPM application(cloud-espm-multi-environment) showcases how to adapt a ESPM application developed to run on SAP Cloud Platform Neo Environment to run on SAP Cloud Platform Cloud Foundry (CF) Environment as well. The adapted application has one code-base for running on both Neo and CF environment.

# Description

## Business Scenario
The business scenario is that of an eCommerce application that sells electronics products.

- The Customers can order products and provide reviews on the products.
- The Retailer can then accept the sales orders created against orders created by customers. The Retailer can also update the product stock information.

ESPM application has 2 underlying applications

- Webshop - this application is a web shopping app, which doesn't have any authentication
- Retailer - this application is used by a retailer to manage stocks, approve/reject sales orders. A user needs to be authenticated and should have a retailer role to perform these operations.

![Architecture Diagram](/docs/architecture.jpg?raw=true)

# Requirements

- [Java8](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
- [Maven](https://maven.apache.org/download.cgi)
- [CF CLI](https://help.sap.com/viewer/65de2977205c403bbc107264b8eccf4b/Cloud/en-US/4ef907afb1254e8286882a2bdef0edf4.html)
- If you do not yet have a SAP Cloud Platform trial or enterprise account, signup for a SAP Cloud Platform trial account by following the [documentation](https://cloudplatform.sap.com/try.html).

For Setting up the development environment, refer to the [quick start guide](https://github.com/SAP/cloud-espm-v2#1-quick-start-guide)

# Download and Installation

## Build the application

From the root directory (i.e. cloud-espm-multi-environment), run the following Maven command
```
    mvn clean install

```
This command builds the application and generates `war` file for both the `Neo` and `CF` environment.

```
Note: When running the application in internet, follow the below steps to download the ngdbc driver to your local ~.m2 repository, as its not available in central maven

1. You need to download the SAP Hana Cloud Platform SDK from here: https://tools.hana.ondemand.com/#cloud
2. Take the latest "Java Web Tomcat 8" from the download section (a package starting with neo-).
3. Unzip the archive to an arbitrary location on your devbox.
4. Extract the JDBC driver (ngdbc.jar) from the archive (you will find the driver in the archive under: repository/.archive/lib/ngdbc.jar). The driver is closed source, so it is NOT available from public Maven repositories!
5. Put the driver either to your local maven repository with:
**mvn install:install-file -Dfile=<path-to-file> -DgroupId=com.sap.db.jdbc \**
    **-DartifactId=ngdbc -Dversion=2.0.13 -Dpackaging=jar**

``` 

## Running the application in NEO environment

### 1. Deploy application

The `war` file for the NEO environment can be found at `neo/espm-cloud-web-neo/target/espm-cloud-web-neo.war` .

Open the hanatrial account cockpit and upload the war file as follows.

- Go to SAP CP Cockpit --> Click on Java Application under Applications --> Click on Deploy Application

- Add War File Location, Give Application Name "espm", select Runtime Name "Java Web Tomcat 8" and JVM Version as "JRE 8"

- After Successful deployment, Click on Start

### 2. Configure Roles

   - Configure the application role assignments from the [cockpit](https://help.hana.ondemand.com/help/frameset.htm?db8175b9d976101484e6fa303b108acd.html). You basically need to add the "Retailer" role to your SAP Cloud Platform user to access the Retailer UI

    You can access the application from the URL

    * The eCommerce site can be accessed via the URL: https://espm\<account\>.hanatrial.ondemand.com/espm-cloud-web/webshop
    * The Retailer UI can be accessed via the URL: https://espm\<account\>.hanatrial.ondemand.com/espm-cloud-web/retailer

    **Note! The application name must be "espm", else the above URL will change based on the application name given during deployment**


### 3. Bind the database to espm application and start espm application

Below is the process to bind the database to the java application in HCP trial account using a Shared HANA database

   - In the cockpit, select an account and choose Persistence -> Databases & Schemas -> in the navigation area.
   - Click on the new button
   - In the popup window, enter the below information
   	```sh
  	Schema ID: espm
  	Database System: HANA (<shared>)
  	Version: 1.00*
  	Click on Save button
	```

   - In the cockpit, select an account and choose Applications -> Java Application -> Click on the name of the espm application that you deployed
   - In the navigation area in the cockpit, select Configuration -> Data Source Bindings
   - Click "New Binding" button in detail plane
   - In the popup window, enter the below information

   	```sh
  	Data Source - <default>
  	DB/Schema ID - select espm (the one that you created above)
  	Click on Save button
    ```

   - Now you need to restart espm application (stop, if already running and start the application) from the cockpit.

## Running the application in CF environment

From root folder, navigate to `cf` folder.

#### 1. Login to Cloud Foundry

```
cf api <api>
cf login -o <org> -s <space>
```

#### 2. Create Service

Depending on the requirement, create a service instance for either of the database (HANA or PostgreSQL).

```
cf create-service hana schema espm-hana (HANA)
or
cf create-service postgresql v9.4-dev espm-postgres (PostgreSQL)
```

Create service instance for the XSUAA

```
cf cs xsuaa default espm-uaa -c xs-security.json
```

#### 3. Edit Manifest

Open the manifest.yml file and edit the following
Replace <i-number> placeholders with your ```I/D/C numbers``` so that the application name and host name is unique in the CF landscape.

```DATABASE_TYPE: <DB name>```

Replace the ```<DB name>``` with the Database name for which you have created the service instance

For HANA – ```hana```

For PostgreSQL – ```postgresql```

```<DB instance name>```

Replace the `<DB instance name>` with the service instance that you have created for the database.

#### 4 Enable security
The security implementation in ESPM for Cloud Foundry is implemented using SAP's `java-container-security` library as mentioned in the help document [Configure Authentication for Java API Using Spring Security](https://help.sap.com/viewer/65de2977205c403bbc107264b8eccf4b/Cloud/en-US/be97ec4a799c4135884c62610fea2a8f.html). This library is available for download for SAP Customers from [SAP Support Portal](https://support.sap.com/en/index.html). Under downloads search for archive XS JAVA 1. More details mentioned in help document [Download and Consume Java Libraries](https://help.sap.com/viewer/4505d0bdaf4948449b7f7379d24d0f0d/2.0.01/en-US/8783c06f6d5a48e791e7ad17d49a95c0.html).

** Please note that if you are unable to download the XS JAVA 1 archive from SAP Support Portal you will not be able to enable security and can skip the below instruction and steps 6 and 7 **

- After unzipping and running a maven build on the XS JAVA 1 check and note down the version of the java-container-security library.
- Open the pom file of project [espm-cloud-web-cf](https://github.com/SAP/cloud-espm-multi-environment/blob/master/cf/espm-cloud-web-cf/pom.xml) and dependency for java-container-security
```xml
  <dependency>
				<groupId>com.sap.xs2.security</groupId>
				<artifactId>java-container-security</artifactId>
				<version>{container.security.version}</version>
		</dependency>
```

- Open and edit the [web.xml](https://github.com/SAP/cloud-espm-multi-environment/blob/master/cf/espm-cloud-web-cf/src/main/webapp/WEB-INF/web.xml) of the espm-cloud-web-cf project. Un comment the block shown below i.e. line 36 - 50

```xml
	<listener>
		<listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
	</listener>
	<context-param>
		<param-name>contextConfigLocation</param-name>
		<param-value>/WEB-INF/spring-security.xml</param-value>
	</context-param>
	<filter>
		<filter-name>springSecurityFilterChain</filter-name>
		<filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
	</filter>
	<filter-mapping>
		<filter-name>springSecurityFilterChain</filter-name>
		<url-pattern>/*</url-pattern>
	</filter-mapping>

```
- Navigate to the root folder of the project (i.e. cloud-espm-multi-environment) and do a maven build ` mvn clean install ` to recreate the binaries for CF application with the changes done for security in this step.

#### 5. Push the application

```cf push -f manifest.yml```

#### 6. Setup Role collections


For the application to work, the Retailer application needs the template role called "Retailer" assigned to the user.

 - In your trial account, in the left pane select Role collections under the Security tab.

 - Add a new Role collection named, "Retailer"

 - Click on the newly created "Retailer "role collection and add new Role.

 - Select the application identifier name similar to the one you have given in the xs-security.json file.

 - Select the role template and assign the Role you have created in the previous step.

#### 7. Assign Role to the user

We need to assign the role which we have created in the previous step to the user.

 - In your trial account, in the left pane select "Trust Configuration" under the Security tab.

 - Click on the default IDP service.

 - Enter username/email and click on the add Assignment button.

 - Select the Role as "Retailer" to assign it to the user.

# Out of Scope
 Document services is out of scope currently in Cloud Foundry application

# Known issues
 None

# Support

Please use GitHub [issues](https://github.com/SAP/cloud-espm-multi-environment/issues/new) for any bugs to be reported.

# License

Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved. This file is licensed under SAP Sample Code License Agreement, except as noted otherwise in the [LICENSE](/LICENSE) file.
