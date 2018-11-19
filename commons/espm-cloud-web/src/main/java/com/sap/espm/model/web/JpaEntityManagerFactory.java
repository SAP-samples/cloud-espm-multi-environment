package com.sap.espm.model.web;

import java.io.IOException;
import java.sql.SQLException;

import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;

public interface JpaEntityManagerFactory {

	/**
	 * The JNDI name of the DataSource.
	 */
	String DATA_SOURCE_NAME = "java:comp/env/jdbc/DefaultDB";
	String dbName = System.getenv("DATABASE_TYPE");
	String PERSISTENCE_UNIT_NAME = "com.sap.espm.model";
	
	public static EntityManagerFactory entityManagerFactory = null;
	
	public static EntityManagerFactory getEntityManagerFactory() {return null;};

}