package com.sap.espm.model.function.impl;

import java.util.List;

import org.apache.olingo.odata2.api.exception.ODataException;

import com.sap.espm.model.SalesOrderHeader;
import com.sap.espm.model.SalesOrderItem;

public interface SalesOrderImpl  {
	public List<SalesOrderHeader> confirmSalesOrderImpl(String salesOrderId) throws ODataException;
	
	public List<SalesOrderHeader> cancelSalesOrderImpl(String salesOrderId) throws ODataException;
	public List<SalesOrderItem> getSalesOrderByIdImpl(String salesOrderId) throws ODataException;
	public List<SalesOrderHeader> getSalesOrderInvoiceByEmailImpl(String emailAddress) throws ODataException;
}
