package com.inventory.config.tax.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.inventory.model.RackModel;
import com.inventory.purchase.model.ItemStockModel;
import com.inventory.purchase.model.StockRackMappingModel;
import com.webspark.common.util.CommonUtil;
import com.webspark.dao.SHibernate;

/**
 * @author Jinshad P.T.
 *
 * Jul 11, 2013
 */
public class StockRackMappingDao extends SHibernate implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4301704757267526904L;
	List resultList=new ArrayList();
	
	public List getPendingAvailableStocksList(long office_id) throws Exception {
		try {
			resultList=null;
			begin();
			
			resultList=getSession().createQuery("select new com.inventory.purchase.model.ItemStockModel(id, " +
					"concat(id,' : ', item.name, ' : Qty : ', quantity, ' : Bal : ', balance,  ' : ', expiry_date)) from ItemStockModel where balance>0 " +
					"and item.office.id=:ofc and stock_arranged='N'").setLong("ofc", office_id)
				.list();
			
			commit();
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return resultList;
		}
	}
	
	
	public List getArrangedAvailabeStocksList(long office_id) throws Exception {
		try {
			begin();
			
			resultList=getSession().createQuery("select new com.inventory.purchase.model.ItemStockModel(id, " +
					"concat(id,' : ', item.name, ' : Qty : ', quantity, ' : Bal : ', balance, ' : ', expiry_date)) from ItemStockModel where balance>0 " +
					"and item.office.id=:ofc and stock_arranged='Y'").setLong("ofc", office_id)
				.list();
			
			commit();
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return resultList;
		}
	}
	
	
	public List getStockListOfPurchase(long purchase_id) throws Exception {
		try {
			begin();
			resultList=getSession().createQuery("select new com.inventory.purchase.model.ItemStockModel(id, " +
					"concat(id,' : ', item.name, ' : Qty : ', quantity, ' : Bal : ', balance, ' : ', expiry_date) ) from ItemStockModel where purchase_id=:pid")
				.setLong("pid", purchase_id).list();
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
	
	
	public ItemStockModel getItemStock(long id) throws Exception {
		ItemStockModel stk=null;
		try {
			begin();
			stk=(ItemStockModel) getSession().get(ItemStockModel.class, id);
			commit();
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return stk;
		}
	}
	
	
	public List getStockRackMappings(long stock_id) throws Exception {
		try {
			begin();
			
			resultList=getSession().createQuery("from StockRackMappingModel where stock.id=:stkid")
							.setLong("stkid", stock_id).list();
			
			commit();
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return resultList;
		}
	}
	
	public void saveStockRackMap(List lst, long stock_id) throws Exception {
		ItemStockModel stk=null;
		try {
			begin();
			
			getSession().createQuery("delete from StockRackMappingModel where stock.id=:stkid")
							.setLong("stkid", stock_id).executeUpdate();
			
			Iterator it=lst.iterator();
			while(it.hasNext()){
				getSession().save(it.next());
			}
			
			updateStockStatus(stock_id);
			
			commit();
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
	}
	
	
	/*	 Use begin() before using this  method	 */
	public void updateStockStatus(long stock_id) throws Exception {
		ItemStockModel stk=null;
		try {
		
			stk=(ItemStockModel)getSession().get(ItemStockModel.class, stock_id);
			Object obj=getSession().createQuery("select sum(quantity_in_basic_unit) from StockRackMappingModel where stock.id=:stkid")
					.setLong("stkid", stk.getId()).uniqueResult();
			
			double val=0;
			if(obj!=null){
				val=(Double)obj;
			}
			
			if(val>=stk.getQuantity()){
				if( stk.getStock_arranged()!='Y')
					getSession().createQuery("update ItemStockModel set stock_arranged='Y' where id=:id")
						.setLong("id", stk.getId()).executeUpdate();
			}
			else if(val<stk.getQuantity()){
				if( stk.getStock_arranged()!='N')
					getSession().createQuery("update ItemStockModel set stock_arranged='N' where id=:id")
					.setLong("id", stk.getId()).executeUpdate();
				
			}
			
		} catch (Exception e) {
			throw e;
		}
	}
	
	
	
	/*	 Use begin() before using this  method	 */
	public void updateAllStocksStatus() throws Exception {
		ItemStockModel stk=null;
		try {
			List list=getSession().createQuery("select new com.inventory.purchase.model.ItemStockModel(id, quantity) " +
					"from ItemStockModel where status=2 and balance>0").list();
			
			Object obj;
			Iterator it1=list.iterator();
			while(it1.hasNext()){
				stk=(ItemStockModel) it1.next();
				obj=getSession().createQuery("select sum(quantity) from StockRackMappingModel where stock.id=:stkid")
						.setLong("stkid", stk.getId()).uniqueResult();
				
				if(obj!=null){
					if((Double)obj>=stk.getQuantity() && stk.getStatus()!=1){
						getSession().createQuery("update ItemStockModel set status='1' where id=:id")
							.setLong("id", stk.getId()).executeUpdate();
					}
					else if(stk.getStatus()==1){
							getSession().createQuery("update ItemStockModel set status='2' where id=:id")
							.setLong("id", stk.getId()).executeUpdate();
						
					}
				}
			}
			
		} catch (Exception e) {
			throw e;
		}
	}
	
	
	
	public List<ItemStockModel> getStockDetails(List salesStockList)
			throws Exception {
		try {
			begin();

			resultList = getSession()
					.createQuery(
							"select new com.inventory.purchase.model.ItemStockModel(id, "
									+ "concat(id,' : ', item.name, ' : Qty : ', quantity, ' : ', expiry_date)) from ItemStockModel where id in (:salesStockList)")
					.setParameterList("salesStockList", salesStockList).list();

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
	
	public List getStockOfPurchase(long purchase_id) throws Exception {
		try {
			begin();
			resultList=getSession().createQuery("select new com.inventory.purchase.model.ItemStockModel(id,barcode , " +
					"concat(id,' : ', item.name, ' : Qty : ', quantity, ' : ', expiry_date)) from ItemStockModel where purchase_id=:pid")
				.setLong("pid", purchase_id).list();
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
	
	
	public List getRacksArranged(long stk_id) throws Exception {
		try {
			begin();
			
			resultList=getSession().createQuery("select distinct a.rack from StockRackMappingModel a where a.stock.id=:stk "
					).setLong("stk", stk_id).list();
			
			commit();
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return resultList;
		}
	}
	
	
	public double getRacksBalance(long rack_id, long stk_id) throws Exception {
		double bal=0;
		try {
			begin();
			
			bal=(Double) getSession().createQuery("select coalesce(sum(a.balance),0) from StockRackMappingModel a where a.rack.id=:rak and a.stock.id=:stk"
					).setLong("rak", rack_id).setLong("stk", stk_id).uniqueResult();
			
			commit();
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return bal;
		}
	}
	
	
	public List getStockRackMaps(long rack_id) throws Exception {
		resultList=null;
		try {
			begin();
			
			resultList=getSession().createQuery("select new com.inventory.model.RackModel(a.id,concat(a.stock.item.name,' Bal : ',a.balance,' (',a.stock.item.unit.symbol,')')) from StockRackMappingModel a where a.rack.id=:rk and balance>0 "
					).setLong("rk", rack_id).list();
			
			commit();
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return resultList;
		}
	}
	
	
	public StockRackMappingModel getStockRackMap(long id) throws Exception {
		StockRackMappingModel stk=null;
		try {
			begin();
			stk=(StockRackMappingModel) getSession().get(StockRackMappingModel.class, id);
			commit();
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
			return stk;
		}
	}
	
	public void transferStocks(long mapId, long toRakId, double bal, double qty) throws Exception {
		StockRackMappingModel stk=null;
		try {
			begin();
			
			if(bal==qty) {
				stk=(StockRackMappingModel)getSession().get(StockRackMappingModel.class, mapId);
				stk.setRack(new RackModel(toRakId));
				getSession().update(stk);
			}
			else {
				stk=(StockRackMappingModel) getSession().get(StockRackMappingModel.class, mapId);
				stk.setBalance(CommonUtil.roundNumber(stk.getBalance()-qty));
//				stk.setQuantity_in_basic_unit(CommonUtil.roundNumber(stk.getBalance()-qty));
				getSession().update(stk);
				
				flush();
				
				getSession().evict(stk);
				
				StockRackMappingModel stkNew=stk;
				stkNew.setId(0);
				double convQty=CommonUtil.roundNumber(stkNew.getQuantity_in_basic_unit()/stkNew.getQuantity());
				stkNew.setBalance(qty);
				stkNew.setQuantity(CommonUtil.roundNumber(qty/convQty));
				stkNew.setQuantity_in_basic_unit(qty);
				stkNew.setRack(new RackModel(toRakId));
				getSession().save(stkNew);
				
			}
			
			commit();
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
			
	}
	
	
	
}
