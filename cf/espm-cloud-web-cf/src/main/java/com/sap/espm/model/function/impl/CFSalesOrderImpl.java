package com.sap.espm.model.function.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.exception.ODataApplicationException;
import org.apache.olingo.odata2.api.exception.ODataException;

import com.sap.espm.model.Customer;
import com.sap.espm.model.Product;
import com.sap.espm.model.SalesOrderHeader;
import com.sap.espm.model.SalesOrderItem;
import com.sap.espm.model.util.Utility;

public class CFSalesOrderImpl implements SalesOrderImpl {

	@Override
	public List<SalesOrderHeader> confirmSalesOrderImpl(String salesOrderId) throws ODataException {
		EntityManagerFactory emf = Utility.getEntityManagerFactory();
		EntityManager em = emf.createEntityManager();
		try {

			Query query = em.createNamedQuery("SalesOrderHeader.getSOHBySaledOrderId");
			query.setParameter("salesOrderId", salesOrderId);
			try {
				SalesOrderHeader so = (SalesOrderHeader) query.getSingleResult();
				em.getTransaction().begin();
				so.setLifeCycleStatus("P");
				so.setLifeCycleStatusName("In Process");
				em.persist(so);
				em.getTransaction().commit();
				List<SalesOrderHeader> salesorderlist = null;

				query = em.createNamedQuery("SalesOrderHeader.getSOHBySaledOrderId");
				query.setParameter("salesOrderId", salesOrderId);
				salesorderlist = query.getResultList();
				return salesorderlist;

			} catch (NoResultException e) {
				throw new ODataApplicationException("No Sales Order with Sales Order Id:" + salesOrderId,
						Locale.ENGLISH, HttpStatusCodes.BAD_REQUEST, e);
			}
		} finally {
			em.close();
		}
	}

	@Override
	public List<SalesOrderHeader> cancelSalesOrderImpl(String salesOrderId) throws ODataException {
		EntityManagerFactory emf = Utility.getEntityManagerFactory();
		EntityManager em = emf.createEntityManager();
		try {

			Query query = em.createNamedQuery("SalesOrderHeader.getSOHBySaledOrderId");
			query.setParameter("salesOrderId", salesOrderId);
			try {
				SalesOrderHeader so = (SalesOrderHeader) query.getSingleResult();
				em.getTransaction().begin();
				so.setLifeCycleStatus("X");
				so.setLifeCycleStatusName("Cancelled");
				em.persist(so);
				em.getTransaction().commit();
				List<SalesOrderHeader> salesOrderList = null;
				query = em.createNamedQuery("SalesOrderHeader.getSOHBySaledOrderId");
				query.setParameter("salesOrderId", salesOrderId);
				salesOrderList = query.getResultList();
				return salesOrderList;
			} catch (NoResultException e) {
				throw new ODataApplicationException("No Sales Order with Sales Order Id:" + salesOrderId,
						Locale.ENGLISH, HttpStatusCodes.BAD_REQUEST , e);
			}
		} finally {
			em.close();
		}
		
	}

	@Override
	public List<SalesOrderItem> getSalesOrderByIdImpl(String salesOrderId) throws ODataException {
		EntityManagerFactory emf = Utility.getEntityManagerFactory();
		EntityManager em = emf.createEntityManager();
		List<SalesOrderItem> soiList = null;
		try {

			Query query = em.createNamedQuery("SalesOrderItem.getSOIBySalesOrderItemId");
			query.setParameter("id", salesOrderId);

			try {

				soiList = query.getResultList();
				if (soiList != null && soiList.size() >= 1) {

					for (SalesOrderItem salesOrderItem : soiList) {
						query = em.createNamedQuery("Product.getProductByProductId");
						query.setParameter("productId", salesOrderItem.getProductId());
						Product product = (Product) query.getSingleResult();
						salesOrderItem.setProduct(product);

					}
				}

			} catch (NoResultException e) {
				throw new ODataApplicationException("No matching Sales Order with Sales Order Id:" + salesOrderId,
						Locale.ENGLISH, HttpStatusCodes.BAD_REQUEST , e);
			} catch(Exception e){
				throw new ODataApplicationException("PDF Report Generation Error for :" + salesOrderId,
						Locale.ENGLISH, HttpStatusCodes.INTERNAL_SERVER_ERROR , e);
			}

			return soiList;
		} finally {
			em.close();
		}
		
	}

	@Override
	public List<SalesOrderHeader> getSalesOrderInvoiceByEmailImpl(String emailAddress) throws ODataException {
		EntityManagerFactory emf = Utility.getEntityManagerFactory();
		EntityManager em = emf.createEntityManager();
		List<SalesOrderHeader> orderList = new ArrayList<>();
		List<SalesOrderHeader> salesOrderHeaderList = new ArrayList<>();
		List<SalesOrderItem> itemList = new ArrayList<>();
		try {
			Query querySOItems;
			Query queryCustomer = em.createNamedQuery("Customer.getCustomerByEmailAddress");
			queryCustomer.setParameter("emailAddress", emailAddress);
			Customer c = (Customer) queryCustomer.getSingleResult();
			String customerId = c.getCustomerId();
			Query querySOHeader = em.createNamedQuery("SalesOrderHeader.getSOHByCustomerId");
			querySOHeader.setParameter("customerId", customerId);
			orderList = querySOHeader.getResultList();
			for (SalesOrderHeader salesOrderHeader : orderList) {
				querySOItems = em.createNamedQuery("SalesOrderItem.getSOIBySalesOrderItemId");
				querySOItems.setParameter("id", salesOrderHeader.getSalesOrderId());
				itemList = querySOItems.getResultList();
				itemList = querySOItems.getResultList();
				salesOrderHeader.setSalesOrderItems(itemList);
				salesOrderHeader.setCustomer(c);
				salesOrderHeaderList.add(salesOrderHeader);

			}

		} catch (NoResultException e) {
			throw new ODataApplicationException("No Sales Order Invoices with emailId Id:......." + emailAddress,
					Locale.ENGLISH, HttpStatusCodes.BAD_REQUEST, e);
		} catch (Exception exception) {
			throw new ODataApplicationException("No Sales Order Invoices with emailId Id:" + emailAddress,
					Locale.ENGLISH, HttpStatusCodes.BAD_REQUEST, exception);
		} finally {
			em.close();
		}

		return salesOrderHeaderList;
		
	}

	

	

}
