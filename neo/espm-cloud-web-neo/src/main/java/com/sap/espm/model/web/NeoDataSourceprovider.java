package com.sap.espm.model.web;

import java.util.Properties;

import javax.inject.Singleton;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

@Singleton
public class NeoDataSourceprovider implements DataSourceProvider {
	
	@Override
	public DataSource getDatasource() throws NamingException {

		InitialContext ctx = new InitialContext();
		return (DataSource) ctx.lookup("java:comp/env/jdbc/DefaultDB");
	}

	@Override
	public Properties getProperties() {
		return new Properties();
	}

}
