package com.sap.espm.model.web;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sql.DataSource;

/*import org.apache.commons.dbcp2.BasicDataSource;*/
import org.eclipse.persistence.config.PersistenceUnitProperties;

import com.sap.espm.model.util.Utility;

public class PersistenceAdapter {
	
	private static EntityManagerFactory entityManagerFactory;
	
	public static final String PERSISTENCE_UNIT_NAME = "com.sap.espm.model";
	
	public static synchronized EntityManagerFactory getEntityManagerFactory()
			throws NamingException, SQLException, IOException {
		
		if(entityManagerFactory == null) {
			DataSourceProvider provider = Consts.injector.getInstance(DataSourceProvider.class);
			Properties properties = provider.getProperties();
			
			DataSource ds = provider.getDatasource();
			
			if (ds != null) {
				String productName = ds.getConnection().getMetaData().getDatabaseProductName();
				

				properties.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, ds);
			}
			
			entityManagerFactory = Persistence.createEntityManagerFactory(
					PERSISTENCE_UNIT_NAME, properties);
			Utility.setEntityManagerFactory(entityManagerFactory);
		}
		return entityManagerFactory;
	}
	

}
