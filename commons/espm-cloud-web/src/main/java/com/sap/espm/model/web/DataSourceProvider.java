package com.sap.espm.model.web;

import java.io.IOException;
import java.util.Properties;

import javax.naming.NamingException;
import javax.sql.DataSource;

public interface DataSourceProvider {
	public DataSource getDatasource() throws NamingException;

	public Properties getProperties();
}

