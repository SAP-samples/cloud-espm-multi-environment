
# ESPM

The ESPM (Enterprise Sales & Procurement Model) application is a reference application which demonstrates how to build applications on SAP Cloud Platform with the Java runtime. This application showcases the migration from Neo to Cloud Foundry (CF) by having one code-base for running the application in both Neo and CF by using dependency injection.

# Description

## Business Scenario
The business scenario is that of an eCommerce site that sells electronic products. 

- The Customers can order products and provide reviews on the products.
- The Retailer can then accept the sales orders created against orders created by customers. The Retailer can also update the product stock information.

ESPM application has 2 underlying applications

- Webshop - this application is a webshopping app, which doesn't have any authentication
- Retailer - this application is used by a retailer to manage stocks, approve/reject sales orders. We use authentication here

![Architecture Diagram](/docs/architecture.jpg?raw=true)

# Requirements

- [Java8](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
- [Maven](https://maven.apache.org/download.cgi)
- [CF CLI](https://help.sap.com/viewer/65de2977205c403bbc107264b8eccf4b/Cloud/en-US/4ef907afb1254e8286882a2bdef0edf4.html)
- If you do not yet have a Cloud Platform trial or enterprise account, signup for a Cloud Platform environment trial account by following the [documentation](https://help.sap.com/viewer/65de2977205c403bbc107264b8eccf4b/Cloud/en-US/76e79d62fa0149d5aa7b0698c9a33687.html)

For Setting up the developer environment, Refer [Here](https://github.com/SAP/cloud-espm-v2#1-quick-start-guide)

# Download and Installation

## Build the application

From the root directory, run the following Maven command

    mvn clean install
  
This command builds the application and generates `war` file for both the `Neo` annd `CF` environment.

## Running the application in NEO environment

### 1. Deploy application

The `war` file for the NEO environment can be found at `neo/espm-cloud-web-neo/target/espm-cloud-web-neo.war`.

Open the hanatrial account cockpit and upload the war file as follows.

- Go to SAP CP Cockpit --> Click on Java Application under Applications --> Click on Deploy Application

- Add War File Location, Give Application Name "espm" ,select Runtime Name "Java Web Tomcat 8" and JVM Version "JRE 8"

- After Successful Deployment , Click on Start

### 2. Configure Roles

   - Configure the application role assignments from the [cockpit](https://help.hana.ondemand.com/help/frameset.htm?db8175b9d976101484e6fa303b108acd.html). You basically need to add the "Retailer" role to your SAP Cloud Platform user to access the Retailer UI

    You can access the application from the URL
    
    * The eCommerce site can be accessed via the URL: https://espm\<account\>.hanatrial.ondemand.com/espm-cloud-web/webshop
    * The Retailer UI can be accessed via the URL: https://espm\<account\>.hanatrial.ondemand.com/espm-cloud-web/retailer

    **Note! The application name must be "espm", else the above URL will change bsaed on the application name given during deployment**


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
	DB/Schema ID - select espm( the one that you created above)
	Click on Save button
	```

   - Now you need to restart your espm application ( stop(if already started) and start the application) from the cockpit. 

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

Replace the <DB instance name> with the service instance that you have created for the database.

#### 4. Push the application

```cf push -f manifest.yml```

#### 5. Setup Role collections

For the application to work, the Retailer application needs the template role called "Retailer" assigned to the user.

 - In your trial account, in the left pane select Role collections under the Security tab.

 - Add a new Role collection named, "Retailer"

 - Click on the newly created "Retailer "role collection and add new Role.

 - Select the application identifier name similar to the one you have given in the xs-security.json file.

 - Select the role template and assign the Role you have created in the previous step.

#### 6. Assign Role to the user

We need to assign the role which we have created in the previous step to the user.

 - In your trial account, in the left pane select "Trust Configuration" under the Security tab.

 - Click on the default IDP service.

 - Enter username/email and click on the add Assignment button.

 - Select the Role as "Retailer" to assign it to the user.
 
 # How to Obtain Support
 
In case you find a bug/need support please create github issues

# License

Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved. This file is licensed under SAP Sample Code License Agreement, except as noted otherwise in the [LICENSE](/LICENSE) file.

