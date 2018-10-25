package com.inventory.dao;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.config.acct.model.BankAccountDepositModel;
import com.inventory.config.acct.model.BankAccountModel;
import com.inventory.config.acct.model.BankAccountPaymentModel;
import com.inventory.config.acct.model.CashAccountDepositModel;
import com.inventory.config.acct.model.CashAccountPaymentModel;
import com.inventory.config.acct.model.ChequeReturnModel;
import com.inventory.config.acct.model.ClearingAgentModel;
import com.inventory.config.acct.model.CreditNoteModel;
import com.inventory.config.acct.model.CustomerModel;
import com.inventory.config.acct.model.DebitCreditInvoiceMapModel;
import com.inventory.config.acct.model.DebitNoteModel;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.acct.model.PdcModel;
import com.inventory.config.acct.model.SupplierModel;
import com.inventory.config.stock.model.BatchModel;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.config.stock.model.ManufacturingMapModel;
import com.inventory.config.stock.model.ManufacturingModel;
import com.inventory.config.stock.model.StockTransferModel;
import com.inventory.journal.model.JournalModel;
import com.inventory.management.model.TasksModel;
import com.inventory.model.DocumentAccessModel;
import com.inventory.model.RackModel;
import com.inventory.payment.model.EmployeeAdvancePaymentModel;
import com.inventory.payroll.model.EmployeeWorkingTimeModel;
import com.inventory.payroll.model.LeaveModel;
import com.inventory.payroll.model.LoanApprovalModel;
import com.inventory.payroll.model.LoanRequestModel;
import com.inventory.payroll.model.PayrollComponentModel;
import com.inventory.payroll.model.PayrollEmployeeMapModel;
import com.inventory.payroll.model.SalaryDisbursalModel;
import com.inventory.purchase.model.ItemStockModel;
import com.inventory.purchase.model.PurchaseGRNDetailsModel;
import com.inventory.purchase.model.PurchaseGRNModel;
import com.inventory.purchase.model.PurchaseInquiryModel;
import com.inventory.purchase.model.PurchaseInventoryDetailsModel;
import com.inventory.purchase.model.PurchaseModel;
import com.inventory.purchase.model.PurchaseOrderModel;
import com.inventory.purchase.model.PurchaseQuotationModel;
import com.inventory.purchase.model.PurchaseReturnInventoryDetailsModel;
import com.inventory.purchase.model.PurchaseReturnModel;
import com.inventory.purchase.model.StockCreateDetailsModel;
import com.inventory.purchase.model.StockCreateModel;
import com.inventory.purchase.model.StockRackMappingModel;
import com.inventory.sales.model.DeliveryNoteDetailsModel;
import com.inventory.sales.model.DeliveryNoteModel;
import com.inventory.sales.model.GrvSalesInventoryDetailsModel;
import com.inventory.sales.model.GrvSalesModel;
import com.inventory.sales.model.PaymentInvoiceMapModel;
import com.inventory.sales.model.QuotationModel;
import com.inventory.sales.model.SalesInquiryModel;
import com.inventory.sales.model.SalesInventoryDetailsModel;
import com.inventory.sales.model.SalesModel;
import com.inventory.sales.model.SalesOrderModel;
import com.inventory.sales.model.SalesReturnInventoryDetailsModel;
import com.inventory.sales.model.SalesReturnModel;
import com.inventory.transaction.model.TransactionDetailsModel;
import com.inventory.transaction.model.TransactionModel;
import com.vaadin.server.VaadinServlet;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;
import com.webspark.uac.model.EmployeeDocumentModel;
import com.webspark.uac.model.UserModel;

/**
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Nov 18, 2013
 */
