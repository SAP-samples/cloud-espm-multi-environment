package com.sap.espm.startup;



import com.google.inject.AbstractModule;
import com.sap.espm.model.function.impl.CFSalesOrderImpl;
import com.sap.espm.model.function.impl.SalesOrderImpl;
import com.sap.espm.model.web.CFDataSourceprovider;
import com.sap.espm.model.web.DataSourceProvider;

public class CFModule extends AbstractModule {
	
	@Override
	protected void configure() {
		bind(SalesOrderImpl.class).to(CFSalesOrderImpl.class);
		bind(DataSourceProvider.class).to(CFDataSourceprovider.class);
	}

}
