package com.sap.espm.model.function.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.olingo.odata2.api.annotation.edm.EdmFunctionImport;
import org.apache.olingo.odata2.api.annotation.edm.EdmFunctionImportParameter;
import org.apache.olingo.odata2.api.annotation.edm.EdmFunctionImport.ReturnType;
import org.apache.olingo.odata2.api.annotation.edm.EdmFunctionImport.ReturnType.Type;
import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.exception.ODataApplicationException;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.espm.model.Customer;
import com.sap.espm.model.Product;
import com.sap.espm.model.SalesOrderHeader;
import com.sap.espm.model.SalesOrderItem;
import com.sap.espm.model.util.Utility;
import com.sap.espm.model.web.Consts;
import com.sap.espm.model.web.DataSourceProvider;

public class SalesOrderProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(SalesOrderProcessor.class);

	/**
	 * Function Import implementation for confirming a sales order
	 * 
	 * @param salesOrderId
	 *            sales order id of sales order to be confirmed
	 * @return SalesOrderHeader entity
	 * @throws ODataException
	 */
	SalesOrderImpl provider = Consts.injector.getInstance(SalesOrderImpl.class);
	
	@SuppressWarnings("unchecked")
	@EdmFunctionImport(name = "ConfirmSalesOrder", entitySet = "SalesOrderHeaders", returnType = @ReturnType(type = Type.ENTITY, isCollection = true))
	public List<SalesOrderHeader> confirmSalesOrder(
			@EdmFunctionImportParameter(name = "SalesOrderId") String salesOrderId) throws ODataException {
	
		return provider.confirmSalesOrderImpl(salesOrderId);	
		
	}


	/**
	 * Function Import implementation for cancelling a sales order
	 * 
	 * @param salesOrderId
	 *            sales order id of sales order to be cancelled
	 * @return SalesOrderHeader entity
	 * @throws ODataException
	 */
	@SuppressWarnings("unchecked")
	@EdmFunctionImport(name = "CancelSalesOrder", entitySet = "SalesOrderHeaders", returnType = @ReturnType(type = Type.ENTITY, isCollection = true))
	public List<SalesOrderHeader> cancelSalesOrder(
			@EdmFunctionImportParameter(name = "SalesOrderId") String salesOrderId) throws ODataException {
		return provider.cancelSalesOrderImpl(salesOrderId);
	}

	/**
	 * Function Import implementation for getting all the Sales Order Items
	 * under a Sales Order Header
	 * 
	 * @param SalesOrderId
	 *            Sales Order Id of a Sales Order
	 * @return SalesOrderItem entity.
	 * @throws ODataException
	 */
	@SuppressWarnings("unchecked")
	@EdmFunctionImport(name = "GetSalesOrderItemsById", entitySet = "SalesOrderItems", returnType = @ReturnType(type = Type.ENTITY, isCollection = true))
	public List<SalesOrderItem> getSalesOrderById(
			@EdmFunctionImportParameter(name = "SalesOrderId") String salesOrderId) throws ODataException {
		return provider.getSalesOrderByIdImpl(salesOrderId);
	}

		
	/**
	 * Function Import implementation for getting all the Sales Order invoices
	 * by email Address under a Sales Order Header
	 * 
	 * @param emailAddreaa
	 * 
	 * @return SalesOrderHeader entity.
	 * @throws ODataException
	 */
	@SuppressWarnings("unchecked")
	@EdmFunctionImport(name = "GetSalesOrderInvoiceByEmail", entitySet = "SalesOrderHeaders", returnType = @ReturnType(type = Type.ENTITY, isCollection = true))
	public List<SalesOrderHeader> getSalesOrderInvoiceByEmail(
			@EdmFunctionImportParameter(name = "EmailAddress") String emailAddress) throws ODataException {
		
		return provider.getSalesOrderInvoiceByEmailImpl(emailAddress);

	}

}