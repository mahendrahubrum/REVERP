package com.inventory.config.stock.dao;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.inventory.config.stock.model.ItemDepartmentModel;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.config.tax.model.TaxModel;
import com.inventory.config.unit.model.ItemUnitMangementModel;
import com.inventory.config.unit.model.UnitModel;
import com.inventory.model.ItemGroupModel;
import com.inventory.model.ItemSubGroupModel;
import com.inventory.purchase.model.ItemStockModel;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;
import com.webspark.uac.model.S_OrganizationModel;

/**
 * @Author Jinshad P.T.
 */

public class ItemDao extends SHibernate implements Serializable{

	private static final long serialVersionUID = 4159558521798750896L;
	List resultList = new ArrayList();

	public long save(List list,long officeId)
			throws Exception {

		long itemId=0;
		try {
			Date dt = CommonUtil.getCurrentSQLDate();

			begin();

			ItemModel mdl=null;
			ItemUnitMangementModel objUnitMngtMdl=null;
			Iterator iter=list.iterator();
			while (iter.hasNext()) {
				objUnitMngtMdl = (ItemUnitMangementModel) iter.next();
				mdl=objUnitMngtMdl.getItem();
				
				getSession().save(mdl);
				
				ItemStockModel stk = new ItemStockModel();
				
				if(mdl.getOffice().getId()==officeId){
					itemId=mdl.getId();
					stk.setQuantity(mdl.getOpening_balance());
					stk.setBalance(mdl.getOpening_balance());
				}else{
					stk.setQuantity(0);
					stk.setBalance(0);
				}
				stk.setExpiry_date(dt);
				stk.setItem(mdl);
				stk.setManufacturing_date(dt);
				stk.setRate(mdl.getRate());
				stk.setPurchase_id(0);
				stk.setStatus(SConstants.stock_statuses.ITEM_CREATION_STOCK);
				stk.setDate_time(CommonUtil.getCurrentDateTime());

				getSession().save(stk);
				
				List lstST = getSession().createQuery("select id from SalesTypeModel where office.id=:ofc").setLong("ofc", mdl.getOffice().getId()).list();
				ItemUnitMangementModel objIUM = null;
				Iterator it = lstST.iterator();
				while (it.hasNext()) {
					long id=(Long) it.next();
					
						objIUM = new ItemUnitMangementModel(0, mdl, objUnitMngtMdl.getBasicUnit(),
															objUnitMngtMdl.getAlternateUnit(), 
															id,
															objUnitMngtMdl.getConvertion_rate(),
															CommonUtil.roundNumber(mdl.getSale_rate()),
															mdl.getSaleCurrency(),
															CommonUtil.roundNumber(mdl.getSale_convertion_rate()),
															objUnitMngtMdl.getStatus());

					
					getSession().save(objIUM);
				}
				flush();
				objIUM = new ItemUnitMangementModel(0, mdl, objUnitMngtMdl.getBasicUnit(),
						objUnitMngtMdl.getAlternateUnit(), 
						0,
						objUnitMngtMdl.getConvertion_rate(),
						CommonUtil.roundNumber(mdl.getRate()),
						mdl.getPurchaseCurrency(),
						CommonUtil.roundNumber(mdl.getPurchase_convertion_rate()),
						objUnitMngtMdl.getStatus());
				getSession().save(objIUM);
				flush();
			}
			
			if(mdl!=null)
				getSession().createQuery("update ItemSubGroupModel set itemParentId=itemParentId+1 where id=:grpId").setParameter("grpId", mdl.getSub_group().getId()).executeUpdate();
			
			
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
		return itemId;
	}

	@SuppressWarnings("rawtypes")
	public long update(List list,long officeId) throws Exception {

		long itemId=0;
		try {

			begin();
			
			
			ItemModel mdl=null;
			Iterator iter=list.iterator();
			while (iter.hasNext()) {
				mdl = (ItemModel) iter.next();
			
			double currBalance = (Double) getSession()
					.createQuery(
							"select current_balalnce from ItemModel where id=:id")
					.setParameter("id", mdl.getId()).uniqueResult();
			
			mdl.setCurrent_balalnce(currBalance);
			getSession().update(mdl);

			// ItemUnitMangementModel objMdl=new ItemUnitMangementModel();
			// objMdl.setAlternateUnit(objModel.getUnit().getId());
			// objMdl.setBasicUnit(objModel.getUnit().getId());
			// objMdl.setConvertion_rate(1);
			// objMdl.setSales_type(0);
			// objMdl.setItem_price(Double.parseDouble(rateTextField.getValue()));
			// objMdl.setStatus(2);

			
			List lstST = getSession().createQuery("select id from SalesTypeModel where office.id=:ofc").setLong("ofc", mdl.getOffice().getId()).list();
			List lis=new ArrayList();
			Iterator it = lstST.iterator();
			while (it.hasNext()) {
				long id=(Long) it.next();
				lis=getSession().createQuery("from ItemUnitMangementModel where item.id=:item and sales_type=:type")
							.setParameter("type", id).setParameter("item", mdl.getId()).list();
				
				if(lis.size()>0){
					getSession().createQuery("update ItemUnitMangementModel set purchaseCurrency=:purCur, item_price=:itemPrc," +
							" purchase_convertion_rate=:purRate where item.id=:item and sales_type=:type and basicUnit=alternateUnit")
								.setParameter("purCur", mdl.getSaleCurrency()).setParameter("itemPrc", CommonUtil.roundNumber(mdl.getSale_rate()))
								.setParameter("purRate", CommonUtil.roundNumber(mdl.getSale_convertion_rate())).setParameter("type", id).setParameter("item", mdl.getId()).executeUpdate();
				}
				else{
					ItemUnitMangementModel objIUM  = new ItemUnitMangementModel((long)0, 
														mdl, 
														mdl.getUnit().getId(), 
														mdl.getUnit().getId(), 
														id,
														(double)1,
														CommonUtil.roundNumber(mdl.getSale_rate()),
														mdl.getSaleCurrency(),
														CommonUtil.roundNumber(mdl.getSale_convertion_rate()),
														(long)2);
					if(objIUM!=null)
						getSession().saveOrUpdate(objIUM);
				}
				
				
			}
			flush();
			
			lis=getSession().createQuery("from ItemUnitMangementModel where item.id=:item and sales_type=0")
							.setParameter("item", mdl.getId()).list();
			
			if(lis.size()>0){
				getSession().createQuery("update ItemUnitMangementModel set purchaseCurrency=:purCur, item_price=:itemPrc," +
						" purchase_convertion_rate=:purRate where item.id=:item and sales_type=0")
							.setParameter("purCur", mdl.getPurchaseCurrency()).setParameter("itemPrc", CommonUtil.roundNumber(mdl.getRate()))
							.setParameter("purRate", CommonUtil.roundNumber(mdl.getPurchase_convertion_rate())).setParameter("item", mdl.getId()).executeUpdate();
			}
			else{
				ItemUnitMangementModel objIUM  = new ItemUnitMangementModel((long)0, 
													mdl, 
													mdl.getUnit().getId(), 
													mdl.getUnit().getId(), 
													(long)0,
													(double)1,
													CommonUtil.roundNumber(mdl.getRate()),
													mdl.getPurchaseCurrency(),
													CommonUtil.roundNumber(mdl.getPurchase_convertion_rate()),
													(long)2);
				getSession().saveOrUpdate(objIUM);
			}
			

			getSession().createQuery("update ItemStockModel set balance=:bal, quantity=:bal, rate=:rat where item.id=:itm and status=2 and purchase_id=0")
					.setParameter("bal", mdl.getOpening_balance())
					.setParameter("rat", mdl.getRate())
					.setLong("itm", mdl.getId()).executeUpdate();

			// ItemStockModel stk=new ItemStockModel();
			//
			// stk.setBalance(obj.getItem().getOpening_balance());
			// stk.setExpiry_date(dt);
			// stk.setItem(obj.getItem());
			// stk.setManufacturing_date(dt);
			// stk.setPurchase_id(0);
			// stk.setQuantity(obj.getItem().getOpening_balance());
			// stk.setStatus(1);
			//
			// getSession().save(stk);
			
			if(mdl.getOffice().getId()==officeId)
				itemId=mdl.getId();

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
		return itemId;
	}

	public void delete(long id) throws Exception {

		try {
			begin();
			
			ItemModel item=(ItemModel) getSession().get(ItemModel.class, id);
			
			Iterator resIter = getSession()
					.createQuery(
							" from ItemModel where sub_group.id=:grp and parentId=:val")
					.setParameter("grp", item.getSub_group().getId())
					.setParameter("val", item.getParentId())
					.list().iterator();
			
			while (resIter.hasNext()) {
				item = (ItemModel) resIter.next();
				
				getSession().createQuery("delete from ItemStockModel where item.id=:id")
				.setParameter("id", item.getId()).executeUpdate();

				getSession().createQuery("delete from ItemUnitMangementModel where item.id=:id")
				.setParameter("id", item.getId()).executeUpdate();

				getSession().delete(item);
				flush();
			}
			commit();

		} catch (Exception e) {
			rollback();
			close();
			throw e;
		}
		flush();
		close();
	}
	
	public List getAllItemsUnderParentFromItem(ItemModel itemModel) throws Exception {
		resultList=null;
		try {
			begin();

			resultList = getSession()
					.createQuery(
							" from ItemModel where sub_group.id=:grp and parentId=:val")
					.setParameter("grp", itemModel.getSub_group().getId())
					.setParameter("val", itemModel.getParentId())
					.list();

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

	public List getAllItems(long ofc_id) throws Exception {

		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.config.stock.model.ItemModel(id, name)"
									+ " from ItemModel where office.id=:ofc order by name")
					.setParameter("ofc", ofc_id).list();
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
	
	public List getAllItemsWithCode(long ofc_id) throws Exception {

		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.config.stock.model.ItemModel(id, concat(name, '  ( ', item_code, ' ) '))"
									+ " from ItemModel where office.id=:ofc")
					.setParameter("ofc", ofc_id).list();
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

	public List getAllActiveItems(long ofc_id) throws Exception {

		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.config.stock.model.ItemModel(id, name)"
									+ " from ItemModel  where office.id=:ofc and status=:sts order by name")
					.setParameter("ofc", ofc_id)
					.setParameter("sts", SConstants.statuses.ITEM_ACTIVE)
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
	
	public List getAllActiveItemsFromOfc(long ofc_id) throws Exception {

		try {
			begin();
			resultList = getSession()
					.createQuery(
							"from ItemModel  where office.id=:ofc and status=:sts order by name")
					.setParameter("ofc", ofc_id)
					.setParameter("sts", SConstants.statuses.ITEM_ACTIVE)
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

	public List getAllActiveItemsWithAppendingItemCode(long ofc_id)
			throws Exception {

		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.config.stock.model.ItemModel(id, concat(name,' ( ', item_code ,' ) '))"
									+ " from ItemModel  where office.id=:ofc and status=:sts order by name")
					.setParameter("ofc", ofc_id)
					.setParameter("sts", SConstants.statuses.ITEM_ACTIVE)
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

	public ItemModel getItem(long id) throws Exception {
		ItemModel mod = null;
		try {
			begin();
			mod = (ItemModel) getSession().get(ItemModel.class, id);
			commit();
		} catch (Exception e) {
			rollback();
			close();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			flush();
			close();
			return mod;
		}
	}

	public double getBuyingPrice(long id) throws Exception {
		double price = 0;
		try {
			begin();

			Object obj = getSession()
					.createQuery("select buying_price from ItemModel where id=:id")
					.setParameter("id", id).uniqueResult();
			if (obj != null)
				price = (Double) obj;

			commit();

		} catch (Exception e) {
			rollback();
			close();
			throw e;
			// TODO Auto-generated catch block
		} finally {
			flush();
			close();
			return price;
		}
	}
	
	public ItemModel getNameAndCurrentBal(long id) throws Exception {
		ItemModel itmObj=null;
		try {
			begin();

			Object obj = getSession()
					.createQuery("select new com.inventory.config.stock.model.ItemModel(id,name,current_balalnce) " +
							"from ItemModel where id=:id")
					.setParameter("id", id).uniqueResult();
			if (obj != null)
				itmObj = (ItemModel) obj;

			commit();

		} catch (Exception e) {
			rollback();
			close();
			throw e;
			// TODO Auto-generated catch block
		} finally {
			flush();
			close();
			return itmObj;
		}
	}

	public double getStandardCost(long id) throws Exception {
		double price = 0;
		try {
			begin();

			Object obj = getSession()
					.createQuery(
							"select standard_cost from ItemModel where id=:id")
					.setParameter("id", id).uniqueResult();
			if (obj != null)
				price = (Double) obj;

			commit();

		} catch (Exception e) {
			rollback();
			close();
			throw e;
			// TODO Auto-generated catch block
		} finally {
			flush();
			close();
			return price;
		}
	}

	public boolean isCessEnabled(long id) throws Exception {
		boolean enable = false;
		try {
			begin();
			if (getSession()
					.createQuery(
							"select id from ItemModel where id=:id and cess_enabled='Y'")
					.setParameter("id", id).list().size() > 0) {
				enable = true;
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
			return enable;
		}
	}

	public List getAllActiveItemsWithAppendingItemCode(long ofc_id,
			long itemSubgroupId, long groupId) throws Exception {

		String condition1 = "";
		String condition2 = "";
		String condition3 = "";
		if (itemSubgroupId != 0) {
			condition1 += " and sub_group.id=" + itemSubgroupId;
		}
		if (groupId != 0) {
			condition2 += " and sub_group.group.id=" + groupId;
		}
		if (ofc_id != 0) {
			condition3 += " and office.id=" + ofc_id;
		}
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.config.stock.model.ItemModel(id, concat(name,' ( ', item_code ,' ) '))"
									+ " from ItemModel  where  status=:sts "
									+ condition1 + condition2 + condition3+" order by name ")
					.setParameter("sts", SConstants.statuses.ITEM_ACTIVE)
					.list();
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

	public List getAllActiveItems(long officeId, long itemId,
			long itemSubgroupId, long groupId, long org_id) throws Exception {

		String condition = "";
		if (itemId != 0) {
			condition += " and id=" + itemId;
		}
		if (itemSubgroupId != 0) {
			condition += " and sub_group.id=" + itemSubgroupId;
		}
		if (groupId != 0) {
			condition += " and sub_group.group.id=" + groupId;
		}
		if (officeId != 0) {
			condition += " and office.id=" + officeId;
		}
		else
			condition += " and office.organization.id=" + org_id;
		
		try {
			begin();
			resultList = getSession().createQuery("from ItemModel  where status=:sts" + condition+" order by name")
					.setParameter("sts", SConstants.statuses.ITEM_ACTIVE).list();
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

	public List getItemsUnderReorderLevel(long officeId) throws Exception {

		try {

			begin();
			resultList = getSession()
					.createQuery(
							"from ItemModel  where  status=:sts and office.id=:ofc and current_balalnce<=reorder_level")
					.setParameter("sts", SConstants.statuses.ITEM_ACTIVE)
					.setParameter("ofc", officeId).list();
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

	// Added By Anil

	public List getItemsUnderReorderLevel(long officeId, long itemGroupId,
			long itemSubGroupId) throws Exception {

		try {
			String condition = "";
			if (itemSubGroupId != 0) {
				condition += " and sub_group.id=" + itemSubGroupId;
			}
			if (itemGroupId != 0) {
				condition += " and sub_group.group.id=" + itemGroupId;
			}

			begin();
			resultList = getSession()
					.createQuery(
							"from ItemModel  where  status=:sts and office.id=:ofc and current_balalnce<=reorder_level"
									+ condition)
					.setParameter("sts", SConstants.statuses.ITEM_ACTIVE)
					.setParameter("ofc", officeId).list();
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

	public List getAllActiveItemsList(long ofc_id) throws Exception {

		try {
			begin();
			resultList = getSession()
					.createQuery(
							" from ItemModel  where office.id=:ofc and status=:sts")
					.setParameter("ofc", ofc_id)
					.setParameter("sts", SConstants.statuses.ITEM_ACTIVE)
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

	public void save(Vector modelVector) throws Exception {
		try {

			begin();
			ItemModel itemMdl = null;
			Date dt = CommonUtil.getCurrentSQLDate();
			ItemStockModel stk;
			ItemUnitMangementModel objMdl = null;
			for (int i = 0; i < modelVector.size(); i++) {

				itemMdl = (ItemModel) modelVector.get(i);
				getSession().save(itemMdl);

				stk = new ItemStockModel();
				stk.setBalance(itemMdl.getOpening_balance());
				stk.setExpiry_date(dt);
				stk.setItem(itemMdl);
				stk.setManufacturing_date(dt);
				stk.setPurchase_id(0);
				stk.setQuantity(itemMdl.getOpening_balance());
				stk.setStatus(2);
				stk.setDate_time(CommonUtil.getCurrentDateTime());

				getSession().save(stk);

				List lstST = getSession()
						.createQuery(
								"select id from SalesTypeModel where office.id=:ofc")
						.setLong("ofc",
								itemMdl.getOffice().getId())
						.list();

				
				Iterator it = lstST.iterator();
				while (it.hasNext()) {

					objMdl = new ItemUnitMangementModel();

					objMdl.setAlternateUnit(itemMdl.getUnit()
							.getId());
					objMdl.setBasicUnit(itemMdl.getUnit().getId());
					objMdl.setConvertion_rate(1);
					objMdl.setSales_type((Long)it.next());
					objMdl.setItem_price(itemMdl.getRate());
					objMdl.setStatus(2);
					objMdl.setItem(itemMdl);
					getSession().save(objMdl);

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

	}

	public boolean isAlreadyExists(long officeId, String name, String code)
			throws Exception {
		boolean flag = false;
		List list = null;
		try {
			begin();
			list = getSession()
					.createQuery(
							"from ItemModel where name=:name and item_code=:code and office.id=:ofc")
					.setParameter("name", name).setParameter("code", code)
					.setParameter("ofc", officeId).list();
			commit();

			if (list.size() > 0)
				flag = true;

		} catch (Exception e) {
			flag = false;
			rollback();
			close();
			e.printStackTrace();
			throw e;
		} finally {
			flush();
			close();
		}
		
		return flag;
	}
	
	public UnitModel isUnitAlreadyExists(long orgId, UnitModel old)
			throws Exception {
		UnitModel newObj = null;
		try {
			begin();
			Object obj = getSession().createQuery(
							"from UnitModel where name=:name and symbol=:code and organization.id=:org")
					.setParameter("name", old.getName()).setParameter("code", old.getSymbol())
					.setParameter("org", orgId).uniqueResult();
			
			if(obj!=null)
				newObj=(UnitModel) obj;
			else {
				old.setId(0);
				old.setOrganization(new S_OrganizationModel(orgId));
				getSession().save(old);
				newObj=old;
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

		return newObj;
	}
	
	public UnitModel getUnitByCreate(long orgId, String name)
			throws Exception {
		UnitModel newObj = null;
		try {
			begin();
			Object obj = getSession().createQuery(
							"from UnitModel where UPPER(name) = :name and organization.id=:org")
					.setParameter("name", name.toUpperCase().trim())
					.setParameter("org", orgId).uniqueResult();
			
			if(obj!=null)
				newObj=(UnitModel) obj;
			else {
				obj = getSession().createQuery(
						"from UnitModel where UPPER(symbol) = :name and organization.id=:org")
				.setParameter("name", name.toUpperCase().trim())
				.setParameter("org", orgId).uniqueResult();
				
				if(obj == null){
					newObj=new UnitModel(0,name,name);
					newObj.setStatus(1);
					newObj.setOrganization(new S_OrganizationModel(orgId));
					getSession().save(newObj);
				} else {
					newObj=(UnitModel) obj;
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

		return newObj;
	}
	
	public ItemSubGroupModel isItemGroupsAlreadyExists(long org_id, ItemSubGroupModel subGp, TaxModel defTax)
			throws Exception {
		ItemSubGroupModel itemSGrp=null;
		try {
			begin();
			
			ItemGroupModel itemGrp=null;
			
			Object objGP= getSession().createQuery(
					"from ItemGroupModel where name=:name and code=:code and organization.id=:org")
					.setParameter("name", subGp.getGroup().getName()).setParameter("code", subGp.getGroup().getCode())
					.setParameter("org", org_id).uniqueResult();
			
			if(objGP!=null)
				itemGrp=(ItemGroupModel) objGP;
			else {
				itemGrp=subGp.getGroup();
				itemGrp.setId(0);
				itemGrp.setOrganization(new S_OrganizationModel(org_id));
				getSession().save(itemGrp);
			}
			
			
			
			
			
			
			Object objSGP= getSession().createQuery(
					"from ItemSubGroupModel where name=:name and code=:code and group.organization.id=:org")
					.setParameter("name", subGp.getName()).setParameter("code", subGp.getCode())
					.setParameter("org", org_id).uniqueResult();
			
			if(objSGP!=null)
				itemSGrp=(ItemSubGroupModel) objSGP;
			else {
				itemSGrp=subGp;
				itemSGrp.setId(0);
				itemSGrp.setGroup(itemGrp);
				getSession().save(itemSGrp);
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

		return itemSGrp;
	}
	
	public ItemSubGroupModel getItemSubGroupByCreate(long org_id, String subGp, TaxModel tax)
			throws Exception {
		ItemSubGroupModel itemSGrp=null;
		try {
			begin();
			
			
			
			Object objSGP= getSession().createQuery(
					"from ItemSubGroupModel where name=:name and group.organization.id=:org")
					.setParameter("name", subGp)
					.setParameter("org", org_id).uniqueResult();
			
			if(objSGP!=null)
				itemSGrp=(ItemSubGroupModel) objSGP;
			else {
				
				ItemGroupModel itemGrp=null;
				
				ItemDepartmentModel departmentModel=new ItemDepartmentModel();
				departmentModel.setName(subGp);
				departmentModel.setCode(subGp);
				departmentModel.setOrganization(new S_OrganizationModel(org_id));
				departmentModel.setStatus(1);
				getSession().save(departmentModel);
				flush();
				
				Object objGP= getSession().createQuery(
						"from ItemGroupModel where name=:name and organization.id=:org")
						.setParameter("name", subGp).setParameter("org", org_id).uniqueResult();
				
				if(objGP!=null)
					itemGrp=(ItemGroupModel) objGP;
				else {
					itemGrp=new ItemGroupModel(0, subGp);
					itemGrp.setStatus(1);
					itemGrp.setOrganization(new S_OrganizationModel(org_id));
					itemGrp.setCode(subGp);
					itemGrp.setItemDepartment(departmentModel);
					getSession().save(itemGrp);
				}
				
				itemSGrp=new ItemSubGroupModel(0, subGp);
				itemSGrp.setStatus(1);
				itemSGrp.setGroup(itemGrp);
				itemSGrp.setCode(subGp);
				getSession().save(itemSGrp);
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

		return itemSGrp;
	}

	public String getIconName(long itmId) throws Exception {
		String str = null;
		try {
			begin();
			str = (String) getSession()
					.createQuery(
							"select icon from ItemModel where id=:id")
					.setParameter("id",itmId).uniqueResult();
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
		
		return str;
	}
	
	public List getAllSalesOnlyItems(long ofc_id) throws Exception {

		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.config.stock.model.ItemModel(id, concat(name,' ( '," +
							"item_code,' )  Bal : ' , current_balalnce))"
									+ " from ItemModel where office.id=:ofc and affect_type=:typ").setParameter("typ", SConstants.affect_type.SALES_ONLY)
					.setParameter("ofc", ofc_id).list();
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
	
	public List getAllPurchaseOnlyItems(long ofc_id)
			throws Exception {

		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.config.stock.model.ItemModel(id, concat(name,' ( ', item_code ,' )  Bal : ' , current_balalnce))"
									+ " from ItemModel  where office.id=:ofc and status=:sts  and affect_type=:typ order by name")
					.setParameter("ofc", ofc_id)
					.setParameter("sts", SConstants.statuses.ITEM_ACTIVE)
					.setParameter("typ", SConstants.affect_type.PURCHASE_ONLY)
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
		}
		return resultList;
	}
	
	public List getAllManufacturingItems(long ofc_id)
			throws Exception {
		
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.config.stock.model.ItemModel(id, concat(name,' ( ', item_code ,' )  Bal : ' , current_balalnce))"
									+ " from ItemModel  where office.id=:ofc and status=:sts  and affect_type=:typ order by name")
									.setParameter("ofc", ofc_id)
									.setParameter("sts", SConstants.statuses.ITEM_ACTIVE)
									.setParameter("typ", SConstants.affect_type.MANUFACTURING)
									.list();
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

	public List getAllAffectAllItems(long ofc_id)
			throws Exception {
		
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.config.stock.model.ItemModel(id, concat(name,' ( ', item_code ,' )  Bal : ' , current_balalnce))"
									+ " from ItemModel  where office.id=:ofc and status=:sts  and affect_type=:typ order by name")
									.setParameter("ofc", ofc_id)
									.setParameter("sts", SConstants.statuses.ITEM_ACTIVE)
									.setParameter("typ", SConstants.affect_type.AFFECT_ALL)
									.list();
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
	
	public List getAllItemsWithCodeAndBalance(long ofc_id)
			throws Exception {
		
		try {
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.config.stock.model.ItemModel(id, concat(name,' ( ', item_code ,' )  Bal: ' , current_balalnce))"
									+ " from ItemModel  where office.id=:ofc and status=:sts  order by name")
									.setParameter("ofc", ofc_id)
									.setParameter("sts", SConstants.statuses.ITEM_ACTIVE)
									.list();
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
	
	public long importItem(ItemModel obj, ItemUnitMangementModel objUnitMngtMdl, String bar_code,long gradeId)
			throws Exception {

		try {
			Date dt = CommonUtil.getCurrentSQLDate();
			ItemStockModel stk = new ItemStockModel();

			begin();

			getSession().save(obj);
//			getSession().save(obj);

			stk.setBalance(obj.getOpening_balance());
			stk.setExpiry_date(dt);
			stk.setItem(obj);
			stk.setManufacturing_date(dt);
			stk.setRate(obj.getRate());
			stk.setPurchase_id(0);
			stk.setQuantity(obj.getOpening_balance());
			stk.setStatus(2);
			stk.setDate_time(CommonUtil.getCurrentDateTime());
			stk.setBarcode(bar_code);
			stk.setItem_tag("");
			stk.setInv_det_id(0);
			stk.setGradeId(gradeId);

			getSession().save(stk);

			List lstST = getSession()
					.createQuery(
							"select id from SalesTypeModel where office.id=:ofc")
					.setLong("ofc", obj.getOffice().getId()).list();

			objUnitMngtMdl.setItem(obj);
			
			ItemUnitMangementModel objIUM;
			Iterator it = lstST.iterator();
			while (it.hasNext()) {

				objIUM = new ItemUnitMangementModel(0,
						obj, objUnitMngtMdl.getBasicUnit(),
						objUnitMngtMdl.getAlternateUnit(), (Long) it.next(),
						objUnitMngtMdl.getConvertion_rate(),
						objUnitMngtMdl.getItem_price(),
						objUnitMngtMdl.getStatus());

				getSession().save(objIUM);
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
			return obj.getId();
		}
	}
	
	public List getAllItemsWithRealStck(long ofc_id) throws Exception {
		resultList=new ArrayList();
		try {
			begin();
			
			Iterator it = getSession().createQuery(
							"select new com.inventory.config.stock.model.ItemModel(id, concat(name,' ( '," +
							"item_code,' )  Bal : ' ), current_balalnce)"
									+ " from ItemModel where office.id=:ofc order by name")
					.setParameter("ofc", ofc_id).list().iterator();
			
			double bal=0;
			while(it.hasNext()) {
				ItemModel obj=(ItemModel) it.next();
				
				bal=(Double) getSession().createQuery("select coalesce(sum(balance),0) from ItemStockModel where item.id=:itm and status=3")
					.setLong("itm", obj.getId()).uniqueResult();
					
				obj.setName(obj.getName()+(obj.getCurrent_balalnce()-bal));
				resultList.add(obj);
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
			return resultList;
		}
	}

	public long getParentUnderGroup(Long grpId) throws Exception {
		long id=0;
		try {

			begin();

			Object obj = getSession().createQuery(
							"select itemParentId from ItemSubGroupModel where id=:id")
							.setParameter("id", grpId).uniqueResult();

			if (obj != null)
				id=(Long) obj;

			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return id;
	}
	
	public long getBatchIdFromStock(Long stkId) throws Exception {
		long id=0;
		try {

			begin();

			Object obj = getSession().createQuery(
							"select batch_id from ItemStockModel where id=:id")
							.setParameter("id", stkId).uniqueResult();

			if (obj != null)
				id=(Long) obj;

			commit();

		} catch (Exception e) {
			rollback();
			close();
			e.printStackTrace();
		} finally {
			flush();
			close();
		}
		return id;
	}
	
	public List<ItemStockModel> getItemStockModelList(long officeId) throws Exception {
		//	ItemModel itmObj=null;
			List<ItemStockModel> obj = null;
			try {
				begin();

				obj = getSession()
						.createQuery("from ItemStockModel where item.office.id=:id")
						.setParameter("id", officeId).list();
				

				commit();

			} catch (Exception e) {
				rollback();
				close();
				throw e;
			} finally {
				flush();
				close();
				
			}
			return obj;
		}
	
	public ItemStockModel getItemStockModel(long id) throws Exception {
		ItemStockModel mod = null;
		try {
			begin();
			mod = (ItemStockModel) getSession().get(ItemStockModel.class, id);
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
		return mod;
	}
	
	public List<ItemStockModel> getItemStockModelList(long officeId, long itemId, long locationId,
			Date fromDate, Date toDate) throws Exception {
		//	ItemModel itmObj=null;
			List<ItemStockModel> obj = null;
			try {
				begin();
				StringBuffer queryStringBuffer = new StringBuffer();
				queryStringBuffer.append("FROM ItemStockModel WHERE item.office.id=:id")
					.append(itemId != 0 ? " AND item.id = "+itemId : " ")
					.append(locationId !=0 ? " AND location_id = "+locationId : " ")
					.append(" AND DATE(date_time) BETWEEN :fromDate AND :toDate")
					.append(" ORDER BY date_time"); 

				obj = getSession()
						.createQuery(queryStringBuffer.toString())
						.setParameter("id", officeId)
						.setDate("fromDate", fromDate)
						.setDate("toDate", toDate).list();
				

				commit();

			} catch (Exception e) {
				rollback();
				close();
				throw e;
			} finally {
				flush();
				close();
				
			}
			return obj;
		}
	
	public List getExpiredItems(long officeId,Date date) throws Exception {
		List resultList=null;
		try {

			begin();
			resultList = getSession()
					.createQuery(
							"from ItemStockModel  where item.office.id=:ofc and expiry_date<:dt and purchase_id!=0 and balance>0")
					.setParameter("ofc", officeId).setParameter("dt", date).list();
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

	public double getItemDiscount(long itemId) throws Exception {
		double disc=0;
		try {

			begin();
			Object dis = getSession()
					.createQuery(
							"select coalesce(discount,0) from ItemModel  where id=:id")
					.setParameter("id", itemId).uniqueResult();
			if(dis!=null)
				disc=(Double) dis;
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
		return disc;
	}

	@SuppressWarnings("rawtypes")
	public List getItemStockList(long item, long office) throws Exception {
		List resultList=new ArrayList();
		try {
			begin();
			resultList = getSession().createQuery("select new com.inventory.purchase.model.ItemStockModel(id, concat(item.name, '- [ Mfg. Date: ', cast(manufacturing_date as string), ' , Expy. Date: ', cast(expiry_date as string), ' ]' ) )" +
					"from ItemStockModel  where item.office.id=:office and item.status=:status")
					.setParameter("office", office).setParameter("status", SConstants.statuses.ITEM_ACTIVE).list();
			commit();
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
			throw e;
		} finally {
			flush();
			close();
		}
		return resultList;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List getItemStockList(long item, long office, Date date) throws Exception {
		List resultList=new ArrayList();
		try {
			begin();
			resultList.addAll(getSession().createQuery("select new com.inventory.purchase.model.ItemStockModel(id, " +
					"concat(item.name, '- [ Mfg. Date: ', cast(manufacturing_date as string), ' , Expy. Date: ', cast(expiry_date as string), ' , Balance- ', balance,' ]' ) )" +
					"from ItemStockModel where item.office.id=:office and item.status=:status and balance>0 and expiry_date<:date")
					.setParameter("office", office).setParameter("status", SConstants.statuses.ITEM_ACTIVE).setParameter("date", date).list());
			
			resultList.addAll(getSession().createQuery("select new com.inventory.purchase.model.ItemStockModel(id, " +
					"concat(item.name, '- [ Mfg. Date: ', cast(manufacturing_date as string), ' , Expy. Date: ', cast(expiry_date as string), ' , Balance- ', balance,' ]' ) )" +
					"from ItemStockModel where item.office.id=:office and item.status=:status and balance>0 and purchase_type=:type")
					.setParameter("office", office).setParameter("status", SConstants.statuses.ITEM_ACTIVE)
					.setParameter("type", SConstants.stockPurchaseType.SALES_RETURN).list());
			
			commit();
		} catch (Exception e) {
			e.printStackTrace();
			rollback();
			close();
			throw e;
		} finally {
			flush();
			close();
		}
		return resultList;
	}

	public String getItemCode(long itemId) throws Exception {
		String str = null;
		try {
			begin();
			str = (String) getSession()
					.createQuery("select item_code from ItemModel where id=:id")
					.setParameter("id",itemId).uniqueResult();
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
		return str;
	}
	

	public List getAllActiveItemsWithAppendingItemCodeUnderGroup(long ofc_id,long groupId)
			throws Exception {

		try {
			String cnd="";
			if(groupId!=0)
				cnd+=" and sub_group.group.id="+groupId;
			
			begin();
			resultList = getSession()
					.createQuery(
							"select new com.inventory.config.stock.model.ItemModel(id, concat(name,' ( ', item_code ,' ) '))"
									+ " from ItemModel  where office.id=:ofc and status=:sts "+cnd+" order by name")
					.setParameter("ofc", ofc_id)
					.setParameter("sts", SConstants.statuses.ITEM_ACTIVE)
					.list();
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
	
	public List getAllActiveItemsWithAppendingItemCode(long ofc_id,boolean multiLanguage, long itemGroupId)
			throws Exception {
		List resultList=null;
		try {
			begin();
			
			if(multiLanguage)
				resultList = getSession()
				.createQuery(
						"select new com.inventory.config.stock.model.ItemModel(id, concat(name,'/',secondName, ' ( ', item_code ,' ) '))"
								+ " from ItemModel  where office.id=:ofc" +
								" and status=:sts" +
								(itemGroupId != 0 ? " AND sub_group.group.id=" + itemGroupId : "")+
								" order by name")
				.setParameter("ofc", ofc_id)
				.setParameter("sts", SConstants.statuses.ITEM_ACTIVE)
				.list();
			else
				resultList = getSession()
					.createQuery(
							"select new com.inventory.config.stock.model.ItemModel(id, concat(name,' ( ', item_code ,' ) '))"
									+ " from ItemModel  where office.id=:ofc and status=:sts" +
									(itemGroupId != 0 ? " AND sub_group.group.id=" + itemGroupId : "")+
									" order by name")
					.setParameter("ofc", ofc_id)
					.setParameter("sts", SConstants.statuses.ITEM_ACTIVE)
					.list();
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

}
