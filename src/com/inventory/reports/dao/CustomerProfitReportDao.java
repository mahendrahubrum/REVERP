package com.inventory.reports.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.config.acct.model.CustomerModel;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.payment.model.TransportationPaymentModel;
import com.inventory.purchase.model.ItemStockModel;
import com.inventory.sales.model.LaundrySalesDetailsModel;
import com.inventory.sales.model.LaundrySalesModel;
import com.inventory.sales.model.SalesInventoryDetailsModel;
import com.inventory.sales.model.SalesModel;
import com.inventory.transaction.model.TransactionDetailsModel;
import com.inventory.transaction.model.TransactionModel;
import com.webspark.bean.ReportBean;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;
import com.webspark.model.S_LoginModel;

/**
 * @author Jinshad P.T.
 * 
 * WebSpark.
 *
 * Jan 23 2014
 */
public class CustomerProfitReportDao extends SHibernate implements Serializable {
	
	private static final long serialVersionUID = -1071364669984964064L;
	
	CommonMethodsDao comDao=new CommonMethodsDao();
	
	public List<Object> getSalesProfitDetails(long custId,
			Date fromDate, Date toDate, long officeId) throws Exception {
		List<Object> resultList = new ArrayList<Object>();
		ItemStockModel stkObj=null;
//		UnitModel unitObj=null;
		try {
			
			begin();
			
			int count=0;
			String condition = "";
			if (custId != 0) {
				condition += " and customer.id=" + custId;
			}
			if (officeId != 0) {
				condition += " and office.id=" + officeId;
			}
			
			if (custId != 0) {
				
				LedgerModel cust = (LedgerModel) getSession().get(LedgerModel.class, custId);
				
				List list = getSession()
						.createQuery("from SalesModel where date>=:fromDate and date<=:toDate and active=true "
										+ condition).setParameter("fromDate", fromDate)
										.setParameter("toDate", toDate).list();
				
				ReportBean rptModel;
				SalesModel salObj;
				double purchase_amt=0;
				double averageAmount=0;
				SalesInventoryDetailsModel invObj;
				String[] stks;
				long stockId;
				double qty=0, saledValue=0, purchasedValue=0, profit=0;
				Object obj;
				
				rptModel=new ReportBean();
				
				rptModel.setClient_name(cust.getName());
//				rptModel.setDate(CommonUtil.formatSQLDateToDDMMMYYYY(salObj.getDate()));
				
//				rptModel.setParticulars(String.valueOf(salObj.getSales_number()));
				Iterator it=list.iterator();
				while (it.hasNext()) {
					
					salObj = (SalesModel) it.next();
					averageAmount = 0;
					rptModel.setId(salObj.getId());
					Iterator it2 = salObj.getInventory_details_list()
							.iterator();
					while (it2.hasNext()) {
						invObj = (SalesInventoryDetailsModel) it2.next();
						count = 0;
						stockId = 0;
						qty = 0;
						purchase_amt = 0;
						if (invObj.getStock_ids().length() > 0) {
							stks = invObj.getStock_ids().toString().split(",");
							for (int i = 0; i < stks.length; i++) {
								try {
									
									stockId = Long.parseLong(stks[0].split(":")[0]);
									qty = Double.parseDouble(stks[0].split(":")[1]);
									
									obj = getSession().createQuery("from ItemStockModel where id="
													+ stockId).uniqueResult();
									if (obj != null) {
										stkObj=(ItemStockModel) obj;
										
										purchase_amt += stkObj.getRate();
										count++;
									}
								}
								catch (Exception e) {
								}

							}
						}
						if (purchase_amt != 0)
							averageAmount += (purchase_amt / count)
									* invObj.getQuantity_in_basic_unit();
					}

					saledValue += salObj.getAmount();
					purchasedValue += averageAmount;
				}
				
				if(saledValue>0) {
					rptModel.setInwards(saledValue);
					rptModel.setOutwards(purchasedValue);
					rptModel.setProfit(saledValue-purchasedValue);
					
					resultList.add(rptModel);
				}
			}
			else {
				
				List custList = getSession()
						.createQuery(
								"select new com.inventory.config.acct.model.CustomerModel(ledger.id, name)"
										+ " from CustomerModel where ledger.office.id=:ofc order by name")
						.setParameter("ofc", officeId).list();
				
				ReportBean rptModel;
				SalesModel salObj;
				double purchase_amt=0;
				double averageAmount=0;
				SalesInventoryDetailsModel invObj;
				String[] stks;
				long stockId;
				double qty=0, saledValue=0, purchasedValue=0, profit=0;
				Object obj;
				
				Iterator itr1=custList.iterator();
				while (itr1.hasNext()) {
					CustomerModel cust = (CustomerModel) itr1.next();
					
					List list = getSession().createQuery(
									"from SalesModel where date>=:fromDate and date<=:toDate and active=true and customer.id=" + cust.getId()+" "
											+ condition).setParameter("fromDate", fromDate)
											.setParameter("toDate", toDate).list();
					
//					unitObj=null;
					stkObj=null;
					purchase_amt=0;
					stockId=0;
					qty=0; saledValue=0; purchasedValue=0;
					profit=0;
					
					rptModel=new ReportBean();
					
					rptModel.setClient_name(cust.getName());
					
					Iterator it=list.iterator();
					while (it.hasNext()) {
						
						salObj=(SalesModel) it.next();
						rptModel.setId(salObj.getId());
						averageAmount=0;
						
						Iterator it2=salObj.getInventory_details_list().iterator();
						while (it2.hasNext()) {
							invObj=(SalesInventoryDetailsModel) it2.next();
							
							purchase_amt=0;
							count=0;
							stockId=0;qty=0;
							if(invObj.getStock_ids().length()>0) {
								stks=invObj.getStock_ids().toString().split(",");
								for (int i = 0; i < stks.length; i++) {
									try {
										
										stockId=Long.parseLong(stks[i].split(":")[0]);
										qty=Double.parseDouble(stks[i].split(":")[1]);
										
										obj=getSession().createQuery(
												"from ItemStockModel where id="+stockId).uniqueResult();
										if(obj!=null) {
											stkObj=(ItemStockModel) obj;
											
//											if(stkObj.getInv_det_id()!=0) {
//												obj = getSession().createQuery("select a.unit from PurchaseInventoryDetailsModel a where a.id="
//														+ stkObj.getInv_det_id()).uniqueResult();
//												if(obj!=null)
//													unitObj=(UnitModel) obj;
//											}
//											else if(stkObj.getPurchase_id()!=0) {
//												try {
//													obj = getSession().createQuery("select b.unit from PurchaseModel a join a.inventory_details_list b where a.id=:id and b.item.id=:itm"
//															).setLong("id", stkObj.getPurchase_id()).setLong("itm", stkObj.getItem().getId()).list().get(0);
//												} catch (Exception e) {
//													unitObj=stkObj.getItem().getUnit();
//													// TODO: handle exception
//												}
//											}
//											else
//												unitObj=invObj.getUnit();
											
											purchase_amt += stkObj.getRate();
//											if(invObj.getUnit().getId()==unitObj.getId()) {
//												purchase_amt += stkObj.getRate();
//											}
//											else {
//												purchase_amt += stkObj.getRate();
//											}
											count++;
										}
										
										
									} catch (Exception e) {
									}
									
								}
							}
							if(purchase_amt!=0)
								averageAmount+=(purchase_amt/count)*invObj.getQuantity_in_basic_unit();
						}
						
						saledValue+=salObj.getAmount();
						purchasedValue+=averageAmount;
//						profit+=(rptModel.getInwards()-purchase_amt);
					}
					
					
					if(saledValue>0) {
						rptModel.setInwards(saledValue);
						rptModel.setOutwards(purchasedValue);
						rptModel.setProfit(saledValue-purchasedValue);
						
						resultList.add(rptModel);
					}
				}
				
			}
			
			commit();

		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
		}
		return resultList;
	}
	
	
	@SuppressWarnings("rawtypes")
	public List<Object> getEmployeeWiseProfitReport(long empId, Date fromDate, Date toDate, long officeId) throws Exception {
		List<Object> resultList = new ArrayList<Object>();
		
		try {
			
			begin();

			String condition = "";
			if (empId != 0) {
				condition += " and responsible_employee=" + empId;
			}
			if (officeId != 0) {
				condition += " and office.id=" + officeId;
			}
			
			if (empId != 0) {
				
				S_LoginModel cust = (S_LoginModel) getSession().get(S_LoginModel.class, empId);
			
				List list = getSession().createQuery("from SalesModel where date between :fromDate and :toDate and active=true "+ condition)
						.setParameter("fromDate", fromDate)
						.setParameter("toDate", toDate).list();
				
				System.out.println("Sales Size "+list.size());
				
				ReportBean rptModel;
				SalesModel salObj;
				double purchase_amt=0, avg_rate=0;
				SalesInventoryDetailsModel invObj;
				String[] stks;
				long stockId;
				double qty=0, saledValue=0, purchasedValue=0, profit=0;
				Object obj;
				int count=0;
				rptModel=new ReportBean();
				
				rptModel.setClient_name(cust.getLogin_name());
//				rptModel.setDate(CommonUtil.formatSQLDateToDDMMMYYYY(salObj.getDate()));
				
//				rptModel.setParticulars(String.valueOf(salObj.getSales_number()));
				
				Iterator it=list.iterator();
				while (it.hasNext()) {
					
					salObj=(SalesModel) it.next();
					rptModel.setId(salObj.getId());
					avg_rate=0;
					Iterator it2=salObj.getInventory_details_list().iterator();
					while (it2.hasNext()) {
						invObj=(SalesInventoryDetailsModel) it2.next();
						count=0;
						purchase_amt=0;
						stockId=0;qty=0;
						if(invObj.getStock_ids().length()>0) {
							stks=invObj.getStock_ids().toString().split(",");
							for (int i = 0; i < stks.length; i++) {
								try {
									
									stockId=Long.parseLong(stks[i].split(":")[0]);
									qty=Double.parseDouble(stks[i].split(":")[1]);
									
									obj=getSession().createQuery("select rate from ItemStockModel where id="+stockId).uniqueResult();
									if(obj!=null) {
										purchase_amt+=(Double)obj;
										count++;
									}
									
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
						if(purchase_amt!=0)
							avg_rate=purchase_amt*invObj.getQuantity_in_basic_unit()/count;
						
					}
					
					saledValue+=salObj.getAmount();
					purchasedValue+=avg_rate;
//					profit+=(rptModel.getInwards()-purchase_amt);
				}
				
				double exp=0;
				double tran=0;
				List transList=new ArrayList();
				transList=getSession().createQuery("select a from TransactionModel a join a.transaction_details_list b " +
						"where a.transaction_type=:typ and b.narration=:narr")
						.setParameter("typ", SConstants.EXPENDETURE_TRANSACTION)
						.setParameter("narr", cust.getId()+"").list();
				if(transList.size()>0){
					Iterator titr=transList.iterator();
					while (titr.hasNext()) {
						TransactionModel trans = (TransactionModel) titr.next();
						if(trans!=null){
							Iterator itr=trans.getTransaction_details_list().iterator();
							while (itr.hasNext()) {
								TransactionDetailsModel det = (TransactionDetailsModel) itr.next();
								if(det.getNarration().equals(cust.getId()+"")){
									exp+=det.getAmount();
								}
							}
						}
					}
				}
				
				
				List transpList=new ArrayList();
				transpList=getSession().createQuery("from TransportationPaymentModel where sales_person=:usr and office.id=:office")
						.setParameter("usr", cust.getId()).setParameter("office", officeId).list();
				if(transpList.size()>0){
					Iterator titr=transpList.iterator();
					while (titr.hasNext()) {
						TransportationPaymentModel trans = (TransportationPaymentModel) titr.next();
						tran+=trans.getPayment_amount();
					}
				}
				
				
				
				if(saledValue>0 || exp>0 || purchasedValue>0 || tran>0) {
					rptModel.setInwards(CommonUtil.roundNumber(saledValue));
					rptModel.setOutwards(CommonUtil.roundNumber(purchasedValue));
					rptModel.setAmount(CommonUtil.roundNumber(exp));
					rptModel.setBalance(CommonUtil.roundNumber(tran));
					rptModel.setProfit(CommonUtil.roundNumber(saledValue-purchasedValue-exp-tran));
					
					resultList.add(rptModel);
				}
			}
			else {
				List userList=new ArrayList();
				List idLst=getSession().createQuery("select b.login_id from SalesManMapModel b where b.office_id=:ofc and b.option_id=:opt")
						.setLong("ofc", officeId).setInteger("opt", SConstants.SALES_MAN).list();
				
				if(idLst!=null && idLst.size()>0) 
					userList= getSession().createQuery("select loginId from UserModel a where a.loginId.id in (:lst) and a.loginId.status!=1 order by a.first_name")
					.setParameterList("lst", idLst).list();
				else
					userList= getSession().createQuery("select loginId from UserModel a where a.loginId.office.id=:ofc and a.loginId.userType.id>1 and a.loginId.status!=1 order by a.first_name")
							.setLong("ofc", officeId).list();
				
//				List userList = getSession()
//						.createQuery(
//								"select new com.webspark.model.S_LoginModel(loginId.id, first_name)"
//										+ " from UserModel where loginId.office.id=:ofc order by first_name")
//						.setParameter("ofc", officeId).list();
				
				
				
				ReportBean rptModel;
				SalesModel salObj;
				double purchase_amt=0;
				SalesInventoryDetailsModel invObj;
				String[] stks;
				long stockId;
				double qty=0, saledValue=0, purchasedValue=0, profit=0, avg_rate=0;
				Object obj;
				int count=0;
				Iterator itr1=userList.iterator();
				while (itr1.hasNext()) {
					S_LoginModel logObj = (S_LoginModel) itr1.next();
					
					List list = getSession().createQuery(
								"from SalesModel where date between :fromDate and :toDate and active=true and responsible_employee=" + logObj.getId()+" "
											+ condition)
							.setParameter("fromDate", fromDate)
							.setParameter("toDate", toDate).list();
					System.out.println("Sales Size "+list.size());
					purchase_amt=0;
					stockId=0;
					qty=0; saledValue=0; purchasedValue=0;
					profit=0;
					
					rptModel=new ReportBean();
					
					rptModel.setClient_name(logObj.getLogin_name());
					
					Iterator it=list.iterator();
					while (it.hasNext()) {
						
						salObj=(SalesModel) it.next();
						rptModel.setId(salObj.getId());
						avg_rate=0;
						
						Iterator it2=salObj.getInventory_details_list().iterator();
						while (it2.hasNext()) {
							invObj=(SalesInventoryDetailsModel) it2.next();
							
							purchase_amt=0;
							count=0;
							stockId=0;qty=0;
							if(invObj.getStock_ids().length()>0) {
								stks=invObj.getStock_ids().toString().split(",");
								for (int i = 0; i < stks.length; i++) {
									try {
										
										stockId=Long.parseLong(stks[i].split(":")[0]);
										qty=Double.parseDouble(stks[i].split(":")[1]);
										
										obj=getSession().createQuery(
												"select rate from ItemStockModel where id="+stockId).uniqueResult();
										if(obj!=null) {
											purchase_amt+=(Double)obj;
											count++;
										}
										
										
									} catch (Exception e) {
										// TODO: handle exception
									}
									
								}
							}
							if(purchase_amt!=0)
								avg_rate+=purchase_amt*invObj.getQuantity_in_basic_unit()/count;
						}
						
						saledValue+=salObj.getAmount();
						purchasedValue+=avg_rate;
//						profit+=(rptModel.getInwards()-purchase_amt);
					}
					
					double exp=0;
					double tran=0;
					List transList=new ArrayList();
					transList=getSession().createQuery("select a from TransactionModel a join a.transaction_details_list b " +
							"where a.transaction_type=:typ and b.narration=:narr")
							.setParameter("typ", SConstants.EXPENDETURE_TRANSACTION)
							.setParameter("narr", logObj.getId()+"").list();
					if(transList.size()>0){
						Iterator titr=transList.iterator();
						while (titr.hasNext()) {
							TransactionModel trans = (TransactionModel) titr.next();
							if(trans!=null){
								Iterator itr=trans.getTransaction_details_list().iterator();
								while (itr.hasNext()) {
									TransactionDetailsModel det = (TransactionDetailsModel) itr.next();
									if(det.getNarration().equals(logObj.getId()+"")){
										exp+=det.getAmount();
									}
								}
							}
						}
					}
					
					List transpList=new ArrayList();
					transpList=getSession().createQuery("from TransportationPaymentModel where sales_person=:usr and office.id=:office")
							.setParameter("usr", logObj.getId()).setParameter("office", officeId).list();
					if(transpList.size()>0){
						Iterator titr=transpList.iterator();
						while (titr.hasNext()) {
							TransportationPaymentModel trans = (TransportationPaymentModel) titr.next();
							tran+=trans.getPayment_amount();
						}
					}
					
					if(saledValue>0 || exp>0 || purchasedValue>0 || tran>0) {
						rptModel.setInwards(CommonUtil.roundNumber(saledValue));
						rptModel.setOutwards(CommonUtil.roundNumber(purchasedValue));
						rptModel.setAmount(CommonUtil.roundNumber(exp));
						rptModel.setBalance(CommonUtil.roundNumber(tran));
						rptModel.setProfit(CommonUtil.roundNumber(saledValue-purchasedValue-exp-tran));
						
						resultList.add(rptModel);
					}
				}
				
			}
			
			commit();

		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
		}

		return resultList;
	}
	
	
	
	
	public List<Object> getLaundryEmployeeWiseProfitReport(long empId,
			Date fromDate, Date toDate, long officeId) throws Exception {
		List<Object> resultList = new ArrayList<Object>();

		try {
			begin();

			String condition = "";
			if (empId != 0) {
				condition += " and sales_person=" + empId;
			}
			if (officeId != 0) {
				condition += " and office.id=" + officeId;
			}
			
			if (empId != 0) {
				
				S_LoginModel cust = (S_LoginModel) getSession().get(S_LoginModel.class, empId);
			
				List list = getSession()
						.createQuery(
								"from LaundrySalesModel where date between :fromDate and :toDate and active=true "
										+ condition)
						.setParameter("fromDate", fromDate)
						.setParameter("toDate", toDate).list();
				
				
				ReportBean rptModel;
				LaundrySalesModel salObj;
				double purchase_amt=0;
				LaundrySalesDetailsModel invObj;
				String[] stks;
				long stockId;
				double qty=0, saledValue=0, purchasedValue=0, profit=0;
				Object obj;
				
				rptModel=new ReportBean();
				
				rptModel.setClient_name(cust.getLogin_name());
				
				Iterator it=list.iterator();
				while (it.hasNext()) {
					salObj=(LaundrySalesModel) it.next();
					saledValue+=salObj.getAmount();
				}
				
				if(saledValue>0) {
					rptModel.setInwards(saledValue);
					rptModel.setOutwards(purchasedValue);
					rptModel.setProfit(saledValue-purchasedValue);
					
					resultList.add(rptModel);
				}
			}
			else {
				
				List userList = getSession()
						.createQuery(
								"select new com.webspark.model.S_LoginModel(loginId.id, first_name)"
										+ " from UserModel where loginId.office.id=:ofc order by first_name")
						.setParameter("ofc", officeId).list();
				
				
				
				ReportBean rptModel;
				LaundrySalesModel salObj;
				double purchase_amt=0;
				LaundrySalesDetailsModel invObj;
				String[] stks;
				long stockId;
				double qty=0, saledValue=0, purchasedValue=0, profit=0;
				Object obj;
				
				Iterator itr1=userList.iterator();
				while (itr1.hasNext()) {
					S_LoginModel logObj = (S_LoginModel) itr1.next();
					
					List list = getSession()
							.createQuery(
									"from LaundrySalesModel where date between :fromDate and :toDate and active=true and sales_person=" + logObj.getId()+" "
											+ condition)
							.setParameter("fromDate", fromDate)
							.setParameter("toDate", toDate).list();
					
					purchase_amt=0;
					stockId=0;
					qty=0; saledValue=0; purchasedValue=0;
					profit=0;
					
					rptModel=new ReportBean();
					
					rptModel.setClient_name(logObj.getLogin_name());
					
					Iterator it=list.iterator();
					while (it.hasNext()) {
						
						salObj=(LaundrySalesModel) it.next();
						
						saledValue+=salObj.getAmount();
					}
					
					
					if(saledValue>0) {
						rptModel.setInwards(saledValue);
						rptModel.setOutwards(purchasedValue);
						rptModel.setProfit(saledValue-purchasedValue);
						
						resultList.add(rptModel);
					}
				}
				
				
				
				
				
				
			}
			
			commit();

		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
		}

		return resultList;
	}
	
	
	
	
	public List<Object> getItemWiseProfitReport(long itmId,
			Date fromDate, Date toDate, long officeId, long itemGrpId) throws Exception {
		List<Object> resultList = new ArrayList<Object>();
		
		ItemStockModel stkObj=null;
//		UnitModel unitObj=null;

		try {
			begin();

			String condition = "";
			if (officeId != 0) {
				condition += " and a.office.id=" + officeId;
			}
			if (itmId != 0) {
				
				ItemModel itmObj = (ItemModel) getSession().get(ItemModel.class, itmId);
				
				List list = getSession()
						.createQuery("select a from SalesModel a join a.inventory_details_list b where a.date between :fromDate and :toDate and a.active=true and b.item.id=:itm "+ condition)
								.setParameter("fromDate", fromDate).setParameter("toDate", toDate).setLong("itm", itmId).list();
				
				/*List list = getSession()
						.createQuery("select b from SalesModel a join a.inventory_details_list b where a.date between :fromDate and :toDate and a.active=true and b.item.id=:itm "+ condition)
								.setParameter("fromDate", fromDate).setParameter("toDate", toDate).setLong("itm", itmId).list();*/
				
				
				ReportBean rptModel;
				SalesModel salObj;
				double purchase_amt=0;
				SalesInventoryDetailsModel invObj;
				String[] stks;
				long stockId;
				double qty=0, saledValue=0, purchasedValue=0, profit=0;
				Object obj;
				int count=0;
				double averageAmount=0;
				
				rptModel=new ReportBean();
				
				rptModel.setClient_name(itmObj.getName());
				
				Iterator itr=list.iterator();
					while (itr.hasNext()) {
						salObj=(SalesModel)itr.next();
						rptModel.setId(salObj.getId());
						List lis=salObj.getInventory_details_list();
						Iterator it=lis.iterator();
						while(it.hasNext()){
							invObj=(SalesInventoryDetailsModel) it.next();
							if(invObj.getItem().getId()==itmId){
								count=0;
								purchase_amt=0;
								stockId=0;qty=0;
								averageAmount=0;
//								double avg_rate=0;
//								if(invObj.getStock_ids().length()>0) {
//									stks=invObj.getStock_ids().toString().split(",");
//									for (int i = 0; i < stks.length; i++) {
//										try {
//											
//											stockId=Long.parseLong(stks[i].split(":")[0]);
//											qty=Double.parseDouble(stks[i].split(":")[1]);
//											
//											obj = getSession().createQuery("from ItemStockModel where id="
//													+ stockId).uniqueResult();
//											if (obj != null) {
//												stkObj=(ItemStockModel) obj;
//												
//													purchase_amt += stkObj.getRate();
//												count++;
//											}
//											
//										} 
//										catch (Exception e) {
//											// TODO: handle exception
//										}
//									}
//									if (purchase_amt != 0)
//										avg_rate=purchase_amt / count;
//										averageAmount = avg_rate* invObj.getQuantity_in_basic_unit();
//									}
								
								if(invObj.getStock_id()!=0) {
									try {
										stockId=invObj.getStock_id();
//										qty=invObj.getQuantity_in_basic_unit();
										
										obj = getSession().createQuery("from ItemStockModel where id="+ stockId).uniqueResult();
										if (obj != null) {
											stkObj=(ItemStockModel) obj;
											purchase_amt += stkObj.getRate();
										}
									}
									catch (Exception e) {}
									
								if (purchase_amt != 0)
//									avg_rate=purchase_amt;
									averageAmount= purchase_amt* invObj.getQuantity_in_basic_unit();
								}
								
//								double conRate=(Double) getSession()
//										.createQuery("select coalesce(convertion_rate,1) from ItemUnitMangementModel where item.id=:itm and alternateUnit=:alt "
//												+ " and sales_type=:st")
//												.setLong("itm", invObj.getItem().getId()).setLong("alt", invObj.getUnit().getId())
//												.setLong("st", salObj.getSales_type()).uniqueResult();
								
								double discountPrice=0;
								if(invObj.getDiscount_type()==1){
									discountPrice=(invObj.getUnit_price()-invObj.getUnit_price()*invObj.getDiscountPercentage()/100);
								}else{
									discountPrice=invObj.getUnit_price()-invObj.getDiscount();
								}
								saledValue+=(invObj.getQunatity()*discountPrice);
								purchasedValue += averageAmount;
							}
							else
								continue;
							
						}
					}
				if(saledValue>0) {
					rptModel.setInwards(saledValue);
					rptModel.setOutwards(purchasedValue);
					rptModel.setProfit(saledValue-purchasedValue);
					
					resultList.add(rptModel);
				}
			}
			else {
				String grpCnd="";
				if(itemGrpId!=0)
					grpCnd=" and sub_group.group.id="+itemGrpId;
				List userList = getSession()
						.createQuery(
								"select new com.inventory.config.stock.model.ItemModel(id, name)"
										+ " from ItemModel where office.id=:ofc "+grpCnd+" order by name")
						.setParameter("ofc", officeId).list();
				
				
				
				ReportBean rptModel;
				SalesModel salObj;
				double purchase_amt=0;
				SalesInventoryDetailsModel invObj;
				String[] stks;
				long stockId;
				double qty=0, saledValue=0, purchasedValue=0, profit=0;
				Object obj;
				int count=0;
				double averageAmount=0;
				
				Iterator itr1=userList.iterator();
				while (itr1.hasNext()) {
					ItemModel itmObj = (ItemModel) itr1.next();
					
					/*List list = getSession()
							.createQuery("select b from SalesModel a join a.inventory_details_list b where a.date between :fromDate and :toDate and a.active=true and b.item.id=:itm "+ condition)
									.setParameter("fromDate", fromDate).setParameter("toDate", toDate).setLong("itm", itmObj.getId()).list();*/
					List list = getSession()
							.createQuery("select a from SalesModel a join a.inventory_details_list b where a.date between :fromDate and :toDate and a.active=true and b.item.id=:itm "+ condition)
									.setParameter("fromDate", fromDate).setParameter("toDate", toDate).setLong("itm", itmObj.getId()).list();
					
					qty=0; 
					saledValue=0; 
					purchasedValue=0;
					profit=0;
					
					rptModel=new ReportBean();
					
					rptModel.setClient_name(itmObj.getName());
					averageAmount=0;
					Iterator itr=list.iterator();
					while (itr.hasNext()) {
						salObj=(SalesModel)itr.next();
						rptModel.setId(salObj.getId());
						List lis=salObj.getInventory_details_list();
						Iterator it=lis.iterator();
						while(it.hasNext()){
							invObj=(SalesInventoryDetailsModel) it.next();
							if(invObj.getItem().getId()==itmObj.getId()){
								count=0;
								purchase_amt=0;
								stockId=0;qty=0;
								double avg_rate=0;
//								if(invObj.getStock_ids().length()>0) {
//									stks=invObj.getStock_ids().toString().split(",");
//									for (int i = 0; i < stks.length; i++) {
//										try {
//											stockId=Long.parseLong(stks[i].split(":")[0]);
//											qty=Double.parseDouble(stks[i].split(":")[1]);
//											
//											obj = getSession().createQuery("from ItemStockModel where id="
//													+ stockId).uniqueResult();
//											if (obj != null) {
//												stkObj=(ItemStockModel) obj;
//												purchase_amt += stkObj.getRate()/invObj.getQuantity_in_basic_unit();
//												count++;
//											}
//										}
//										catch (Exception e) {
//										}
//									}
//									if (purchase_amt != 0)
//										avg_rate=purchase_amt/count;
//										averageAmount= avg_rate* invObj.getQuantity_in_basic_unit();
//								}
								if(invObj.getStock_id()!=0) {
									try {
										stockId=invObj.getStock_id();
//										qty=invObj.getQuantity_in_basic_unit();
										
										obj = getSession().createQuery("from ItemStockModel where id="+ stockId).uniqueResult();
										if (obj != null) {
											stkObj=(ItemStockModel) obj;
											purchase_amt += stkObj.getRate()/stkObj.getQuantity();
										}
									}
									catch (Exception e) {}
									
								if (purchase_amt != 0)
									avg_rate=purchase_amt;
									averageAmount= avg_rate* invObj.getQuantity_in_basic_unit();
								}

								double conRate=(Double) getSession()
										.createQuery("select coalesce(convertion_rate,0) from ItemUnitMangementModel where item.id=:itm and alternateUnit=:alt "
												+ " and sales_type=:st")
												.setLong("itm", invObj.getItem().getId()).setLong("alt", invObj.getUnit().getId())
												.setLong("st", salObj.getSales_type()).uniqueResult();
									
								
								double discountPrice=0;
								if(invObj.getDiscount_type()==1){
										discountPrice=(invObj.getUnit_price()-invObj.getUnit_price()*invObj.getDiscountPercentage()/100);
								}else{
									discountPrice=invObj.getUnit_price()-invObj.getDiscount();
								}
								saledValue+=(invObj.getQunatity()*discountPrice);
								purchasedValue += averageAmount*conRate;
							}
							else
								continue;
						}
					}
					if(saledValue>0) {
						rptModel.setInwards(saledValue);
						rptModel.setOutwards(purchasedValue);
						rptModel.setProfit(saledValue-purchasedValue);
						
						resultList.add(rptModel);
					}
				}
				
			}
			
			commit();

		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
		}

		return resultList;
	}
	
	
	
	public List<Object> getLaundryItemWiseProfitReport(long itmId,
			Date fromDate, Date toDate, long officeId) throws Exception {
		List<Object> resultList = new ArrayList<Object>();

		try {
			begin();

			String condition = "";
//			if (itmId != 0) {
//				condition += " and login.id=" + itmId;
//			}
			if (officeId != 0) {
				condition += " and a.office.id=" + officeId;
			}
			
			if (itmId != 0) {
				
				ItemModel itmObj = (ItemModel) getSession().get(ItemModel.class, itmId);
				
				List list = getSession()
						.createQuery("select b from LaundrySalesModel a join a.details_list b where a.date between :fromDate and :toDate and a.active=true and b.item.id=:itm "+ condition)
								.setParameter("fromDate", fromDate).setParameter("toDate", toDate).setLong("itm", itmId).list();
				
				ReportBean rptModel;
				LaundrySalesModel salObj;
				double purchase_amt=0;
				LaundrySalesDetailsModel invObj;
				String[] stks;
				long stockId;
				double qty=0, saledValue=0, purchasedValue=0, profit=0;
				Object obj;
				
				rptModel=new ReportBean();
				
				rptModel.setClient_name(itmObj.getName());
				
				Iterator it=list.iterator();
//				while (it.hasNext()) {
//					
//					salObj=(SalesModel) it.next();
//					
//					
//					purchase_amt=0;
//					Iterator it2=salObj.getInventory_details_list().iterator();
					while (it.hasNext()) {
						invObj=(LaundrySalesDetailsModel) it.next();
						
						saledValue+=(invObj.getQuantity()*invObj.getUnit_price());
					}
					
					
//					profit+=(rptModel.getInwards()-purchase_amt);
//				}
				
				if(saledValue>0) {
					rptModel.setInwards(saledValue);
					rptModel.setOutwards(purchasedValue);
					rptModel.setProfit(saledValue-purchasedValue);
					
					resultList.add(rptModel);
				}
			}
			else {
				
				List userList = getSession()
						.createQuery(
								"select new com.inventory.config.stock.model.ItemModel(id, name)"
										+ " from ItemModel where office.id=:ofc order by name")
						.setParameter("ofc", officeId).list();
				
				
				ReportBean rptModel;
				LaundrySalesModel salObj;
				double purchase_amt=0;
				LaundrySalesDetailsModel invObj;
				String[] stks;
				long stockId;
				double qty=0, saledValue=0, purchasedValue=0, profit=0;
				Object obj;
				
				Iterator itr1=userList.iterator();
				while (itr1.hasNext()) {
					ItemModel itmObj = (ItemModel) itr1.next();
				
					List list = getSession()
							.createQuery("select b from LaundrySalesModel a join a.details_list b where a.date between :fromDate and :toDate and a.active=true and b.item.id=:itm "+ condition)
									.setParameter("fromDate", fromDate).setParameter("toDate", toDate).setLong("itm", itmObj.getId()).list();
					
					purchase_amt=0;
					qty=0; saledValue=0; purchasedValue=0; profit=0;
					
					rptModel=new ReportBean();
					
					rptModel.setClient_name(itmObj.getName());
					
					Iterator it=list.iterator();
					while (it.hasNext()) {
						invObj=(LaundrySalesDetailsModel) it.next();
						
						saledValue+=(invObj.getQuantity()*invObj.getUnit_price());
					}
					
					if(saledValue>0) {
						rptModel.setInwards(saledValue);
						rptModel.setOutwards(purchasedValue);
						rptModel.setProfit(saledValue-purchasedValue);
						
						resultList.add(rptModel);
					}
				}
				
				
				
				
				
				
			}
			
			commit();

		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
		} finally {
			flush();
			close();
		}

		return resultList;
	}
	
	
	
	
	
	
	
	
	
}
