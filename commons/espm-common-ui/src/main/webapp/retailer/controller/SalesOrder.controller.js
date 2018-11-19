jQuery.sap.require("com.sap.espm.retailer.model.format");
sap.ui.define([
	"sap/ui/core/mvc/Controller",
	"com/sap/espm/retailer/model/formatter",
	"sap/m/MessageBox",
	"sap/m/MessageToast"
	
	
], function(Controller, formatter, MessageBox, MessageToast) {
	"use strict";

	var responseData;
	
	return Controller.extend("com.sap.espm.retailer.controller.SalesOrder", {

		formatter: formatter,
		
		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.sap.espm.retailer.view.SalesOrder
		 */
			onInit: function() {
				
				var deviceModel = new sap.ui.model.json.JSONModel({
 				    isPhone: sap.ui.Device.system.phone
 				});
 				this.getView().setModel(deviceModel, "device");
		
			},

		/**
		 * Similar to onAfterRendering, but this hook is invoked before the controller's View is re-rendered
		 * (NOT before the first rendering! onInit() is used for that one!).
		 * @memberOf com.sap.espm.retailer.view.SalesOrder
		 */
		//	onBeforeRendering: function() {
		//
		//	},

		/**
		 * Called when the View has been rendered (so its HTML is part of the document). Post-rendering manipulations of the HTML could be done here.
		 * This hook is the same one that SAPUI5 controls get after being rendered.
		 * @memberOf com.sap.espm.retailer.view.SalesOrder
		 */
			onAfterRendering: function() {
				
			},

		/**
		 * Called when the Controller is destroyed. Use this one to free resources and finalize activities.
		 * @memberOf com.sap.espm.retailer.view.SalesOrder
		 */
		//	onExit: function() {
		//
		//	}
		
		listUpdateFinished: function(){
			
			this.getView().byId("detailPageId").setVisible(false);  
		},
		
		handleListItemPress: function(event){
			
			this.getView().byId("detailPageId").setVisible(true);
			
			var context = event.getSource().getBindingContextPath();
			
			this.getView().byId("detailObjectHeader").bindElement(context);
			
			var objectStatus = new sap.m.ObjectStatus({
				text : event.getSource().getFirstStatus().getText()
				//state:data.results[0].LifeCycleStatus//"{ path: 'LifeCycleStatus', formatter: 'com.sap.espm.retailer.model.format.statusState' }"
			});
			
			this.getView().byId("detailObjectHeader").setFirstStatus(objectStatus);
			
			this.getView().byId("customerForm").bindElement(context+"/Customer");
			
			var oTemplate = new sap.m.ColumnListItem({
				cells:[
					new sap.m.ObjectIdentifier({
						title : "{ProductId}"
					}),
					new sap.m.Text({
						text : "{path: 'DeliveryDate', formatter: 'com.sap.espm.retailer.model.format.date'}"
					}),
					new sap.m.Text({
						text : "{path: 'Quantity', formatter: 'com.sap.espm.retailer.model.format.quantity'}"
					}),
					new sap.m.ObjectNumber({
						emphasized : false,
						number : "{path: 'GrossAmount', formatter:'com.sap.espm.retailer.model.format.formatAmount'}",
						unit : "{CurrencyCode}"
					})
					]
			});
			
			var oTable = this.getView().byId("lineItemsId");
			var bindString = context + "/SalesOrderItems";
			oTable.bindItems(bindString, oTemplate);
			
			if(this.getView().getModel("device").oData.isPhone){
 				this.byId("splitContId").to(this.byId("detailPageId"));
 			}
			
		},
		
		handleSearch : function (evt) 
		{ // create model filter 
			
			var filters = []; 
			var query = evt.getParameter("query"); 
			if (query && query.length > 0) { 
				var filter = new sap.ui.model.Filter("SalesOrderId", sap.ui.model.FilterOperator.Contains, query); 
				filters.push(filter); } // update list binding 
			var list = this.getView().byId("list"); 
			var binding = list.getBinding("items"); 
			binding.filter(filters); 
			
		},
		handleApprove: function(){
			
			var bundle = this.getView().getModel("i18n").getResourceBundle(); 
			var that = this;
			
			MessageBox.confirm( bundle.getText("sales.approveDialogMsg"), 
					function (oAction) { 
						if (MessageBox.Action.OK === oAction) { 
						// notify user 
							
							var id = that.getView().byId("detailObjectHeader").getTitle();
							var sFilterString = "SalesOrderId='" + id + "'";
							var aParams = [];
							aParams.push(sFilterString);
							
							var oDataModel = that.getView().getModel("espmRetailerModel");
							oDataModel.setHeaders({  
				            "Content-Type": "application/json",
				            "Accept": "application/json"
				        	}); 
							
							oDataModel.read("ConfirmSalesOrder", null, aParams, false, function(data){
								
								responseData =data;
								
								var oComponent = that.getOwnerComponent();
								var model = oComponent.getModel("espmRetailerModel");
								model.refresh();
								
								var successMsg = bundle.getText("sales.aproveDialogSuccessMsg"); 
								MessageToast.show(successMsg);
							 	},
							 	function()
							 	{
							 		MessageToast.show(bundle.getText("sales.approvalFailed"));
							 	});
						} 
						}, 
						bundle.getText("sales.approveDialogTitle") ); 
			
					
		},
		
		handleReject: function(){
			
			var bundle = this.getView().getModel("i18n").getResourceBundle(); 
			var that = this;
			MessageBox.confirm( bundle.getText("sales.rejectDialogMsg"), 
					function (oAction) { 
						if (MessageBox.Action.OK === oAction) { 
						// notify user 
							
							var id = that.getView().byId("detailObjectHeader").getTitle();
							var sFilterString = "SalesOrderId='" + id + "'";
							var aParams = [];
							aParams.push(sFilterString);
							
							var oDataModel = that.getView().getModel("espmRetailerModel");
							oDataModel.setHeaders({  
				            "Content-Type": "application/json",
				            "Accept": "application/json"
				        	}); 
							
							oDataModel.read("CancelSalesOrder", null, aParams, false, function(data){

								responseData =data;
								var oComponent = that.getOwnerComponent();
								var model = oComponent.getModel("espmRetailerModel");
								model.refresh();
								
								var successMsg = bundle.getText("sales.rejectDialogSuccessMsg"); 
								MessageToast.show(successMsg);
							 	},function(){
							 		MessageToast.show(bundle.getText("sales.rejectFailed"));});
						} 
						}, 
						bundle.getText("sales.rejectDialogTitle") ); 
			
					
		},
		onNavBack: function(){
			window.history.go(-1);
		},
		
		handleNavButtonPress: function(){
     		var oSplitCont = this.byId("splitContId");
     		var oMaster = oSplitCont.getMasterPages()[0];
     		oSplitCont.toMaster(oMaster);
  		}

	});

});