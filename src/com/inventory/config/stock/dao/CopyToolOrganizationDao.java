package com.inventory.config.stock.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.config.unit.model.ItemUnitMangementModel;
import com.inventory.purchase.model.ItemStockModel;
import com.inventory.transaction.model.TransactionModel;
import com.webspark.common.util.CommonUtil;
import com.webspark.dao.SHibernate;

@SuppressWarnings("serial")
public class CopyToolOrganizationDao extends SHibernate implements Serializable{

	public boolean isItemExist(String code, long office) throws Exception{
		
		boolean exist=false;
		try{
			begin();
			ItemModel item=(ItemModel)getSession().createQuery("from ItemModel where item_code=:code and office.id=:office")
										.setParameter("code", code).setParameter("office", office).uniqueResult();
			if(item!=null)
				exist=true;
			else
				exist=false;
			commit();
		}
		catch(Exception e){
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally{
			flush();
			close();
		}
		return exist;
	}
	
	
	public boolean isLedgerExist(String name, long office) throws Exception{
		
		boolean exist=false;
		try{
			begin();
			LedgerModel ledger=(LedgerModel)getSession().createQuery("from LedgerModel where name=:code and office.id=:office")
											.setParameter("code", name).setParameter("office", office).uniqueResult();
			if(ledger!=null)
				exist=true;
			else
				exist=false;
			commit();
		}
		catch(Exception e){
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally{
			flush();
			close();
		}
		return exist;
	}
	
	
	@SuppressWarnings("rawtypes")
	public boolean save(List<LedgerModel> list)  throws Exception{
		try{
			begin();
			Iterator itr=list.iterator();
			while (itr.hasNext()) {
				LedgerModel ledger = (LedgerModel) itr.next();
				getSession().save(ledger);
			}
			commit();
		}
		catch(Exception e){
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally{
			flush();
			close();
		}
		return true;
	}
	
	public boolean save(Hashtable<ItemModel, ItemUnitMangementModel> hash)  throws Exception{
		try{
			begin();
			Iterator itr=hash.keySet().iterator();
			ItemModel price=null;
			ItemUnitMangementModel unit=null;
			while (itr.hasNext()) {
				price=(ItemModel)itr.next();
				unit=hash.get(price);
				
				Date dt = CommonUtil.getCurrentSQLDate();
				ItemStockModel stk = new ItemStockModel();
				getSession().save(price);
				stk.setBalance(price.getOpening_balance());
				stk.setExpiry_date(dt);
				stk.setItem(price);
				stk.setManufacturing_date(dt);
				stk.setRate(price.getRate());
				stk.setPurchase_id(0);
				stk.setQuantity(price.getOpening_balance());
				stk.setStatus(2);
				stk.setDate_time(CommonUtil.getCurrentDateTime());

				getSession().save(stk);
				
				List lstST = getSession().createQuery("select id from SalesTypeModel where office.id=:ofc")
						.setLong("ofc", price.getOffice().getId()).list();
				unit.setItem(price);
				ItemUnitMangementModel objIUM;
				Iterator it = lstST.iterator();
				while (it.hasNext()) {

					objIUM = new ItemUnitMangementModel(0,
							price, unit.getBasicUnit(),
							unit.getAlternateUnit(), (Long) it.next(),
							unit.getConvertion_rate(),
							unit.getItem_price(),
							unit.getStatus());
					getSession().save(objIUM);
				}
			}
			commit();
		}
		catch(Exception e){
			rollback();
			close();
			e.printStackTrace();
			throw e;
		}
		finally{
			flush();
			close();
		}
		return true;
	}
	
}
