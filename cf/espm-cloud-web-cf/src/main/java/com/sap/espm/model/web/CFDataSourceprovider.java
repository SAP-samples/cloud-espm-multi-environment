package com.sap.espm.model.web;

import java.io.IOException;
import java.util.Properties;

import javax.inject.Singleton;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

//import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Singleton
public class CFDataSourceprovider implements DataSourceProvider {
	
	public static final String dbName = System.getenv("DATABASE_TYPE");
	

	@Override
	public DataSource getDatasource() throws NamingException {
		
		JsonNode credentials;
		credentials = readCredentialsFromEnvironment();
		InitialContext ctx = new InitialContext();
		
		DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/DefaultDB");
		
		if (!(ds instanceof BasicDataSource)) {
			throw new IllegalArgumentException(
					"The data source is not an instance of type " + BasicDataSource.class.getName());
		}
		BasicDataSource bds = (BasicDataSource) ds;
		
		if(dbName.equals("postgresql")){
			bds.setDriverClassName("org.postgresql.Driver");
			bds.setUrl(buildJdbcUrl(credentials));
			bds.setUsername(credentials.get("username").asText());
		    }
		    else{
		    	bds.setDriverClassName(credentials.get("driver").asText());
		    	bds.setUrl(credentials.get("url").asText());
		    	bds.setUsername(credentials.get("user").asText());
		    }
		bds.setPassword(credentials.get("password").asText());
		
		return bds;
	}

	 private static JsonNode readCredentialsFromEnvironment() {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode actualObj = null;
			try {
				actualObj = mapper.readTree(System.getenv("VCAP_SERVICES"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return actualObj.get(dbName).get(0).get("credentials");
	 }

	@Override
	public Properties getProperties() {
		return new Properties();
	}
	
	public static String buildJdbcUrl(JsonNode credentials) {
		return String.format("%s%s://%s:%s/%s?user=%s&password=%s",
	            "jdbc:", "postgresql", credentials.get("hostname").asText(),
				credentials.get("port").asText(), credentials.get("dbname").asText(), credentials.get("username").asText(),credentials.get("password").asText());
	}

}
