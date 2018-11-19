package com.sap.espm.startup;



import com.google.inject.AbstractModule;
import com.sap.espm.model.function.impl.NeoSalesOrderImpl;
import com.sap.espm.model.function.impl.SalesOrderImpl;
import com.sap.espm.model.web.DataSourceProvider;
import com.sap.espm.model.web.NeoDataSourceprovider;

public class NeoModule extends AbstractModule {
	
	@Override
	protected void configure() {
		bind(SalesOrderImpl.class).to(NeoSalesOrderImpl.class);
		bind(DataSourceProvider.class).to(NeoDataSourceprovider.class);
	}

}
