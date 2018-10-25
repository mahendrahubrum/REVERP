package com.inventory.purchase.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.inventory.purchase.model.StockRateModel;
import com.inventory.sales.model.SalesInventoryDetailsModel;
import com.inventory.sales.model.SalesModel;
import com.inventory.sales.model.SalesStockMapModel;
import com.inventory.transaction.model.TransactionDetailsModel;
import com.inventory.transaction.model.TransactionModel;
import com.webspark.dao.SHibernate;

/**
 * @Author Jinshad P.T.
 */

public class StockRateUpdateDao extends SHibernate implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2130771295587292922L;
	List resultList = new ArrayList();

	
	public List getAllStocksWithBalance(long item_id) throws Exception {
		try {
			
			resultList=new ArrayList();
			
			
			begin();
			resultList.addAll(getSession()
					.createQuery("select new com.inventory.purchase.bean.InventoryDetailsPojo("
									+ " id, concat('TAG:',item_tag,' Stock ID : ',id,' , ', ' Bal: ' , balance, ' ', item.unit.symbol ),status) from ItemStockModel where item.id=:itm and balance>0 order by id")
									.setLong("itm", item_id).list());
			commit();
		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return resultList;
		}
	}
	
	
	public double getStockSalesRate(long stk_id, long unit_id) throws Exception {
		double rate=0;
		try {
			
			begin();
			
			Object obj= getSession().createQuery("select a.rate from StockRateModel a where a.stock_id=:id and a.unit_id=:unit")
				.setLong("id", stk_id).setLong("unit", unit_id).uniqueResult();
			
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
			return rate;
		}
	}
	
	public void updateStockSalesRate(StockRateModel objModel) throws Exception {
		try {
			StockRateModel ratObj=null;
			begin();
			
			Object obj=getSession().createQuery("from StockRateModel where stock_id=:stk and unit_id=:unit")
				.setParameter("stk", objModel.getStock_id()).setParameter("unit", objModel.getUnit_id()).uniqueResult();
			
			if(obj!=null) {
				ratObj=(StockRateModel) obj;
				ratObj.setRate(objModel.getRate());
				getSession().update(ratObj);
			}
			else {
				getSession().save(objModel);
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
	

	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void updateSalesTransaction(SalesModel mdl, TransactionModel transaction)
			throws Exception {
		try {

			begin();

			List transactionList=new ArrayList();
			
			List<Long> transactionOldList=new ArrayList<Long>();
			
			
			transactionList=getSession().createQuery("select b from TransactionModel a join a.transaction_details_list b where a.id=:id")
									.setParameter("id", mdl.getTransaction_id()).list();
			
			Iterator transItr=transactionList.iterator();
			while (transItr.hasNext()) {
				TransactionDetailsModel tr = (TransactionDetailsModel) transItr.next();

				getSession().createQuery("update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tr.getAmount()).setLong("id", tr.getFromAcct().getId()).executeUpdate();

				getSession().createQuery("update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tr.getAmount()).setLong("id", tr.getToAcct().getId()).executeUpdate();
				
				flush();
				
				transactionOldList.add(tr.getId());
			}
			
			
			getSession().update(transaction);
			
			Iterator<TransactionDetailsModel> aciter = transaction.getTransaction_details_list().iterator();
			
			while (aciter.hasNext()) {
				TransactionDetailsModel tdm = aciter.next();

				getSession().createQuery("update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tdm.getAmount()).setLong("id", tdm.getFromAcct().getId()).executeUpdate();

				getSession().createQuery("update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tdm.getAmount()).setLong("id", tdm.getToAcct().getId()).executeUpdate();
				flush();
				
			}
			
			flush();
			
			if(transactionOldList.size()>0){
				getSession().createQuery("delete from TransactionDetailsModel where id in (:lst)")
							.setParameterList("lst", (Collection) transactionOldList).executeUpdate();
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

}
