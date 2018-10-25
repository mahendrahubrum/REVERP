package com.inventory.common.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.config.unit.model.ItemUnitMangementModel;
import com.inventory.purchase.bean.InventoryDetailsPojo;
import com.inventory.purchase.model.ItemStockModel;
import com.inventory.reports.bean.AcctReportMainBean;
import com.vaadin.server.WrappedSession;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.KeyValue;
import com.webspark.common.util.SConstants;
import com.webspark.common.util.SessionUtil;
import com.webspark.dao.SHibernate;
import com.webspark.model.ActivityLogModel;
import com.webspark.model.BillModel;
import com.webspark.model.ReportIssueModel;
import com.webspark.model.ReviewModel;
import com.webspark.model.SessionActivityModel;

/**
 * @Author Jinshad P.T.
 */

public class CommonMethodsDao extends SHibernate implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4560808150616987115L;

	List resultList = new ArrayList();

	WrappedSession session = new SessionUtil().getHttpSession();

	boolean isFIFO = (Boolean) session.getAttribute("isFIFO");
	SettingsValuePojo settings = (SettingsValuePojo) session.getAttribute("settings");

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List getAllItemUnitDetails(long item_id) throws Exception {

		try {

			List unitIdList = new ArrayList();

			begin();
			
			unitIdList.addAll(getSession()
					.createQuery(
							"select distinct basicUnit from ItemUnitMangementModel"
									+ " where item.id=:itm and convertion_rate>0")
					.setLong("itm", item_id).list());
			unitIdList.addAll(getSession()
					.createQuery(
							"select distinct alternateUnit from ItemUnitMangementModel"
									+ " where item.id=:itm and convertion_rate>0")
					.setLong("itm", item_id).list());

			if (unitIdList.size() > 0) {
				resultList = getSession()
						.createQuery(
								"select new com.inventory.config.unit.model.UnitModel(id, symbol)"
										+ " from UnitModel where status=:val and id in (:ids)")
						.setParameter("val", SConstants.statuses.RACK_ACTIVE)
						.setParameterList("ids", unitIdList).list();

			}else{
				resultList=new ArrayList();
				resultList.add(getSession()
						.createQuery("select a.unit from ItemModel a where a.id=:id").setParameter("id", item_id).uniqueResult());
			}
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return resultList;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List getAllItemUnitDetailsForPurchase(long item_id) throws Exception {
		
		try {
			
			List unitIdList = new ArrayList();
			
			begin();
			unitIdList.addAll(getSession()
					.createQuery(
							"select distinct basicUnit from ItemUnitMangementModel"
									+ " where item.id=:itm and convertion_rate>0")
									.setLong("itm", item_id).list());
			unitIdList.addAll(getSession()
					.createQuery(
							"select distinct alternateUnit from ItemUnitMangementModel"
									+ " where item.id=:itm and convertion_rate>0 and sales_type=0")
									.setLong("itm", item_id).list());
			
			if (unitIdList.size() > 0) {
				resultList = getSession()
						.createQuery(
								"select new com.inventory.config.unit.model.UnitModel(id, symbol)"
										+ " from UnitModel where status=:val and id in (:ids)")
										.setParameter("val", SConstants.statuses.RACK_ACTIVE)
										.setParameterList("ids", unitIdList).list();
				
			}
			commit();
			
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return resultList;
	}
	
	public double getItemPrice(long item_id, long unit_id, long sales_type) throws Exception {
		double price = 0;
		try {
			
			begin();
			Object obj = getSession()
					.createQuery(
							"select item_price from ItemUnitMangementModel where item.id=:itm and alternateUnit=:alt "
									+ " and sales_type=:st")
					.setLong("itm", item_id).setLong("alt", unit_id)
					.setLong("st", sales_type).uniqueResult();
			commit();
			

			if (obj != null) {
				price = (Double) obj;
			}
			else {
				Object obj1=null;
				
				if(sales_type==SConstants.SALES){
					obj1 = getSession().createQuery("select sale_rate from ItemModel where id="+item_id).uniqueResult();
				}
				else 
					obj1 = getSession().createQuery("select rate from ItemModel where id="+item_id).uniqueResult();
				
				if(obj1!=null)
					price=(Double) obj1;
			}
			
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return price;
	}
	
	public long getItemCurrency(long item_id, long unit_id, long sales_type) throws Exception {
		long price = 0;
		try {
			
			begin();
			Object obj = getSession().createQuery("select purchaseCurrency.id from ItemUnitMangementModel where item.id=:itm and alternateUnit=:alt "
									+ " and sales_type=:st")
					.setLong("itm", item_id).setLong("alt", unit_id)
					.setLong("st", sales_type).uniqueResult();
			commit();
			

			if (obj != null) {
				price = (Long) obj;
			}
			else {
				Object obj1=null;
				
				if(sales_type==SConstants.SALES){
					obj1 = getSession().createQuery("select saleCurrency.id from ItemModel where id="+item_id).uniqueResult();
				}
				else
					obj1 = getSession().createQuery("select purchaseCurrency.id from ItemModel where id="+item_id).uniqueResult();
				
				if(obj1!=null)
					price=(Long) obj1;
			}
			
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return price;
	}
	
	public double getCustomerSpecificPercentage(long item_id, long customerId)
			throws Exception {
		double price = 0;
		try {
			
			begin();
			
			Object ob=getSession().createQuery("select coalesce(percentage,0) from ItemCustomerBarcodeMapModel" +
						" where customerId=:cust and itemId=:itm").setParameter("cust", customerId).setParameter("itm", item_id).uniqueResult();
			commit();
			
			if(ob!=null)
				price=(Double)ob;
			
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return price;
	}
	
	public double getConvertionRate(long item_id, long unit_id, long sales_type)
			throws Exception {
		double conv_rate = 1;
		try {
			begin();
			Object obj = getSession()
					.createQuery("select convertion_rate from ItemUnitMangementModel where item.id=:itm and alternateUnit=:alt "
									+ " and sales_type=:st")
					.setLong("itm", item_id).setLong("alt", unit_id)
					.setLong("st", sales_type).uniqueResult();
			
			
			if (obj != null) {
				conv_rate = (Double) obj;
			
			}
			else {
				
				List lst = getSession()
						.createQuery("select convertion_rate from ItemUnitMangementModel where item.id=:itm and alternateUnit=:alt "
										+ "")
						.setLong("itm", item_id).setLong("alt", unit_id)
						.list();
				if(lst.size()>0) {
					conv_rate=(Double) lst.get(0);
				}
				
			}
			
			commit();

			

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return conv_rate;
	}
	
	public String decreaseStock(long item_id, double qty_in_basic_unit)
			throws Exception {
		
		double qty = qty_in_basic_unit;

		String stock_ids = "";

		String stkDecrsOrder = "min(id)";
		if (!isFIFO)
			stkDecrsOrder = "max(id)";

		while (qty != 0) {

			Object obj = getSession()
					.createQuery(
							"from ItemStockModel where id=(select "
									+ stkDecrsOrder
									+ " from ItemStockModel where item.id=:itm and balance>0 and status!=3)")
					.setLong("itm", item_id).uniqueResult();

			if (obj != null) {

				ItemStockModel stk = (ItemStockModel) obj;

				if (qty > stk.getBalance()) {
					qty = CommonUtil.roundNumberTwoDigit(qty-stk.getBalance());
					
					
					stock_ids += stk.getId() + ":" + stk.getBalance() + ",";
					
					stk.setBlocked(true);
					stk.setBalance(0);
					getSession().update(stk);

					
				} else {
					stk.setBalance(CommonUtil.roundNumberTwoDigit(stk.getBalance() - qty));
					stk.setBlocked(true);
					getSession().update(stk);
					stock_ids += stk.getId() + ":" + qty + ",";
					qty = 0;
				}
				flush();

			} else {
				Object obj1 = getSession()
						.createQuery(
								"from ItemStockModel where id=(select max(id) from ItemStockModel where item.id=:itm and balance>0  and status!=3)")
						.setLong("itm", item_id).uniqueResult();
				if (obj1 != null) {
					ItemStockModel stk = (ItemStockModel) obj1;
					stk.setBalance(CommonUtil.roundNumberTwoDigit(stk.getBalance() - qty));
					stk.setBlocked(true);
					getSession().update(stk);
					flush();
					stock_ids += stk.getId() + ":" + qty + ",";
					qty = 0;
					
				} else {
					Object obj2 = getSession()
							.createQuery(
									"from ItemStockModel where id=(select max(id) from ItemStockModel where item.id=:itm  and status!=3)")
							.setLong("itm", item_id).uniqueResult();
					if (obj2 != null) {
						ItemStockModel stk = (ItemStockModel) obj2;
						stk.setBalance(CommonUtil.roundNumberTwoDigit(stk.getBalance() - qty));
						stk.setBlocked(true);
						getSession().update(stk);
						flush();
						stock_ids += stk.getId() + ":" + qty + ",";
						qty = 0;
						
					} else
						break;
				}
			}
		}
		return stock_ids;
	}

	public void increaseStock(long item_id, double qty_in_basic_unit)
			throws Exception {

		double qty = qty_in_basic_unit;

		String stkDecrsOrder = "min(id)", stkDecrsOrder1 = "max(id)";
		if (!isFIFO) {
			stkDecrsOrder = "max(id)";
			stkDecrsOrder1 = "min(id)";
		}

		int flag = 0;
		while (qty != 0) {

			Object obj = null;

			if (flag == 0) {
				obj = getSession()
						.createQuery(
								"from ItemStockModel where id=(select "
										+ stkDecrsOrder
										+ " from ItemStockModel where item.id=:itm and balance<quantity and balance!=0  and status!=3)")
						.setLong("itm", item_id).uniqueResult();

				if (obj == null) {
					obj = getSession()
							.createQuery(
									"from ItemStockModel where id=(select "
											+ stkDecrsOrder1
											+ " from ItemStockModel where item.id=:itm and balance=0 and quantity!=0  and status!=3)")
							.setLong("itm", item_id).uniqueResult();
				}

				flag = 1;
			} else {

				if (!isFIFO) {
					obj = getSession()
							.createQuery(
									"from ItemStockModel where id=(select max(id) from ItemStockModel where item.id=:itm and balance<quantity and balance!=0  and status!=3)")
							.setLong("itm", item_id).uniqueResult();

					if (obj == null) {
						obj = getSession()
								.createQuery(
										"from ItemStockModel where id=(select max(id) from ItemStockModel where item.id=:itm and balance=0 and quantity!=0  and status!=3)")
								.setLong("itm", item_id).uniqueResult();
					}
				} else {
					obj = getSession()
							.createQuery(
									"from ItemStockModel where id=(select "
											+ stkDecrsOrder1
											+ " from ItemStockModel where item.id=:itm and balance<=0 and quantity>0  and status!=3)")
							.setLong("itm", item_id).uniqueResult();
				}
			}
			
			if (obj != null) {
				ItemStockModel stk = (ItemStockModel) obj;
				
				double balnc = stk.getBalance();
				double quant=stk.getQuantity();
				double fill_bal=CommonUtil.roundNumberTwoDigit(quant-balnc);
				
				if (qty > fill_bal) {
					stk.setBlocked(false);
					stk.setBalance(quant);
					getSession().update(stk);
					qty = CommonUtil.roundNumberTwoDigit(qty-(quant - balnc));
				} else {
					stk.setBalance(CommonUtil.roundNumberTwoDigit(balnc + qty));
					getSession().update(stk);
					qty = 0;
				}
				flush();
			} else {
				Object obj1 = getSession()
						.createQuery(
								"from ItemStockModel where id=(select max(id) from ItemStockModel where item.id=:itm  and status!=3)")
						.setLong("itm", item_id).uniqueResult();
				if (obj1 != null) {
					ItemStockModel stk = (ItemStockModel) obj1;
					double balnc=stk.getBalance();
					stk.setBalance(CommonUtil.roundNumberTwoDigit(balnc - qty));
					getSession().update(stk);
					flush();
					qty = 0;
				} else
					break;
			}
		}

	}

	public String getBillName(long officeID, int type) throws Exception {
		
		String name = "";
		try {

			begin();
			Object obj = getSession()
					.createQuery(
							"from BillModel where office.id=:ofc and type=:type")
					.setLong("ofc", officeID).setInteger("type", type)
					.uniqueResult();
			commit();

			if (obj != null) {
				name = ((BillModel) obj).getBill_name().getBill_name();
			}

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return name;
	}

	public void saveActivityLog(ActivityLogModel model) throws Exception {
		try {

			begin();
			getSession().save(model);
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
	}
	
	
//	@SuppressWarnings("finally")
//	public List getStocks(long item_id) throws Exception {
//		try {
//			
//			resultList=new ArrayList();
//			
//			
//			begin();
//			resultList.addAll(getSession()
//					.createQuery("select new com.inventory.purchase.bean.InventoryDetailsPojo("
//									+ " id, concat(' Stock ID : ',id,' , ', ' Bal: ' , balance, ' ', item.unit.symbol ),status) from ItemStockModel where item.id=:itm and balance>0 and (status=1 or status=2 or status=4)  order by id")
//									.setLong("itm", item_id).list());
//			
//			if(resultList==null || resultList.size()<=0) {
//				
//				 Object obj= getSession()
//						.createQuery("select max(id) from ItemStockModel where item.id=:itm and (status=1 or status=2 or status=4)")
//										.setLong("itm", item_id).uniqueResult();
//				 
//				 if(obj!=null)
//					 resultList.addAll(getSession()
//								.createQuery("select new com.inventory.purchase.bean.InventoryDetailsPojo("
//												+ " id, concat(' Stock ID : ',id,' , ', ' Bal: ' , balance, ' ', item.unit.symbol ),status) from ItemStockModel where id=:id")
//												.setLong("id", (Long) obj).list());
//				
//			}
//			
////			resultList.addAll(getSession()
////					.createQuery("select new com.inventory.purchase.bean.InventoryDetailsPojo("
////									+ " id, concat('<<GRV>> Stock ID : ',id,' , ', ' Bal: ' , balance, ' ', item.unit.symbol ),status) from ItemStockModel where item.id=:itm and balance>0 and status=3  order by id")
////									.setLong("itm", item_id).list());
//			
//			commit();
//		} catch (Exception e) {
//			rollback();
//			close();
//			e.printStackTrace();
//			throw e;
//		} finally {
//			flush();
//			close();
//			return resultList;
//		}
//	}
	
	
	@SuppressWarnings({"unchecked", "rawtypes" })
	public List getStocks(long item_id, boolean isUseTag) throws Exception {
		try {

			resultList = new ArrayList();
			List beanList = new ArrayList();
			
			String tag_crit="";
			if(isUseTag)
				tag_crit="'TAG:',item_tag,', ',";

			begin();
			beanList
					.addAll(getSession()
							.createQuery(
									"select new com.inventory.purchase.bean.InventoryDetailsPojo("
											+ " id, concat("+tag_crit+" 'Stock ID : ',id,' , '," +
											" ' Bal: ' , balance, ' ', item.unit.symbol,', P.Rate:',round(rate,2) ),status) from ItemStockModel a where item.id=:itm and balance>0 and" +
											" (status=1 or status=2 or status="+SConstants.stock_statuses.TRANSFERRED_STOCK
											+" or status="+SConstants.stock_statuses.MANUFACTURED_STOCK+" or status="+SConstants.stock_statuses.ITEM_CREATION_STOCK+") " +
													" and purchase_type!=:type  order by id")
							.setLong("itm", item_id).setParameter("type", SConstants.stockPurchaseType.SALES_RETURN).list());

			if (beanList == null || beanList.size() <= 0) {

				Object obj = getSession()
						.createQuery(
								"select max(id) from ItemStockModel where item.id=:itm and (status=1 or status=2 or status="+SConstants.stock_statuses.TRANSFERRED_STOCK+" or status="+SConstants.stock_statuses.MANUFACTURED_STOCK+")")
						.setLong("itm", item_id).uniqueResult();

				if (obj != null)
					beanList
							.addAll(getSession()
									.createQuery(
											"select new com.inventory.purchase.bean.InventoryDetailsPojo("
													+ " id, concat("+tag_crit+" 'Stock ID : ',id,' , ', ' Bal: ' , balance, ' ', item.unit.symbol,', P.Rate:',round(rate,2) ),status) from ItemStockModel where id=:id")
									.setLong("id", (Long) obj).list());

			}

			// resultList.addAll(getSession()
			// .createQuery("select new com.inventory.purchase.bean.InventoryDetailsPojo("
			// +
			// " id, concat('<<GRV>> Stock ID : ',id,' , ', ' Bal: ' , balance, ' ', item.unit.symbol ),status) from ItemStockModel where item.id=:itm and balance>0 and status=3 order by id")
			// .setLong("itm", item_id).list());
			
			Iterator iter=beanList.iterator();
			InventoryDetailsPojo bean;
			while (iter.hasNext()) {
				bean = (InventoryDetailsPojo) iter.next();
				ItemStockModel mdl=(ItemStockModel) getSession().get(ItemStockModel.class, bean.getId());
				String tag="";
				AcctReportMainBean accBean=new AcctReportMainBean("","",mdl.getRate());
				if(isUseTag)
					tag=mdl.getItem_tag();
					
				if(mdl.getPurchase_id()!=0){
					switch (mdl.getPurchase_type()) {
					case SConstants.stockPurchaseType.PURCHASE:
						// Contsructor #34
						accBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
								"'',concat('Purchase : ',a.purchase_no),coalesce(b.unit_price/(b.qty_in_basic_unit/b.qunatity),0))from PurchaseModel a join " +
								" a.purchase_details_list b where b.id=:id")
							.setParameter("id", mdl.getInv_det_id()).uniqueResult();
						
					break;
					case SConstants.stockPurchaseType.PURCHASE_GRN:
						accBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
								"'',concat('GRN :', a.grn_no),coalesce(b.unit_price/(b.qty_in_basic_unit/b.qunatity),0))from PurchaseGRNModel a " +
								" join a.grn_details_list b where b.id=:id")
							.setParameter("id", mdl.getInv_det_id()).uniqueResult();
					break;
					case SConstants.stockPurchaseType.SALES_RETURN:
						accBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
								"case when b.reasonId=0 then '' else (select name from ReasonModel c where c.id=b.reasonId) end" +
								",concat('Sale Return :', a.return_no),coalesce(b.unit_price/(b.quantity_in_basic_unit/b.qunatity),0))from SalesReturnModel a " +
								" join a.inventory_details_list b where b.id=:id")
							.setParameter("id", mdl.getInv_det_id()).uniqueResult();
					break;
					case SConstants.stockPurchaseType.STOCK_CREATE:
						accBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
								"'',concat('Stock Create :',a.purchase_number),coalesce(b.unit_price/(b.qty_in_basic_unit/b.qunatity),0))from StockCreateModel a" +
								" join a.inventory_details_list b where b.id=:id")
						.setParameter("id", mdl.getInv_det_id()).uniqueResult();
					break;
					case SConstants.stockPurchaseType.STOCK_TRANSFER:
						accBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
								"'',concat('Stock Transfer :',cast(a.transfer_no as string)),coalesce(b.unit_price/(b.quantity_in_basic_unit/b.quantity),0))from StockTransferModel a " +
								" join a.inventory_details_list b where b.id=:id")
						.setParameter("id", mdl.getInv_det_id()).uniqueResult();
					break;

					default:
						accBean=new AcctReportMainBean("","",mdl.getRate());
					break;
				}
				}
				bean.setStock_details(tag+" "+accBean.getBill_no()+", ID : "+mdl.getId()+" ," +
						"  Bal:  "+ mdl.getBalance()+" "+mdl.getItem().getUnit().getSymbol()+
						" , P.Rate: "+CommonUtil.roundNumberToString(accBean.getAmount())+", "+accBean.getName());
				resultList.add(bean);
			}

			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return resultList;
	}
	@SuppressWarnings({"unchecked", "rawtypes" })
	public List getGRVStocks(long item_id, boolean isUseTag) throws Exception {
		try {
			
			resultList = new ArrayList();
			List beanList = new ArrayList();
			
			String tag_crit="";
			if(isUseTag)
				tag_crit="'TAG:',item_tag,', ',";
			
			begin();
			beanList
			.addAll(getSession()
					.createQuery(
							"select new com.inventory.purchase.bean.InventoryDetailsPojo("
									+ " id, concat("+tag_crit+" 'Stock ID : ',id,' , '," +
									" ' Bal: ' , balance, ' ', item.unit.symbol,', P.Rate:',round(rate,2) ),status) from ItemStockModel a where item.id=:itm and balance>0 and" +
									" status="+SConstants.stock_statuses.GOOD_STOCK+" " +
							" and purchase_type=:type  order by id")
							.setLong("itm", item_id).setParameter("type", SConstants.stockPurchaseType.SALES_RETURN).list());
			
			if (beanList == null || beanList.size() <= 0) {
				
				Object obj = getSession()
						.createQuery(
								"select max(id) from ItemStockModel where item.id=:itm and  status="+SConstants.stock_statuses.GRV_STOCK)
								.setLong("itm", item_id).uniqueResult();
				
				if (obj != null)
					beanList
					.addAll(getSession()
							.createQuery(
									"select new com.inventory.purchase.bean.InventoryDetailsPojo("
											+ " id, concat("+tag_crit+" 'Stock ID : ',id,' , ', ' Bal: ' , balance, ' ', item.unit.symbol,', P.Rate:',round(rate,2) ),status) from ItemStockModel where id=:id")
											.setLong("id", (Long) obj).list());
				
			}
			
			// resultList.addAll(getSession()
			// .createQuery("select new com.inventory.purchase.bean.InventoryDetailsPojo("
			// +
			// " id, concat('<<GRV>> Stock ID : ',id,' , ', ' Bal: ' , balance, ' ', item.unit.symbol ),status) from ItemStockModel where item.id=:itm and balance>0 and status=3 order by id")
			// .setLong("itm", item_id).list());
			
			Iterator iter=beanList.iterator();
			InventoryDetailsPojo bean;
			while (iter.hasNext()) {
				bean = (InventoryDetailsPojo) iter.next();
				ItemStockModel mdl=(ItemStockModel) getSession().get(ItemStockModel.class, bean.getId());
				AcctReportMainBean accBean=new AcctReportMainBean("","",mdl.getRate());
					String tag="";
					if(isUseTag)
						tag=mdl.getItem_tag();
					
					if(mdl.getPurchase_id()!=0){
					
						switch (mdl.getPurchase_type()) {
						case SConstants.stockPurchaseType.PURCHASE:
							// Contsructor #34
							accBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
									"'',concat('Purchase : ',a.purchase_no),coalesce(b.unit_price/(b.qty_in_basic_unit/b.qunatity),0))from PurchaseModel a join " +
									" a.purchase_details_list b where b.id=:id")
								.setParameter("id", mdl.getInv_det_id()).uniqueResult();
							
						break;
						case SConstants.stockPurchaseType.PURCHASE_GRN:
							accBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
									"'',concat('GRN :', a.grn_no),coalesce(b.unit_price/(b.qty_in_basic_unit/b.qunatity),0))from PurchaseGRNModel a " +
									" join a.grn_details_list b where b.id=:id")
								.setParameter("id", mdl.getInv_det_id()).uniqueResult();
						break;
						case SConstants.stockPurchaseType.SALES_RETURN:
							accBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
									"cast(b.reasonId as string),concat('Sale Return :', a.return_no),coalesce(b.unit_price/(b.quantity_in_basic_unit/b.qunatity),0))from SalesReturnModel a " +
									" join a.inventory_details_list b where b.id=:id")
								.setParameter("id", mdl.getInv_det_id()).uniqueResult();
						break;
						case SConstants.stockPurchaseType.STOCK_CREATE:
							accBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
									"'',concat('Stock Create :',a.purchase_number),coalesce(b.unit_price/(b.qty_in_basic_unit/b.qunatity),0))from StockCreateModel a" +
									" join a.inventory_details_list b where b.id=:id")
							.setParameter("id", mdl.getInv_det_id()).uniqueResult();
						break;
						case SConstants.stockPurchaseType.STOCK_TRANSFER:
							accBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
									"'',concat('Stock Transfer :',cast(a.transfer_no as string)),coalesce(b.unit_price/(b.quantity_in_basic_unit/b.quantity),0))from StockTransferModel a " +
									" join a.inventory_details_list b where b.id=:id")
							.setParameter("id", mdl.getInv_det_id()).uniqueResult();
						break;

						default:
							accBean=new AcctReportMainBean("","",mdl.getRate());
						break;
					}
					}
					String reason="";
					if(!accBean.getName().equals("")&&!accBean.getName().equals("0"))
						reason=(String) getSession().createQuery("select name from ReasonModel where id=:id").setParameter("id", Long.parseLong(accBean.getName())).uniqueResult();
					bean.setStock_details(tag+" "+accBean.getBill_no()+", ID : "+mdl.getId()+" ," +
							"  Bal:  "+ mdl.getBalance()+" "+mdl.getItem().getUnit().getSymbol()+
							" , P.Rate: "+CommonUtil.roundNumberToString(accBean.getAmount())+", "+reason);
					resultList.add(bean);
			}
			
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return resultList;
	}
	
	@SuppressWarnings({ "finally", "unchecked" })
	public List getUsedStocks(List<Long> stocks, boolean isUseTag) throws Exception {
		try {

			resultList = new ArrayList();
			ArrayList	beanList = new ArrayList();
			
			String tag_crit="";
			if(isUseTag)
				tag_crit="'TAG:',item_tag,', ',";
			
			begin();
			beanList
					.addAll(getSession()
							.createQuery(
									"select new com.inventory.purchase.bean.InventoryDetailsPojo("
											+ " id, concat("+tag_crit+" 'Stock ID : ',id,' , ', ' Bal: ' ," +
											" balance, ' ', item.unit.symbol,', P.Rate:',round(rate,2) ),status)" +
											"from ItemStockModel where id in (:ids)")
							.setParameterList("ids", stocks).list());
			
			Iterator iter=beanList.iterator();
			InventoryDetailsPojo bean;
			while (iter.hasNext()) {
				bean = (InventoryDetailsPojo) iter.next();
				ItemStockModel mdl=(ItemStockModel) getSession().get(ItemStockModel.class, bean.getId());
				AcctReportMainBean accBean=new AcctReportMainBean("","",mdl.getRate());
					String tag="";
					if(isUseTag)
						tag=mdl.getItem_tag();
					if(mdl.getPurchase_id()!=0){
						switch (mdl.getPurchase_type()) {
						case SConstants.stockPurchaseType.PURCHASE:
							// Contsructor #34
							accBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
									"'',concat('Purchase : ',a.purchase_no),coalesce(b.unit_price/(b.qty_in_basic_unit/b.qunatity),0))from PurchaseModel a join " +
									" a.purchase_details_list b where b.id=:id")
								.setParameter("id", mdl.getInv_det_id()).uniqueResult();
							
						break;
						case SConstants.stockPurchaseType.PURCHASE_GRN:
							accBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
									"'',concat('GRN :', a.grn_no),coalesce(b.unit_price/(b.qty_in_basic_unit/b.qunatity),0))from PurchaseGRNModel a " +
									" join a.grn_details_list b where b.id=:id")
								.setParameter("id", mdl.getInv_det_id()).uniqueResult();
						break;
						case SConstants.stockPurchaseType.SALES_RETURN:
							accBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
									"cast(b.reasonId as string),concat('Sale Return :', a.return_no),coalesce(b.unit_price/(b.quantity_in_basic_unit/b.qunatity),0))from SalesReturnModel a " +
									" join a.inventory_details_list b where b.id=:id")
								.setParameter("id", mdl.getInv_det_id()).uniqueResult();
						break;
						case SConstants.stockPurchaseType.STOCK_CREATE:
							accBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
									"'',concat('Stock Create :',a.purchase_number),coalesce(b.unit_price/(b.qty_in_basic_unit/b.qunatity),0))from StockCreateModel a" +
									" join a.inventory_details_list b where b.id=:id")
							.setParameter("id", mdl.getInv_det_id()).uniqueResult();
						break;
						case SConstants.stockPurchaseType.STOCK_TRANSFER:
							accBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
									"'',concat('Stock Transfer :',cast(a.transfer_no as string)),coalesce(b.unit_price/(b.quantity_in_basic_unit/b.quantity),0))from StockTransferModel a " +
									" join a.inventory_details_list b where b.id=:id")
							.setParameter("id", mdl.getInv_det_id()).uniqueResult();
						break;

						default:
							accBean=new AcctReportMainBean("","",mdl.getRate());
						break;
					}
					}
					String reason="";
					if(!accBean.getName().equals("")&&!accBean.getName().equals("0"))
						reason=(String) getSession().createQuery("select name from ReasonModel where id=:id").setParameter("id", Long.parseLong(accBean.getName())).uniqueResult();
					bean.setStock_details(tag+" "+accBean.getBill_no()+", ID : "+mdl.getId()+" ," +
							"  Bal:  "+ mdl.getBalance()+" "+mdl.getItem().getUnit().getSymbol()+
							" , P.Rate: "+CommonUtil.roundNumberToString(accBean.getAmount())+", "+reason);
					resultList.add(bean);

			}
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return resultList;
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List getStocksWithGRV(long item_id, boolean isUseTag) throws Exception {
		try {
			
			resultList = new ArrayList();
			ArrayList beanList = new ArrayList();
			
			String tag_crit="";
			if(isUseTag)
				tag_crit="'TAG:',item_tag,', ',";
			
			begin();
			beanList
			.addAll(getSession()
					.createQuery(
							"select new com.inventory.purchase.bean.InventoryDetailsPojo("
									+ " id, concat("+tag_crit+" 'Stock ID : ',id,' , ', ' Bal: ' , balance, ' ', item.unit.symbol,', P.Rate:',round(rate,2) ),status) from ItemStockModel where item.id=:itm and balance>0 and (status=1 or status=2 or status="+SConstants.stock_statuses.GOOD_STOCK+" or status="+SConstants.stock_statuses.MANUFACTURED_STOCK+" or status="+SConstants.stock_statuses.GRV_STOCK+") order by id")
									.setLong("itm", item_id).list());
			
			if (beanList == null || beanList.size() <= 0) {
				
				Object obj = getSession()
						.createQuery(
								"select max(id) from ItemStockModel where item.id=:itm and (status=1 or status=2 or status="+SConstants.stock_statuses.GOOD_STOCK+" or status="+SConstants.stock_statuses.MANUFACTURED_STOCK+" or status="+SConstants.stock_statuses.GRV_STOCK+")")
								.setLong("itm", item_id).uniqueResult();
				
				if (obj != null)
					beanList
					.addAll(getSession()
							.createQuery(
									"select new com.inventory.purchase.bean.InventoryDetailsPojo("
											+ " id, concat("+tag_crit+" 'Stock ID : ',id,' , ', ' Bal: ' , balance, ' ', item.unit.symbol,', P.Rate:',round(rate,2) ),status) from ItemStockModel where id=:id")
											.setLong("id", (Long) obj).list());
				
			}
			
			Iterator iter=beanList.iterator();
			InventoryDetailsPojo bean;
			while (iter.hasNext()) {
				bean = (InventoryDetailsPojo) iter.next();
				ItemStockModel mdl=(ItemStockModel) getSession().get(ItemStockModel.class, bean.getId());
				AcctReportMainBean accBean=new AcctReportMainBean("","",mdl.getRate());
					String tag="";
					if(isUseTag)
						tag=mdl.getItem_tag();
					if(mdl.getPurchase_id()!=0){
						switch (mdl.getPurchase_type()) {
						case SConstants.stockPurchaseType.PURCHASE:
							// Contsructor #34
							accBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
									"'',concat('Purchase : ',a.purchase_no),coalesce(b.unit_price/(b.qty_in_basic_unit/b.qunatity),0))from PurchaseModel a join " +
									" a.purchase_details_list b where b.id=:id")
								.setParameter("id", mdl.getInv_det_id()).uniqueResult();
							
						break;
						case SConstants.stockPurchaseType.PURCHASE_GRN:
							accBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
									"'',concat('GRN :', a.grn_no),coalesce(b.unit_price/(b.qty_in_basic_unit/b.qunatity),0))from PurchaseGRNModel a " +
									" join a.grn_details_list b where b.id=:id")
								.setParameter("id", mdl.getInv_det_id()).uniqueResult();
						break;
						case SConstants.stockPurchaseType.SALES_RETURN:
							accBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
									"cast(b.reasonId as string),concat('Sale Return :', a.return_no),coalesce(b.unit_price/(b.quantity_in_basic_unit/b.qunatity),0))from SalesReturnModel a " +
									" join a.inventory_details_list b where b.id=:id")
								.setParameter("id", mdl.getInv_det_id()).uniqueResult();
						break;
						case SConstants.stockPurchaseType.STOCK_CREATE:
							accBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
									"'',concat('Stock Create :',a.purchase_number),coalesce(b.unit_price/(b.qty_in_basic_unit/b.qunatity),0))from StockCreateModel a" +
									" join a.inventory_details_list b where b.id=:id")
							.setParameter("id", mdl.getInv_det_id()).uniqueResult();
						break;
						case SConstants.stockPurchaseType.STOCK_TRANSFER:
							accBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
									"'',concat('Stock Transfer :',cast(a.transfer_no as string)),coalesce(b.unit_price/(b.quantity_in_basic_unit/b.quantity),0))from StockTransferModel a " +
									" join a.inventory_details_list b where b.id=:id")
							.setParameter("id", mdl.getInv_det_id()).uniqueResult();
						break;

						default:
							accBean=new AcctReportMainBean("","",mdl.getRate());
						break;
					}
					}
					String reason="";
					if(!accBean.getName().equals("")&&!accBean.getName().equals("0"))
						reason=(String) getSession().createQuery("select name from ReasonModel where id=:id").setParameter("id", Long.parseLong(accBean.getName())).uniqueResult();
					bean.setStock_details(tag+" "+accBean.getBill_no()+", ID : "+mdl.getId()+" ," +
							"  Bal:  "+ mdl.getBalance()+" "+mdl.getItem().getUnit().getSymbol()+
							" , P.Rate: "+CommonUtil.roundNumberToString(accBean.getAmount())+", "+reason);
					resultList.add(bean);
			}
			
			// resultList.addAll(getSession()
			// .createQuery("select new com.inventory.purchase.bean.InventoryDetailsPojo("
			// +
			// " id, concat('<<GRV>> Stock ID : ',id,' , ', ' Bal: ' , balance, ' ', item.unit.symbol ),status) from ItemStockModel where item.id=:itm and balance>0 and status=3 order by id")
			// .setLong("itm", item_id).list());
			
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return resultList;
	}
	
	
	public Long getDefaultStockToSelect(long item_id) throws Exception {
		long stk_id=0;
		try {
			
			begin();
			
			if(isFIFO){
				stk_id = (Long) getSession()
					.createQuery(
							"select coalesce(min(id),0) from ItemStockModel where item.id=:itm and balance>0 and status!=:sts")
					.setLong("itm", item_id).setParameter("sts", SConstants.stock_statuses.GOOD_STOCK).uniqueResult();
			}else{
				stk_id = (Long) getSession()
						.createQuery(
								"select coalesce(max(id),0) from ItemStockModel where item.id=:itm and balance>0 and status!=:sts")
						.setLong("itm", item_id).setParameter("sts", SConstants.stock_statuses.GOOD_STOCK).uniqueResult();
			}
			
			if(stk_id==0){
				if(isFIFO){
					stk_id = (Long) getSession()
						.createQuery(
								"select coalesce(min(id),0) from ItemStockModel where item.id=:itm and status!=:sts")
						.setLong("itm", item_id).setParameter("sts", SConstants.stock_statuses.GOOD_STOCK).uniqueResult();
				}else{
					stk_id = (Long) getSession()
							.createQuery(
									"select coalesce(max(id),0) from ItemStockModel where item.id=:itm  and status!=:sts")
							.setLong("itm", item_id).setParameter("sts", SConstants.stock_statuses.GOOD_STOCK).uniqueResult();
				}
			}

			commit();
			
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return stk_id;
		
	}
	
	
	
	
	public void decreaseStockByStockID(long stk_id, double qty_in_basic_unit) throws Exception {
		if (stk_id != 0) {
			getSession().createQuery(
							"update ItemStockModel set balance=balance-:qty, blocked=true  where id=:id")
					.setLong("id", stk_id).setDouble("qty", qty_in_basic_unit)
					.executeUpdate();
		}

	}
	
	
	public void decreaseStockByStockID(long stk_id, double qty_in_basic_unit, boolean block) throws Exception {
		if (stk_id != 0) {
			getSession().createQuery("update ItemStockModel set balance=balance-:qty, blocked=:block  where id=:id")
					.setLong("id", stk_id).setParameter("block", block).setDouble("qty", qty_in_basic_unit).executeUpdate();
		}

	}

	
	public void increaseStockByStockID(long stk_id, double qty_in_basic_unit, boolean block) throws Exception {
		if (stk_id != 0) {
			getSession().createQuery("update ItemStockModel set balance=balance+:qty, blocked=:block  where id=:id")
					.setLong("id", stk_id).setParameter("block", block).setDouble("qty", qty_in_basic_unit).executeUpdate();
		}

	}

	
	public void increaseStockByStockID(long stk_id, double qty_in_basic_unit)
			throws Exception {
		
		if (stk_id != 0) {

			ItemStockModel obj = (ItemStockModel) getSession().get(
					ItemStockModel.class, stk_id);
			if (obj != null) {
				obj.setBalance(obj.getBalance() + qty_in_basic_unit);

				if (obj.getBalance() < obj.getQuantity()) {
					obj.setBlocked(true);
				} else {
					obj.setBlocked(false);
				}
				getSession().update(obj);

				/*
				 * getSession().createQuery(
				 * "update ItemStockModel set balance=balance+:qty  where id=:id"
				 * ) .setLong("id", stk_id).setDouble("qty", qty_in_basic_unit)
				 * .executeUpdate();
				 */
			}
		}
	}
	
	
	
	
	
	
	
	
	public void updateWorkingDate(long officeID, long org_id, Date date) throws Exception {

		try {

			begin();
			
			if(org_id==0) {
				getSession().createQuery(
						"update S_OfficeModel set workingDate=:dt")
						.setParameter("dt", date).executeUpdate();
			}
			else if(officeID==0) {
				getSession().createQuery(
						"update S_OfficeModel set workingDate=:dt where organization.id=:id")
						.setParameter("dt", date).setLong("id", org_id).executeUpdate();
			}
			else {
				getSession().createQuery(
						"update S_OfficeModel set workingDate=:dt where id=:id")
						.setParameter("dt", date).setLong("id", officeID).executeUpdate();
			}
			
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
	}
	
	
	public double getLedgerCurrentBalance(long led_id) throws Exception {
		double current_balance = 0;
		try {
			begin();
			current_balance = (Double) getSession().createQuery("select coalesce(current_balance,0) from LedgerModel where id=:led")
							.setLong("led", led_id).uniqueResult();
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return current_balance;
	}
	
	
	public double getItemBalanceAtDate(long item_id, Date date) throws Exception {
		double balance=0;
		try {
			
			begin();
			
			
			Object obj= getSession().createQuery("select max(date) from StockResetDetailsModel where item.id=:itm and date<:dt")
					.setParameter("dt", date).setLong("itm", item_id).uniqueResult();
			
			
			if(obj!=null) {
				
				
				double saleQty=(Double) getSession().createQuery("select coalesce(sum(b.quantity_in_basic_unit),0)"
								+ " from SalesModel a join a.inventory_details_list b  where a.date>:stdt and a.date<=:dt and b.item.id=:itm and a.active=true")
								.setParameter("stdt", obj).setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				double purchaseQty=(Double) getSession().createQuery("select coalesce(sum(b.qty_in_basic_unit),0)"
						+ " from PurchaseModel a join a.purchase_details_list b  where a.date>:stdt and a.date<=:dt and b.item.id=:itm and a.active=true")
						.setParameter("stdt", obj).setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				
				double saleRtnQty=(Double) getSession().createQuery("select coalesce(sum(b.qunatity),0)"
						+ " from SalesReturnModel a join a.inventory_details_list b  where a.date>:stdt and a.date<=:dt and b.item.id=:itm and a.active=true")
						.setParameter("stdt", obj).setParameter("dt", date).setLong("itm", item_id).uniqueResult();
		
				double purchaseRtnQty=(Double) getSession().createQuery("select coalesce(sum(b.qty_in_basic_unit),0)"
						+ " from PurchaseReturnModel a join a.inventory_details_list b  where a.date>:stdt and a.date<=:dt and b.item.id=:itm and a.active=true")
						.setParameter("stdt", obj).setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				
				double deliveryNoteQty=(Double) getSession().createQuery("select coalesce(sum(b.qty_in_basic_unit),0)"
						+ " from DeliveryNoteModel a join a.delivery_note_details_list b  where a.date>:stdt and a.date<=:dt and b.item.id=:itm and a.active=true")
						.setParameter("stdt", obj).setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				
				double itmTransfredQty=(Double) getSession().createQuery("select coalesce(sum(b.quantity_in_basic_unit),0)"
						+ " from ItemTransferModel a join a.inventory_details_list b  where a.date>:stdt and a.date<=:dt and b.item.id=:itm")
						.setParameter("stdt", obj).setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				double itmReceivedQty=(Double) getSession().createQuery("select coalesce(sum(stock.quantity),0)"
						+ " from TransferStockMap where stock.manufacturing_date>:stdt and stock.manufacturing_date<=:dt and stock.item.id=:itm")
						.setParameter("stdt", obj).setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				
				double resetedQty=(Double) getSession().createQuery("select reseted_quantity from StockResetDetailsModel where id=(select max(id) from StockResetDetailsModel where date=:dt and item.id=:itm)")
						.setParameter("dt", obj).setLong("itm", item_id).uniqueResult();
				
				double manufactruedQty=(Double) getSession().createQuery("select coalesce(sum(qty_in_basic_unit),0)"
						+ " from ManufacturingModel  where date<=:dt and item.id=:itm")
						.setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				double rawMaterialQty=(Double) getSession().createQuery("select coalesce(sum(b.quantity),0)"
						+ " from ManufacturingModel a join a.manufacturing_details_list b where a.date<=:dt and b.item.id=:itm")
						.setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
//				double resetedQty=(Double) getSession().createQuery("select coalesce(sum(-(balance_before_reset-reseted_quantity)),0)"
//						+ " from StockResetDetailsModel where date<=:dt and item.id=:itm")
//						.setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				
				
				
				balance=CommonUtil.roundNumber(resetedQty)+CommonUtil.roundNumber(purchaseQty)-
						CommonUtil.roundNumber(saleQty)+CommonUtil.roundNumber(saleRtnQty)-
						CommonUtil.roundNumber(purchaseRtnQty)-CommonUtil.roundNumber(deliveryNoteQty)
						-CommonUtil.roundNumber(itmTransfredQty)+CommonUtil.roundNumber(itmReceivedQty)+CommonUtil.roundNumber(manufactruedQty)
						-CommonUtil.roundNumber(rawMaterialQty)
						;
			}
			else {
				double openingBal=(Double) getSession().createQuery("select opening_balance from ItemModel where id=:id")
						.setLong("id", item_id).uniqueResult();
		
				double saleQty=(Double) getSession().createQuery("select coalesce(sum(b.quantity_in_basic_unit),0)"
								+ " from SalesModel a join a.inventory_details_list b  where a.date<=:dt and b.item.id=:itm and a.active=true")
								.setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				double purchaseQty=(Double) getSession().createQuery("select coalesce(sum(b.qty_in_basic_unit),0)"
						+ " from PurchaseModel a join a.purchase_details_list b  where a.date<=:dt and b.item.id=:itm and a.active=true")
						.setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				double saleRtnQty=(Double) getSession().createQuery("select coalesce(sum(b.qunatity),0)"
						+ " from SalesReturnModel a join a.inventory_details_list b  where a.date<=:dt and b.item.id=:itm and a.active=true")
						.setParameter("dt", date).setLong("itm", item_id).uniqueResult();
		
				double purchaseRtnQty=(Double) getSession().createQuery("select coalesce(sum(b.qty_in_basic_unit),0)"
						+ " from PurchaseReturnModel a join a.inventory_details_list b  where a.date<=:dt and b.item.id=:itm and a.active=true")
						.setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				
				double deliveryNoteQty=(Double) getSession().createQuery("select coalesce(sum(b.qty_in_basic_unit),0)"
						+ " from DeliveryNoteModel a join a.delivery_note_details_list b  where a.date<=:dt and b.item.id=:itm and a.active=true")
						.setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				
				double itmTransfredQty=(Double) getSession().createQuery("select coalesce(sum(b.quantity_in_basic_unit),0)"
						+ " from ItemTransferModel a join a.inventory_details_list b  where a.date<=:dt and b.item.id=:itm")
						.setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				double itmReceivedQty=(Double) getSession().createQuery("select coalesce(sum(stock.quantity),0)"
						+ " from TransferStockMap where stock.manufacturing_date<=:dt and stock.item.id=:itm")
						.setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				
				double resetedQty=(Double) getSession().createQuery("select coalesce(sum(-(balance_before_reset-reseted_quantity)),0)"
						+ " from StockResetDetailsModel where date<=:dt and item.id=:itm")
						.setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				double manufactruedQty=(Double) getSession().createQuery("select coalesce(sum(qty_in_basic_unit),0)"
						+ " from ManufacturingModel  where date<=:dt and item.id=:itm")
						.setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				double rawMaterialQty=(Double) getSession().createQuery("select coalesce(sum(b.quantity),0)"
						+ " from ManufacturingModel a join a.manufacturing_details_list b where a.date<=:dt and b.item.id=:itm")
						.setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				
				balance=CommonUtil.roundNumber(openingBal)+CommonUtil.roundNumber(purchaseQty)-
						CommonUtil.roundNumber(saleQty)+CommonUtil.roundNumber(saleRtnQty)-
						CommonUtil.roundNumber(purchaseRtnQty)-CommonUtil.roundNumber(deliveryNoteQty)
						-CommonUtil.roundNumber(itmTransfredQty)+CommonUtil.roundNumber(itmReceivedQty)
						+CommonUtil.roundNumber(resetedQty)+CommonUtil.roundNumber(manufactruedQty)-CommonUtil.roundNumber(rawMaterialQty);
			}
			
			
			
			commit();
			
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return CommonUtil.roundNumber(balance);
		
	}
	
	
	public double getItemBalanceAtDateNew(long item_id, Date date, long office) throws Exception {
		double balance=0;
		try {
			
			begin();
			String cdn="";
			String transCdn="";
			String recCdn="";
			if(office!=0){
				transCdn+=" and a.from_office.id="+office;
				recCdn+=" and a.to_office.id="+office;
			}
			
			Object obj= getSession().createQuery("select max(date) from StockResetDetailsModel where item.id=:itm and date<:dt")
					.setParameter("dt", date).setLong("itm", item_id).uniqueResult();
			
			if(obj!=null) {
				
				double saleQty=(Double) getSession().createQuery("select coalesce(sum(b.quantity_in_basic_unit),0) from SalesModel a " +
								" join a.inventory_details_list b  where a.date>:stdt and a.date<=:dt and b.delivery_id=0 and b.item.id=:itm and a.active=true"+cdn)
								.setParameter("stdt", obj).setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				double deliveryNoteQty=(Double) getSession().createQuery("select coalesce(sum(b.qty_in_basic_unit),0) from DeliveryNoteModel " +
						" a join a.delivery_note_details_list b  where a.date>:stdt and a.date<=:dt and b.item.id=:itm and a.active=true"+cdn)
								.setParameter("stdt", obj).setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				double purchaseQty=(Double) getSession().createQuery("select coalesce(sum(b.qty_in_basic_unit),0) from PurchaseModel a " +
						" join a.purchase_details_list b  where a.date>:stdt and a.date<=:dt and b.item.id=:itm and b.grn_id=0 and a.active=true"+cdn)
						.setParameter("stdt", obj).setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				double grnQty=(Double) getSession().createQuery("select coalesce(sum(b.qty_in_basic_unit),0) from PurchaseGRNModel a " +
						" join a.grn_details_list b  where a.date>:stdt and a.date<=:dt and b.item.id=:itm and a.active=true"+cdn)
						.setParameter("stdt", obj).setParameter("dt", date).setLong("itm", item_id).uniqueResult();
								
				double saleRtnQty=(Double) getSession().createQuery("select coalesce(sum(b.quantity_in_basic_unit),0) from SalesReturnModel " +
						" a join a.inventory_details_list b  where a.date>:stdt and a.date<=:dt and b.item.id=:itm and a.active=true"+cdn)
						.setParameter("stdt", obj).setParameter("dt", date).setLong("itm", item_id).uniqueResult();
		
				double purchaseRtnQty=(Double) getSession().createQuery("select coalesce(sum(b.qty_in_basic_unit),0) from PurchaseReturnModel " +
						" a join a.inventory_details_list b  where a.date>:stdt and a.date<=:dt and b.item.id=:itm and a.active=true"+cdn)
						.setParameter("stdt", obj).setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				double stockTransferred=(Double) getSession().createQuery("select coalesce(sum(b.quantity_in_basic_unit),0) from StockTransferModel " +
						" a join a.inventory_details_list b  where a.transfer_date>:stdt and a.transfer_date<=:dt and b.stock_id.item.id=:itm "+transCdn)
						.setParameter("stdt", obj).setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				double stockReceived=(Double) getSession().createQuery("select coalesce(sum(b.quantity_in_basic_unit),0) from StockTransferModel " +
						" a join a.inventory_details_list b  where a.transfer_date>:stdt and a.transfer_date<=:dt and b.stock_id.item.id=:itm "+recCdn)
						.setParameter("stdt", obj).setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				double stockCreate=(Double) getSession().createQuery("select coalesce(sum(b.qty_in_basic_unit),0) from StockCreateModel " +
						" a join a.inventory_details_list b  where a.date>:stdt and a.date<=:dt and b.item.id=:itm "+cdn)
						.setParameter("stdt", obj).setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				double stockDispose=(Double) getSession().createQuery("select coalesce(sum(b.qty_in_basic_unit),0) from DisposeItemsModel " +
						" a join a.item_details_list b  where a.date>:stdt and a.date<=:dt and b.item.id=:itm ")
						.setParameter("stdt", obj).setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				balance=CommonUtil.roundNumber(purchaseQty)+CommonUtil.roundNumber(grnQty)-
						CommonUtil.roundNumber(saleQty)-CommonUtil.roundNumber(deliveryNoteQty)-
						CommonUtil.roundNumber(purchaseRtnQty)+CommonUtil.roundNumber(saleRtnQty)-
						CommonUtil.roundNumber(stockTransferred)+CommonUtil.roundNumber(stockReceived)
						+CommonUtil.roundNumber(stockCreate)-CommonUtil.roundNumber(stockDispose);
			}
			else {
				double openingBal=(Double) getSession().createQuery("select opening_balance from ItemModel where id=:id").setLong("id", item_id).uniqueResult();
		
				double saleQty=(Double) getSession().createQuery("select coalesce(sum(b.quantity_in_basic_unit),0) from SalesModel a " +
						" join a.inventory_details_list b  where a.date<=:dt and b.item.id=:itm and b.delivery_id=0 and a.active=true"+cdn)
						.setParameter("dt", date).setLong("itm", item_id).uniqueResult();
		
				double deliveryNoteQty=(Double) getSession().createQuery("select coalesce(sum(b.qty_in_basic_unit),0) from DeliveryNoteModel " +
						" a join a.delivery_note_details_list b  where a.date<=:dt and b.item.id=:itm and a.active=true"+cdn)
							.setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				double purchaseQty=(Double) getSession().createQuery("select coalesce(sum(b.qty_in_basic_unit),0) from PurchaseModel a " +
						" join a.purchase_details_list b  where a.date<=:dt and b.item.id=:itm and b.grn_id=0 and  a.active=true"+cdn)
						.setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				double grnQty=(Double) getSession().createQuery("select coalesce(sum(b.qty_in_basic_unit),0) from PurchaseGRNModel a " +
						" join a.grn_details_list b  where a.date<=:dt and b.item.id=:itm and a.active=true"+cdn)
						.setParameter("dt", date).setLong("itm", item_id).uniqueResult();
								
				double saleRtnQty=(Double) getSession().createQuery("select coalesce(sum(b.quantity_in_basic_unit),0) from SalesReturnModel " +
						" a join a.inventory_details_list b  where a.date<=:dt and b.item.id=:itm and a.active=true"+cdn)
						.setParameter("dt", date).setLong("itm", item_id).uniqueResult();
		
				double purchaseRtnQty=(Double) getSession().createQuery("select coalesce(sum(b.qty_in_basic_unit),0) from PurchaseReturnModel " +
						" a join a.inventory_details_list b  where a.date<=:dt and b.item.id=:itm and a.active=true"+cdn)
						.setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				double stockTransferred=(Double) getSession().createQuery("select coalesce(sum(b.quantity_in_basic_unit),0) from StockTransferModel " +
						" a join a.inventory_details_list b  where a.transfer_date<=:dt and b.stock_id.item.id=:itm "+transCdn)
						.setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				double stockReceived=(Double) getSession().createQuery("select coalesce(sum(b.quantity_in_basic_unit),0) from StockTransferModel " +
						" a join a.inventory_details_list b  where a.transfer_date<=:dt and b.stock_id.item.id=:itm "+recCdn)
						.setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				double stockCreate=(Double) getSession().createQuery("select coalesce(sum(b.qty_in_basic_unit),0) from StockCreateModel " +
						" a join a.inventory_details_list b  where  a.date<=:dt and b.item.id=:itm "+cdn)
						.setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				
				double stockDispose=(Double) getSession().createQuery("select coalesce(sum(b.qty_in_basic_unit),0) from DisposeItemsModel " +
						" a join a.item_details_list b  where a.date<=:dt and b.item.id=:itm ")
						.setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				balance=CommonUtil.roundNumber(openingBal)+CommonUtil.roundNumber(purchaseQty)+CommonUtil.roundNumber(grnQty)-
						CommonUtil.roundNumber(saleQty)-CommonUtil.roundNumber(deliveryNoteQty)-
						CommonUtil.roundNumber(purchaseRtnQty)+CommonUtil.roundNumber(saleRtnQty)-
						CommonUtil.roundNumber(stockTransferred)+CommonUtil.roundNumber(stockReceived)+
						CommonUtil.roundNumber(stockCreate)-CommonUtil.roundNumber(stockDispose);
			}
			commit();
			
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return CommonUtil.roundNumber(balance);
	}
	
	public double getItemBalanceAtDateFromStock(long item_id, long location, Date date, long office) throws Exception {
		double balance=0;
		try {
			
			begin();
			String locCon="";
			if(location!=0){
				locCon+=" and location_id="+location;
			}
			ItemStockModel stkMdl;
			List stockList = getSession()
					.createQuery(
							"from ItemStockModel where item.id=:itm and date(date_time)<=:dt"+locCon)
					.setParameter("itm", item_id).setParameter("dt", date).list();
			
			Iterator iter=stockList.iterator();
			while (iter.hasNext()) {
				stkMdl = (ItemStockModel) iter.next();
				
				double saleQty=(Double) getSession().createQuery("select coalesce(sum(b.quantity_in_basic_unit),0) from SalesModel a " +
						" join a.inventory_details_list b  where a.date<=:dt and b.item.id=:itm and b.delivery_id=0 and a.active=true and b.stock_id=:stk ")
						.setParameter("dt", date).setLong("itm", item_id).setLong("stk", stkMdl.getId()).uniqueResult();
		
				double deliveryNoteQty=(Double) getSession().createQuery("select coalesce(sum(b.qty_in_basic_unit),0) from DeliveryNoteModel " +
						" a join a.delivery_note_details_list b  where a.date<=:dt and b.item.id=:itm and a.active=true and b.stock_id=:stk ")
							.setParameter("dt", date).setLong("itm", item_id).setLong("stk", stkMdl.getId()).uniqueResult();
				
				double purchaseRtnQty=(Double) getSession().createQuery("select coalesce(sum(b.qty_in_basic_unit),0) from PurchaseReturnModel " +
						" a join a.inventory_details_list b  where a.date<=:dt and b.item.id=:itm and a.active=true  and b.stock_id=:stk")
						.setParameter("dt", date).setLong("itm", item_id).setLong("stk", stkMdl.getId()).uniqueResult();
				
				double stockTransferred=(Double) getSession().createQuery("select coalesce(sum(b.quantity_in_basic_unit),0) from StockTransferModel " +
						" a join a.inventory_details_list b  where a.transfer_date<=:dt and b.stock_id.item.id=:itm and b.stock_id.id=:stk")
						.setParameter("dt", date).setLong("itm", item_id).setLong("stk", stkMdl.getId()).uniqueResult();
				
				
				double stockDispose=(Double) getSession().createQuery("select coalesce(sum(b.qty_in_basic_unit),0) from DisposeItemsModel " +
						" a join a.item_details_list b  where a.date<=:dt and b.item.id=:itm and b.stockId=:stk")
						.setParameter("dt", date).setLong("itm", item_id).setLong("stk", stkMdl.getId()).uniqueResult();
				
				balance+=stkMdl.getQuantity()-CommonUtil.roundNumber(saleQty)-CommonUtil.roundNumber(deliveryNoteQty)-
						CommonUtil.roundNumber(purchaseRtnQty)-CommonUtil.roundNumber(stockTransferred)
						-CommonUtil.roundNumber(stockDispose);
				
			}
			
			
			commit();
			
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return CommonUtil.roundNumber(balance);
	}
	
	
	
	public double getGRVStockQtyAtDate(long item_id, Date date) throws Exception {
		double grvStk=0;
		try {
			
			begin();
			
			Object obj= getSession().createQuery("select max(date) from StockResetDetailsModel where item.id=:itm and date<:dt")
						.setParameter("dt", date).setLong("itm", item_id).uniqueResult();
			
			
			if(obj!=null) {
				grvStk=(Double) getSession().createQuery("select coalesce(sum(b.stock_quantity),0)"
						+ " from SalesReturnModel a join a.inventory_details_list b  where a.date>:stdt and a.date<=:dt and b.item.id=:itm and a.active=true")
						.setParameter("stdt", obj).setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				grvStk-=(Double) getSession()
						.createQuery(
								"select coalesce(sum(b.quantity_in_basic_unit),0) from SalesModel a join a.inventory_details_list b where a.date>:stdt and a.date<=:dt and b.item.id=:itm and a.type=2 and a.active=true")
						.setParameter("stdt", obj).setParameter("itm", item_id)
						.setParameter("dt", date).uniqueResult();

			}
			else {
				grvStk=(Double) getSession().createQuery("select coalesce(sum(b.stock_quantity),0)"
						+ " from SalesReturnModel a join a.inventory_details_list b  where a.date<=:dt and b.item.id=:itm and a.active=true")
						.setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				grvStk-=(Double) getSession()
						.createQuery(
								"select coalesce(sum(b.quantity_in_basic_unit),0) from SalesModel a join a.inventory_details_list b where a.date <=:dt and b.item.id=:itm and a.type=2 and a.active=true")
						.setParameter("itm", item_id)
						.setParameter("dt", date).uniqueResult();
			}
			
						
//			grvStk-=(Double) getSession().createQuery("select coalesce(sum(b.stock_quantity),0)"
//					+ " from SalesReturnModel a join a.inventory_details_list b  where a.date<=:dt and b.item.id=:itm")
//					.setParameter("dt", date).setLong("itm", item_id).uniqueResult();
			
			commit();
			
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return CommonUtil.roundNumber(grvStk);
	}
	
	
	
	public double getGoodStockQtyAtDate(long item_id, Date date) throws Exception {
		double grvStk=0;
		try {
			
			begin();
			
			Object obj= getSession().createQuery("select max(date) from StockResetDetailsModel where item.id=:itm and date<:dt")
						.setParameter("dt", date).setLong("itm", item_id).uniqueResult();
			
			if(obj!=null) {
				grvStk=(Double) getSession().createQuery("select coalesce(sum(b.stock_quantity),0)"
						+ " from SalesReturnModel a join a.inventory_details_list b  where a.date>:stdt and a.date<=:dt and b.item.id=:itm and a.active=true")
						.setParameter("stdt", obj).setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				grvStk-=(Double) getSession()
						.createQuery("select coalesce(sum(b.quantity_in_basic_unit),0) from SalesModel a join a.inventory_details_list b where a.date>:stdt and a.date<=:dt and b.item.id=:itm and a.type=2 and a.active=true")
						.setParameter("stdt", obj).setParameter("itm", item_id)
						.setParameter("dt", date).uniqueResult();

			}
			else {
				grvStk=(Double) getSession().createQuery("select coalesce(sum(b.stock_quantity),0)"
						+ " from SalesReturnModel a join a.inventory_details_list b  where a.date<=:dt and b.item.id=:itm and a.active=true")
						.setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				grvStk-=(Double) getSession()
						.createQuery(
								"select coalesce(sum(b.quantity_in_basic_unit),0) from SalesModel a join a.inventory_details_list b where a.date <=:dt and b.item.id=:itm and a.type=2 and a.active=true")
						.setParameter("itm", item_id)
						.setParameter("dt", date).uniqueResult();
			}
			
						
//			grvStk-=(Double) getSession().createQuery("select coalesce(sum(b.stock_quantity),0)"
//					+ " from SalesReturnModel a join a.inventory_details_list b  where a.date<=:dt and b.item.id=:itm")
//					.setParameter("dt", date).setLong("itm", item_id).uniqueResult();
			
			commit();
			
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return CommonUtil.roundNumber(grvStk);
	}
	
	
	
	
	
	@SuppressWarnings("rawtypes")
	public double getPurchaseRateFromSalesID(long item_id, long salesID) throws Exception {
		double rate=0;
		try {
			
			begin();
			
			List list = getSession()
					.createQuery("select b.stock_ids from SalesModel a join a.inventory_details_list b where a.id=:id and b.item.id=:itm")
					.setLong("id", salesID).setLong("itm", item_id).list();
			
			rate=(Double) getSession().createQuery("select rate from ItemModel where id=:id")
			  			.setLong("id", item_id).uniqueResult();
			
			String[] stks;
			if(list.size()>0) {
				
				stks=list.get(0).toString().split(",");
				if(stks.length>0) {
					if(stks[0].length()>0) {
						long stockId=Long.parseLong(stks[0].split(":")[0]);
						
						Object obj=getSession().createQuery(
								"select rate from ItemStockModel where id="+stockId).uniqueResult();
						if(obj!=null) {
							rate=(Double)obj;
						}
					}
				}
			}
				
			commit();
			
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return CommonUtil.roundNumber(rate);
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List getAllEmails(long login_id) throws Exception {
		resultList=new ArrayList();
		try {
			
			begin();
			
			List lst = getSession().createQuery("select distinct emails from MyMailsModel where user_id=:usr order by emails")
					.setLong("usr", login_id).list();
			
			commit();
			Iterator itr=lst.iterator();
			while (itr.hasNext()) {
				Pattern p = Pattern.compile("\\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}\\b",Pattern.CASE_INSENSITIVE);
				Matcher m = p.matcher((String)itr.next());
				while (m.find()){
					if(m.group().length()>3)
						resultList.add(m.group());
				}
			}
			
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return resultList;
		
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List getAllEmailsAsKeyValueObject(long login_id) throws Exception {
		resultList=new ArrayList();
		try {
			
			begin();
			List lst =new ArrayList();
			lst.addAll(getSession().createQuery("select distinct emails from MyMailsModel where user_id=:usr order by emails").setLong("usr", login_id).list());
			commit();
			String email="";
			Iterator itr=lst.iterator();
			while (itr.hasNext()) {
				Pattern p = Pattern.compile("\\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}\\b",Pattern.CASE_INSENSITIVE);
				Matcher m = p.matcher((String)itr.next());
				while (m.find()){
					if(m.group().length()>3) {
						email=m.group();
						resultList.add(new KeyValue(email,email));
					}
				}
			}
			
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return resultList;
		
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List getEmailsAsKeyValueObject(long login_id,long officeId) throws Exception {
		resultList=new ArrayList();
		try {
			
			begin();
			List lst =new ArrayList();
			lst.addAll(getSession().createQuery("select distinct emails from MyMailsModel where user_id=:usr order by emails")
					.setLong("usr", login_id).list());
			lst.addAll(getSession().createQuery("select distinct address.email from CustomerModel where ledger.office.id=:ofc and address.email is not null")
					.setLong("ofc", officeId).list());
			lst.addAll(getSession().createQuery("select distinct address.email from SupplierModel where ledger.office.id=:ofc and address.email is not null")
					.setLong("ofc", officeId).list());
			lst.addAll(getSession().createQuery("select distinct email from ContactModel where login.id=:usr and email is not null")
					.setLong("usr", login_id).list());
			commit();
			String email="";
			Iterator itr=lst.iterator();
			while (itr.hasNext()) {
				Pattern p = Pattern.compile("\\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}\\b",Pattern.CASE_INSENSITIVE);
				Matcher m = p.matcher((String)itr.next());
				while (m.find()){
					if(m.group().length()>3) {
						email=m.group();
						resultList.add(new KeyValue(email,email));
					}
				}
			}
			
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return resultList;
		
	}
	
	public boolean isStockBlocked(long purch_id,long inv_det_id, int type) throws Exception {
		boolean blocked=false;
		try {
			begin();
			Object obj = getSession().createQuery("select blocked from ItemStockModel where purchase_id=:pid and inv_det_id=:inv and purchase_type=:type")
							.setLong("pid", purch_id).setLong("inv", inv_det_id).setParameter("type", type).uniqueResult();
			commit();
			if(obj!=null)
				blocked=(Boolean) obj;
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally {
			flush();
			close();
		}
		return blocked;
	}
	
	public boolean isStockBlocked(long stockId) throws Exception {
		boolean blocked=false;
		try {
			begin();
			Object obj = getSession().createQuery("select blocked from ItemStockModel where id=:pid")
					.setLong("pid", stockId).uniqueResult();
			commit();
			if(obj!=null)
				blocked=(Boolean) obj;
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally {
			flush();
			close();
		}
		return blocked;
	}
	
	public double getUnitConvertionValue(long item_id, long pur_unit_id,long sal_unit_id) throws Exception {
		double convVal = 1;
		try {
			if(pur_unit_id!=sal_unit_id)
				convVal=getConvertionQty(item_id, sal_unit_id,0)/getConvertionQty(item_id, pur_unit_id,0);

		} catch (Exception e) {
			convVal = 1;
		} 
		return convVal;
	}
	
	public double getUnitConvertionToSalesUnit(long item_id, long pur_unit_id,long sal_unit_id) throws Exception {
		double convVal = 1;
		try {
			if(pur_unit_id!=sal_unit_id)
				convVal=getConvertionQty(item_id, pur_unit_id, 0)/getConvertionQty(item_id, sal_unit_id, 0);

		} catch (Exception e) {
			convVal = 1;
		} 
		return convVal;
	}
	
	public double getUnitConvertionToToUnit(long item_id, long fromUnit,long toUnit) throws Exception {
		double convVal = 1;
		try {
			if(fromUnit!=toUnit)
				convVal=getConvertionQtyToPurchase(item_id, fromUnit, 0)/getConvertionQtyToPurchase(item_id, toUnit, 0);
			
		} catch (Exception e) {
			convVal = 1;
		} 
		return convVal;
	}
	
	@SuppressWarnings("rawtypes")
	public double getConvertionQty(long item_id, long unit_id, int sales_type) throws Exception {
		double conv_qty = 1;
		try {
			
			Object obj = getSession().createQuery("select convertion_rate from ItemUnitMangementModel where item.id=:itm and alternateUnit=:alt "
									+ " and sales_type=:st")
					.setLong("itm", item_id).setLong("alt", unit_id)
					.setLong("st", sales_type).uniqueResult();

			if (obj != null) {
				conv_qty = (Double) obj;
			}
			else{
				List list= getSession().createQuery("select convertion_rate from ItemUnitMangementModel where item.id=:itm and alternateUnit=:alt ")
						.setLong("itm", item_id).setLong("alt", unit_id)
						.list();
				
				if (list != null&&list.size()>0) {
					conv_qty = (Double) list.get(0);
				}
			}
			
		} catch (Exception e) {
			throw e;
		} 
			return conv_qty;
	}
	
	@SuppressWarnings("rawtypes")
	public double getConvertionQtyToPurchase(long item_id, long unit_id, int sales_type) throws Exception {
		double conv_qty = 1;
		try {
			
			Object obj = getSession().createQuery("select convertion_rate from ItemUnitMangementModel where item.id=:itm and alternateUnit=:alt "
					+ " and sales_type=:st")
					.setLong("itm", item_id).setLong("alt", unit_id)
					.setLong("st", sales_type).uniqueResult();
			
			if (obj != null) {
				conv_qty = (Double) obj;
			}
			else{
				List list= getSession().createQuery("select convertion_rate from ItemUnitMangementModel where item.id=:itm and alternateUnit=:alt ")
						.setLong("itm", item_id).setLong("alt", unit_id)
						.list();
				
				if (list != null&&list.size()>0) {
					conv_qty = (Double) list.get(0);
				}
			}
			
		} catch (Exception e) {
			throw e;
		} 
		return conv_qty;
	}
	
	public static void main(String args[]) {
		
		try {
			new CommonMethodsDao().getAllItemUnitDetails(4);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List getStocksWithSupplier(long item_id) throws Exception {
		try {
			
			resultList=new ArrayList();
			ArrayList	beanList=new ArrayList();
			
			
			begin();
			beanList.addAll(getSession()
					.createQuery("select new com.inventory.purchase.bean.InventoryDetailsPojo("
									+ " a.id, concat('TAG:',item_tag,', Stock ID : ',a.id,' , ', ' Bal: ' , a.balance, ' ', a.item.unit.symbol,' , ', ' Supp: ' ,case when a.purchase_id=0 then '' else b.supplier.name end,' , ', ' Rate: ' ,round(a.rate,2) ),a.status) " +
									"from ItemStockModel a, PurchaseModel b  where  a.purchase_id in (b.id,0) and a.item.id=:itm and a.balance>0 and (a.status=1 or a.status=2 or a.status=4)  order by a.id")
									.setLong("itm", item_id).list());
			
			if(beanList==null || beanList.size()<=0) {
				
				 Object obj= getSession()
						.createQuery("select max(id) from ItemStockModel where item.id=:itm and (status=1 or status=2  or status=4)")
										.setLong("itm", item_id).uniqueResult();
				 
				 if(obj!=null)
					 beanList.addAll(getSession()
								.createQuery("select new com.inventory.purchase.bean.InventoryDetailsPojo("
												+ " a.id, concat('TAG:',item_tag,' Stock ID : ',a.id,' , ', ' Bal: ' , a.balance, ' ', a.item.unit.symbol,' , ', ' Supp: ' ," +
									" case when a.purchase_id=0 then '' else b.supplier.name end,' , ', ' Rate: ' ,round(a.rate,2)),a.status) from ItemStockModel a, PurchaseModel b  where  a.purchase_id in (b.id,0) and a.id=:id")
												.setLong("id", (Long) obj).list());
				
			}
			
			Iterator iter=beanList.iterator();
			InventoryDetailsPojo bean;
			while (iter.hasNext()) {
				bean = (InventoryDetailsPojo) iter.next();
				ItemStockModel mdl=(ItemStockModel) getSession().get(ItemStockModel.class, bean.getId());
				AcctReportMainBean accBean=new AcctReportMainBean("","",mdl.getRate());
					String tag="";
						tag=mdl.getItem_tag();
					if(mdl.getPurchase_id()!=0){
						switch (mdl.getPurchase_type()) {
						case SConstants.stockPurchaseType.PURCHASE:
							// Contsructor #34
							accBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
									"'',concat('Purchase : ',a.purchase_no),coalesce(b.unit_price/(b.qty_in_basic_unit/b.qunatity),0))from PurchaseModel a join " +
									" a.purchase_details_list b where b.id=:id")
								.setParameter("id", mdl.getInv_det_id()).uniqueResult();
							
						break;
						case SConstants.stockPurchaseType.PURCHASE_GRN:
							accBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
									"'',concat('GRN :', a.grn_no),coalesce(b.unit_price/(b.qty_in_basic_unit/b.qunatity),0))from PurchaseGRNModel a " +
									" join a.grn_details_list b where b.id=:id")
								.setParameter("id", mdl.getInv_det_id()).uniqueResult();
						break;
						case SConstants.stockPurchaseType.SALES_RETURN:
							accBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
									"cast(b.reasonId as string),concat('Sale Return :', a.return_no),coalesce(b.unit_price/(b.quantity_in_basic_unit/b.qunatity),0))from SalesReturnModel a " +
									" join a.inventory_details_list b where b.id=:id")
								.setParameter("id", mdl.getInv_det_id()).uniqueResult();
						break;
						case SConstants.stockPurchaseType.STOCK_CREATE:
							accBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
									"'',concat('Stock Create :',a.purchase_number),coalesce(b.unit_price/(b.qty_in_basic_unit/b.qunatity),0))from StockCreateModel a" +
									" join a.inventory_details_list b where b.id=:id")
							.setParameter("id", mdl.getInv_det_id()).uniqueResult();
						break;
						case SConstants.stockPurchaseType.STOCK_TRANSFER:
							accBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
									"'',concat('Stock Transfer :',cast(a.transfer_no as string)),coalesce(b.unit_price/(b.quantity_in_basic_unit/b.quantity),0))from StockTransferModel a " +
									" join a.inventory_details_list b where b.id=:id")
							.setParameter("id", mdl.getInv_det_id()).uniqueResult();
						break;

						default:
							accBean=new AcctReportMainBean("","",mdl.getRate());
						break;
					}
					}
					String reason="";
					if(!accBean.getName().equals("")&&!accBean.getName().equals("0"))
						reason=(String) getSession().createQuery("select name from ReasonModel where id=:id").setParameter("id", Long.parseLong(accBean.getName())).uniqueResult();
					bean.setStock_details(tag+" "+accBean.getBill_no()+", ID : "+mdl.getId()+" ," +
							"  Bal:  "+ mdl.getBalance()+" "+mdl.getItem().getUnit().getSymbol()+
							" , P.Rate: "+CommonUtil.roundNumberToString(accBean.getAmount())+", "+reason);
					resultList.add(bean);
				}
			
//			resultList.addAll(getSession()
//					.createQuery("select new com.inventory.purchase.bean.InventoryDetailsPojo("
//									+ " id, concat('<<GRV>> Stock ID : ',id,' , ', ' Bal: ' , balance, ' ', item.unit.symbol ),status) from ItemStockModel where item.id=:itm and balance>0 and status=3  order by id")
//									.setLong("itm", item_id).list());
			
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return resultList;
	}
	
	public void updateConvertionQtyAndRate(int settings, long item_id, long unit_id, 
			long salesType, double rate, double convertionQty) throws Exception {
		ItemUnitMangementModel objMdl;
		try {
			
			Object obj=getSession().createQuery("from ItemUnitMangementModel"
							+ " where item.id=:itm and alternateUnit=:altunit and sales_type=:st")
							.setLong("itm", item_id).setLong("altunit", unit_id).setLong("st", salesType).uniqueResult();
			
			if(obj!=null) {
				
				objMdl=(ItemUnitMangementModel) obj;
				if(settings==SConstants.rateAndConvQty_update.RATE_ONLY) {
					objMdl.setItem_price(rate);
				}
				else if(settings==SConstants.rateAndConvQty_update.CONVERTION_QTY_ONLY) {
					objMdl.setConvertion_rate(convertionQty);
				}
				else if(settings==SConstants.rateAndConvQty_update.UPDATE_ALL) {
					objMdl.setConvertion_rate(convertionQty);
					objMdl.setItem_price(rate);
				}
				getSession().update(objMdl);
				flush();
			}
			else {
				objMdl = new ItemUnitMangementModel();
				ItemModel itmObj=(ItemModel) getSession().get(ItemModel.class, item_id);
				getSession().evict(itmObj);
				
				objMdl.setAlternateUnit(unit_id);
				objMdl.setBasicUnit(itmObj.getUnit().getId());
				objMdl.setConvertion_rate(convertionQty);
				objMdl.setItem(itmObj);
				objMdl.setSales_type(salesType);
				objMdl.setStatus(1);
				objMdl.setItem_price(rate);
				
				getSession().save(objMdl);
				flush();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} 
	}
	
	public void saveSessionActivity(SessionActivityModel model) throws Exception {
		try {
			
			begin();
			
			getSession().createQuery("delete from SessionActivityModel where login=:lg and option=:opt" +
					" and billId=:bil").setLong("lg", model.getLogin()).setLong("opt", model.getOption())
					.setLong("bil", model.getBillId()).executeUpdate();
			
			getSession().save(model);
			
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
	}
	
	@SuppressWarnings("rawtypes")
	public List getSavedSessionActivities(long login_id, long officeId) throws Exception {
		resultList=null;
		try {
			
			begin();
			
			resultList=getSession().createQuery("select new com.webspark.bean.SessionActivityBean(a.id,b,a.details,a.billId) " +
					"from SessionActivityModel a, S_OptionModel b where b.option_id=a.option and office_id=:ofc and a.login=:lg")
					.setLong("ofc", officeId).setLong("lg", login_id).list();
			
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return resultList;
	}
	
	public void deleteSavedSessionActivities(long login, long option, long bill) throws Exception {
		try {
			
			begin();
			
			getSession().createQuery("delete from SessionActivityModel where login=:lg and option=:opt" +
					" and billId=:bil").setLong("lg", login).setLong("opt", option)
					.setLong("bil", bill).executeUpdate();
			
			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
	}
	
	public void saveReportedIssue(ReportIssueModel model) throws Exception {
		try {
			
			begin();
			getSession().save(model);
			commit();
			
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
	}
	
	public void saveReview(ReviewModel model) throws Exception {
		try {
			
			begin();
			getSession().save(model);
			commit();
			
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		
	}
	
	public double getCurrencyRate(Date date, long currency) throws Exception {
		double rate=0;
		try {
			begin();
			Object obj = getSession().createQuery("select coalesce(rate, 0) from CurrencyRateModel where date=(select max(date) from CurrencyRateModel where date<=:date) and currencyId.id=:currency")
					.setParameter("date", date).setParameter("currency", currency).uniqueResult();
			if(obj!=null)
				rate=(Double)obj;
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return rate;
	}

	public List getStocksInLocation(Long item_id,	boolean isUseTag,long locationId) throws Exception {
		try {
			ArrayList beanList=new ArrayList();
			resultList=new ArrayList();
			String tag_crit="",cndtn="";
			if(isUseTag)
				tag_crit="'TAG:',item_tag,', ',";
			
			if(locationId!=0)
				cndtn+="  and location_id="+locationId;
				
			begin();
			beanList.addAll(getSession()
							.createQuery(
									"select new com.inventory.purchase.bean.InventoryDetailsPojo("
											+ " id, concat("+tag_crit+" 'Stock ID : ',id,' , '," +
											" ' Bal: ' , balance, ' ', item.unit.symbol,', P.Rate:',round(rate,2) ),status)" +
											" from ItemStockModel a where item.id=:itm and balance>0 and status!="+SConstants.stock_statuses.GRV_STOCK+cndtn+" order by id")
							.setLong("itm", item_id).list());

			if (beanList == null || beanList.size() <= 0) {

				Object obj = getSession()
						.createQuery(
								"select max(id) from ItemStockModel where item.id=:itm and status!="+SConstants.stock_statuses.GRV_STOCK+cndtn)
						.setLong("itm", item_id).uniqueResult();

				if (obj != null)
					beanList
							.addAll(getSession()
									.createQuery(
											"select new com.inventory.purchase.bean.InventoryDetailsPojo("
													+ " id, concat("+tag_crit+" 'Stock ID : ',id,' , ', ' Bal: ' , balance, ' ', item.unit.symbol,', P.Rate:',round(rate,2) ),status) from ItemStockModel where id=:id")
									.setLong("id", (Long) obj).list());

			}
			
			Iterator iter=beanList.iterator();
			InventoryDetailsPojo bean;
			while (iter.hasNext()) {
				bean = (InventoryDetailsPojo) iter.next();
				ItemStockModel mdl=(ItemStockModel) getSession().get(ItemStockModel.class, bean.getId());
				AcctReportMainBean accBean=new AcctReportMainBean("","",mdl.getRate());
					String tag="";
					if(isUseTag)
						tag=mdl.getItem_tag();
					if(mdl.getPurchase_id()!=0){
						switch (mdl.getPurchase_type()) {
						case SConstants.stockPurchaseType.PURCHASE:
							// Contsructor #34
							accBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
									"'',concat('Purchase : ',a.purchase_no),coalesce(b.unit_price/(b.qty_in_basic_unit/b.qunatity),0))from PurchaseModel a join " +
									" a.purchase_details_list b where b.id=:id")
								.setParameter("id", mdl.getInv_det_id()).uniqueResult();
							
						break;
						case SConstants.stockPurchaseType.PURCHASE_GRN:
							accBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
									"'',concat('GRN :', a.grn_no),coalesce(b.unit_price/(b.qty_in_basic_unit/b.qunatity),0))from PurchaseGRNModel a " +
									" join a.grn_details_list b where b.id=:id")
								.setParameter("id", mdl.getInv_det_id()).uniqueResult();
						break;
						case SConstants.stockPurchaseType.SALES_RETURN:
							accBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
									"cast(b.reasonId as string),concat('Sale Return :', a.return_no),coalesce(b.unit_price/(b.quantity_in_basic_unit/b.qunatity),0))from SalesReturnModel a " +
									" join a.inventory_details_list b where b.id=:id")
								.setParameter("id", mdl.getInv_det_id()).uniqueResult();
						break;
						case SConstants.stockPurchaseType.STOCK_CREATE:
							accBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
									"'',concat('Stock Create :',a.purchase_number),coalesce(b.unit_price/(b.qty_in_basic_unit/b.qunatity),0))from StockCreateModel a" +
									" join a.inventory_details_list b where b.id=:id")
							.setParameter("id", mdl.getInv_det_id()).uniqueResult();
						break;
						case SConstants.stockPurchaseType.STOCK_TRANSFER:
							accBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
									"'',concat('Stock Transfer :',cast(a.transfer_no as string)),coalesce(b.unit_price/(b.quantity_in_basic_unit/b.quantity),0))from StockTransferModel a " +
									" join a.inventory_details_list b where b.id=:id")
							.setParameter("id", mdl.getInv_det_id()).uniqueResult();
						break;

						default:
							accBean=new AcctReportMainBean("","",mdl.getRate());
						break;
					}
					}
					String reason="";
					if(!accBean.getName().equals("")&&!accBean.getName().equals("0"))
						reason=(String) getSession().createQuery("select name from ReasonModel where id=:id").setParameter("id", Long.parseLong(accBean.getName())).uniqueResult();
					bean.setStock_details(tag+" "+accBean.getBill_no()+", ID : "+mdl.getId()+" ," +
							"  Bal:  "+ mdl.getBalance()+" "+mdl.getItem().getUnit().getSymbol()+
							" , P.Rate: "+CommonUtil.roundNumberToString(accBean.getAmount())+", "+reason);
					resultList.add(bean);
			}
			

			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return resultList;
	}
	public List getGRVStocksInLocation(Long item_id,	boolean isUseTag,long locationId) throws Exception {
		try {
			ArrayList beanList=new ArrayList();
			resultList=new ArrayList();
			String tag_crit="",cndtn="";
			if(isUseTag)
				tag_crit="'TAG:',item_tag,', ',";
			
			if(locationId!=0)
				cndtn+="  and location_id="+locationId;
			
			begin();
			beanList.addAll(getSession()
					.createQuery(
							"select new com.inventory.purchase.bean.InventoryDetailsPojo("
									+ " id, concat("+tag_crit+" 'Stock ID : ',id,' , '," +
									" ' Bal: ' , balance, ' ', item.unit.symbol,', P.Rate:',round(rate,2) ),status)" +
									" from ItemStockModel a where item.id=:itm and balance>0 and status="+SConstants.stock_statuses.GRV_STOCK+cndtn+" order by id")
									.setLong("itm", item_id).list());
			
			if (beanList == null || beanList.size() <= 0) {
				
				Object obj = getSession()
						.createQuery(
								"select max(id) from ItemStockModel where item.id=:itm and status="+SConstants.stock_statuses.GRV_STOCK+cndtn)
								.setLong("itm", item_id).uniqueResult();
				
				if (obj != null)
					beanList
					.addAll(getSession()
							.createQuery(
									"select new com.inventory.purchase.bean.InventoryDetailsPojo("
											+ " id, concat("+tag_crit+" 'Stock ID : ',id,' , ', ' Bal: ' , balance, ' ', item.unit.symbol,', P.Rate:',round(rate,2) ),status) from ItemStockModel where id=:id")
											.setLong("id", (Long) obj).list());
				
			}
			
			Iterator iter=beanList.iterator();
			InventoryDetailsPojo bean;
			while (iter.hasNext()) {
				bean = (InventoryDetailsPojo) iter.next();
				ItemStockModel mdl=(ItemStockModel) getSession().get(ItemStockModel.class, bean.getId());
				AcctReportMainBean accBean=new AcctReportMainBean("","",mdl.getRate());
					String tag="";
					if(isUseTag)
						tag=mdl.getItem_tag();
					if(mdl.getPurchase_id()!=0){
						switch (mdl.getPurchase_type()) {
						case SConstants.stockPurchaseType.PURCHASE:
							// Contsructor #34
							accBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
									"'',concat('Purchase : ',a.purchase_no),coalesce(b.unit_price/(b.qty_in_basic_unit/b.qunatity),0))from PurchaseModel a join " +
									" a.purchase_details_list b where b.id=:id")
								.setParameter("id", mdl.getInv_det_id()).uniqueResult();
							
						break;
						case SConstants.stockPurchaseType.PURCHASE_GRN:
							accBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
									"'',concat('GRN :', a.grn_no),coalesce(b.unit_price/(b.qty_in_basic_unit/b.qunatity),0))from PurchaseGRNModel a " +
									" join a.grn_details_list b where b.id=:id")
								.setParameter("id", mdl.getInv_det_id()).uniqueResult();
						break;
						case SConstants.stockPurchaseType.SALES_RETURN:
							accBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
									"cast(b.reasonId as string),concat('Sale Return :', a.return_no),coalesce(b.unit_price/(b.quantity_in_basic_unit/b.qunatity),0))from SalesReturnModel a " +
									" join a.inventory_details_list b where b.id=:id")
								.setParameter("id", mdl.getInv_det_id()).uniqueResult();
						break;
						case SConstants.stockPurchaseType.STOCK_CREATE:
							accBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
									"'',concat('Stock Create :',a.purchase_number),coalesce(b.unit_price/(b.qty_in_basic_unit/b.qunatity),0))from StockCreateModel a" +
									" join a.inventory_details_list b where b.id=:id")
							.setParameter("id", mdl.getInv_det_id()).uniqueResult();
						break;
						case SConstants.stockPurchaseType.STOCK_TRANSFER:
							accBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
									"'',concat('Stock Transfer :',cast(a.transfer_no as string)),coalesce(b.unit_price/(b.quantity_in_basic_unit/b.quantity),0))from StockTransferModel a " +
									" join a.inventory_details_list b where b.id=:id")
							.setParameter("id", mdl.getInv_det_id()).uniqueResult();
						break;

						default:
							accBean=new AcctReportMainBean("","",mdl.getRate());
						break;
					}
					}
					String reason="";
					if(!accBean.getName().equals("")&&!accBean.getName().equals("0"))
						reason=(String) getSession().createQuery("select name from ReasonModel where id=:id").setParameter("id", Long.parseLong(accBean.getName())).uniqueResult();
					bean.setStock_details(tag+" "+accBean.getBill_no()+", ID : "+mdl.getId()+" ," +
							"  Bal:  "+ mdl.getBalance()+" "+mdl.getItem().getUnit().getSymbol()+
							" , P.Rate: "+CommonUtil.roundNumberToString(accBean.getAmount())+", "+reason);
					resultList.add(bean);
			}
			
			
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return resultList;
	}
	
	public double getItemValueOnDate(long item_id, long location,Date fromDate, Date date) throws Exception {
		double balance=0;
		try {
			
			begin();
			String cdn="";
			if(location!=0){
				cdn+=" and b.location_id="+location;
			}
			
			double rate=getPurchaseRateOfItem(item_id,settings.getPROFIT_CALCULATION(),date);
			
			Object obj= getSession().createQuery("select max(date) from StockResetDetailsModel where item.id=:itm and date<:dt")
					.setParameter("dt", date).setLong("itm", item_id).uniqueResult();
			
			
			if(obj!=null) {
				
				double saleQty=(Double) getSession().createQuery("select coalesce(sum(b.qunatity),0) from SalesModel a " +
								" join a.inventory_details_list b  where a.date>:stdt and a.date<=:dt and b.item.id=:itm and a.active=true"+cdn)
								.setParameter("stdt", obj).setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				double deliveryNoteQty=(Double) getSession().createQuery("select coalesce(sum(b.qunatity),0) from DeliveryNoteModel " +
						" a join a.delivery_note_details_list b  where a.date>:stdt and a.date<=:dt and b.item.id=:itm and a.active=true"+cdn)
								.setParameter("stdt", obj).setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				double purchaseQty=(Double) getSession().createQuery("select coalesce(sum(b.qunatity),0) from PurchaseModel a " +
						" join a.purchase_details_list b  where a.date>:stdt and a.date<=:dt and b.item.id=:itm and a.active=true"+cdn)
						.setParameter("stdt", obj).setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				double grnQty=(Double) getSession().createQuery("select coalesce(sum(b.qunatity),0) from PurchaseGRNModel a " +
						" join a.grn_details_list b  where a.date>:stdt and a.date<=:dt and b.item.id=:itm and a.active=true"+cdn)
						.setParameter("stdt", obj).setParameter("dt", date).setLong("itm", item_id).uniqueResult();
								
				double saleRtnQty=(Double) getSession().createQuery("select coalesce(sum(b.qunatity),0) from SalesReturnModel " +
						" a join a.inventory_details_list b  where a.date>:stdt and a.date<=:dt and b.item.id=:itm and a.active=true"+cdn)
						.setParameter("stdt", obj).setParameter("dt", date).setLong("itm", item_id).uniqueResult();
		
				double purchaseRtnQty=(Double) getSession().createQuery("select coalesce(sum(b.qunatity),0) from PurchaseReturnModel " +
						" a join a.inventory_details_list b  where a.date>:stdt and a.date<=:dt and b.item.id=:itm and a.active=true"+cdn)
						.setParameter("stdt", obj).setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				double stockCreate=(Double) getSession().createQuery("select coalesce(sum(b.qunatity),0) from StockCreateModel " +
						" a join a.inventory_details_list b  where a.date>:stdt and a.date<=:dt and b.item.id=:itm ")
						.setParameter("stdt", obj).setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				double disposeQty=(Double) getSession().createQuery("select coalesce(sum(b.qunatity),0) from DisposeItemsModel a " +
						" join a.item_details_list b  where  a.date between :stdt and :dt and b.item.id=:itm ")
						.setParameter("stdt", obj).setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				balance=CommonUtil.roundNumber(purchaseQty)+CommonUtil.roundNumber(grnQty)-
						CommonUtil.roundNumber(saleQty)-CommonUtil.roundNumber(deliveryNoteQty)-
						CommonUtil.roundNumber(purchaseRtnQty)+CommonUtil.roundNumber(saleRtnQty)+CommonUtil.roundNumber(stockCreate)-CommonUtil.roundNumber(disposeQty);;
				balance=rate*balance;
			}
			else {
				double openingBal=(Double) getSession().createQuery("select opening_balance from ItemModel where id=:id").setLong("id", item_id).uniqueResult();
		
				double saleQty=(Double) getSession().createQuery("select coalesce(sum(b.qunatity),0) from SalesModel a " +
						" join a.inventory_details_list b  where a.date between :frm and :to and b.item.id=:itm and a.active=true"+cdn)
						.setParameter("frm", fromDate).setParameter("to", date).setLong("itm", item_id).uniqueResult();
		
				double deliveryNoteQty=(Double) getSession().createQuery("select coalesce(sum(b.qunatity),0) from DeliveryNoteModel " +
						" a join a.delivery_note_details_list b  where a.date between :frm and :to and b.item.id=:itm and a.active=true"+cdn)
							.setParameter("frm", fromDate).setParameter("to", date).setLong("itm", item_id).uniqueResult();
				
				double purchaseQty=(Double) getSession().createQuery("select coalesce(sum(b.qunatity),0) from PurchaseModel a " +
						" join a.purchase_details_list b  where a.date between :frm and :to and b.item.id=:itm and a.active=true"+cdn)
						.setParameter("frm", fromDate).setParameter("to", date).setLong("itm", item_id).uniqueResult();
				
				double grnQty=(Double) getSession().createQuery("select coalesce(sum(b.qunatity),0) from PurchaseGRNModel a " +
						" join a.grn_details_list b  where a.date between :frm and :to and b.item.id=:itm and a.active=true"+cdn)
						.setParameter("frm", fromDate).setParameter("to", date).setLong("itm", item_id).uniqueResult();
								
				double saleRtnQty=(Double) getSession().createQuery("select coalesce(sum(b.qunatity),0) from SalesReturnModel " +
						" a join a.inventory_details_list b  where a.date between :frm and :to and b.item.id=:itm and a.active=true"+cdn)
						.setParameter("frm", fromDate).setParameter("to", date).setLong("itm", item_id).uniqueResult();
		
				double purchaseRtnQty=(Double) getSession().createQuery("select coalesce(sum(b.qunatity),0) from PurchaseReturnModel " +
						" a join a.inventory_details_list b  where a.date between :frm and :to and b.item.id=:itm and a.active=true"+cdn)
						.setParameter("frm", fromDate).setParameter("to", date).setLong("itm", item_id).uniqueResult();
				
				double stockCreate=(Double) getSession().createQuery("select coalesce(sum(b.qunatity),0) from StockCreateModel " +
						" a join a.inventory_details_list b  where a.date>:stdt and a.date<=:dt and b.item.id=:itm ")
						.setParameter("stdt", obj).setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				double disposeQty=(Double) getSession().createQuery("select coalesce(sum(b.qunatity),0) from DisposeItemsModel a " +
						" join a.item_details_list b  where   a.date<=:dt and b.item.id=:itm ")
						.setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				balance=CommonUtil.roundNumber(openingBal)+CommonUtil.roundNumber(purchaseQty)+CommonUtil.roundNumber(grnQty)-
						CommonUtil.roundNumber(saleQty)-CommonUtil.roundNumber(deliveryNoteQty)-
						CommonUtil.roundNumber(purchaseRtnQty)+CommonUtil.roundNumber(saleRtnQty)+CommonUtil.roundNumber(stockCreate)-CommonUtil.roundNumber(disposeQty);;
				balance=rate*balance;
			}
			commit();
			
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return CommonUtil.roundNumber(balance);
		
	}
	
	public double getItemValueBetweenDatesInFinancialYear(long item_id, long location,Date fromDate, Date date,Date finStartDate) throws Exception {
		double balance=0;
		try {
			
			begin();
			String cdn="";
			if(location!=0){
				cdn+=" and b.location_id="+location;
			}
			double rate=getPurchaseRateOfItem(item_id,settings.getPROFIT_CALCULATION(),date);
			
			Object obj= getSession().createQuery("select max(date) from StockResetDetailsModel where item.id=:itm and date<:dt")
					.setParameter("dt", date).setLong("itm", item_id).uniqueResult();
			
			if(obj!=null) {
				Date frmDate=fromDate;
				
				if(((Date)obj).compareTo(finStartDate)>0){
					frmDate=finStartDate;
				}else{
					frmDate=(Date)obj;
				}
				
				double saleQty=(Double) getSession().createQuery("select coalesce(sum(b.qunatity),0) from SalesModel a " +
								" join a.inventory_details_list b  where a.date>:stdt and a.date<=:dt and b.item.id=:itm and b.delivery_id=0 and a.active=true"+cdn)
								.setParameter("stdt", frmDate).setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				double deliveryNoteQty=(Double) getSession().createQuery("select coalesce(sum(b.qunatity),0) from DeliveryNoteModel " +
						" a join a.delivery_note_details_list b  where a.date>:stdt and a.date<=:dt and b.item.id=:itm and a.active=true"+cdn)
								.setParameter("stdt", frmDate).setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				double purchaseQty=(Double) getSession().createQuery("select coalesce(sum(b.qunatity),0) from PurchaseModel a " +
						" join a.purchase_details_list b  where a.date>:stdt and a.date<=:dt and b.item.id=:itm and b.grn_id=0 and a.active=true"+cdn)
						.setParameter("stdt", obj).setParameter("dt", frmDate).setLong("itm", item_id).uniqueResult();
				
				double grnQty=(Double) getSession().createQuery("select coalesce(sum(b.qunatity),0) from PurchaseGRNModel a " +
						" join a.grn_details_list b  where a.date>:stdt and a.date<=:dt and b.item.id=:itm and a.active=true"+cdn)
						.setParameter("stdt", obj).setParameter("dt", frmDate).setLong("itm", item_id).uniqueResult();
								
				double saleRtnQty=(Double) getSession().createQuery("select coalesce(sum(b.qunatity),0) from SalesReturnModel " +
						" a join a.inventory_details_list b  where a.date>:stdt and a.date<=:dt and b.item.id=:itm and a.active=true"+cdn)
						.setParameter("stdt", obj).setParameter("dt", frmDate).setLong("itm", item_id).uniqueResult();
		
				double purchaseRtnQty=(Double) getSession().createQuery("select coalesce(sum(b.qunatity),0) from PurchaseReturnModel " +
						" a join a.inventory_details_list b  where a.date>:stdt and a.date<=:dt and b.item.id=:itm and a.active=true"+cdn)
						.setParameter("stdt", obj).setParameter("dt", frmDate).setLong("itm", item_id).uniqueResult();
				
				double stockCreate=(Double) getSession().createQuery("select coalesce(sum(b.qunatity),0) from StockCreateModel " +
						" a join a.inventory_details_list b  where a.date>:stdt and a.date<=:dt and b.item.id=:itm ")
						.setParameter("stdt", obj).setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				double disposeQty=(Double) getSession().createQuery("select coalesce(sum(b.qunatity),0) from DisposeItemsModel a " +
						" join a.item_details_list b  where  a.date between :stdt and :dt and b.item.id=:itm ")
						.setParameter("stdt", obj).setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				balance=CommonUtil.roundNumber(purchaseQty)+CommonUtil.roundNumber(grnQty)-
						CommonUtil.roundNumber(saleQty)-CommonUtil.roundNumber(deliveryNoteQty)-
						CommonUtil.roundNumber(purchaseRtnQty)+CommonUtil.roundNumber(saleRtnQty)+CommonUtil.roundNumber(stockCreate)-CommonUtil.roundNumber(disposeQty);;
				balance=rate*balance;
			}
			else {
				double openingBal=(Double) getSession().createQuery("select opening_balance from ItemModel where id=:id").setLong("id", item_id).uniqueResult();
		
				double saleQty=(Double) getSession().createQuery("select coalesce(sum(b.qunatity),0) from SalesModel a " +
						" join a.inventory_details_list b  where a.date between :frm and :to and b.item.id=:itm and b.delivery_id=0 and a.active=true"+cdn)
						.setParameter("frm", fromDate).setParameter("to", date).setLong("itm", item_id).uniqueResult();
		
				double deliveryNoteQty=(Double) getSession().createQuery("select coalesce(sum(b.qunatity),0) from DeliveryNoteModel " +
						" a join a.delivery_note_details_list b  where a.date between :frm and :to and b.item.id=:itm and a.active=true"+cdn)
							.setParameter("frm", fromDate).setParameter("to", date).setLong("itm", item_id).uniqueResult();
				
				double purchaseQty=(Double) getSession().createQuery("select coalesce(sum(b.qunatity),0) from PurchaseModel a " +
						" join a.purchase_details_list b  where a.date between :frm and :to and b.item.id=:itm and b.grn_id=0 and a.active=true"+cdn)
						.setParameter("frm", fromDate).setParameter("to", date).setLong("itm", item_id).uniqueResult();
				
				double grnQty=(Double) getSession().createQuery("select coalesce(sum(b.qunatity),0) from PurchaseGRNModel a " +
						" join a.grn_details_list b  where a.date between :frm and :to and b.item.id=:itm and a.active=true"+cdn)
						.setParameter("frm", fromDate).setParameter("to", date).setLong("itm", item_id).uniqueResult();
								
				double saleRtnQty=(Double) getSession().createQuery("select coalesce(sum(b.qunatity),0) from SalesReturnModel " +
						" a join a.inventory_details_list b  where a.date between :frm and :to and b.item.id=:itm and a.active=true"+cdn)
						.setParameter("frm", fromDate).setParameter("to", date).setLong("itm", item_id).uniqueResult();
		
				double purchaseRtnQty=(Double) getSession().createQuery("select coalesce(sum(b.qunatity),0) from PurchaseReturnModel " +
						" a join a.inventory_details_list b  where a.date between :frm and :to and b.item.id=:itm and a.active=true"+cdn)
						.setParameter("frm", fromDate).setParameter("to", date).setLong("itm", item_id).uniqueResult();
				double stockCreate=0;
				try {
					 stockCreate=(Double) getSession().createQuery("select coalesce(sum(b.qunatity),0) from StockCreateModel " +
						" a join a.inventory_details_list b  where a.date>:stdt and a.date<=:dt and b.item.id=:itm ")
						.setParameter("stdt", obj).setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				} catch (Exception e) {}
				
				double disposeQty=(Double) getSession().createQuery("select coalesce(sum(b.qunatity),0) from DisposeItemsModel a " +
						" join a.item_details_list b  where   a.date<=:dt and b.item.id=:itm ")
						.setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				balance=CommonUtil.roundNumber(openingBal)+CommonUtil.roundNumber(purchaseQty)+CommonUtil.roundNumber(grnQty)-
						CommonUtil.roundNumber(saleQty)-CommonUtil.roundNumber(deliveryNoteQty)-
						CommonUtil.roundNumber(purchaseRtnQty)+CommonUtil.roundNumber(saleRtnQty)+CommonUtil.roundNumber(stockCreate)-CommonUtil.roundNumber(disposeQty);;
				balance=rate*balance;
			}
			commit();
			
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return CommonUtil.roundNumber(balance);
		
	}
	
	public double getItemValueTillDate(long item_id, long location,Date date) throws Exception {
		double balance=0;
		try {
			
			begin();
			String cdn="";
			if(location!=0){
				cdn+=" and b.location_id="+location;
			}
			
			double rate=getPurchaseRateOfItem(item_id,settings.getPROFIT_CALCULATION(),date);
			
			Object obj= getSession().createQuery("select max(date) from StockResetDetailsModel where item.id=:itm and date<:dt")
					.setParameter("dt", date).setLong("itm", item_id).uniqueResult();
			
			if(obj!=null) {
				
				double saleQty=(Double) getSession().createQuery("select coalesce(sum(b.qunatity),0) from SalesModel a " +
								" join a.inventory_details_list b  where  a.date between :stdt and :dt and b.item.id=:itm and b.delivery_id=0 and a.active=true"+cdn)
								.setParameter("stdt", obj).setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				double deliveryNoteQty=(Double) getSession().createQuery("select coalesce(sum(b.qunatity),0) from DeliveryNoteModel " +
						" a join a.delivery_note_details_list b  where a.date between :stdt and :dt and b.item.id=:itm and a.active=true"+cdn)
								.setParameter("stdt", obj).setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				double purchaseQty=(Double) getSession().createQuery("select coalesce(sum(b.qunatity),0) from PurchaseModel a " +
						" join a.purchase_details_list b  where a.date between :stdt and :dt and b.item.id=:itm and b.grn_id=0 and a.active=true"+cdn)
						.setParameter("stdt", obj).setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				double grnQty=(Double) getSession().createQuery("select coalesce(sum(b.qunatity),0) from PurchaseGRNModel a " +
						" join a.grn_details_list b  where a.date between :stdt and :dt and b.item.id=:itm and a.active=true"+cdn)
						.setParameter("stdt", obj).setParameter("dt", date).setLong("itm", item_id).uniqueResult();
								
				double saleRtnQty=(Double) getSession().createQuery("select coalesce(sum(b.qunatity),0) from SalesReturnModel " +
						" a join a.inventory_details_list b  where a.date between :stdt and :dt and b.item.id=:itm and a.active=true"+cdn)
						.setParameter("stdt", obj).setParameter("dt", date).setLong("itm", item_id).uniqueResult();
		
				double purchaseRtnQty=(Double) getSession().createQuery("select coalesce(sum(b.qunatity),0) from PurchaseReturnModel " +
						" a join a.inventory_details_list b  where a.date between :stdt and :dt and b.item.id=:itm and a.active=true"+cdn)
						.setParameter("stdt", obj).setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				double stockCreate=(Double) getSession().createQuery("select coalesce(sum(b.qunatity),0) from StockCreateModel " +
						" a join a.inventory_details_list b  where a.date between :stdt and :dt and b.item.id=:itm ")
						.setParameter("stdt", obj).setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				double disposeQty=(Double) getSession().createQuery("select coalesce(sum(b.qunatity),0) from DisposeItemsModel a " +
						" join a.item_details_list b  where  a.date between :stdt and :dt and b.item.id=:itm ")
						.setParameter("stdt", obj).setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				balance=CommonUtil.roundNumber(purchaseQty)+CommonUtil.roundNumber(grnQty)-
						CommonUtil.roundNumber(saleQty)-CommonUtil.roundNumber(deliveryNoteQty)-
						CommonUtil.roundNumber(purchaseRtnQty)+CommonUtil.roundNumber(saleRtnQty)+CommonUtil.roundNumber(stockCreate)-CommonUtil.roundNumber(disposeQty);
				balance=rate*balance;
			}
			else {
				double openingBal=(Double) getSession().createQuery("select opening_balance from ItemModel where id=:id").setLong("id", item_id).uniqueResult();
		
				double saleQty=(Double) getSession().createQuery("select coalesce(sum(b.qunatity),0) from SalesModel a " +
						" join a.inventory_details_list b  where a.date <=:to and b.item.id=:itm and b.delivery_id=0 and a.active=true"+cdn)
						.setParameter("to", date).setLong("itm", item_id).uniqueResult();
		
				double deliveryNoteQty=(Double) getSession().createQuery("select coalesce(sum(b.qunatity),0) from DeliveryNoteModel " +
						" a join a.delivery_note_details_list b  where a.date <=:to and b.item.id=:itm and a.active=true"+cdn)
							.setParameter("to", date).setLong("itm", item_id).uniqueResult();
				
				double purchaseQty=(Double) getSession().createQuery("select coalesce(sum(b.qunatity),0) from PurchaseModel a " +
						" join a.purchase_details_list b  where a.date <=:to and b.item.id=:itm and b.grn_id=0 and a.active=true"+cdn)
						.setParameter("to", date).setLong("itm", item_id).uniqueResult();
				
				double grnQty=(Double) getSession().createQuery("select coalesce(sum(b.qunatity),0) from PurchaseGRNModel a " +
						" join a.grn_details_list b  where a.date <=:to and b.item.id=:itm and a.active=true"+cdn)
						.setParameter("to", date).setLong("itm", item_id).uniqueResult();
								
				double saleRtnQty=(Double) getSession().createQuery("select coalesce(sum(b.qunatity),0) from SalesReturnModel " +
						" a join a.inventory_details_list b  where a.date <=:to and b.item.id=:itm and a.active=true"+cdn)
						.setParameter("to", date).setLong("itm", item_id).uniqueResult();
		
				double purchaseRtnQty=(Double) getSession().createQuery("select coalesce(sum(b.qunatity),0) from PurchaseReturnModel " +
						" a join a.inventory_details_list b  where a.date <=:to and b.item.id=:itm and a.active=true"+cdn)
						.setParameter("to", date).setLong("itm", item_id).uniqueResult();
				double stockCreate=0;
				try {
					 stockCreate=(Double) getSession().createQuery("select coalesce(sum(b.qunatity),0) from StockCreateModel " +
						" a join a.inventory_details_list b  where a.date<=:dt and b.item.id=:itm ")
						.setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				} catch (Exception e) {}
				
				double disposeQty=(Double) getSession().createQuery("select coalesce(sum(b.qunatity),0) from DisposeItemsModel a " +
						" join a.item_details_list b  where   a.date<=:dt and b.item.id=:itm ")
						.setParameter("dt", date).setLong("itm", item_id).uniqueResult();
				
				balance=CommonUtil.roundNumber(openingBal)+CommonUtil.roundNumber(purchaseQty)+CommonUtil.roundNumber(grnQty)-
						CommonUtil.roundNumber(saleQty)-CommonUtil.roundNumber(deliveryNoteQty)-
						CommonUtil.roundNumber(purchaseRtnQty)+CommonUtil.roundNumber(saleRtnQty)+CommonUtil.roundNumber(stockCreate)-CommonUtil.roundNumber(disposeQty);
				
				balance=rate*balance;
			}
			commit();
			
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		return CommonUtil.roundNumber(balance);
		
	}

	
	private double getPurchaseRateOfItem(long item_id, int profit_calculation, Date date) {
		double rate=0;
		 try {
			if(profit_calculation==SConstants.profitCalcutaion.AVERAGE){
				rate=(Double) getSession()
						.createQuery(
								"select coalesce(sum(b.unit_price)/count(distinct a.id),0) from PurchaseModel a join a.purchase_details_list b" +
								"  where a.date <=:to and b.item.id=:itm group by a.id")
						.setParameter("to", date).setLong("itm", item_id)
						.uniqueResult();
			}else if(profit_calculation==SConstants.profitCalcutaion.LIFO){
				List list=getSession()
						.createQuery(
								"select coalesce(b.unit_price,0) from PurchaseModel a join a.purchase_details_list b" +
								"  where a.date <=:to and b.item.id=:itm and a.id=(select max(c.id) from PurchaseModel c join c.purchase_details_list d where c.date <=:to and d.item.id=:itm)")
						.setParameter("to", date).setLong("itm", item_id)
						.list();
				if(list!=null&&list.size()>0){
					rate=(Double)list.get(0);
				}
			}else if(profit_calculation==SConstants.profitCalcutaion.FIFO){
				
				List list= getSession()
						.createQuery(
								"select coalesce(b.unit_price,0) from PurchaseModel a join a.purchase_details_list b" +
								"  where a.date <=:to and b.item.id=:itm and a.id=(select min(c.id) from PurchaseModel c join c.purchase_details_list d where c.date <=:to and d.item.id=:itm)")
						.setParameter("to", date).setLong("itm", item_id)
						.list();
				if(list!=null&&list.size()>0){
					rate=(Double)list.get(0);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rate;
	}

	public Date getCurrencyDate(Date date, long currency) throws Exception {
        Date rate=null;
        try {
                begin();
                Object obj = getSession().createQuery("select date from CurrencyRateModel where date=(select max(date) from CurrencyRateModel where date<=:date) and currencyId.id=:currency")
                                .setParameter("date", date).setParameter("currency", currency).uniqueResult();
                if(obj!=null)
                        rate=(Date)obj;
                commit();
        } catch (Exception e) {
                rollback();
                close();
                e.printStackTrace();
                throw e;
        } finally {
                flush();
                close();
        }
        return rate;
}
	
	
	public InventoryDetailsPojo getStockModelWithDetails(long stockId) throws Exception{
		InventoryDetailsPojo stk=new InventoryDetailsPojo();
		  try {
              begin();
              
              	ItemStockModel mdl=(ItemStockModel) getSession().get(ItemStockModel.class, stockId);
				AcctReportMainBean accBean=new AcctReportMainBean("","",mdl.getRate());
					if(mdl.getPurchase_id()!=0){
						switch (mdl.getPurchase_type()) {
						case SConstants.stockPurchaseType.PURCHASE:
							// Contsructor #34
							accBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
									"'',concat('Purchase : ',a.purchase_no),coalesce(b.unit_price/(b.qty_in_basic_unit/b.qunatity),0))from PurchaseModel a join " +
									" a.purchase_details_list b where b.id=:id")
								.setParameter("id", mdl.getInv_det_id()).uniqueResult();
							
						break;
						case SConstants.stockPurchaseType.PURCHASE_GRN:
							accBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
									"'',concat('GRN :', a.grn_no),coalesce(b.unit_price/(b.qty_in_basic_unit/b.qunatity),0))from PurchaseGRNModel a " +
									" join a.grn_details_list b where b.id=:id")
								.setParameter("id", mdl.getInv_det_id()).uniqueResult();
						break;
						case SConstants.stockPurchaseType.SALES_RETURN:
							accBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
									"cast(b.reasonId as string),concat('Sale Return :', a.return_no),coalesce(b.unit_price/(b.quantity_in_basic_unit/b.qunatity),0))from SalesReturnModel a " +
									" join a.inventory_details_list b where b.id=:id")
								.setParameter("id", mdl.getInv_det_id()).uniqueResult();
						break;
						case SConstants.stockPurchaseType.STOCK_CREATE:
							accBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
									"'',concat('Stock Create :',a.purchase_number),coalesce(b.unit_price/(b.qty_in_basic_unit/b.qunatity),0))from StockCreateModel a" +
									" join a.inventory_details_list b where b.id=:id")
							.setParameter("id", mdl.getInv_det_id()).uniqueResult();
						break;
						case SConstants.stockPurchaseType.STOCK_TRANSFER:
							accBean=(AcctReportMainBean) getSession().createQuery("select new com.inventory.reports.bean.AcctReportMainBean(" +
									"'',concat('Stock Transfer :',cast(a.transfer_no as string)),coalesce(b.unit_price/(b.quantity_in_basic_unit/b.quantity),0))from StockTransferModel a " +
									" join a.inventory_details_list b where b.id=:id")
							.setParameter("id", mdl.getInv_det_id()).uniqueResult();
						break;

						default:
							accBean=new AcctReportMainBean("","",mdl.getRate());
						break;
					}
					}
					String reason="";
					if(!accBean.getName().equals("")&&!accBean.getName().equals("0"))
						reason=(String) getSession().createQuery("select name from ReasonModel where id=:id").setParameter("id", Long.parseLong(accBean.getName())).uniqueResult();
					stk.setStock_details(accBean.getBill_no()+", ID : "+mdl.getId()+" ," +
							"  Bal:  "+ mdl.getBalance()+" "+mdl.getItem().getUnit().getSymbol()+
							" , P.Rate: "+CommonUtil.roundNumberToString(accBean.getAmount())+", "+reason);
					
              commit();
      } catch (Exception e) {
              rollback();
              close();
              e.printStackTrace();
              throw e;
      } finally {
              flush();
              close();
      }
      return stk;
	}

}