public class DeleteDao extends SHibernate implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1172252762610392737L;
	CommonMethodsDao comDao = new CommonMethodsDao();

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean delete(List selected, long officeId) throws Exception {

		try {
			begin();
			List idList = null;
			List childList = null;
			Iterator commonItr = null;
			int type = 0;

			for (int i = 0; i < selected.size(); i++) {

				type = (Integer) selected.get(i);

				switch (type) {
				case SConstants.deleteObjects.SALES_ENQUIREY:
					idList=getSession().createQuery("from SalesInquiryModel where office.id=:offc").setParameter("offc",officeId).list();
					commonItr=idList.iterator();
					SalesInquiryModel salesinq;
					while(commonItr.hasNext()){
						salesinq= (SalesInquiryModel) commonItr.next();
						getSession().delete(salesinq);
						
					}
					break;
				case SConstants.deleteObjects.SALES_QUOTATION:
					idList=getSession().createQuery("from QuotationModel where office.id=:offc").setParameter("offc", officeId).list();
					commonItr=idList.iterator();
					QuotationModel qutnmdl;
					while(commonItr.hasNext()){
						qutnmdl=(QuotationModel)commonItr.next();
						getSession().delete(qutnmdl);
						
					}
					break;
				case SConstants.deleteObjects.SALES_ORDER:
					idList=getSession().createQuery("from SalesOrderModel where office.id=:offc").setParameter("offc", officeId).list();
					commonItr=idList.iterator();
					SalesOrderModel salesorder;
					while(commonItr.hasNext()){
						salesorder=(SalesOrderModel) commonItr.next();
						getSession().delete(salesorder);
					}
					break;
					
				case SConstants.deleteObjects.DELIVERY_NOTE:
					idList=getSession().createQuery("from DeliveryNoteModel where office.id=:offc").setParameter("offc", officeId).list();
					commonItr=idList.iterator();
					DeliveryNoteModel dlvrymdl;
					while(commonItr.hasNext()){
						dlvrymdl=(DeliveryNoteModel) commonItr.next();
						Iterator itr=dlvrymdl.getDelivery_note_details_list().iterator();
						List<Long> orderList=new ArrayList<Long>();
						while (itr.hasNext()) {
							DeliveryNoteDetailsModel det = (DeliveryNoteDetailsModel) itr.next();
							
							// Update Sales Order Child
							if(det.getOrder_child_id()!=0) {
								getSession().createQuery("update SalesOrderDetailsModel set quantity_sold=quantity_sold-:qty where id=:id")
											.setParameter("id", det.getOrder_child_id()).setParameter("qty", det.getQty_in_basic_unit()).executeUpdate();
							}
							flush();
							
							// Update Sales Order Parent
							if(det.getOrder_id()!=0){
								if(!orderList.contains(det.getOrder_id())) {
									orderList.add(det.getOrder_id());
									getSession().createQuery("update SalesOrderModel a set a.lock_count=(a.lock_count-1) where a.id=:id")
												.setParameter("id", det.getOrder_id()).executeUpdate();
								}
							}
							flush();
							
							// Update Item Balance
							getSession().createQuery("update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
										.setParameter("id", det.getItem().getId()).setParameter("qty", det.getQty_in_basic_unit()).executeUpdate();
							flush();
							
							comDao.increaseStockByStockID(det.getStock_id(),  det.getQty_in_basic_unit());
						}
						getSession().delete(dlvrymdl);
						flush();
						getSession().createQuery("delete from SalesStockMapModel where salesId=:id and type=2").setLong("id", dlvrymdl.getId()).executeUpdate();
						flush();
					
					}
				
					break;
					
				case SConstants.deleteObjects.SALES:
					idList=getSession().createQuery("from SalesModel where office.id=:offc").setParameter("offc", officeId).list();
					commonItr=idList.iterator();
					SalesModel salesmdl;

					List<Long> orderList=new ArrayList<Long>();
					List<Long> deliveryList=new ArrayList<Long>(); 
					while(commonItr.hasNext()){
						salesmdl=(SalesModel) commonItr.next();
						// Transaction Related

						TransactionModel transObj = (TransactionModel) getSession().get(TransactionModel.class, salesmdl.getTransaction_id());

						TransactionDetailsModel tr;
						Iterator<TransactionDetailsModel> aciter = transObj
								.getTransaction_details_list().iterator();
						while (aciter.hasNext()) {
							tr = aciter.next();

							getSession().createQuery("update LedgerModel set current_balance=current_balance+:amt where id=:id")
									.setDouble("amt", tr.getAmount()).setLong("id", tr.getFromAcct().getId()).executeUpdate();

							getSession().createQuery("update LedgerModel set current_balance=current_balance-:amt where id=:id")
									.setDouble("amt", tr.getAmount()).setLong("id", tr.getToAcct().getId()).executeUpdate();

							flush();
						}

						getSession().delete(transObj);
						
						flush();

						Iterator<SalesInventoryDetailsModel> it = salesmdl.getInventory_details_list().iterator();
						while (it.hasNext()) {
							SalesInventoryDetailsModel det = it.next();

							if(det.getDelivery_id()!=0){
								
								if(det.getDelivery_id()!=0){	
									if(!deliveryList.contains(det.getDelivery_id())) {
										deliveryList.add(det.getDelivery_id());
										getSession().createQuery("update DeliveryNoteModel a set a.lock_count=(a.lock_count-1) where a.id=:id")
													.setParameter("id", det.getDelivery_id()).executeUpdate();
										flush();
									}
								}
								
								if(det.getDelivery_child_id()!=0) {	
									getSession().createQuery("update DeliveryNoteDetailsModel a set a.lock_count=(a.lock_count-1) where a.id=:id").setParameter("id", det.getDelivery_child_id()).executeUpdate();
									flush();
								}
								
							}
							else{
								
								if(det.getOrder_id()!=0){
									if(!orderList.contains(det.getOrder_id())) {
										orderList.add(det.getOrder_id());
										getSession().createQuery("update SalesOrderModel a set a.lock_count=(a.lock_count-1) where a.id=:id")
													.setParameter("id", det.getOrder_id()).executeUpdate();
										flush();
									}
								}	
									
								if(det.getOrder_child_id()!=0) {
									getSession().createQuery("update SalesOrderDetailsModel set quantity_sold=quantity_sold-:qty where id=:id")
												.setParameter("id", det.getOrder_child_id()).setParameter("qty", det.getQuantity_in_basic_unit()).executeUpdate();
									flush();
								}
								
								getSession().createQuery("update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
											.setParameter("id", det.getItem().getId()).setParameter("qty", det.getQuantity_in_basic_unit()).executeUpdate();
				
								comDao.increaseStockByStockID(det.getStock_id(), det.getQuantity_in_basic_unit());
					
								flush();
						getSession().delete(salesmdl);
							}
							}	
					}
					break;
						
				case SConstants.deleteObjects.SALES_RETURN:
					idList=getSession().createQuery("from SalesReturnModel where office.id=:offc").setParameter("offc", officeId).list();
					commonItr=idList.iterator();
					SalesReturnModel salesrtn;
					List<Long> salesList=new ArrayList<Long>(); 
					                                             
					while(commonItr.hasNext()){
						
						salesrtn=(SalesReturnModel) commonItr.next();
						
						TransactionModel transObj = (TransactionModel) getSession().get(TransactionModel.class, salesrtn.getTransaction_id());
						
						Iterator<TransactionDetailsModel> aciter = transObj.getTransaction_details_list().iterator();
						while (aciter.hasNext()) {
						
							TransactionDetailsModel tr = aciter.next();
							getSession().createQuery("update LedgerModel set current_balance=current_balance+:amt where id=:id")
									.setDouble("amt", tr.getAmount()).setLong("id", tr.getFromAcct().getId()).executeUpdate();

							getSession().createQuery("update LedgerModel set current_balance=current_balance-:amt where id=:id")
									.setDouble("amt", tr.getAmount()).setLong("id", tr.getToAcct().getId()).executeUpdate();

							flush();
						}

						getSession().delete(transObj);
						
						flush();
						
						Iterator itr=salesrtn.getInventory_details_list().iterator();
						
						while (itr.hasNext()) {
							SalesReturnInventoryDetailsModel det = (SalesReturnInventoryDetailsModel) itr.next();
							
							if(det.getSales_id()!=0){
								
								if(det.getSales_id()!=0){
									if(!salesList.contains(det.getSales_id())) {
										salesList.add(det.getSales_id());
										getSession().createQuery("update SalesModel a set a.lock_count=(a.lock_count-1) where a.id=:id")
												.setParameter("id", det.getSales_id()).executeUpdate();
										flush();
									}
								}
								
								if(det.getSales_child_id()!=0) {
									getSession().createQuery("update SalesInventoryDetailsModel a set a.lock_count=(a.lock_count-1), " +
															" a.quantity_returned=(a.quantity_returned-:qty) where a.id=:id")
												.setParameter("id", det.getSales_child_id()).setParameter("qty", det.getQuantity_in_basic_unit()).executeUpdate();
									flush();
								}
								
							}
							
							if(det.getStock_id()!=0){
								ItemStockModel  stck=(ItemStockModel)getSession().get(ItemStockModel.class, det.getStock_id());
								getSession().delete(stck);
								flush();
							}
							
							getSession().createQuery("update ItemModel set current_balalnce=current_balalnce-:qty where id=:id")
										.setParameter("id", det.getItem().getId()).setParameter("qty", det.getQuantity_in_basic_unit()).executeUpdate();
							flush();
						}
						getSession().delete(salesrtn);
						flush();
					} 
			
					break;
					
			case SConstants.deleteObjects.GRV_SALES:
				idList=getSession().createQuery("from GrvSalesModel where office.id=:offc").setParameter("offc", officeId).list();
				GrvSalesModel grvmdl;
				commonItr=idList.iterator();
				while(commonItr.hasNext()){
					grvmdl=(GrvSalesModel) commonItr.next();
				
					GrvSalesInventoryDetailsModel det;
					Iterator<GrvSalesInventoryDetailsModel> it = grvmdl.getInventory_details_list().iterator();
					while (it.hasNext()) {
						det = it.next();

						getSession().createQuery("update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
									.setParameter("id", det.getItem().getId()).setParameter("qty", det.getQuantity_in_basic_unit()).executeUpdate();

						comDao.increaseStockByStockID(det.getStock_id(), det.getQuantity_in_basic_unit());
					
						flush();
						
					}
					getSession().createQuery("update GrvSalesModel set active=false where id=:id").setParameter("id", grvmdl.getId()).executeUpdate();
					getSession().createQuery("delete from SalesStockMapModel where salesId=:id and type=1").setLong("id", grvmdl.getId()).executeUpdate();
					flush();
					commit();
					
					
					
					
					getSession().delete(grvmdl);
				}
				break;
				
			case SConstants.deleteObjects.PURCHASE_INQUIRY:
				idList=getSession().createQuery("from PurchaseInquiryModel where office.id=:offc").setParameter("offc",officeId).list();
				commonItr=idList.iterator();
				PurchaseInquiryModel purchseinqmdl;
				while(commonItr.hasNext()){
				purchseinqmdl=(PurchaseInquiryModel) commonItr.next();
				getSession().delete(purchseinqmdl);
				}
				break;
				
			case SConstants.deleteObjects.PURCHASE_QUOTATION:
					idList=getSession().createQuery("from PurchaseQuotationModel where office.id=:offc").setParameter("offc",officeId).list();
					commonItr=idList.iterator();
					PurchaseQuotationModel purchaseqtnmdl;
					while(commonItr.hasNext()){
					purchaseqtnmdl= (PurchaseQuotationModel)commonItr.next();
					getSession().delete(purchaseqtnmdl);
						
					}
					break;
					
			case SConstants.deleteObjects.PURCHASE_ORDER:
				idList=getSession().createQuery("from PurchaseOrderModel where office.id=:offc").setParameter("offc",officeId).list();
				PurchaseOrderModel prchaseordermdl;
				commonItr=idList.iterator();
				while(commonItr.hasNext()){
					prchaseordermdl=(PurchaseOrderModel) commonItr.next();
					getSession().delete(prchaseordermdl);
				
				}
				break;
				
			case SConstants.deleteObjects.GRN:
				idList=getSession().createQuery("from PurchaseGRNModel where office.id=:offc").setParameter("offc",officeId).list();
				commonItr=idList.iterator();
				PurchaseGRNModel grnmdl;
				List<Long> orderList1=new ArrayList<Long>();
				while(commonItr.hasNext()){
					
					grnmdl=(PurchaseGRNModel)commonItr.next();	
					Iterator itr=grnmdl.getGrn_details_list().iterator();
					while (itr.hasNext()) {
						PurchaseGRNDetailsModel det = (PurchaseGRNDetailsModel) itr.next();
					
						// Update Purchase Order Child
						if(det.getOrder_child_id()!=0) {
							getSession().createQuery("update PurchaseOrderDetailsModel set quantity_received=quantity_received-:qty where id=:id")
										.setParameter("id", det.getOrder_child_id()).setParameter("qty", det.getQty_in_basic_unit()).executeUpdate();
						}
						flush();
						
						// Update Purchase Order Parent
						if(det.getOrder_id()!=0){
							if(!orderList1.contains(det.getOrder_id())) {
								orderList1.add(det.getOrder_id());
								getSession().createQuery("update PurchaseOrderModel a set a.lock_count=(a.lock_count-1) where a.id=:id")
											.setParameter("id", det.getOrder_id()).executeUpdate();
							}
						}
						flush();
						
						// Update Item Balance
						getSession().createQuery("update ItemModel set current_balalnce=current_balalnce-:qty where id=:id")
									.setParameter("id", det.getItem().getId()).setParameter("qty", det.getQty_in_basic_unit()).executeUpdate();
						flush();
						
						if(det.getStock_id()!=0){
							ItemStockModel  stck=(ItemStockModel)getSession().get(ItemStockModel.class, det.getStock_id());
							getSession().delete(stck);
						}
						flush();
						
						if(det.getBatch_id()!=0){
							BatchModel  batch=(BatchModel)getSession().get(BatchModel.class, det.getBatch_id());
							getSession().delete(batch);
						}
						flush();
						
					}
					getSession().delete(grnmdl);
				}
				break;	
				
			case SConstants.deleteObjects.PURCHASE:
				
				idList=getSession().createQuery("from PurchaseModel where office.id=:offc").setParameter("offc",officeId).list();
				commonItr=idList.iterator();
				PurchaseModel pmdl;
				List<Long> orderList2=new ArrayList<Long>();
				List<Long> grnList=new ArrayList<Long>(); 
			
				while(commonItr.hasNext()){
					pmdl=(PurchaseModel) commonItr.next();
					TransactionModel transaction=(TransactionModel)getSession().get(TransactionModel.class, pmdl.getTransaction_id());
					
					if(transaction!=null){
						Iterator transItr=transaction.getTransaction_details_list().iterator();
					while (transItr.hasNext()) {
						
						TransactionDetailsModel tdm = (TransactionDetailsModel) transItr.next();
						
						getSession().createQuery("update LedgerModel set current_balance=current_balance+:amt where id=:id")
									.setDouble("amt", tdm.getAmount()).setLong("id", tdm.getFromAcct().getId()).executeUpdate();

						getSession().createQuery("update LedgerModel set current_balance=current_balance-:amt where id=:id")
									.setDouble("amt", tdm.getAmount()).setLong("id", tdm.getToAcct().getId()).executeUpdate();

						flush();
						
					}
					
					getSession().delete(transaction);
				}
				
				
				flush();
				
				Iterator itr=pmdl.getPurchase_details_list().iterator();
				
				while (itr.hasNext()) {
					PurchaseInventoryDetailsModel det = (PurchaseInventoryDetailsModel) itr.next();
					
					if(det.getGrn_id()!=0){
						
						// Update Purchase GRN Parent
						if(det.getGrn_id()!=0){	
							if(!grnList.contains(det.getGrn_id())) {
								grnList.add(det.getGrn_id());
								getSession().createQuery("update PurchaseGRNModel a set a.lock_count=(a.lock_count-1) where a.id=:id")
											.setParameter("id", det.getGrn_id()).executeUpdate();
								flush();
							}
						}
						
						// Update Purchase GRN Child
						if(det.getGrn_child_id()!=0) {	
							getSession().createQuery("update PurchaseGRNDetailsModel a set a.lock_count=(a.lock_count-1) where a.id=:id")
										.setParameter("id", det.getGrn_child_id()).executeUpdate();
							flush();
						}
						
					}
					else{
						
						// Update Purchase Order Parent
						if(det.getOrder_id()!=0){
							if(!orderList2.contains(det.getOrder_id())) {
								orderList2.add(det.getOrder_id());
								getSession().createQuery("update PurchaseOrderModel a set a.lock_count=(a.lock_count-1) where a.id=:id")
											.setParameter("id", det.getOrder_id()).executeUpdate();
								flush();
							}
						}
						
						// Update Purchase Order Child
						if(det.getOrder_child_id()!=0) {
							getSession().createQuery("update PurchaseOrderDetailsModel set quantity_received=quantity_received-:qty where id=:id")
										.setParameter("id", det.getOrder_child_id()).setParameter("qty", det.getQty_in_basic_unit()).executeUpdate();
							flush();
						}
						
					}
					
					if(det.getGrn_id()==0){
						
						if(det.getStock_id()!=0){
							ItemStockModel  stck=(ItemStockModel)getSession().get(ItemStockModel.class, det.getStock_id());
							getSession().delete(stck);
							flush();
						}
						
						if(det.getBatch_id()!=0){
							BatchModel  batch=(BatchModel)getSession().get(BatchModel.class, det.getBatch_id());
							getSession().delete(batch);
							flush();
						}
						
						// Update Item Balance
						getSession().createQuery("update ItemModel set current_balalnce=current_balalnce-:qty where id=:id")
									.setParameter("id", det.getItem().getId()).setParameter("qty", det.getQty_in_basic_unit()).executeUpdate();
						flush();
					}
				}
				getSession().delete(pmdl);
					
				}
				
			break;
			case SConstants.deleteObjects.STOCK_CREATE:
				idList=getSession().createQuery("from StockCreateModel where office.id=:offc").setParameter("offc",officeId).list();
				commonItr=idList.iterator();
				StockCreateModel stckcrtemdl;
				commonItr=idList.iterator();
				while(commonItr.hasNext()){
					stckcrtemdl=(StockCreateModel) commonItr.next();
					Iterator itr = stckcrtemdl.getInventory_details_list().iterator();
					while (itr.hasNext()) {
						
						StockCreateDetailsModel det = (StockCreateDetailsModel)itr.next();
						
						getSession().createQuery("update ItemModel set current_balalnce=current_balalnce-:qty where id=:id")
									.setParameter("id", det.getItem().getId()).setParameter("qty", det.getQty_in_basic_unit()).executeUpdate();
						
						flush();
						
						if(det.getStock_id()!=0){
							ItemStockModel  stck=(ItemStockModel)getSession().get(ItemStockModel.class, det.getStock_id());
							getSession().delete(stck);
							flush();
						}
						
						if(det.getBatch_id()!=0){
							BatchModel  batch=(BatchModel)getSession().get(BatchModel.class, det.getBatch_id());
							getSession().delete(batch);
							flush();
						}
					}
						
					
					
			getSession().delete(stckcrtemdl);
				}
				break;
			case SConstants.deleteObjects.MANUFACTURING:
				
				idList=getSession().createQuery("from ManufacturingModel where office.id=:offc").setParameter("offc",officeId).list();
				
				commonItr=idList.iterator();
				ManufacturingModel manumdl;
				
				while(commonItr.hasNext()){
					manumdl=(ManufacturingModel) commonItr.next();
					getSession().delete(manumdl);
					}
				break;
			    case SConstants.deleteObjects.MANUFACTURINGMAP:
				idList=getSession().createQuery("from ManufacturingMapModel where item.office.id=:offc").setParameter("offc",officeId).list();	
				commonItr=idList.iterator();
				ManufacturingMapModel manumapmdl;
				while(commonItr.hasNext()){
					manumapmdl=(ManufacturingMapModel) commonItr.next();
					getSession().delete(manumapmdl);
					flush();
					}
				break;
				
			case SConstants.deleteObjects.PURCHASE_RETURN:
				
				idList=getSession().createQuery("from PurchaseReturnModel where office.id=:offc").setParameter("offc",officeId).list();
				commonItr=idList.iterator();
				PurchaseReturnModel prmdl;
				List<Long> purchaseList=new ArrayList<Long>();
				while(commonItr.hasNext()){
				prmdl=(PurchaseReturnModel) commonItr.next();
				
				TransactionModel transaction=(TransactionModel)getSession().get(TransactionModel.class, prmdl.getTransaction_id());
				
				if(transaction!=null){
					Iterator transItr=transaction.getTransaction_details_list().iterator();
				while (transItr.hasNext()) {
					TransactionDetailsModel tdm = (TransactionDetailsModel) transItr.next();
					
					getSession().createQuery("update LedgerModel set current_balance=current_balance+:amt where id=:id")
								.setDouble("amt", tdm.getAmount()).setLong("id", tdm.getFromAcct().getId()).executeUpdate();

					getSession().createQuery("update LedgerModel set current_balance=current_balance-:amt where id=:id")
								.setDouble("amt", tdm.getAmount()).setLong("id", tdm.getToAcct().getId()).executeUpdate();

					flush();
					
				}
				getSession().delete(transaction);
			}
			
			flush();
			
			Iterator itr=prmdl.getInventory_details_list().iterator();
			
			while (itr.hasNext()) {
				PurchaseReturnInventoryDetailsModel det = (PurchaseReturnInventoryDetailsModel) itr.next();
				
				if(det.getPurchase_id()!=0){
					
					if(!purchaseList.contains(det.getPurchase_id())) {
						purchaseList.add(det.getPurchase_id());
						getSession().createQuery("update PurchaseModel a set a.lock_count=(a.lock_count-1) where a.id=:id")
									.setParameter("id", det.getPurchase_id()).executeUpdate();
						flush();
					}
					
					if(det.getPurchase_child_id()!=0) {
						getSession().createQuery("update PurchaseInventoryDetailsModel a set a.lock_count=(a.lock_count-1) where a.id=:id")
									.setParameter("id", det.getPurchase_child_id()).executeUpdate();
						flush();
					}
					
				}
				
				if(det.getStock_id()!=0)
					new CommonMethodsDao().increaseStockByStockID(det.getStock_id(), CommonUtil.roundNumber(det.getQty_in_basic_unit()), false);
				
				// Update Item Balance
				getSession().createQuery("update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
							.setParameter("id", det.getItem().getId()).setParameter("qty", det.getQty_in_basic_unit()).executeUpdate();
				flush();
				
			}
			getSession().delete(prmdl);
				}
				break;
				
			case SConstants.deleteObjects.BANK_ACCOUNT_PAYMENTS:
				idList=getSession().createQuery("from BankAccountPaymentModel where office_id=:offc").setParameter("offc", officeId).list();
				commonItr=idList.iterator();
				BankAccountPaymentModel bankaccntmdl;
				while(commonItr.hasNext()){
					bankaccntmdl=(BankAccountPaymentModel) commonItr.next();
					getSession().delete(bankaccntmdl);
				}
				break;
				
			case SConstants.deleteObjects.BANK_ACCOUNT_DEPOSITS:
				idList=getSession().createQuery("from BankAccountDepositModel where office_id=:offc").setParameter("offc",officeId).list();
				BankAccountDepositModel bankaccdepmdl;
				PurchaseModel pmdl1;
				commonItr=idList.iterator();
				while(commonItr.hasNext()){
					bankaccdepmdl=(BankAccountDepositModel) commonItr.next();
					getSession().delete(bankaccdepmdl);
				}
				break;
				
				case SConstants.deleteObjects.CASH_ACCOUNT_PAYMENTS:
					idList=getSession().createQuery("from CashAccountPaymentModel where office_id=:offc").setParameter("offc",officeId).list();
					CashAccountPaymentModel cashaccpaymntmdl;
					commonItr=idList.iterator();
					
					while(commonItr.hasNext()){
						cashaccpaymntmdl=(CashAccountPaymentModel) commonItr.next();
						List oldMapList=new ArrayList();
						TransactionModel transaction=(TransactionModel) getSession().get(TransactionModel.class, cashaccpaymntmdl.getTransactionId());
						
						
						oldMapList=getSession().createQuery("from PaymentInvoiceMapModel where paymentId=:id and payment_type=:payment_type and type=:type and office_id=:office")
												.setParameter("id", cashaccpaymntmdl.getId())
												.setParameter("office", cashaccpaymntmdl.getOffice_id())
												.setParameter("payment_type", SConstants.CASH_ACCOUNT_PAYMENTS)
												.setParameter("type", SConstants.PURCHASE).list();


						Iterator mapItr=oldMapList.iterator();
						while (mapItr.hasNext()) {
							PaymentInvoiceMapModel map = (PaymentInvoiceMapModel) mapItr.next();
							
							PurchaseModel prmdl1=(PurchaseModel)getSession().get(PurchaseModel.class, map.getInvoiceId());
							double amount=prmdl1.getPaid_by_payment()-map.getAmount();
							prmdl1.setPaid_by_payment(CommonUtil.roundNumber(amount));
							
							if(((prmdl1.getAmount() - prmdl1.getExpenseAmount()) +
								(prmdl1.getExpenseAmount() - prmdl1.getExpenseCreditAmount()) - 
								prmdl1.getPaymentAmount() - amount)>0) {
								
								prmdl1.setPayment_done('N');
								prmdl1.setPayment_status(SConstants.PARTIALLY_PAID);
							}
							else {
								prmdl1.setPayment_done('Y');
								prmdl1.setPayment_status(SConstants.FULLY_PAID);
							}
							getSession().update(prmdl1);
							flush();
						}
						getSession().createQuery("delete from PaymentInvoiceMapModel where paymentId=:id and payment_type=:payment_type and type=:type and office_id=:office")
									.setParameter("id", cashaccpaymntmdl.getId())
									.setParameter("office", cashaccpaymntmdl.getOffice_id())
									.setParameter("payment_type", SConstants.CASH_ACCOUNT_PAYMENTS)
									.setParameter("type", SConstants.PURCHASE).executeUpdate();
						flush();


						Iterator<TransactionDetailsModel> aciter = transaction.getTransaction_details_list().iterator();
						while (aciter.hasNext()) {
						
							TransactionDetailsModel tdm = aciter.next();
							
							getSession().createQuery("update LedgerModel set current_balance=current_balance+:amount where id=:id")
									.setDouble("amount", tdm.getAmount()).setLong("id", tdm.getFromAcct().getId()).executeUpdate();
							
							getSession().createQuery("update LedgerModel set current_balance=current_balance-:amount where id=:id")
									.setDouble("amount", tdm.getAmount()).setLong("id", tdm.getToAcct().getId()).executeUpdate();
							
							flush();
						
						}
						getSession().delete(transaction);
						flush();
						getSession().delete(cashaccpaymntmdl);
					}
					break;
					
				case SConstants.deleteObjects.CASH_ACCOUNT_DEPOSITS:
					idList=getSession().createQuery("from CashAccountDepositModel where office_id=:offc").setParameter("offc",officeId).list();
					CashAccountDepositModel cashaccntdpst;
					commonItr=idList.iterator();
					
					while(commonItr.hasNext()){
						cashaccntdpst=(CashAccountDepositModel) commonItr.next();
					TransactionModel transaction=(TransactionModel) getSession().get(TransactionModel.class, cashaccntdpst.getTransactionId());
					List oldMapList=new ArrayList();
					oldMapList=getSession().createQuery("from PaymentInvoiceMapModel where paymentId=:id and payment_type=:payment_type and type=:type and office_id=:office")
											.setParameter("id", cashaccntdpst.getId())
											.setParameter("office", cashaccntdpst.getOffice_id())
											.setParameter("payment_type", SConstants.CASH_ACCOUNT_DEPOSITS)
											.setParameter("type", SConstants.SALES).list();
					Iterator mapItr=oldMapList.iterator();
					while (mapItr.hasNext()) {
						PaymentInvoiceMapModel map = (PaymentInvoiceMapModel) mapItr.next();
						
						SalesModel smdl=(SalesModel)getSession().get(SalesModel.class, map.getInvoiceId());
						double amount=smdl.getPaid_by_payment()-map.getAmount();
						smdl.setPaid_by_payment(CommonUtil.roundNumber(amount));
						
						if(((smdl.getAmount() - smdl.getExpenseAmount()) +
							(smdl.getExpenseAmount() - smdl.getExpenseCreditAmount()) - 
							smdl.getPayment_amount() - amount)>0) {
							
							smdl.setPayment_done('N');
							smdl.setStatus(SConstants.PARTIALLY_PAID);
						}
						else {
							smdl.setPayment_done('Y');
							smdl.setStatus(SConstants.FULLY_PAID);
						}
						getSession().update(smdl);
						flush();
					}
					getSession().createQuery("delete from PaymentInvoiceMapModel where paymentId=:id and payment_type=:payment_type and type=:type and office_id=:office")
								.setParameter("id", cashaccntdpst.getId())
								.setParameter("payment_type", SConstants.CASH_ACCOUNT_DEPOSITS)
								.setParameter("office", cashaccntdpst.getOffice_id())
								.setParameter("type", SConstants.SALES).executeUpdate();
					flush();


					Iterator<TransactionDetailsModel> aciter = transaction.getTransaction_details_list().iterator();
					while (aciter.hasNext()) {
					
						TransactionDetailsModel tdm = aciter.next();
						
						getSession().createQuery("update LedgerModel set current_balance=current_balance+:amount where id=:id")
								.setDouble("amount", tdm.getAmount()).setLong("id", tdm.getFromAcct().getId()).executeUpdate();
						
						getSession().createQuery("update LedgerModel set current_balance=current_balance-:amount where id=:id")
								.setDouble("amount", tdm.getAmount()).setLong("id", tdm.getToAcct().getId()).executeUpdate();
						
						flush();
					
					}
					getSession().delete(transaction);
					flush();
					getSession().delete(cashaccntdpst);
					}
					break;
				case SConstants.deleteObjects.JOURNEL:
					idList=getSession().createQuery("from JournalModel where office_id=:offc").setParameter("offc", officeId).list();
					commonItr=idList.iterator();
					JournalModel jnmdl;
					while(commonItr.hasNext()){
						jnmdl=(JournalModel)commonItr.next();
						getSession().delete(jnmdl);
					}
				case SConstants.deleteObjects.CREDIT_NOTE:
					idList=getSession().createQuery("from CreditNoteModel where office_id=:offc").setParameter("offc", officeId).list();
					commonItr=idList.iterator();
					CreditNoteModel cmdl;
					while(commonItr.hasNext()){
						cmdl=(CreditNoteModel)commonItr.next();
					
					TransactionModel transaction=(TransactionModel) getSession().get(TransactionModel.class, cmdl.getTransactionId());
					List oldMapList=new ArrayList();
					oldMapList=getSession().createQuery("from DebitCreditInvoiceMapModel where paymentId=:id and type=:type and office_id=:office")
											.setParameter("id", cmdl.getId())
											.setParameter("office", cmdl.getOffice_id())
											.setParameter("type", SConstants.creditDebitNote.CREDIT).list();
					Iterator mapItr=oldMapList.iterator();
					while (mapItr.hasNext()) {
						DebitCreditInvoiceMapModel map = (DebitCreditInvoiceMapModel) mapItr.next();
						
						double amount=0;
						if(map.getSupplier_customer()==SConstants.creditDebitNote.SUPPLIER){
							PurchaseModel pmdlcrdtnote=(PurchaseModel)getSession().get(PurchaseModel.class, map.getInvoiceId());
							amount=pmdlcrdtnote.getCredit_note()-map.getAmount();
							pmdlcrdtnote.setCredit_note(CommonUtil.roundNumber(amount));
							
							if(((pmdlcrdtnote.getAmount() - pmdlcrdtnote.getExpenseAmount()) + (pmdlcrdtnote.getExpenseAmount() - pmdlcrdtnote.getExpenseCreditAmount()) + amount 
									- pmdlcrdtnote.getPaymentAmount() - pmdlcrdtnote.getCredit_note() - pmdlcrdtnote.getPaid_by_payment())>0) {

								pmdlcrdtnote.setPayment_done('N');
								pmdlcrdtnote.setPayment_status(SConstants.PARTIALLY_PAID);
									
								}
								else {
									
									pmdlcrdtnote.setPayment_done('Y');
									pmdlcrdtnote.setPayment_status(SConstants.FULLY_PAID);
									
								}
							getSession().update(pmdlcrdtnote);
							flush();
						}
						
						/*else if(map.getSupplier_customer()==SConstants.creditDebitNote.CUSTOMER) {
							
							SalesModel smdl=(SalesModel)getSession().get(SalesModel.class, map.getInvoiceId());
							amount=smdl.getCredit_note()-map.getAmount();
							smdl.setCredit_note(CommonUtil.roundNumber(amount));
							if(((smdl.getAmount() - smdl.getExpenseAmount()) + (smdl.getExpenseAmount() - smdl.getExpenseCreditAmount()) + amount 
									- smdl.getPayment_amount() - smdl.getCredit_note() - smdl.getPaid_by_payment())>0) {

									smdl.setPayment_done('N');
									smdl.setStatus(SConstants.PARTIALLY_PAID);
									
								}
								else {
									
									smdl.setPayment_done('Y');
									smdl.setStatus(SConstants.FULLY_PAID);
									
								}
							getSession().update(smdl);
							flush();
						}*/
					}
					getSession().createQuery("delete from DebitCreditInvoiceMapModel where paymentId=:id and type=:type and office_id=:office")
								.setParameter("id", cmdl.getId())
								.setParameter("office", cmdl.getOffice_id())
								.setParameter("type", SConstants.creditDebitNote.CREDIT).executeUpdate();
					flush();


					Iterator<TransactionDetailsModel> aciter = transaction.getTransaction_details_list().iterator();
					while (aciter.hasNext()) {
					
						TransactionDetailsModel tdm = aciter.next();
						
						getSession().createQuery("update LedgerModel set current_balance=current_balance+:amount where id=:id")
								.setDouble("amount", tdm.getAmount()).setLong("id", tdm.getFromAcct().getId()).executeUpdate();
						
						getSession().createQuery("update LedgerModel set current_balance=current_balance-:amount where id=:id")
								.setDouble("amount", tdm.getAmount()).setLong("id", tdm.getToAcct().getId()).executeUpdate();
						
						flush();
					
					}
					getSession().delete(transaction);
					flush();
					getSession().delete(cmdl);
					}
					break;
				case SConstants.deleteObjects.DEBIT_NOTE:
					idList=getSession().createQuery("from DebitNoteModel where office_id=:offc").setParameter("offc",officeId).list();
					DebitNoteModel dbtmdl;  
					commonItr=idList.iterator();
					
					while(commonItr.hasNext()){
						dbtmdl=(DebitNoteModel) commonItr.next();
						
						TransactionModel transaction=(TransactionModel) getSession().get(TransactionModel.class, dbtmdl.getTransactionId());
//						List oldMapList=new ArrayList();
//						oldMapList=getSession().createQuery("from DebitCreditInvoiceMapModel where paymentId=:id and type=:type and office_id=:office")
//												.setParameter("id", dbtmdl.getId())
//												.setParameter("office", dbtmdl.getOffice_id())
//												.setParameter("type", SConstants.creditDebitNote.DEBIT).list();
//						Iterator mapItr=oldMapList.iterator();
//						while (mapItr.hasNext()) {
//							DebitCreditInvoiceMapModel map = (DebitCreditInvoiceMapModel) mapItr.next();
//							
//							double amount=0;
//							if(map.getSupplier_customer()==SConstants.creditDebitNote.SUPPLIER){
//								PurchaseModel dpmdl=(PurchaseModel)getSession().get(PurchaseModel.class, map.getInvoiceId());
//								amount=dpmdl.getDebit_note()-map.getAmount();
//								dpmdl.setDebit_note(CommonUtil.roundNumber(amount));
//								
//								if(((dpmdl.getAmount() - dpmdl.getExpenseAmount()) + (dpmdl.getExpenseAmount() - dpmdl.getExpenseCreditAmount()) + amount 
//										- dpmdl.getPaymentAmount() - dpmdl.getCredit_note() - dpmdl.getPaid_by_payment())>0) {
//
//									dpmdl.setPayment_done('N');
//									dpmdl.setPayment_status(SConstants.PARTIALLY_PAID);
//										
//									}
//									else {
//										
//										dpmdl.setPayment_done('Y');
//										dpmdl.setPayment_status(SConstants.FULLY_PAID);
//										
//									}
//								getSession().update(dpmdl);
//								flush();
//							}
							
//							else if(map.getSupplier_customer()==SConstants.creditDebitNote.CUSTOMER) {
//								
//								SalesModel smdl=(SalesModel)getSession().get(SalesModel.class, map.getInvoiceId());
//								amount=smdl.getDebit_note()-map.getAmount();
//								smdl.setDebit_note(CommonUtil.roundNumber(amount));
//								if(((smdl.getAmount() - smdl.getExpenseAmount()) + (smdl.getExpenseAmount() - smdl.getExpenseCreditAmount()) + amount 
//										- smdl.getPayment_amount() - smdl.getCredit_note() - smdl.getPaid_by_payment())>0) {
//
//										smdl.setPayment_done('N');
//										smdl.setStatus(SConstants.PARTIALLY_PAID);
//										
//									}
//									else {
//										
//										smdl.setPayment_done('Y');
//										smdl.setStatus(SConstants.FULLY_PAID);
//										
//									}
//								getSession().update(smdl);
//								flush();
//							}
					/*	}*/
						getSession().createQuery("delete from DebitCreditInvoiceMapModel where paymentId=:id and type=:type and office_id=:office")
									.setParameter("id", dbtmdl.getId())
									.setParameter("office", dbtmdl.getOffice_id())
									.setParameter("type", SConstants.creditDebitNote.DEBIT).executeUpdate();
						flush();


						Iterator<TransactionDetailsModel> aciter = transaction.getTransaction_details_list().iterator();
						while (aciter.hasNext()) {
						
							TransactionDetailsModel tdm = aciter.next();
							
							getSession().createQuery("update LedgerModel set current_balance=current_balance+:amount where id=:id")
									.setDouble("amount", tdm.getAmount()).setLong("id", tdm.getFromAcct().getId()).executeUpdate();
							
							getSession().createQuery("update LedgerModel set current_balance=current_balance-:amount where id=:id")
									.setDouble("amount", tdm.getAmount()).setLong("id", tdm.getToAcct().getId()).executeUpdate();
							
							flush();
						
						}
						getSession().delete(transaction);
						flush();
						getSession().delete(dbtmdl);
					}
					break;
				case SConstants.deleteObjects.PDC:
					idList=getSession().createQuery("from PdcModel where office_id=:offc").setParameter("offc",officeId).list();
					PdcModel pdcmdl;
					commonItr=idList.iterator();
					while(commonItr.hasNext()){
						pdcmdl=(PdcModel) commonItr.next();
						getSession().delete(pdcmdl);
					}
					break;
				case SConstants.deleteObjects.CHEQUE_RETURN:
					idList=getSession().createQuery("from ChequeReturnModel where office_id=:offc").setParameter("offc",officeId).list();
					ChequeReturnModel chqmdl;
					commonItr=idList.iterator();
					while(commonItr.hasNext()){
						chqmdl=(ChequeReturnModel) commonItr.next();
						getSession().delete(chqmdl);
					}
				case SConstants.deleteObjects.PAYROLL_TRANSACTIONS:
					idList=getSession().createQuery("from SalaryDisbursalModel where officeId=:offc").setParameter("offc", officeId).list();
					SalaryDisbursalModel mdl;
					EmployeeWorkingTimeModel empwtmdl;
					PayrollEmployeeMapModel payempmdl;
					PayrollComponentModel paycmdl;
					commonItr=idList.iterator();
					while(commonItr.hasNext()){
						mdl=(SalaryDisbursalModel) commonItr.next();
						getSession().delete(mdl);
					}
					flush();
					idList=getSession().createQuery("from EmployeeWorkingTimeModel where employee.office.id=:offc").setParameter("offc",officeId).list();
					commonItr=idList.iterator();
					while(commonItr.hasNext()){
						empwtmdl=(EmployeeWorkingTimeModel) commonItr.next();
						getSession().delete(empwtmdl);
					}
					flush();
					idList=getSession().createQuery("from PayrollEmployeeMapModel where employee.office.id=:offc").setParameter("offc",officeId).list();
					commonItr=idList.iterator();
					while(commonItr.hasNext()){
						payempmdl=(PayrollEmployeeMapModel) commonItr.next();
						getSession().delete(payempmdl);
					}
					flush();
					idList=getSession().createQuery("from PayrollComponentModel where office.id=:offc").setParameter("offc",officeId).list();
					commonItr=idList.iterator();
					while(commonItr.hasNext()){
						paycmdl=(PayrollComponentModel) commonItr.next();
						getSession().delete(paycmdl);
					}
					flush();
					break;
				case SConstants.deleteObjects.EMPLOYEE_ADVANCES:
					idList=getSession().createQuery("from EmployeeAdvancePaymentModel where office.id=:offc").setParameter("offc", officeId).list();
					EmployeeAdvancePaymentModel empadvmdl;
					commonItr=idList.iterator();
					while(commonItr.hasNext()){
						empadvmdl=(EmployeeAdvancePaymentModel) commonItr.next();
						getSession().delete(empadvmdl);
					}
					break;
				case SConstants.deleteObjects.STOCK_TRANSFER:
					idList=getSession().createQuery("from StockTransferModel where from_office.id=:offc").setParameter("offc", officeId).list();
					StockTransferModel stckmdl;
					commonItr=idList.iterator();
					while(commonItr.hasNext()){
						stckmdl=(StockTransferModel)commonItr.next();
						getSession().delete(stckmdl);
						}
					
		            break;
				case SConstants.deleteObjects.GENERAL_LEDGERS:
					idList=getSession().createQuery("from LedgerModel where office.id=:offc").setParameter("offc", officeId).list();
					LedgerModel ldgmdl;
					Iterator subitr=null;
					TransactionModel tr;
					commonItr=idList.iterator();
					while(commonItr.hasNext()){
						ldgmdl=(LedgerModel)commonItr.next();
						List list=new ArrayList();
						list=  getSession().createQuery("select a from TransactionModel a join a.transaction_details_list b "
								+ "where b.fromAcct.id=:id or b.toAcct.id=:id")
								.setParameter("id", ldgmdl.getId())
								.list();
                      subitr=list.iterator();
                      while(subitr.hasNext()){
                    	  tr=(TransactionModel) subitr.next();
                    	  getSession().delete(tr);  
                      }

                      flush();
                        getSession().createQuery("Delete from TransactionDetailsModel where fromAcct.id=:id or toAcct.id=:id").setParameter("id",ldgmdl.getId()).executeUpdate();
						getSession().createQuery("Delete from LedgerOpeningBalanceModel where ledger.id=:id").setParameter("id",ldgmdl.getId()).executeUpdate();
						getSession().createQuery("Delete from PaymentModeModel where ledger.id=:id").setParameter("id",ldgmdl.getId()).executeUpdate();
						flush();
						getSession().delete(ldgmdl);
						}
					break;
				case SConstants.deleteObjects.BANK_ACCOUNTS:
					idList=getSession().createQuery("from BankAccountModel where ledger.office.id=:offc").setParameter("offc", officeId).list();
					BankAccountModel bnkaccmdl;
					commonItr=idList.iterator();
					while(commonItr.hasNext()){
						bnkaccmdl=(BankAccountModel) commonItr.next();
						getSession().delete(bnkaccmdl);
					}
                      

					
				case SConstants.deleteObjects.CUSTOMER:
					idList=getSession().createQuery("from CustomerModel where ledger.office.id=:offc").setParameter("offc",officeId).list();
					CustomerModel csmdl;
					commonItr=idList.iterator();
					while(commonItr.hasNext()){
						csmdl=(CustomerModel) commonItr.next();
						getSession().delete(csmdl);
					}
					break;
				case SConstants.deleteObjects.SUPPLIER:
					idList=getSession().createQuery("from SupplierModel where ledger.office.id=:office").setParameter("office",officeId).list();
					SupplierModel spmdl;
					commonItr=idList.iterator();
					while(commonItr.hasNext()){
						spmdl=(SupplierModel) commonItr.next();
								getSession().delete(spmdl);
					}
					break;
					
				case SConstants.deleteObjects.CLEARING_AGENT:
					idList=getSession().createQuery("from ClearingAgentModel where ledger.office.id=:office").setParameter("office",officeId).list();
					ClearingAgentModel clmdl;
					commonItr=idList.iterator();
					while(commonItr.hasNext()){
						clmdl=(ClearingAgentModel) commonItr.next();
								getSession().delete(clmdl);
								
						}	
					break;
				case SConstants.deleteObjects.ITEM:
					idList=getSession().createQuery("from ItemModel where office.id=:offc").setParameter("offc",officeId).list();
					ItemModel itmdl;
					commonItr=idList.iterator();
					while(commonItr.hasNext()){
						itmdl=(ItemModel) commonItr.next();
						
							
							getSession().createQuery("delete from ItemStockModel where item.id=:id")
							.setParameter("id", itmdl.getId()).executeUpdate();

							getSession().createQuery("delete from ItemUnitMangementModel where item.id=:id")
							.setParameter("id", itmdl.getId()).executeUpdate();

							getSession().delete(itmdl);
							flush();
						}
					break;
				case SConstants.deleteObjects.USERS:
					idList=getSession().createQuery("from UserModel where office.id=:offc").setParameter("offc",officeId).list();
					UserModel user;
					commonItr=idList.iterator();
					long loginId=0,addr1=0,addr2=0,addr3=0;
					List userList=new ArrayList();
					Iterator itr=null;
					while(commonItr.hasNext()){
						user=(UserModel) commonItr.next();

						if(user.getUser_role().getId()!=SConstants.ROLE_SUPER_ADMIN){
						
					
						userList=getSession().createQuery("from EmployeeDocumentModel where employee_id="+user.getId()).list();
						itr=userList.iterator();
						while (itr.hasNext()) {
							EmployeeDocumentModel emdl = (EmployeeDocumentModel) itr.next();
							String DIR = VaadinServlet.getCurrent().getServletContext().getRealPath("/")+"VAADIN/themes/testappstheme/EmployeeDocuments/".trim();
							if(emdl.getFilename().trim().length()>2){
								String[] arr=emdl.getFilename().trim().split(",");
								List fileNameList=Arrays.asList(arr);
								for (int j = 0; j < fileNameList.size(); j++) {
									try {
										File file=new File(DIR.trim()+fileNameList.get(j).toString().trim());
										if(file.exists() && !file.isDirectory())
											file.delete();
									} catch (Exception e) {
										e.printStackTrace();
										continue;
									}
								}
							}
							getSession().delete(emdl);
						}
						flush();

						// Payroll
						userList=getSession().createQuery("from SalaryDisbursalModel where user.id="+user.getId()).list();
						itr=userList.iterator();
						while (itr.hasNext()) {
							SalaryDisbursalModel smdl = (SalaryDisbursalModel) itr.next();
							getSession().delete(smdl);
						}
						flush();
						
						userList=getSession().createQuery("from DocumentAccessModel where creator.id="+user.getId()).list();
						itr=userList.iterator();
						while (itr.hasNext()) {
							DocumentAccessModel dmdl = (DocumentAccessModel) itr.next();
							getSession().delete(dmdl);
						}
						flush();
						
						userList=getSession().createQuery("from LoanApprovalModel where loanRequest.user.id="+user.getId()).list();
						itr=userList.iterator();
						while (itr.hasNext()) {
							LoanApprovalModel lnmdl = (LoanApprovalModel) itr.next();
							getSession().createQuery("delete from LoanDateModel where loan.id=:id").setParameter("id", lnmdl.getId()).executeUpdate();
							getSession().delete(lnmdl);
						}
						flush();
						
						userList=getSession().createQuery("from LeaveModel where user.id="+user.getId()).list();
						itr=userList.iterator();
						while (itr.hasNext()) {
							LeaveModel lvmdl = (LeaveModel) itr.next();
							getSession().createQuery("delete from LeaveHistoryModel where leave=:id").setParameter("id", lvmdl.getId()).executeUpdate();
							getSession().createQuery("delete from LeaveDateModel where leave.id=:id").setParameter("id", lvmdl.getId()).executeUpdate();
							getSession().delete(lvmdl);
						}
						flush();
						
						getSession().createQuery("delete from EmployeeStatusModel where user.id="+user.getId()).executeUpdate();
						
						getSession().createQuery("delete from SalesManCommissionMapModel where userId="+user.getId()).executeUpdate();
						
						getSession().createQuery("delete from UserLeaveAllocationModel where user.id="+user.getId()).executeUpdate();
						
						getSession().createQuery("delete from UserLeaveMapModel where userId="+user.getId()).executeUpdate();
						
						getSession().createQuery("delete from LoanRequestModel where user.id="+user.getId()).executeUpdate();
						
						getSession().createQuery("delete from UserQualificationModel where user.id="+user.getId()).executeUpdate();
						
						getSession().createQuery("delete from UserContactModel where user.id="+user.getId()).executeUpdate();
						
						getSession().createQuery("delete from UserFamilyContactModel where user.id="+user.getId()).executeUpdate();
						
						getSession().createQuery("delete from UserPreviousEmployerModel where user.id="+user.getId()).executeUpdate();
						
						getSession().createQuery("delete from CommissionSalaryModel where employee="+user.getId()).executeUpdate();

						getSession().createQuery("delete from SalaryBalanceMapModel where employee.id="+user.getId()).executeUpdate();
						
						getSession().createQuery("delete from MyMailsModel where user_id="+user.getId()).executeUpdate();
						
						getSession().createQuery("delete from EmailConfigurationModel where user_id="+user.getId()).executeUpdate();
						
						getSession().createQuery("delete from PayrollEmployeeMapModel where employee.id="+user.getId()).executeUpdate();
						
						getSession().createQuery("delete from AttendanceModel where userId="+user.getId()).executeUpdate();
						
						getSession().createQuery("delete from EmployeeWorkingTimeModel where employee.id="+user.getId()).executeUpdate();
						
						getSession().createQuery("delete from EmployeeAdvancePaymentModel where user.id="+user.getId()).executeUpdate();
						
						flush();
						
						if(user.getLoginId()!=null){
					
							getSession().createQuery("delete from SalesManMapModel where login_id="+user.getLoginId().getId()).executeUpdate();
							
							getSession().createQuery("delete from QuickMenuModel where login_id="+user.getLoginId().getId()).executeUpdate();
							
							getSession().createQuery("delete from SessionActivityModel where login="+user.getLoginId().getId()).executeUpdate();
							
							getSession().createQuery("delete from ReportIssueModel where login="+user.getLoginId().getId()).executeUpdate();
							
							getSession().createQuery("delete from ReviewModel where login="+user.getLoginId().getId()).executeUpdate();
							
							getSession().createQuery("delete from OfficeAllocationModel where login_id="+user.getLoginId().getId()).executeUpdate();
							
							getSession().createQuery("delete from PrivilageSetupModel where login_id="+user.getLoginId().getId()).executeUpdate();
							
							getSession().createQuery("delete from ActivityLogModel where login="+user.getLoginId().getId()).executeUpdate();
							
							getSession().createQuery("delete from S_LoginOptionMappingModel where login_id.id="+user.getLoginId().getId()).executeUpdate();
							
							getSession().createQuery("delete from S_LoginHistoryModel where login_id="+user.getLoginId().getId()).executeUpdate();
							
							loginId=user.getLoginId().getId();
							flush();
						}
						
						if(user.getWork_address()!=null)
							addr1=user.getWork_address().getId();
							
						if(user.getLocal_address()!=null)
							addr2=user.getLocal_address().getId();
						
						if(user.getAddress()!=null)
							addr3=user.getAddress().getId();
						
						getSession().delete(user);
						flush();
						
						if(addr1!=0)
							getSession().createQuery("delete from AddressModel where id="+addr1).executeUpdate();
						
						if(addr2!=0)
							getSession().createQuery("delete from AddressModel where id="+addr2).executeUpdate();
						
						if(addr3!=0)
							getSession().createQuery("delete from AddressModel where id="+addr3).executeUpdate();
						
						if(loginId!=0)
							getSession().createQuery("delete from S_LoginModel where id="+loginId).executeUpdate();
						
						flush();
					
						}
					}
					
					break;
				case SConstants.deleteObjects.BUILDING:
					System.out.println("Deleteing BUILDING");
					
										idList = getSession()
												.createQuery(
														"select id from RackModel where room.building.office.id=:ofc")
												.setLong("ofc", officeId).list();
					
										RackModel rac;
										commonItr = idList.iterator();
										while (commonItr.hasNext()) {
											rac = (RackModel) getSession().get(RackModel.class,
													(Long) commonItr.next());
					
											getSession().delete(rac);
											getSession().delete(rac.getRoom());
											getSession().delete(rac.getRoom().getBuilding());
										}
					
					
					break;
				case SConstants.deleteObjects.TASK:
					System.out.println("Deleteing TASK");
					 long id = (Long) getSession()
					 .createQuery(
					 "select organization.id from S_OfficeModel where id=:id")
					 .setParameter("id", officeId).uniqueResult();
					 idList = getSession()
					 .createQuery(
					 "select a from TasksModel a join a.componentDetailsList b where b.task_component.organization_id=:id")
					 .setParameter("id", id).list();
					 System.out.println("Size = " + idList.size());
					 commonItr = idList.iterator();
					 System.out.println(idList.size());
					 TasksModel tmdl;
					 while (commonItr.hasNext()) {
					 tmdl = (TasksModel) commonItr.next();
					
					 getSession().delete(tmdl);
					 }
					 break;
				case SConstants.deleteObjects.EMPLOYEE_LOAN:
					
					idList=getSession().createQuery("from LoanApprovalModel where loanRequest.user.office.id=:offc").setParameter("offc",officeId).list();
					LoanApprovalModel lmdl;
					commonItr=idList.iterator();
					
					while(commonItr.hasNext()){
						lmdl=(LoanApprovalModel)commonItr.next(); 
					 getSession().delete(lmdl);
					}
					
					 idList=getSession().createQuery("from LoanRequestModel where user.office.id=:offc").setParameter("offc",officeId).list(); 
					 LoanRequestModel lrmdl;
						commonItr=idList.iterator();
						while(commonItr.hasNext()){
							lrmdl=(LoanRequestModel)commonItr.next(); 
						 getSession().delete(lrmdl);
						}
				
				break;
			
			
			}
			}
				commit();
				return true;
				
			
		}catch (Exception e) {
				e.printStackTrace();
				rollback();
				close();
				throw e;
			} finally {
				flush();
				close();
			}
		}
				
//
//					System.out.println("Deleteing Sales");
//
//					idList = getSession()
//							.createQuery(
//									"select id from SalesModel where office.id=:ofc")
//							.setLong("ofc", officeId).list();
//
//					commonItr = idList.iterator();
//					SalesModel sal;
//					TransactionModel tr;
//					while (commonItr.hasNext()) {
//						sal = (SalesModel) getSession().get(SalesModel.class,
//								(Long) commonItr.next());
//						TransactionModel transObj = (TransactionModel) getSession()
//								.get(TransactionModel.class,
//										sal.getTransaction_id());
//						if (transObj != null) {                      
//							TransactionDetailsModel tdm;
//							Iterator<TransactionDetailsModel> aciter = transObj
//									.getTransaction_details_list().iterator();
//							while (aciter.hasNext()) {
//								tdm = aciter.next();
//
//								getSession()
//										.createQuery(
//												"update LedgerModel set current_balance=current_balance+:amt where id=:id")
//										.setDouble("amt", tdm.getAmount())
//										.setLong("id",
//												tdm.getFromAcct().getId())
//										.executeUpdate();
//
//								getSession()
//										.createQuery(
//												"update LedgerModel set current_balance=current_balance-:amt where id=:id")
//										.setDouble("amt", tdm.getAmount())
//										.setLong("id", tdm.getToAcct().getId())
//										.executeUpdate();
//
//								flush();
//							}
//
//							getSession().delete(transObj);
//
//							flush();
//						}
//						// Transaction Related
//
//						SalesInventoryDetailsModel invObj;
//						List list;
//						Iterator<SalesInventoryDetailsModel> it = sal
//								.getInventory_details_list().iterator();
//						while (it.hasNext()) {
//							invObj = it.next();
//
//							if (invObj.getOrder_id() != 0) {
//
//								list = getSession()
//										.createQuery(
//												"select b.id from SalesOrderModel a join a.inventory_details_list b  where a.id=:id "
//														+ "and b.item.id=:itm and b.unit.id=:un ")
//										.setParameter("itm",
//												invObj.getItem().getId())
//										.setParameter("id",
//												invObj.getOrder_id())
//										.setParameter("un",
//												invObj.getUnit().getId())
//										.list();
//
//								getSession()
//										.createQuery(
//												"update SalesInventoryDetailsModel set balance=balance+:qty where id in (:lst)")
//										.setParameterList("lst", list)
//										.setParameter("qty",
//												invObj.getQunatity())
//										.executeUpdate();
//							}
//
//							getSession()
//									.createQuery(
//											"update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
//									.setParameter("id",
//											invObj.getItem().getId())
//									.setParameter("qty",
//											invObj.getQuantity_in_basic_unit())
//									.executeUpdate();
//
//							flush();
//
//							comDao.increaseStockByStockID(invObj.getStk_id(),
//									invObj.getQuantity_in_basic_unit());
//
//							increaseRackQty(invObj.getRack_id(),
//									invObj.getStk_id(),
//									invObj.getQuantity_in_basic_unit());
//
//						}
//
//						getSession().delete(sal);
//
//						getSession()
//								.createQuery(
//										"delete from SalesStockMapModel where salesId=:id")
//								.setLong("id", sal.getId()).executeUpdate();
//
//					}
//					flush();
//					break;
//
//				case SConstants.deleteObjects.PURCHASE:
//
//					System.out.println("Deleteing Purchase");
//
//					idList = getSession()
//							.createQuery(
//									"select id from PurchaseModel where office.id=:ofc")
//							.setLong("ofc", officeId).list();
//
//					PurchaseModel pur;
//					commonItr = idList.iterator();
//					while (commonItr.hasNext()) {
//						pur = (PurchaseModel) getSession().get(
//								PurchaseModel.class, (Long) commonItr.next());
//
//						TransactionModel transObj = (TransactionModel) getSession()
//								.get(TransactionModel.class,
//										pur.getTransaction_id());
//						if (transObj != null) {
//							TransactionDetailsModel tdm;
//							Iterator<TransactionDetailsModel> aciter = transObj
//									.getTransaction_details_list().iterator();
//							while (aciter.hasNext()) {
//								tdm = aciter.next();
//
//								getSession()
//										.createQuery(
//												"update LedgerModel set current_balance=current_balance+:amt where id=:id")
//										.setDouble("amt", tdm.getAmount())
//										.setLong("id",
//												tdm.getFromAcct().getId())
//										.executeUpdate();
//
//								getSession()
//										.createQuery(
//												"update LedgerModel set current_balance=current_balance-:amt where id=:id")
//										.setDouble("amt", tdm.getAmount())
//										.setLong("id", tdm.getToAcct().getId())
//										.executeUpdate();
//
//								flush();
//							}
//
//							getSession().delete(transObj);
//
//							flush();
//						}
//						// Transaction Related
//
//						PurchaseInventoryDetailsModel invObj;
//						List list;
//						Iterator<PurchaseInventoryDetailsModel> it = pur
//								.getInventory_details_list().iterator();
//						while (it.hasNext()) {
//							invObj = it.next();
//
//							if (invObj.getOrder_id() != 0) {
//
//								list = getSession()
//										.createQuery(
//												"select b.id from PurchaseOrderModel a join a.inventory_details_list b  where a.id=:id "
//														+ "and b.item.id=:itm and b.unit.id=:un ")
//										.setParameter("itm",
//												invObj.getItem().getId())
//										.setParameter("id",
//												invObj.getOrder_id())
//										.setParameter("un",
//												invObj.getUnit().getId())
//										.list();
//
//								getSession()
//										.createQuery(
//												"update PurchaseInventoryDetailsModel set balance=balance+:qty where id in (:lst)")
//										.setParameterList("lst", list)
//										.setParameter("qty",
//												invObj.getQunatity())
//										.executeUpdate();
//							}
//
//							getSession()
//									.createQuery(
//											"update ItemModel set current_balalnce=current_balalnce-:qty where id=:id")
//									.setParameter("id",
//											invObj.getItem().getId())
//									.setParameter("qty",
//											invObj.getQty_in_basic_unit())
//									.executeUpdate();
//
//							flush();
//
//						}
//
//						getSession().delete(pur);
//
//						flush();
//
//						getSession()
//								.createQuery(
//										"delete from StockRackMappingModel where stock.id in (select "
//												+ " id from ItemStockModel where purchase_id=:purId)")
//								.setLong("purId", pur.getId()).executeUpdate();
//
//						getSession()
//								.createQuery(
//										"delete from ItemStockModel where purchase_id=:pid")
//								.setLong("pid", pur.getId()).executeUpdate();
//
//					}
//					flush();
//					break;
//
//				case SConstants.deleteObjects.PURCHASE_RETURN:
//
//					System.out.println("Deleteing Purchase Ret");
//
//					idList = getSession()
//							.createQuery(
//									"select id from PurchaseReturnModel where office.id=:ofc")
//							.setLong("ofc", officeId).list();
//
//					PurchaseReturnModel purRet;
//					commonItr = idList.iterator();
//					while (commonItr.hasNext()) {
//						purRet = (PurchaseReturnModel) getSession().get(
//								PurchaseReturnModel.class,
//								(Long) commonItr.next());
//						TransactionModel transObj = (TransactionModel) getSession()
//								.get(TransactionModel.class,
//										purRet.getTransaction_id());
//						if (transObj != null) {
//							TransactionDetailsModel tdm;
//							Iterator<TransactionDetailsModel> aciter = transObj
//									.getTransaction_details_list().iterator();
//							while (aciter.hasNext()) {
//								tdm = aciter.next();
//
//								getSession()
//										.createQuery(
//												"update LedgerModel set current_balance=current_balance+:amt where id=:id")
//										.setDouble("amt", tdm.getAmount())
//										.setLong("id",
//												tdm.getFromAcct().getId())
//										.executeUpdate();
//
//								getSession()
//										.createQuery(
//												"update LedgerModel set current_balance=current_balance-:amt where id=:id")
//										.setDouble("amt", tdm.getAmount())
//										.setLong("id", tdm.getToAcct().getId())
//										.executeUpdate();
//
//								flush();
//							}
//
//							getSession().delete(transObj);
//						}
//						// Transaction Related
//
//						PurchaseReturnInventoryDetailsModel invObj;
//						List list;
//						Iterator<PurchaseReturnInventoryDetailsModel> it = purRet
//								.getInventory_details_list().iterator();
//						while (it.hasNext()) {
//							invObj = it.next();
//
//							if (invObj.getOrder_id() != 0) {
//								list = null;
//								list = getSession()
//										.createQuery(
//												"select b.id from PurchaseModel a join a.inventory_details_list b  where a.id=:id "
//														+ "and b.item.id=:itm and b.unit.id=:un ")
//										.setParameter("itm",
//												invObj.getItem().getId())
//										.setParameter("id",
//												invObj.getOrder_id())
//										.setParameter("un",
//												invObj.getUnit().getId())
//										.list();
//								if (list.size() != 0) {
//									getSession()
//											.createQuery(
//													"update PurchaseInventoryDetailsModel set balance=balance+:qty where id in (:lst)")
//											.setParameterList("lst", list)
//											.setParameter("qty",
//													invObj.getQunatity())
//											.executeUpdate();
//								}
//							}
//
//							getSession()
//									.createQuery(
//											"update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
//									.setParameter("id",
//											invObj.getItem().getId())
//									.setParameter("qty",
//											invObj.getQty_in_basic_unit())
//									.executeUpdate();
//							flush();
//
//							comDao.increaseStock(invObj.getItem().getId(),
//									invObj.getQty_in_basic_unit());
//
//						}
//
//						getSession().delete(purRet);
//
//					}
//
//					flush();
//					break;
//
//				case SConstants.deleteObjects.SALES_RETURN:
//
//					System.out.println("Deleteing Sales Ret");
//					idList = getSession()
//							.createQuery(
//									"select id from SalesReturnModel where office.id=:ofc")
//							.setLong("ofc", officeId).list();
//
//					SalesReturnModel salRet;
//					commonItr = idList.iterator();
//					while (commonItr.hasNext()) {
//						salRet = (SalesReturnModel) getSession()
//								.get(SalesReturnModel.class,
//										(Long) commonItr.next());
//						TransactionModel transObj = (TransactionModel) getSession()
//								.get(TransactionModel.class,
//										salRet.getTransaction_id());
//						if (transObj != null) {
//							TransactionDetailsModel tdm;
//							Iterator<TransactionDetailsModel> aciter = transObj
//									.getTransaction_details_list().iterator();
//							while (aciter.hasNext()) {
//								tdm = aciter.next();
//								getSession()
//										.createQuery(
//												"update LedgerModel set current_balance=current_balance+:amt where id=:id")
//										.setDouble("amt", tdm.getAmount())
//										.setLong("id",
//												tdm.getFromAcct().getId())
//										.executeUpdate();
//
//								getSession()
//										.createQuery(
//												"update LedgerModel set current_balance=current_balance-:amt where id=:id")
//										.setDouble("amt", tdm.getAmount())
//										.setLong("id", tdm.getToAcct().getId())
//										.executeUpdate();
//
//								flush();
//							}
//							/*
//							 * 
//							 * Test it thoroughly
//							 
//							getSession()
//									.createQuery(
//											"delete from PaymentDepositModel a where a.transaction.transaction_id=:tid")
//									.setParameter("tid",
//											transObj.getTransaction_id())
//									.executeUpdate();
//							getSession().delete(transObj);
//
//							flush();
//						}
//						// Transaction Related
//
//						SalesReturnInventoryDetailsModel invObj;
//						List list;
//						Iterator<SalesReturnInventoryDetailsModel> it = salRet
//								.getInventory_details_list().iterator();
//						while (it.hasNext()) {
//							invObj = it.next();
//
//							getSession()
//									.createQuery(
//											"update ItemModel set current_balalnce=current_balalnce-:qty where id=:id")
//									.setParameter("id",
//											invObj.getItem().getId())
//									.setParameter(
//											"qty",
//											invObj.getStock_quantity()
//													+ invObj.getGood_stock()
//													+ invObj.getReturned_quantity())
//									.executeUpdate();
//
//							flush();
//						}
//
//						PurchaseReturnModel pretObj;
//						Iterator itr4 = getSession()
//								.createQuery(
//										"select a.id from PurchaseReturnModel a join a.inventory_details_list b where b.order_id=:ret and a.status=2 group by a.id")
//								.setLong("ret", salRet.getId()).list()
//								.iterator();
//						while (itr4.hasNext()) {
//							deletePurchReturn((Long) itr4.next());
//						}
//
//						flush();
//
//						getSession().delete(salRet);
//
//						flush();
//
//						getSession()
//								.createQuery(
//										"delete from StockRackMappingModel where stock.id in (select "
//												+ " id from ItemStockModel where status=:sts and purchase_id=:pid)")
//								.setLong("sts",
//										SConstants.SALES_RETURN_STOCK_STATUS)
//								.setLong("pid", salRet.getId()).executeUpdate();
//
//						getSession()
//								.createQuery(
//										"delete from ItemStockModel where (status=:sts or status=:sts1) and purchase_id=:pid")
//								.setLong("sts",
//										SConstants.SALES_RETURN_STOCK_STATUS)
//								.setLong("sts1",
//										SConstants.stock_statuses.GOOD_STOCK)
//								.setLong("pid", salRet.getId()).executeUpdate();
//
//						flush();
//					}
//
//					break;
//
//				case SConstants.deleteObjects.PURCHASE_ORDER:
//
//					System.out.println("Deleteing PURCHASE_ORDER");
//
//					idList = getSession()
//							.createQuery(
//									"select id from PurchaseOrderModel where office.id=:ofc")
//							.setLong("ofc", officeId).list();
//
//					PurchaseOrderModel purOrd;
//					commonItr = idList.iterator();
//					while (commonItr.hasNext()) {
//						purOrd = (PurchaseOrderModel) getSession().get(
//								PurchaseOrderModel.class,
//								(Long) commonItr.next());
//						getSession().delete(purOrd);
//					}
//
//					getSession()
//							.createSQLQuery(
//									"delete from i_purchase_inventory_details where id  not in"
//											+ " (select item_details_id from  purchase_inv_link) and id not in (select item_details_id from  PO_inv_details_link)")
//							.executeUpdate();
//					// if (childList != null && childList.size() > 0)
//					// getSession()
//					// .createQuery(
//					// "delete from PurchaseInventoryDetailsModel where id in (:ids)")
//					// .setParameterList("ids", childList)
//					// .executeUpdate();
//
//					flush();
//					break;
//
//				case SConstants.deleteObjects.SALES_ORDER:
//
//					System.out.println("Deleteing SALES_ORDER");
//
//					idList = getSession()
//							.createQuery(
//									"select id from SalesOrderModel where office.id=:ofc")
//							.setLong("ofc", officeId).list();
//
//					SalesOrderModel salOrd;
//					commonItr = idList.iterator();
//					while (commonItr.hasNext()) {
//						try {
//							salOrd = (SalesOrderModel) getSession().get(
//									SalesOrderModel.class,
//									(Long) commonItr.next());
//							List childLists = null;
//							Iterator childIter;
//							SalesInventoryDetailsModel item;
//							ManufacturingMapModel mapMdl;
//							Iterator iter = salOrd.getInventory_details_list()
//									.iterator();
//							while (iter.hasNext()) {
//								item = (SalesInventoryDetailsModel) iter.next();
//
//								childList = getSession()
//										.createQuery(
//												"from ManufacturingMapModel where item.id=:item")
//										.setParameter("item",
//												item.getItem().getId()).list();
//								if (childList != null) {
//									childIter = childList.iterator();
//
//									while (childIter.hasNext()) {
//										mapMdl = (ManufacturingMapModel) childIter
//												.next();
//
//										getSession()
//												.createQuery(
//														"update ItemModel set reservedQuantity=reservedQuantity-:resqty where id=:id")
//												.setParameter(
//														"id",
//														mapMdl.getSubItem()
//																.getId())
//												.setParameter(
//														"resqty",
//														(item.getQunatity() * mapMdl
//																.getQuantity()))
//												.executeUpdate();
//									}
//								}
//							}
//
//							getSession().delete(salOrd);
//
//						} catch (Exception e) {
//						}
//
//					}
//
//					getSession()
//							.createSQLQuery(
//									"delete from i_sales_inventory_details where id not in "
//											+ "(select item_details_id from  sales_inv_link) "
//											+ " and id not in (select item_details_id from SO_inv_details_link)")
//							.executeUpdate();
//
//					// if (childList != null && childList.size() > 0)
//					// getSession()
//					// .createQuery(
//					// "delete from SalesInventoryDetailsModel where id in (:ids)")
//					// .setParameterList("ids", childList)
//					// .executeUpdate();
//
//					flush();
//					break;
//
//				case SConstants.deleteObjects.DELIVERY_NOTE:
//
//					System.out.println("Deleteing DELIVERY_NOTE");
//
//					idList = getSession()
//							.createQuery(
//									"select id from DeliveryNoteModel where office.id=:ofc")
//							.setLong("ofc", officeId).list();
//
//					DeliveryNoteModel delv;
//					commonItr = idList.iterator();
//					while (commonItr.hasNext()) {
//						delv = (DeliveryNoteModel) getSession().get(
//								DeliveryNoteModel.class,
//								(Long) commonItr.next());
//						SalesInventoryDetailsModel invObj;
//						List list;
//						Iterator<SalesInventoryDetailsModel> it = delv
//								.getInventory_details_list().iterator();
//						while (it.hasNext()) {
//							invObj = it.next();
//
//							if (invObj.getOrder_id() != 0) {
//
//								list = getSession()
//										.createQuery(
//												"select b.id from SalesOrderModel a join a.inventory_details_list b  where a.id=:id "
//														+ "and b.item.id=:itm and b.unit.id=:un ")
//										.setParameter("itm",
//												invObj.getItem().getId())
//										.setParameter("id",
//												invObj.getOrder_id())
//										.setParameter("un",
//												invObj.getUnit().getId())
//										.list();
//
//								getSession()
//										.createQuery(
//												"update SalesInventoryDetailsModel set balance=balance+:qty where id in (:lst)")
//										.setParameterList("lst", list)
//										.setParameter("qty",
//												invObj.getQunatity())
//										.executeUpdate();
//							}
//
//							getSession()
//									.createQuery(
//											"update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
//									.setParameter("id",
//											invObj.getItem().getId())
//									.setParameter("qty", invObj.getQunatity())
//									.executeUpdate();
//
//							comDao.increaseStock(invObj.getItem().getId(),
//									invObj.getQuantity_in_basic_unit());
//
//						}
//
//						getSession().delete(delv);
//					}
//
//					flush();
//					break;
//
//				case SConstants.deleteObjects.SUPPLIER_PAYMENTS:
//
//					System.out.println("Deleteing SUPPLIER_PAYMENTS");
//
//					idList = getSession()
//							.createQuery(
//									"select id from PaymentModel where office.id=:ofc and type=:typ")
//							.setParameter("typ", SConstants.SUPPLIER_PAYMENTS)
//							.setLong("ofc", officeId).list();
//
//					PaymentModel suppPay;
//					commonItr = idList.iterator();
//					while (commonItr.hasNext()) {
//						suppPay = (PaymentModel) getSession().get(
//								PaymentModel.class, (Long) commonItr.next());
//						TransactionModel transObj = (TransactionModel) getSession()
//								.get(TransactionModel.class,
//										suppPay.getTransaction_id());
//						if (transObj != null) {
//							Iterator<TransactionDetailsModel> aciter = transObj
//									.getTransaction_details_list().iterator();
//							TransactionDetailsModel tdm;
//							while (aciter.hasNext()) {
//								tdm = aciter.next();
//
//								getSession()
//										.createQuery(
//												"update LedgerModel set current_balance=current_balance+:amt where id=:id")
//										.setDouble("amt", tdm.getAmount())
//										.setLong("id",
//												tdm.getFromAcct().getId())
//										.executeUpdate();
//
//								getSession()
//										.createQuery(
//												"update LedgerModel set current_balance=current_balance-:amt where id=:id")
//										.setDouble("amt", tdm.getAmount())
//										.setLong("id", tdm.getToAcct().getId())
//										.executeUpdate();
//
//								flush();
//							}
//
//							getSession().delete(transObj);
//							getSession().delete(suppPay);
//						}
//					}
//
//					flush();
//					break;
//				case SConstants.deleteObjects.CUSTOMER_PAYMENTS:
//
//					System.out.println("Deleteing CUSTOMER_PAYMENTS");
//
//					idList = getSession()
//							.createQuery(
//									"select id from PaymentModel where office.id=:ofc and type=:typ")
//							.setParameter("typ", SConstants.CUSTOMER_PAYMENTS)
//							.setLong("ofc", officeId).list();
//
//					PaymentModel custPay;
//					commonItr = idList.iterator();
//					while (commonItr.hasNext()) {
//						custPay = (PaymentModel) getSession().get(
//								PaymentModel.class, (Long) commonItr.next());
//						TransactionModel transObj = (TransactionModel) getSession()
//								.get(TransactionModel.class,
//										custPay.getTransaction_id());
//						if (transObj != null) {
//							Iterator<TransactionDetailsModel> aciter = transObj
//									.getTransaction_details_list().iterator();
//							TransactionDetailsModel tdm;
//							while (aciter.hasNext()) {
//								tdm = aciter.next();
//
//								getSession()
//										.createQuery(
//												"update LedgerModel set current_balance=current_balance+:amt where id=:id")
//										.setDouble("amt", tdm.getAmount())
//										.setLong("id",
//												tdm.getFromAcct().getId())
//										.executeUpdate();
//
//								getSession()
//										.createQuery(
//												"update LedgerModel set current_balance=current_balance-:amt where id=:id")
//										.setDouble("amt", tdm.getAmount())
//										.setLong("id", tdm.getToAcct().getId())
//										.executeUpdate();
//
//								flush();
//							}
//
//							getSession().delete(transObj);
//							getSession().delete(custPay);
//						}
//					}
//
//					flush();
//					break;
//
//				case SConstants.deleteObjects.CONTRACTOR_PAYMENTS:
//
//					System.out.println("Deleteing CONTRACTOR_PAYMENTS");
//
//					idList = getSession()
//							.createQuery(
//									"select id from PaymentModel where office.id=:ofc and type=:typ")
//							.setParameter("typ", SConstants.CONTRACTOR_PAYMENTS)
//							.setLong("ofc", officeId).list();
//
//					PaymentModel contPay;
//					commonItr = idList.iterator();
//					while (commonItr.hasNext()) {
//						contPay = (PaymentModel) getSession().get(
//								PaymentModel.class, (Long) commonItr.next());
//						TransactionModel transObj = (TransactionModel) getSession()
//								.get(TransactionModel.class,
//										contPay.getTransaction_id());
//						if (transObj != null) {
//							Iterator<TransactionDetailsModel> aciter = transObj
//									.getTransaction_details_list().iterator();
//							TransactionDetailsModel tdm;
//							while (aciter.hasNext()) {
//								tdm = aciter.next();
//
//								getSession()
//										.createQuery(
//												"update LedgerModel set current_balance=current_balance+:amt where id=:id")
//										.setDouble("amt", tdm.getAmount())
//										.setLong("id",
//												tdm.getFromAcct().getId())
//										.executeUpdate();
//
//								getSession()
//										.createQuery(
//												"update LedgerModel set current_balance=current_balance-:amt where id=:id")
//										.setDouble("amt", tdm.getAmount())
//										.setLong("id", tdm.getToAcct().getId())
//										.executeUpdate();
//
//								flush();
//							}
//
//							getSession().delete(transObj);
//							getSession().delete(contPay);
//						}
//					}
//
//					flush();
//					break;
//
//				case SConstants.deleteObjects.JOURNAL:
//
//					System.out.println("Deleteing JOURNAL");
//
//					idList = getSession()
//							.createQuery(
//									"select id from JournalModel where office_id=:ofc")
//							.setLong("ofc", officeId).list();
//
//					JournalModel jour;
//					commonItr = idList.iterator();
//					while (commonItr.hasNext()) {
//						jour = (JournalModel) getSession().get(
//								JournalModel.class, (Long) commonItr.next());
//						TransactionDetailsModel tdm;
//						Iterator<TransactionDetailsModel> aciter = jour
//								.getTransaction().getTransaction_details_list()
//								.iterator();
//						while (aciter.hasNext()) {
//							tdm = aciter.next();
//
//							getSession()
//									.createQuery(
//											"update LedgerModel set current_balance=current_balance+:amt where id=:id")
//									.setDouble("amt", tdm.getAmount())
//									.setLong("id", tdm.getFromAcct().getId())
//									.executeUpdate();
//
//							getSession()
//									.createQuery(
//											"update LedgerModel set current_balance=current_balance-:amt where id=:id")
//									.setDouble("amt", tdm.getAmount())
//									.setLong("id", tdm.getToAcct().getId())
//									.executeUpdate();
//
//							flush();
//						}
//
//						getSession().delete(
//								getSession().get(
//										TransactionModel.class,
//										jour.getTransaction()
//												.getTransaction_id()));
//						getSession().delete(jour);
//					}
//
//					flush();
//					break;
//
//				case SConstants.deleteObjects.BANK_ACCOUNT_DEPOSITS:
//
//					System.out.println("Deleteing BANK_ACCOUNT_DEPOSITS");
//
//					idList = getSession()
//							.createQuery(
//									"select id from BankAccountDepositModel where office_id=:ofc")
//							.setLong("ofc", officeId).list();
//
//					BankAccountDepositModel bankDep;
//					commonItr = idList.iterator();
//					while (commonItr.hasNext()) {
//						bankDep = (BankAccountDepositModel) getSession().get(
//								BankAccountDepositModel.class,
//								(Long) commonItr.next());
//						List transList = getSession().createQuery(
//								"select b from TransactionModel a join a.transaction_details_list b "
//										+ "where a.id="
//										+ bankDep.getTransaction()
//												.getTransaction_id()).list();
//						Iterator<TransactionDetailsModel> aciter = transList
//								.iterator();
//						TransactionDetailsModel tdm;
//						while (aciter.hasNext()) {
//							tdm = aciter.next();
//
//							getSession()
//									.createQuery(
//											"update LedgerModel set current_balance=current_balance+:amt where id=:id")
//									.setDouble("amt", tdm.getAmount())
//									.setLong("id", tdm.getFromAcct().getId())
//									.executeUpdate();
//
//							getSession()
//									.createQuery(
//											"update LedgerModel set current_balance=current_balance-:amt where id=:id")
//									.setDouble("amt", tdm.getAmount())
//									.setLong("id", tdm.getToAcct().getId())
//									.executeUpdate();
//
//							flush();
//
//						}
//
//						getSession().delete(
//								getSession().get(
//										TransactionModel.class,
//										bankDep.getTransaction()
//												.getTransaction_id()));
//						getSession().delete(bankDep);
//					}
//
//					flush();
//					break;
//
//				case SConstants.deleteObjects.BANK_ACCOUNT_PAYMENTS:
//
//					System.out.println("Deleteing BANK_ACCOUNT_PAYMENTS");
//
//					idList = getSession()
//							.createQuery(
//									"select id from BankAccountPaymentModel where office_id=:ofc")
//							.setLong("ofc", officeId).list();
//
//					BankAccountPaymentModel bankPay;
//					commonItr = idList.iterator();
//					while (commonItr.hasNext()) {
//						bankPay = (BankAccountPaymentModel) getSession().get(
//								BankAccountPaymentModel.class,
//								(Long) commonItr.next());
//						List transList = getSession().createQuery(
//								"select b from TransactionModel a join a.transaction_details_list b "
//										+ "where a.id="
//										+ bankPay.getTransaction()
//												.getTransaction_id()).list();
//						Iterator<TransactionDetailsModel> aciter = transList
//								.iterator();
//						TransactionDetailsModel tdm;
//						while (aciter.hasNext()) {
//							tdm = aciter.next();
//
//							getSession()
//									.createQuery(
//											"update LedgerModel set current_balance=current_balance+:amt where id=:id")
//									.setDouble("amt", tdm.getAmount())
//									.setLong("id", tdm.getFromAcct().getId())
//									.executeUpdate();
//
//							getSession()
//									.createQuery(
//											"update LedgerModel set current_balance=current_balance-:amt where id=:id")
//									.setDouble("amt", tdm.getAmount())
//									.setLong("id", tdm.getToAcct().getId())
//									.executeUpdate();
//
//							flush();
//
//						}
//
//						getSession().delete(
//								getSession().get(
//										TransactionModel.class,
//										bankPay.getTransaction()
//												.getTransaction_id()));
//						getSession().delete(bankPay);
//					}
//
//					flush();
//					break;
//
//				case SConstants.deleteObjects.EXPENDETURE_TRANSACTION:
//
//					System.out.println("Deleteing EXPENDETURE_TRANSACTION");
//
//					idList = getSession()
//							.createQuery(
//									"select id from PaymentDepositModel where office_id=:ofc and type=:typ")
//							.setParameter("typ",
//									(long) SConstants.EXPENDETURE_TRANSACTION)
//							.setLong("ofc", officeId).list();
//
//					PaymentDepositModel payDep = null;
//					commonItr = idList.iterator();
//					if (commonItr != null) {
//						while (commonItr.hasNext()) {
//							payDep = (PaymentDepositModel) getSession().get(
//									PaymentDepositModel.class,
//									(Long) commonItr.next());
//
//							Iterator<TransactionDetailsModel> aciter = payDep
//									.getTransaction()
//									.getTransaction_details_list().iterator();
//							TransactionDetailsModel tdm;
//							while (aciter.hasNext()) {
//								tdm = aciter.next();
//
//								getSession()
//										.createQuery(
//												"update LedgerModel set current_balance=current_balance+:amt where id=:id")
//										.setDouble("amt", tdm.getAmount())
//										.setLong("id",
//												tdm.getFromAcct().getId())
//										.executeUpdate();
//
//								getSession()
//										.createQuery(
//												"update LedgerModel set current_balance=current_balance-:amt where id=:id")
//										.setDouble("amt", tdm.getAmount())
//										.setLong("id", tdm.getToAcct().getId())
//										.executeUpdate();
//
//								flush();
//
//							}
//
//							getSession().delete(
//									getSession().get(
//											TransactionModel.class,
//											payDep.getTransaction()
//													.getTransaction_id()));
//							getSession().delete(payDep);
//						}
//					}
//					flush();
//					break;
//
//				case SConstants.deleteObjects.INCOME_TRANSACTION:
//
//					System.out.println("Deleteing INCOME_TRANSACTION");
//
//					idList = getSession()
//							.createQuery(
//									"select id from PaymentDepositModel where office_id=:ofc and type=:typ")
//							.setParameter("typ",
//									(long) SConstants.INCOME_TRANSACTION)
//							.setLong("ofc", officeId).list();
//
//					commonItr = idList.iterator();
//					while (commonItr.hasNext()) {
//						payDep = (PaymentDepositModel) getSession().get(
//								PaymentDepositModel.class,
//								(Long) commonItr.next());
//						Iterator<TransactionDetailsModel> aciter = payDep
//								.getTransaction().getTransaction_details_list()
//								.iterator();
//						TransactionDetailsModel tdm;
//						while (aciter.hasNext()) {
//							tdm = aciter.next();
//
//							getSession()
//									.createQuery(
//											"update LedgerModel set current_balance=current_balance+:amt where id=:id")
//									.setDouble("amt", tdm.getAmount())
//									.setLong("id", tdm.getFromAcct().getId())
//									.executeUpdate();
//
//							getSession()
//									.createQuery(
//											"update LedgerModel set current_balance=current_balance-:amt where id=:id")
//									.setDouble("amt", tdm.getAmount())
//									.setLong("id", tdm.getToAcct().getId())
//									.executeUpdate();
//
//							flush();
//
//						}
//
//						getSession().delete(
//								getSession().get(
//										TransactionModel.class,
//										payDep.getTransaction()
//												.getTransaction_id()));
//						getSession().delete(payDep);
//					}
//
//					flush();
//					break;
//
//				case SConstants.deleteObjects.TRANSPORTATION_PAYMENT:
//
//					System.out.println("Deleteing TRANSPORTATION_PAYMENT");
//
//					idList = getSession()
//							.createQuery(
//									"select id from TransportationPaymentModel where office.id=:ofc")
//							.setLong("ofc", officeId).list();
//
//					TransportationPaymentModel tranMdl;
//					commonItr = idList.iterator();
//					while (commonItr.hasNext()) {
//						tranMdl = (TransportationPaymentModel) getSession()
//								.get(TransportationPaymentModel.class,
//										(Long) commonItr.next());
//						TransactionModel transObj = (TransactionModel) getSession()
//								.get(TransactionModel.class,
//										tranMdl.getTransaction_id());
//						if (transObj != null) {
//							Iterator<TransactionDetailsModel> aciter = transObj
//									.getTransaction_details_list().iterator();
//							TransactionDetailsModel tdm;
//							while (aciter.hasNext()) {
//								tdm = aciter.next();
//
//								getSession()
//										.createQuery(
//												"update LedgerModel set current_balance=current_balance+:amt where id=:id")
//										.setDouble("amt", tdm.getAmount())
//										.setLong("id",
//												tdm.getFromAcct().getId())
//										.executeUpdate();
//
//								getSession()
//										.createQuery(
//												"update LedgerModel set current_balance=current_balance-:amt where id=:id")
//										.setDouble("amt", tdm.getAmount())
//										.setLong("id", tdm.getToAcct().getId())
//										.executeUpdate();
//
//								flush();
//							}
//
//							getSession().delete(transObj);
//							getSession().delete(tranMdl);
//						}
//					}
//
//					flush();
//					break;
//
//				case SConstants.deleteObjects.PAYROLL_TRANSACTIONS:
//
//					System.out.println("Deleteing PAYROLL TRANSACTIONS");
//
//					idList = getSession().createQuery("select id from SalaryDisbursalNewModel where employ.loginId.office.id=:ofc")
//								.setLong("ofc", officeId).list();
//
//					SalaryDisbursalNewModel salDisp;
//					commonItr = idList.iterator();
//					while (commonItr.hasNext()) {
//						salDisp = (SalaryDisbursalNewModel) getSession().get(SalaryDisbursalNewModel.class,(Long) commonItr.next());
//						tr = (TransactionModel) getSession().get(TransactionModel.class,salDisp.getTransaction_id());
//						if (tr != null) {
//							getSession().delete(tr);
//							Iterator<TransactionDetailsModel> aciter = tr.getTransaction_details_list().iterator();
//							TransactionDetailsModel tdm;
//							while (aciter.hasNext()) {
//								tdm = aciter.next();
//								getSession().createQuery("update LedgerModel set current_balance=current_balance+:amt where id=:id")
//										.setDouble("amt", tdm.getAmount())
//										.setLong("id",tdm.getFromAcct().getId()).executeUpdate();
//
//								getSession().createQuery("update LedgerModel set current_balance=current_balance-:amt where id=:id")
//										.setDouble("amt", tdm.getAmount())
//										.setLong("id", tdm.getToAcct().getId()).executeUpdate();
//								flush();
//							}
//						}
//						getSession().delete(salDisp);
//						flush();
//					}
//					
//					idList = getSession().createQuery("select id from SalaryDisbursalModel where employ.loginId.office.id=:ofc")
//							.setLong("ofc", officeId).list();
//
//				SalaryDisbursalModel salObj;
//				commonItr = idList.iterator();
//				while (commonItr.hasNext()) {
//					salObj = (SalaryDisbursalModel) getSession().get(SalaryDisbursalModel.class,(Long) commonItr.next());
//					getSession().delete(salObj);
//					
//				}
//				flush();
//					idList = getSession().createQuery("select a from EmployeeWorkingTimeModel a where a.employee.loginId.office.id=:ofc")
//								.setLong("ofc", officeId).list();
//					commonItr = idList.iterator();
//					EmployeeWorkingTimeModel ewmdl;
//					while (commonItr.hasNext()) {
//						ewmdl = (EmployeeWorkingTimeModel) commonItr.next();
//						getSession().delete(ewmdl);
//					}
//					idList = getSession().createQuery("select a from PayrollEmployeeMapModel a where a.component.office.id=:ofc")
//							.setLong("ofc", officeId).list();
//					commonItr = idList.iterator();
//
//					PayrollEmployeeMapModel pemdl;
//					while (commonItr.hasNext()) {
//						pemdl = (PayrollEmployeeMapModel) commonItr.next();
//						getSession().delete(pemdl);
//					}
//
//					idList = getSession().createQuery("from PayrollComponentModel where office.id=:ofc").setLong("ofc", officeId).list();
//					commonItr = idList.iterator();
//					PayrollComponentModel pcmdl;
//					while (commonItr.hasNext()) {
//						pcmdl = (PayrollComponentModel) commonItr.next();
//						getSession().delete(pcmdl);
//					}
//					
//					flush();
//					System.out.println("PAYROLL TRANSACTIONS Deleted");
//					break;
//
//				case SConstants.deleteObjects.EMPLOYEE_ADVANCES:
//
//					System.out.println("Deleteing EMPLOYEE_ADVANCES");
//
//					idList = getSession()
//							.createQuery(
//									"select id from EmployeeAdvancePaymentModel where office.id=:ofc")
//							.setLong("ofc", officeId).list();
//
//					EmployeeAdvancePaymentModel empAdv;
//					commonItr = idList.iterator();
//					while (commonItr.hasNext()) {
//						empAdv = (EmployeeAdvancePaymentModel) getSession()
//								.get(EmployeeAdvancePaymentModel.class,
//										(Long) commonItr.next());
//						TransactionModel transObj = (TransactionModel) getSession()
//								.get(TransactionModel.class,
//										empAdv.getTransaction_id());
//						if (transObj != null) {
//							Iterator<TransactionDetailsModel> aciter = transObj
//									.getTransaction_details_list().iterator();
//							TransactionDetailsModel tdm;
//							while (aciter.hasNext()) {
//								tdm = aciter.next();
//
//								getSession()
//										.createQuery(
//												"update LedgerModel set current_balance=current_balance+:amt where id=:id")
//										.setDouble("amt", tdm.getAmount())
//										.setLong("id",
//												tdm.getFromAcct().getId())
//										.executeUpdate();
//
//								getSession()
//										.createQuery(
//												"update LedgerModel set current_balance=current_balance-:amt where id=:id")
//										.setDouble("amt", tdm.getAmount())
//										.setLong("id", tdm.getToAcct().getId())
//										.executeUpdate();
//
//								flush();
//							}
//
//							getSession().delete(transObj);
//							getSession().delete(empAdv);
//						}
//					}
//					flush();
//					break;
//
//				case SConstants.deleteObjects.CUSTOMER:
//
//					System.out.println("Deleteing CUSTOMER");
//
//					idList = getSession()
//							.createQuery(
//									"select id from CustomerModel where ledger.office.id=:ofc")
//							.setLong("ofc", officeId).list();
//
//					CustomerModel cust;
//					commonItr = idList.iterator();
//
//					while (commonItr.hasNext()) {
//						cust = (CustomerModel) getSession().get(
//								CustomerModel.class, (Long) commonItr.next());
//						deleteTransaction(cust.getLedger().getId(), cust
//								.getLedger().getOffice().getId());
//						getSession().delete(cust.getLedger().getAddress());
//						getSession().delete(cust.getLedger());
//
//						if (cust.getLogin_id() != 0) {
//							getSession()
//									.createQuery(
//											"delete from S_LoginOptionMappingModel where login_id.id=:log")
//									.setParameter("log", cust.getLogin_id())
//									.executeUpdate();
//
//							getSession()
//									.createQuery(
//											"delete from S_LoginModel where id=:Id")
//									.setParameter("Id", cust.getLogin_id())
//									.executeUpdate();
//						}
//						System.out.println("Reached Here");
//						getSession()
//								.createSQLQuery(
//										"delete from i_transaction_details where id not in "
//												+ "(select details_id from  transaction_details_link) ")
//								.executeUpdate();
//						getSession().delete(cust);
//
//					}
//
//					flush();
//					break;
//
//				case SConstants.deleteObjects.SUPPLIER:
//
//					System.out.println("Deleteing SUPPLIER");
//
//					idList = getSession()
//							.createQuery(
//									"select id from SupplierModel where ledger.office.id=:ofc")
//							.setLong("ofc", officeId).list();
//
//					SupplierModel supp;
//					commonItr = idList.iterator();
//					while (commonItr.hasNext()) {
//						supp = (SupplierModel) getSession().get(
//								SupplierModel.class, (Long) commonItr.next());
//						TransactionModel tdm;
//						deleteTransaction(supp.getLedger().getId(), supp
//								.getLedger().getOffice().getId());
//						getSession().delete(supp.getLedger().getAddress());
//						getSession().delete(supp.getLedger());
//
//						getSession()
//								.createQuery(
//										"delete from S_LoginOptionMappingModel where login_id.id=:log")
//								.setParameter("log", supp.getLogin_id())
//								.executeUpdate();
//
//						getSession()
//								.createQuery(
//										"delete from S_LoginModel where id=:Id")
//								.setParameter("Id", supp.getLogin_id())
//								.executeUpdate();
//						getSession()
//								.createSQLQuery(
//										"delete from i_transaction_details where id not in "
//												+ "(select details_id from  transaction_details_link) ")
//								.executeUpdate();
//
//						getSession().delete(supp);
//
//						flush();
//
//					}
//					flush();
//					break;
//
//				case SConstants.deleteObjects.CONTRACTOR:
//
//					System.out.println("Deleteing CONTRACTOR");
//
//					idList = getSession()
//							.createQuery(
//									"select id from ContractorModel where ledger.office.id=:ofc")
//							.setLong("ofc", officeId).list();
//
//					ContractorModel cont;
//					commonItr = idList.iterator();
//					while (commonItr.hasNext()) {
//						cont = (ContractorModel) getSession().get(
//								ContractorModel.class, (Long) commonItr.next());
//
//						getSession().delete(cont);
//						getSession().delete(cont.getLedger());
//						getSession().delete(cont.getLedger().getAddress());
//					}
//
//					flush();
//					break;
//
//				case SConstants.deleteObjects.TRANSPORTATION:
//
//					System.out.println("Deleteing TRANSPORTATION");
//					idList = getSession()
//							.createQuery(
//									"select id from TranspotationModel where ledger.office.id=:ofc")
//							.setLong("ofc", officeId).list();
//
//					TranspotationModel transMdl;
//					commonItr = idList.iterator();
//					while (commonItr.hasNext()) {
//						try {
//							transMdl = (TranspotationModel) getSession().get(
//									TranspotationModel.class,
//									(Long) commonItr.next());
//							getSession()
//									.createQuery(
//											"select a from TransactionModel a join a.transaction_details_list b where b.fromAcct.id=:id or b.toAcct.id=:id")
//									.setParameter("id",
//											transMdl.getLedger().getId())
//									.list();
//
//							getSession().delete(transMdl);
//							getSession().delete(transMdl.getLedger());
//							getSession().delete(
//									transMdl.getLedger().getAddress());
//							flush();
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//					}
//
//					flush();
//					break;
//
//				case SConstants.deleteObjects.CASH_INVESTMENTS:
//
//					System.out.println("Deleteing CASH_INVESTMENTS");
//
//					idList = getSession()
//							.createQuery(
//									"select id from CashInvestmentModel where office.id=:ofc")
//							.setLong("ofc", officeId).list();
//
//					CashInvestmentModel cashModel;
//					commonItr = idList.iterator();
//					while (commonItr.hasNext()) {
//						cashModel = (CashInvestmentModel) getSession().get(
//								CashInvestmentModel.class,
//								(Long) commonItr.next());
//						TransactionModel transObj = (TransactionModel) getSession()
//								.get(TransactionModel.class,
//										cashModel.getTransaction_id());
//						if (transObj != null) {
//							Iterator<TransactionDetailsModel> aciter = transObj
//									.getTransaction_details_list().iterator();
//							TransactionDetailsModel tdm;
//							while (aciter.hasNext()) {
//								tdm = aciter.next();
//
//								getSession()
//										.createQuery(
//												"update LedgerModel set current_balance=current_balance+:amt where id=:id")
//										.setDouble("amt", tdm.getAmount())
//										.setLong("id",
//												tdm.getFromAcct().getId())
//										.executeUpdate();
//
//								getSession()
//										.createQuery(
//												"update LedgerModel set current_balance=current_balance-:amt where id=:id")
//										.setDouble("amt", tdm.getAmount())
//										.setLong("id", tdm.getToAcct().getId())
//										.executeUpdate();
//
//								flush();
//							}
//
//							getSession().delete(transObj);
//							getSession().delete(cashModel);
//						}
//					}
//
//					flush();
//					break;
//
//				case SConstants.deleteObjects.ITEM_DAILY_RATE:
//
//					System.out.println("Deleteing ITEM_DAILY_RATE");
//
//					idList = getSession()
//							.createQuery(
//									"select id from ItemDailyRateModel where office_id=:ofc")
//							.setLong("ofc", officeId).list();
//
//					ItemDailyRateModel rateMdl;
//					commonItr = idList.iterator();
//					while (commonItr.hasNext()) {
//						rateMdl = (ItemDailyRateModel) getSession().get(
//								ItemDailyRateModel.class,
//								(Long) commonItr.next());
//
//						getSession().delete(rateMdl);
//					}
//
//					flush();
//					break;
//
//				case SConstants.deleteObjects.SUPPLIER_QUOTATION:
//
//					System.out.println("Deleteing SUPPLIER_QUOTATION");
//					try {
//
//						idList = getSession()
//								.createQuery(
//										"from SupplierQuotationModel where office_id=:ofc")
//								.setLong("ofc", officeId).list();
//						SupplierQuotationModel sqm;
//						commonItr = idList.iterator();
//						while (commonItr.hasNext()) {
//							sqm = (SupplierQuotationModel) commonItr.next();
//							getSession().delete(sqm);
//						}
//
//						flush();
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//					break;
//
//				case SConstants.deleteObjects.DAILY_QUOTATION:
//
//					System.out.println("Deleteing DAILY_QUOTATION");
//
//					idList = getSession()
//							.createQuery(
//									"select id from DailyQuotationModel where login.office.id=:ofc")
//							.setLong("ofc", officeId).list();
//
//					DailyQuotationModel dailyModel = null;
//					commonItr = idList.iterator();
//					while (commonItr.hasNext()) {
//						try {
//
//							dailyModel = (DailyQuotationModel) getSession()
//									.get(DailyQuotationModel.class,
//											(Long) commonItr.next());
//
//							getSession().delete(dailyModel);
//
//						} catch (Exception e) {
//
//							System.out.println("Problem Maker Quot"
//									+ dailyModel.getId());
//						}
//					}
//
//					getSession()
//							.createSQLQuery(
//									"delete from i_daily_quotation_details where id not in "
//											+ "(select details_id from  daily_quotation_link)")
//							.executeUpdate();
//
//					flush();
//
//					break;
//
//				case SConstants.deleteObjects.MANUAL_TRADING:
//
//					System.out.println("Deleteing MANUAL_TRADING");
//
//					idList = getSession()
//							.createQuery(
//									"select id from ManualTradingMasterModel where office_id=:ofc")
//							.setLong("ofc", officeId).list();
//
//					ManualTradingMasterModel manualMdl;
//					commonItr = idList.iterator();
//					while (commonItr.hasNext()) {
//						manualMdl = (ManualTradingMasterModel) getSession()
//								.get(ManualTradingMasterModel.class,
//										(Long) commonItr.next());
//
//						getSession().delete(manualMdl);
//					}
//
//					flush();
//					break;
//
//				case SConstants.deleteObjects.BANK_ACCOUNTS:
//
//					System.out.println("Deleteing BANK_ACCOUNTS");
//
//					idList = getSession()
//							.createQuery(
//									"select id from BankAccountModel where ledger.office.id=:ofc")
//							.setLong("ofc", officeId).list();
//
//					BankAccountModel bank;
//					commonItr = idList.iterator();
//					while (commonItr.hasNext()) {
//						try {
//
//							bank = (BankAccountModel) getSession().get(
//									BankAccountModel.class,
//									(Long) commonItr.next());
//
//							getSession().delete(bank);
//							getSession().delete(bank.getLedger());
//							getSession().delete(bank.getLedger().getAddress());
//						} catch (Exception e) {
//						}
//					}
//
//					flush();
//					break;
//
//				case SConstants.deleteObjects.GENERAL_LEDGERS:
//
//					System.out.println("Deleteing GENERAL_LEDGERS");
//
//					List<Long> list = new ArrayList<Long>();
//					list.add(SConstants.SUPPLIER_GROUP_ID);
//					list.add(SConstants.CUSTOMER_GROUP_ID);
//					list.add(SConstants.CONTRACTOR_GROUP_ID);
//					list.add(SConstants.BANK_ACCOUNT_GROUP_ID);
//					list.add(SConstants.TRANSPORTATION_GROUP_ID);
//					list.add(SConstants.INDIRECT_EXPENSE_GROUP_ID);
//
//					idList = getSession()
//							.createQuery(
//									"select id  from LedgerModel where office.id=:ofc "
//											+ "and group.id not in (select id from GroupModel where root_id in (:list) or id in (:list))")
//							.setParameterList("list", list)
//							.setLong("ofc", officeId).list();
//
//					LedgerModel ledg;
//					commonItr = idList.iterator();
//					while (commonItr.hasNext()) {
//						try {
//							ledg = (LedgerModel) getSession().get(
//									LedgerModel.class, (Long) commonItr.next());
//
//							getSession()
//									.createQuery(
//											"delete from AddressModel where id in (select address.id from LedgerModel where office.id=:ofc)")
//									.setLong("ofc", officeId).executeUpdate();
//
//							tr = (TransactionModel) getSession()
//									.createQuery(
//											"select a from TransactionModel a join a.transaction_details_list b where b.fromAcct.id=:id or b.toAcct.id=:id")
//									.setParameter("id", ledg.getId())
//									.uniqueResult();
//
//							getSession().delete(ledg);
//							getSession().delete(tr);
//							if (ledg.getAddress() != null)
//								getSession().delete(ledg.getAddress());
//						} catch (Exception e) {
//						}
//					}
//
//					flush();
//					break;
//
//				case SConstants.deleteObjects.STOCK_TRANSFER:
//
//					System.out.println("Deleteing STOCK_TRANSFER");
//					idList = getSession()
//							.createQuery(
//									"select id from StockTransferModel where office.id=:ofc")
//							.setLong("ofc", officeId).list();
//
//					StockTransferModel stkTr;
//					commonItr = idList.iterator();
//					while (commonItr.hasNext()) {
//						stkTr = (StockTransferModel) getSession().get(
//								StockTransferModel.class,
//								(Long) commonItr.next());
//						StockTransferInventoryDetails invObj;
//						Iterator<StockTransferInventoryDetails> it = stkTr
//								.getInventory_details_list().iterator();
//						while (it.hasNext()) {
//							invObj = it.next();
//
//							// For Stock Update
//							getSession()
//									.createQuery(
//											"update ItemStockModel set balance=balance+:qty where id=:id")
//									.setLong("id", invObj.getStock_id())
//									.setDouble("qty", invObj.getQunatity())
//									.executeUpdate();
//
//							getSession()
//									.createQuery(
//											"update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
//									.setParameter("id",
//											invObj.getItem().getId())
//									.setParameter("qty",
//											invObj.getQuantity_in_basic_unit())
//									.executeUpdate();
//						}
//
//						getSession().delete(stkTr);
//
//					}
//
//					flush();
//
//					idList = getSession()
//							.createQuery(
//									"select id from ItemTransferModel where office.id=:ofc")
//							.setLong("ofc", officeId).list();
//
//					ItemTransferModel itmTr;
//					commonItr = idList.iterator();
//					while (commonItr.hasNext()) {
//						itmTr = (ItemTransferModel) getSession().get(
//								ItemTransferModel.class,
//								(Long) commonItr.next());
//
//						getSession().delete(itmTr);
//					}
//
//					flush();
//					break;
//
//				case SConstants.deleteObjects.STOCK:
//
//					System.out.println("Deleteing STOCK");
//
//					idList = getSession()
//							.createQuery(
//									"select id from ItemStockModel where item.office.id=:ofc")
//							.setLong("ofc", officeId).list();
//
//					ItemStockModel stk;
//					commonItr = idList.iterator();
//					while (commonItr.hasNext()) {
//						stk = (ItemStockModel) getSession().get(
//								ItemStockModel.class, (Long) commonItr.next());
//
//						getSession()
//								.createQuery(
//										"delete from StockRackMappingModel where stock.id=:stk")
//								.setParameter("stk", stk.getId())
//								.executeUpdate();
//						getSession().delete(stk);
//						flush();
//					}
//
//					break;
//
//				case SConstants.deleteObjects.ITEM:
//
//					System.out.println("Deleteing ITEM");
//
//					idList = getSession()
//							.createQuery(
//									"select id from ItemModel where office.id=:ofc")
//							.setLong("ofc", officeId).list();
//
//					ItemModel itm;
//					commonItr = idList.iterator();
//					while (commonItr.hasNext()) {
//
//						try {
//							itm = (ItemModel) getSession().get(ItemModel.class,
//									(Long) commonItr.next());
//
//							getSession()
//									.createQuery(
//											"delete from ItemClosingStockModel where item.id=:itm")
//									.setParameter("itm", itm.getId())
//									.executeUpdate();
//
//							getSession()
//									.createQuery(
//											"delete from StockResetDetailsModel where item.id=:itm")
//									.setParameter("itm", itm.getId())
//									.executeUpdate();
//
//							getSession()
//
//									.createQuery(
//											"delete from ItemPriceModel where item.id=:itm")
//									.setParameter("itm", itm.getId())
//									.executeUpdate();
//
//							getSession()
//									.createQuery(
//											"delete from ItemUnitMangementModel where item.id=:itm")
//									.setParameter("itm", itm.getId())
//									.executeUpdate();
//							getSession()
//									.createQuery(
//											"delete from SalesReturnInventoryDetailsModel where item.id=:itm")
//									.setParameter("itm", itm.getId())
//									.executeUpdate();
//							getSession()
//									.createSQLQuery(
//											"delete from i_sales_return where id not in "
//													+ "(select 	master_id from sales_return_link) ")
//									.executeUpdate();
//							getSession()
//									.createQuery(
//											"delete from ItemModel where id=:itm")
//									.setParameter("itm", itm.getId())
//									.executeUpdate();
//
//							flush();
//
//						} catch (Exception e) {
//
//						}
//					}
//
//					flush();
//
//					break;
//
//				case SConstants.deleteObjects.TAX:
//
//					System.out.println("Deleteing TAX");
//
//					idList = getSession()
//							.createQuery(
//									"select id from TaxModel where office.id=:ofc")
//							.setLong("ofc", officeId).list();
//
//					TaxModel tax;
//					commonItr = idList.iterator();
//					while (commonItr.hasNext()) {
//						tax = (TaxModel) getSession().get(TaxModel.class,
//								(Long) commonItr.next());
//
//						getSession().delete(tax);
//					}
//
//					flush();
//					break;
//
//				case SConstants.deleteObjects.WORK_ORDER:
//
//					System.out.println("Deleteing WORK_ORDER");
//
//					idList = getSession()
//							.createQuery(
//									"select new com.inventory.sales.model.WorkOrderModel(id,cast(work_order_number as string) )"
//											+ " from WorkOrderModel where office.id=:ofc")
//							.setLong("ofc", officeId).list();
//					WorkOrderModel wom;
//					commonItr = idList.iterator();
//					while (commonItr.hasNext()) {
//						wom = (WorkOrderModel) commonItr.next();
//						SalesInventoryDetailsModel invObj;
//						Iterator<SalesInventoryDetailsModel> it = wom
//								.getInventory_details_list().iterator();
//						while (it.hasNext()) {
//							invObj = it.next();
//
//							getSession()
//									.createQuery(
//											"update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
//									.setParameter("id",
//											invObj.getItem().getId())
//									.setParameter("qty", invObj.getQunatity())
//									.executeUpdate();
//
//							comDao.increaseStock(invObj.getItem().getId(),
//									invObj.getQuantity_in_basic_unit());
//
//						}
//
//						getSession().delete(wom);
//					}
//					flush();
//					break;
//
//				case SConstants.deleteObjects.BUDGET:
//
//					System.out.println("Deleteing BUDGET");
//					idList = getSession()
//							.createQuery(
//									"from BudgetLVMasterModel where office_id.id=:ofc")
//							.setParameter("ofc", officeId).list();
//					commonItr = idList.iterator();
//					BudgetLVMasterModel bdm;
//					while (commonItr.hasNext()) {
//						bdm = (BudgetLVMasterModel) commonItr.next();
//						getSession().delete(bdm);
//						flush();
//					}
//					idList = getSession()
//							.createQuery(
//									"from BudgetModel where office_id.id=:ofc")
//							.setParameter("ofc", officeId).list();
//					commonItr = idList.iterator();
//					BudgetModel budMdl;
//					while (commonItr.hasNext()) {
//						budMdl = (BudgetModel) commonItr.next();
//						getSession().delete(budMdl.getBudgetDef_id());
//						getSession().delete(budMdl);
//						flush();
//					}
//					idList = getSession()
//							.createQuery(
//									"from BudgetDefinitionModel where office_id.id=:ofc")
//							.setParameter("ofc", officeId).list();
//					commonItr = idList.iterator();
//					BudgetDefinitionModel bdMdl;
//					while (commonItr.hasNext()) {
//						bdMdl = (BudgetDefinitionModel) commonItr.next();
//						getSession().delete(bdMdl);
//						flush();
//					}
//					flush();
//					break;
//
//				case SConstants.deleteObjects.FINANCE_COMPONENT:
//
//					System.out.println("Deleteing FINANCE COMPONENT");
//
//					idList = getSession()
//							.createQuery(
//									"select a from FinanceComponentModel a where officeId=:id")
//							.setParameter("id", officeId).list();
//					commonItr = idList.iterator();
//					FinanceComponentModel fcm;
//					while (commonItr.hasNext()) {
//						fcm = (FinanceComponentModel) commonItr.next();
//						getSession().delete(fcm);
//					}
//					flush();
//					break;
//
//				case SConstants.deleteObjects.FINANCE_PAYMENT:
//
//					System.out.println("Deleteing FINANCE_PAYMENT");
//					int count = 0;
//					idList = getSession()
//							.createQuery(
//									"select distinct a from FinancePaymentModel a join a.finance_payment_list b where b.from_account.officeId=:ofc order by a.id desc")
//							.setParameter("ofc", (long) officeId).list();
//					commonItr = idList.iterator();
//					FinancePaymentModel objMdl;
//					while (commonItr.hasNext()) {
//						objMdl = (FinancePaymentModel) commonItr.next();
//
//						if (objMdl != null) {
//							FinancePaymentDetailsModel invObj;
//							Iterator<FinancePaymentDetailsModel> itr = objMdl
//									.getFinance_payment_list().iterator();
//							while (itr.hasNext()) {
//								invObj = itr.next();
//
//								getSession()
//										.createQuery(
//												"update FinanceComponentModel set current_balance=current_balance + :amnt where id=:id")
//										.setParameter("amnt",
//												invObj.getAmount())
//										.setParameter(
//												"id",
//												invObj.getFrom_account()
//														.getId())
//										.executeUpdate();
//
//								getSession()
//										.createQuery(
//												"update FinanceComponentModel set current_balance=current_balance - :amnt where id=:id")
//										.setParameter("amnt",
//												invObj.getAmount())
//										.setParameter("id",
//												invObj.getTo_account().getId())
//										.executeUpdate();
//
//							}
//							getSession().delete(objMdl);
//							flush();
//						}
//					}
//					flush();
//					break;
//
//				case SConstants.deleteObjects.RENT:
//
//					System.out.println("Deleteing Rent");
//
//					idList = getSession()
//							.createQuery(
//									"select a from RentDetailsModel a where office.id=:id")
//							.setParameter("id", officeId).list();
//					getSession().createSQLQuery(
//							"delete from rent_management where id not in "
//									+ "( select master_id from rent_inv_link)")
//							.executeUpdate();
//					commonItr = idList.iterator();
//					RentDetailsModel rdMdl;
//					while (commonItr.hasNext()) {
//						rdMdl = (RentDetailsModel) commonItr.next();
//						TransactionModel transObj = (TransactionModel) getSession()
//								.get(TransactionModel.class,
//										rdMdl.getTransaction_id());
//						TransactionDetailsModel tdm;
//						Iterator<TransactionDetailsModel> aciter = transObj
//								.getTransaction_details_list().iterator();
//						while (aciter.hasNext()) {
//							tdm = aciter.next();
//
//							getSession()
//									.createQuery(
//											"update LedgerModel set current_balance=current_balance+:amt where id=:id")
//									.setDouble("amt", tdm.getAmount())
//									.setLong("id", tdm.getFromAcct().getId())
//									.executeUpdate();
//
//							getSession()
//									.createQuery(
//											"update LedgerModel set current_balance=current_balance-:amt where id=:id")
//									.setDouble("amt", tdm.getAmount())
//									.setLong("id", tdm.getToAcct().getId())
//									.executeUpdate();
//
//							flush();
//						}
//
//						getSession().delete(transObj);
//
//						RentInventoryDetailsModel invObj;
//						List list1;
//						Iterator<RentInventoryDetailsModel> it = rdMdl
//								.getInventory_details_list().iterator();
//						while (it.hasNext()) {
//							invObj = it.next();
//
//							getSession()
//									.createQuery(
//											"update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
//									.setParameter("id",
//											invObj.getItem().getId())
//									.setParameter("qty",
//											invObj.getQuantity_in_basic_unit())
//									.executeUpdate();
//
//							flush();
//
//							comDao.increaseStock(invObj.getItem().getId(),
//									invObj.getQuantity_in_basic_unit());
//
//						}
//						List retlist = getSession()
//								.createQuery(
//										"select a from RentReturnItemDetailModel a where rent_number=:rid")
//								.setParameter("rid", rdMdl.getId()).list();
//						Iterator retitr = retlist.iterator();
//						RentReturnItemDetailModel mdl;
//						while (retitr.hasNext()) {
//							mdl = (RentReturnItemDetailModel) retitr.next();
//							getSession().delete(mdl);
//						}
//						List idlist = getSession()
//								.createQuery(
//										"select a from RentPaymentModel a where office.id=:id and rent_number=:rid")
//								.setParameter("id", officeId)
//								.setParameter("rid", rdMdl.getId()).list();
//						Iterator itr = idlist.iterator();
//						RentPaymentModel rpMdl;
//						while (itr.hasNext()) {
//							rpMdl = (RentPaymentModel) itr.next();
//							getSession().delete(rpMdl);
//						}
//						getSession().delete(rdMdl);
//					}
//
//					flush();
//					break;
//
//				// case SConstants.deleteObjects.TASK:
//				//
//				// System.out.println("Deleteing TASK");
//				// long id = (Long) getSession()
//				// .createQuery(
//				// "select organization.id from S_OfficeModel where id=:id")
//				// .setParameter("id", officeId).uniqueResult();
//				// idList = getSession()
//				// .createQuery(
//				// "select a from TasksModel a join a.componentDetailsList b where b.task_component.organization_id=:id")
//				// .setParameter("id", id).list();
//				// System.out.println("Size = " + idList.size());
//				// commonItr = idList.iterator();
//				// System.out.println(idList.size());
//				// TasksModel tmdl;
//				// while (commonItr.hasNext()) {
//				// tmdl = (TasksModel) commonItr.next();
//				//
//				// getSession().delete(tmdl);
//				// }
//				//
//				// flush();
//				// break;
//
//				case SConstants.deleteObjects.TASK:
//
//					System.out.println("Deleteing TASK");
//
//					idList = getSession()
//							.createQuery(
//									"select a from TasksModel a join a.assignedList b where b.user.office.id=:oid")
//							.setParameter("oid", officeId).list();
//					System.out.println("Size = " + idList.size());
//					commonItr = idList.iterator();
//					System.out.println(idList.size());
//					TasksModel tmdl;
//					while (commonItr.hasNext()) {
//						tmdl = (TasksModel) commonItr.next();
//
//						getSession().delete(tmdl);
//					}
//
//					flush();
//					break;
//
//				case SConstants.deleteObjects.USERS:
//
//					System.out.println("Deleteing USER");
//
//					idList = getSession()
//							.createQuery(
//									"select a from S_LoginModel a where office.id=:ofc and userType.id !=:id")
//							.setParameter("ofc", officeId)
//							.setParameter("id", (long) 1).list();
//
//					System.out.println("Id List SIze = " + idList.size());
//
//					commonItr = idList.iterator();
//					S_LoginModel lmdl;
//					UserModel userMdl;
//					SalaryDisbursalNewModel smdl;
//
//					SalesInventoryDetailsModel simdl;
//					SalesModel salmdl;
//					List salaryList;
//					Iterator salIterator;
//					Object obj;
//					while (commonItr.hasNext()) {
//						lmdl = (S_LoginModel) commonItr.next();
//						obj = getSession()
//								.createQuery(
//										"from UserModel where loginId.id=:itm")
//								.setParameter("itm", lmdl.getId())
//								.uniqueResult();
//						getSession()
//								.createQuery(
//										"delete from S_LoginOptionMappingModel where login_id.id=:itm")
//								.setParameter("itm", lmdl.getId())
//								.executeUpdate();
//
//						System.out.println("Current Id = " + lmdl.getId());
//						ContactModel cmdl = null;
//						List contactList = getSession()
//								.createQuery(
//										"from ContactModel where login.id=:id")
//								.setParameter("id", lmdl.getId()).list();
//						System.out.println("Contact List Size = "
//								+ contactList.size());
//						Iterator contactIterator = contactList.iterator();
//						while (contactIterator.hasNext()) {
//							cmdl = (ContactModel) contactIterator.next();
//							System.out.println("Contact Id = " + cmdl.getId());
//							getSession()
//									.createQuery(
//											"delete from MailModel where contact_id=:cnt")
//									.setLong("cnt", cmdl.getId())
//									.executeUpdate();
//							getSession().delete(cmdl);
//						}
//						flush();
//
//						if (obj != null) {
//							try {
//								userMdl = (UserModel) obj;
////								deleteTransactions(userMdl.getLedger().getId());
////								System.out.println("Ledger Id = "
////										+ userMdl.getLedger().getId());
////								List salesList = getSession()
////										.createQuery(
////												"from SalesModel where customer.id=:id")
////										.setParameter("id",
////												userMdl.getLedger().getId())
////										.list();
////								if (salesList.size() != 0) {
//
////								} else {
////									getSession().delete(userMdl.getLedger());
//									getSession().delete(userMdl.getAddress());
////									if (userMdl.getLedger().getAddress() != null)
////										getSession().delete(
////												userMdl.getLedger()
////														.getAddress());
//									getSession().delete(userMdl);
//								flush();
//							} catch (Exception e) {
//								e.printStackTrace();
//							}
//						}
//
//						flush();
//
//						getSession().delete(lmdl);
//					}
//
//					flush();
//					break;
//
//				case SConstants.deleteObjects.BUILDING:
//
//					System.out.println("Deleteing BUILDING");
//
//					idList = getSession()
//							.createQuery(
//									"select id from RackModel where room.building.office.id=:ofc")
//							.setLong("ofc", officeId).list();
//
//					RackModel rac;
//					commonItr = idList.iterator();
//					while (commonItr.hasNext()) {
//						rac = (RackModel) getSession().get(RackModel.class,
//								(Long) commonItr.next());
//
//						getSession().delete(rac);
//						getSession().delete(rac.getRoom());
//						getSession().delete(rac.getRoom().getBuilding());
//					}
//
//					flush();
//					break;
//					
//				case SConstants.deleteObjects.RENTAL:
//
//					System.out.println("Deleteing Rental");
//
//					idList = getSession().createQuery("select id from SubscriptionPaymentModel where subscription.subscription.subscription_type.officeId=:ofc order by id DESC").setLong("ofc", officeId).list();
//					SubscriptionPaymentModel mdl;
//					commonItr = idList.iterator();
//					while (commonItr.hasNext()) {
//						mdl = (SubscriptionPaymentModel) getSession().get(SubscriptionPaymentModel.class,(Long) commonItr.next());
//						
//						if(mdl.getCredit_transaction()!=0){
//							TransactionModel trans = (TransactionModel) getSession().get(TransactionModel.class, mdl.getCredit_transaction());
//							Iterator<TransactionDetailsModel> acitr = trans.getTransaction_details_list().iterator();
//							TransactionDetailsModel tdm;
//							while (acitr.hasNext()) {
//								tdm = acitr.next();
//								getSession()
//										.createQuery(
//												"update LedgerModel set current_balance=current_balance+:amt where id=:id")
//										.setDouble("amt", tdm.getAmount())
//										.setLong("id", tdm.getFromAcct().getId())
//										.executeUpdate();
//								getSession()
//										.createQuery(
//												"update LedgerModel set current_balance=current_balance-:amt where id=:id")
//										.setDouble("amt", tdm.getAmount())
//										.setLong("id", tdm.getToAcct().getId()).executeUpdate();
//								flush();
//							}
//							getSession().delete(trans);
//							flush();
//						}
//						
//						TransactionModel transObj = (TransactionModel) getSession().get(TransactionModel.class, mdl.getTransaction_id());
//						Iterator<TransactionDetailsModel> aciter = transObj.getTransaction_details_list().iterator();
//						TransactionDetailsModel trs;
//						while (aciter.hasNext()) {
//							trs = aciter.next();
//							getSession().createQuery("update LedgerModel set current_balance=current_balance+:amt where id=:id")
//									.setDouble("amt", trs.getAmount())
//									.setLong("id", trs.getFromAcct().getId())
//									.executeUpdate();
//
//							getSession().createQuery("update LedgerModel set current_balance=current_balance-:amt where id=:id")
//									.setDouble("amt", trs.getAmount())
//									.setLong("id", trs.getToAcct().getId()).executeUpdate();
////							flush();
////							getSession().delete(trs);
//							flush();
//						}
//						getSession().delete(transObj);
//						flush();
//						getSession().delete(mdl);
//						flush();
//					}
//					flush();
//					
//					
//					idList = getSession().createQuery("select id from SubscriptionInModel where subscription.subscription_type.officeId=:ofc order by id DESC")
//							.setLong("ofc", officeId).list();
//					SubscriptionInModel ssimdl;
//					commonItr = idList.iterator();
//					while (commonItr.hasNext()) {
//						ssimdl = (SubscriptionInModel) getSession().get(SubscriptionInModel.class,(Long) commonItr.next());
//						getSession().delete(ssimdl);
//						flush();
//					}
//					flush();
//					
//					
//					idList = getSession().createQuery("select id from SubscriptionCreationModel where subscription_type.officeId=:ofc order by id DESC")
//							.setLong("ofc", officeId).list();
//					SubscriptionCreationModel sscmdl;
//					commonItr = idList.iterator();
//					while (commonItr.hasNext()) {
//						sscmdl = (SubscriptionCreationModel) getSession().get(SubscriptionCreationModel.class,(Long) commonItr.next());
//						getSession().delete(sscmdl);
//						flush();
//						getSession().delete(sscmdl.getLedger());
//						flush();
//					}
//					flush();
//					
//					
//					idList = getSession().createQuery("select id from SubscriptionConfigurationModel where officeId=:ofc order by id DESC")
//							.setLong("ofc", officeId).list();
//					SubscriptionConfigurationModel ssccmdl;
//					commonItr = idList.iterator();
//					while (commonItr.hasNext()) {
//						ssccmdl = (SubscriptionConfigurationModel) getSession().get(SubscriptionConfigurationModel.class,(Long) commonItr.next());
//						getSession().delete(ssccmdl);
//						flush();
//					}
//					flush();
//					
//					
//					
//					flush();
//					break;	
//
//				case SConstants.deleteObjects.REMAINING:
//
//					System.out.println("Deleteing REMAINING");
//
//					try {
//
//						getSession()
//								.createQuery(
//										"delete from AddressModel where id in (select address.id from LedgerModel where office.id=:ofc and address!=null)")
//								.setLong("ofc", officeId).executeUpdate();
//						getSession()
//								.createQuery(
//										"delete from LedgerModel where office.id=:ofc")
//								.setLong("ofc", officeId).executeUpdate();
//
//					} catch (Exception e) {
//					}
//
//					flush();
//					break;
//
//				default:
//					break;
//				}


	private void deleteTransactions(long ledgId) {

		try {
			TransactionModel tr;
			List tranSList = getSession()
					.createQuery(
							"select a from TransactionModel a join a.transaction_details_list b where b.fromAcct.id=:id or b.toAcct.id=:id")
					.setParameter("id", ledgId).list();
			System.out.println("List size " + tranSList.size());
			if (tranSList.size() != 0) {
				Iterator iter = tranSList.iterator();
				while (iter.hasNext()) {
					tr = (TransactionModel) iter.next();
					if (tr != null) {
						getSession().delete(tr);
						System.out.println("Deleted");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Couldnt Delete");
		}
	}

	public void deleteSales(long id) {
		try {
			System.out.println("Id Passed here = " + id);

			List salesList = getSession()
					.createQuery("from SalesModel where customer.id=:id")
					.setParameter("id", id).list();
			System.out.println("Sales List Size + " + salesList.size());
			if (salesList.size() != 0) {
				SalesModel obj = null;
				Iterator itr = salesList.iterator();
				while (itr.hasNext()) {
					obj = (SalesModel) getSession().get(SalesModel.class,
							(Long) itr.next());
				}

				if (obj != null) {
					System.out.println(obj.getId());
					SalesInventoryDetailsModel invObj;
					List list;
					Iterator<SalesInventoryDetailsModel> it = obj
							.getInventory_details_list().iterator();
					while (it.hasNext()) {
						invObj = it.next();

						if (invObj.getOrder_id() != 0) {

							list = getSession()
									.createQuery(
											"select b.id from SalesModel a join a.inventory_details_list b  where a.customer.id=:id "
													+ "and b.item.id=:itm and b.unit.id=:un ")
									.setParameter("itm",
											invObj.getItem().getId())
									.setParameter("id", id)
									.setParameter("un",
											invObj.getUnit().getId()).list();

							getSession()
									.createQuery(
											"update SalesInventoryDetailsModel set balance=balance+:qty where id in (:lst)")
									.setParameterList("lst", list)
									.setParameter("qty", invObj.getQunatity())
									.executeUpdate();
						}

						getSession()
								.createQuery(
										"update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
								.setParameter("id", invObj.getItem().getId())
								.setParameter("qty",
										invObj.getQuantity_in_basic_unit())
								.executeUpdate();

						comDao.increaseStock(invObj.getItem().getId(),
								invObj.getQuantity_in_basic_unit());

					}

					getSession().delete(obj);

					getSession()
							.createQuery(
									"delete from SalesStockMapModel where salesId=:id")
							.setLong("id", obj.getId()).executeUpdate();
				}
			} else {
				System.out.println("Object is null");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void deleteTransaction(long ledgId, long office) {

		try {
			TransactionModel tr;
			List tranSList = getSession()
					.createQuery(
							"select a from TransactionModel a join a.transaction_details_list b where b.fromAcct.id=:id or b.toAcct.id=:id and a.office.id=:oid")
					.setParameter("id", ledgId).setParameter("oid", office)
					.list();
			System.out.println(ledgId + " Printing IDS " + office);
			if (tranSList != null) {
				Iterator iter = tranSList.iterator();

				while (iter.hasNext()) {
					tr = (TransactionModel) iter.next();
					if (tr != null) {
						// getSession().delete(tr.getTransaction_details_list());
						getSession().delete(tr);
						System.out.println("Deleted");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Couldnt Delete");
		}
	}

	public void decreaseRackQty(long rack_id, long stk_id, double qty)
			throws Exception {
		try {

			if (rack_id != 0) {

				double bal = qty;
				StockRackMappingModel obj;

				List lst = getSession()
						.createQuery(
								"select a from StockRackMappingModel a where a.rack.id=:rack and a.stock.id=:stk and a.balance>0 ")
						.setLong("rack", rack_id).setLong("stk", stk_id).list();

				if (lst == null || lst.size() <= 0) {
					lst = getSession()
							.createQuery(
									"select a from StockRackMappingModel a where a.rack.id=:rack and a.stock.id=:stk order by a.id desc")
							.setLong("rack", rack_id).setLong("stk", stk_id)
							.list();
					if (lst.size() > 0) {
						obj = (StockRackMappingModel) lst.get(0);
						obj.setBalance(obj.getBalance() - bal);
						getSession().update(obj);
					}
				} else {
					for (int i = 0; i < lst.size(); i++) {
						obj = (StockRackMappingModel) lst.get(i);

						if (obj.getBalance() > bal) {
							obj.setBalance(CommonUtil.roundNumber(obj
									.getBalance() - bal));
							getSession().update(obj);
							flush();
							bal = 0;
							break;
						} else {
							bal = CommonUtil
									.roundNumber(bal - obj.getBalance());
							obj.setBalance(0);
							getSession().update(obj);
							flush();
						}

						if (bal <= 0)
							break;

						if (i == (lst.size() - 1) && bal > 0) {
							obj.setBalance(obj.getBalance() - bal);
							getSession().update(obj);
						}
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}
	}

	public void increaseRackQty(long rack_id, long stk_id, double qty)
			throws Exception {
		try {

			if (rack_id != 0) {

				List lst = getSession()
						.createQuery(
								"select a from StockRackMappingModel a where a.rack.id=:rack and a.stock.id=:stk and a.balance!=0")
						.setLong("rack", rack_id).setLong("stk", stk_id).list();

				if (lst == null || lst.size() <= 0) {
					lst = getSession()
							.createQuery(
									"select a from StockRackMappingModel a where a.rack.id=:rack and a.stock.id=:stk  order by a.id desc ")
							.setLong("rack", rack_id).setLong("stk", stk_id)
							.list();
				}

				if (lst.size() > 0) {
					StockRackMappingModel obj = (StockRackMappingModel) lst
							.get(0);
					obj.setBalance(CommonUtil.roundNumber(obj.getBalance()
							+ qty));
					getSession().update(obj);
				}

			}
		} catch (Exception e) {
			throw e;
		}
	}

	public void deletePurchReturn(long id) throws Exception {

		try {

			PurchaseReturnModel obj = (PurchaseReturnModel) getSession().get(
					PurchaseReturnModel.class, id);

			// Transaction Related

			TransactionModel transObj = (TransactionModel) getSession().get(
					TransactionModel.class, obj.getTransaction_id());

			TransactionDetailsModel tr;
			Iterator<TransactionDetailsModel> aciter = transObj
					.getTransaction_details_list().iterator();
			while (aciter.hasNext()) {
				tr = aciter.next();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getFromAcct().getId())
						.executeUpdate();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tr.getAmount())
						.setLong("id", tr.getToAcct().getId()).executeUpdate();

				flush();
			}

			getSession().delete(transObj);

			flush();

			// Transaction Related

			PurchaseReturnInventoryDetailsModel invObj;
			List list;
			Iterator<PurchaseReturnInventoryDetailsModel> it = obj
					.getInventory_details_list().iterator();
			while (it.hasNext()) {
				invObj = it.next();

				/*
				 * if (invObj.getOrder_id() != 0) { list=null; list =
				 * getSession() .createQuery(
				 * "select b.id from PurchaseModel a join a.inventory_details_list b  where a.id=:id "
				 * + "and b.item.id=:itm and b.unit.id=:un ")
				 * .setParameter("itm", invObj.getItem().getId())
				 * .setParameter("id", invObj.getOrder_id()) .setParameter("un",
				 * invObj.getUnit().getId()) .list();
				 * 
				 * getSession() .createQuery(
				 * "update PurchaseInventoryDetailsModel set balance=balance+:qty where id in (:lst)"
				 * ) .setParameterList("lst", list) .setParameter("qty",
				 * invObj.getQunatity()) .executeUpdate(); }
				 */

				getSession()
						.createQuery(
								"update ItemModel set current_balalnce=current_balalnce+:qty where id=:id")
						.setParameter("id", invObj.getItem().getId())
						.setParameter("qty", invObj.getQty_in_basic_unit())
						.executeUpdate();
				flush();

				comDao.increaseStock(invObj.getItem().getId(),
						invObj.getQty_in_basic_unit());

			}

			getSession().delete(obj);

		} catch (Exception e) {
			throw e;
		}
		flush();

	}
}
