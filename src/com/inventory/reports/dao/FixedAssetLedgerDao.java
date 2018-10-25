package com.inventory.reports.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


import com.inventory.fixedasset.model.FixedAssetDepreciationMainModel;
import com.inventory.fixedasset.model.FixedAssetDepreciationModel;
import com.inventory.fixedasset.model.FixedAssetModel;
import com.inventory.fixedasset.model.FixedAssetPurchaseDetailsModel;
import com.inventory.fixedasset.model.FixedAssetPurchaseModel;
import com.inventory.fixedasset.model.FixedAssetSalesDetailsModel;
import com.inventory.fixedasset.model.FixedAssetSalesModel;
import com.inventory.reports.bean.FixedAssetLedgerBean;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

@SuppressWarnings("serial")
public class FixedAssetLedgerDao extends SHibernate implements Serializable{
	@SuppressWarnings({ "rawtypes", "finally" })
	public List getAllActiveAssetTypeGroupList(long org_id, long office_id) throws Exception {
		List resultList = null;
		try {
			begin();
			resultList = getSession().createQuery("SELECT new com.inventory.config.acct.model.GroupModel(assetTypeGroup.id, assetTypeGroup.name)" +
					" FROM FixedAssetModel" +
					" WHERE office.id = :office_id" +
					" AND assetTypeGroup.status=:val" +
					" AND assetTypeGroup.organization.id=:org" +
					" ORDER BY assetTypeGroup.name")
					.setParameter("org", org_id)
					.setParameter("office_id", office_id)
					.setParameter("val", SConstants.statuses.GROUP_ACTIVE).list();
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
	
	@SuppressWarnings("unchecked")
	public List<FixedAssetLedgerBean> getFixedAssetLedger(long office_id, long assetTypeGroupId, long fixedAssetId,
			Date fromDate, Date toDate,String currency) throws Exception {

		List<FixedAssetLedgerBean> mainList = new ArrayList<FixedAssetLedgerBean>();
		try {
			begin();
			List<FixedAssetModel> list = getSession()
					.createQuery(" FROM FixedAssetModel" +
							" WHERE office.id = :officeid" +
										(assetTypeGroupId != 0 ? " AND assetTypeGroup.id = "+assetTypeGroupId : " ")+
										(fixedAssetId != 0 ? " AND id = "+fixedAssetId : " "))											
					.setParameter("officeid", office_id).list();
			
//			List<FixedAssetPurchaseModel> list = getSession()
//					.createQuery(" FROM FixedAssetPurchaseModel a JOIN a.fixed_asset_purchase_details_list b" +
//							" WHERE a.office.id = :officeid" +
//										(assetTypeGroupId != 0 ? " AND b.fixedAsset.assetTypeGroup.id = "+assetTypeGroupId : " ")+
//										(fixedAssetId != 0 ? " AND b.fixedAsset.id = "+fixedAssetId : " ")+
//										" AND a.date <= :toDate")			
//										
//					.setParameter("officeid", office_id)
//					.setParameter("toDate", toDate).list();
			Iterator<FixedAssetModel> itr = list.iterator();
			double openingBal;
			double openingQty;
			double quantity;
			HashMap<Long, ArrayList<Double>> openingHashMap = new HashMap<Long, ArrayList<Double>>();
			ArrayList<Double> openingArrayList = null;
			while(itr.hasNext()){
				FixedAssetModel model = itr.next();
				
				openingQty = getOpeningQty(model.getId(), fromDate);
				quantity = getQuantity(model.getId(), fromDate, toDate);
				if(openingQty == 0 && quantity == 0){
					continue;
				}
				
				openingBal = getOpeningBalance(model.getId(),fromDate);
				
				openingArrayList = new ArrayList<Double>();
				openingArrayList.add(openingQty);
				openingArrayList.add(openingBal);
				
				openingHashMap.put(model.getId(), openingArrayList);
				
				mainList.addAll(getPurchaseBeanList(model.getId(), fromDate, toDate,currency));
				mainList.addAll(getSalesBeanList(model.getId(), fromDate, toDate,currency));
				mainList.addAll(getDepreciationBeanList(model.getId(), fromDate, toDate,currency));
			}
			Collections.sort(mainList, new Comparator<FixedAssetLedgerBean>() {

				@Override
				public int compare(FixedAssetLedgerBean o1,
						FixedAssetLedgerBean o2) {
					if(o1.getId() == o2.getId()){
						return o1.getDate().compareTo(o2.getDate());
					}else {
						return (int) (o1.getId() - o2.getId());
					}					
				}
			});
			long prevId = 0;
			double closingQty;
			double closingBal;
			openingQty = 0;
			openingBal = 0;
			for (FixedAssetLedgerBean bean : mainList) {
				if(prevId != bean.getId()){
					openingQty = openingHashMap.get(bean.getId()).get(0);
					openingBal = openingHashMap.get(bean.getId()).get(1);
					
					closingQty = openingQty + bean.getOpeningQty();
					closingBal = openingBal + bean.getOpeningBal();				
					
					bean.setOpeningQty(CommonUtil.roundNumber(openingQty));
					bean.setOpeningBal(CommonUtil.roundNumber(openingBal));					
					bean.setClosingQty(CommonUtil.roundNumber(closingQty));
					bean.setClosingBalance(CommonUtil.roundNumber(closingBal));
				} else {
					bean.setGroup("");
					bean.setFixedAsset("");						
					
					closingQty = openingQty + bean.getOpeningQty();
					closingBal = openingBal + bean.getOpeningBal();				
					
					bean.setOpeningQty(CommonUtil.roundNumber(openingQty));
					bean.setOpeningBal(CommonUtil.roundNumber(openingBal));					
					bean.setClosingQty(CommonUtil.roundNumber(closingQty));
					bean.setClosingBalance(CommonUtil.roundNumber(closingBal));
				}
				
				openingQty = closingQty;
				openingBal = closingBal;
				
				prevId = bean.getId();
			}
			commit();
		} catch (Exception e) {
			rollback();
			close();
			throw e;

		} finally {
			flush();
			close();
		}
		return mainList;
	
		
	}

	@SuppressWarnings("unchecked")
	private List<FixedAssetLedgerBean> getPurchaseBeanList(long fixedAssetId,
			Date fromDate, Date toDate, String currency) throws Exception{
		List<FixedAssetLedgerBean> mainList = new ArrayList<FixedAssetLedgerBean>();
		List<FixedAssetPurchaseModel> list = getSession()
				.createQuery("SELECT a FROM FixedAssetPurchaseModel a JOIN a.fixed_asset_purchase_details_list b" +
						" WHERE b.fixedAsset.id = :id" +
						" AND a.date BETWEEN :fromDate AND :toDate")				
				.setParameter("id", fixedAssetId)
				.setParameter("fromDate", fromDate)
				.setParameter("toDate", toDate).list();
		Iterator<FixedAssetPurchaseModel> itr = list.iterator();
		
		while(itr.hasNext()){
			FixedAssetPurchaseModel model = itr.next();
			Iterator<FixedAssetPurchaseDetailsModel> innerItr = model.getFixed_asset_purchase_details_list().iterator();
			while(innerItr.hasNext()){
				FixedAssetPurchaseDetailsModel detModel = innerItr.next();
				FixedAssetLedgerBean bean = new FixedAssetLedgerBean();
				bean.setId(fixedAssetId);
				bean.setDate(model.getDate());
				bean.setGroup(detModel.getFixedAsset().getAssetTypeGroup().getName());
				bean.setFixedAsset(detModel.getFixedAsset().getName());
				bean.setParticulars("Purchase ("+model.getAssetNo()+")");
				bean.setOpeningQty(detModel.getQuantity());
				bean.setOpeningBal(detModel.getQuantity() * detModel.getUnitPrice());
				
				bean.setQty(detModel.getQuantity());
				bean.setUnit(detModel.getFixedAsset().getUnit().getSymbol());
				bean.setUnitPrice(detModel.getUnitPrice());
				bean.setAmount(bean.getOpeningBal());
				bean.setDepPercentage(0);
				bean.setDepValue(0);
				bean.setCurrency(currency);
				
				mainList.add(bean);
			}			
		}		
		return mainList;
	}
	
	@SuppressWarnings("unchecked")
	private List<FixedAssetLedgerBean> getSalesBeanList(long fixedAssetId,
			Date fromDate, Date toDate, String currency) throws Exception{
		List<FixedAssetLedgerBean> mainList = new ArrayList<FixedAssetLedgerBean>();
		List<FixedAssetSalesModel> list = getSession()
				.createQuery("SELECT a FROM FixedAssetSalesModel a JOIN a.fixed_asset_sales_details_list b" +
						" WHERE b.depreciationId.fixedAssetPurchaseDetailsId.fixedAsset.id = :id" +
						" AND a.date BETWEEN :fromDate AND :toDate")				
				.setParameter("id", fixedAssetId)
				.setParameter("fromDate", fromDate)
				.setParameter("toDate", toDate).list();
		Iterator<FixedAssetSalesModel> itr = list.iterator();
		
		while(itr.hasNext()){
			FixedAssetSalesModel model = itr.next();
			Iterator<FixedAssetSalesDetailsModel> innerItr = model.getFixed_asset_sales_details_list().iterator();
			while(innerItr.hasNext()){
				FixedAssetSalesDetailsModel detModel = innerItr.next();
				FixedAssetLedgerBean bean = new FixedAssetLedgerBean();
				bean.setId(fixedAssetId);
				bean.setDate(model.getDate());
				bean.setGroup(detModel.getDepreciationId().getFixedAssetPurchaseDetailsId().getFixedAsset().getAssetTypeGroup().getName());
				bean.setFixedAsset(detModel.getDepreciationId().getFixedAssetPurchaseDetailsId().getFixedAsset().getName());
				bean.setParticulars("Sales ("+model.getAssetSalesNo()+")");
				bean.setOpeningQty(- detModel.getSalesQuantity());
				bean.setOpeningBal(- detModel.getSalesQuantity() * detModel.getDepreciationId().getFixedAssetPurchaseDetailsId().getUnitPrice());
				
				bean.setQty(Math.abs(bean.getOpeningQty()));
				bean.setUnit(detModel.getDepreciationId().getFixedAssetPurchaseDetailsId().getFixedAsset().getUnit().getSymbol());
				bean.setUnitPrice(detModel.getDepreciationId().getFixedAssetPurchaseDetailsId().getUnitPrice());
				bean.setAmount(Math.abs(bean.getOpeningBal()));
				bean.setDepPercentage(detModel.getDepreciationId().getPercentage());
				bean.setDepValue(detModel.getDepreciationId().getAmount());
				bean.setCurrency(currency);
				
				mainList.add(bean);
			}			
		}		
		return mainList;
	}
	
	@SuppressWarnings("unchecked")
	private List<FixedAssetLedgerBean> getDepreciationBeanList(long fixedAssetId,
			Date fromDate, Date toDate, String currency) throws Exception{
		List<FixedAssetLedgerBean> mainList = new ArrayList<FixedAssetLedgerBean>();
		List<FixedAssetDepreciationMainModel> list = getSession()
				.createQuery("SELECT a FROM FixedAssetDepreciationMainModel a JOIN a.fixed_asset_depreciation_list b" +
						" WHERE b.fixedAssetPurchaseDetailsId.fixedAsset.id = :id" +
						" AND a.date BETWEEN :fromDate AND :toDate")				
				.setParameter("id", fixedAssetId)
				.setParameter("fromDate", fromDate)
				.setParameter("toDate", toDate).list();
		Iterator<FixedAssetDepreciationMainModel> itr = list.iterator();
		
		while(itr.hasNext()){
			FixedAssetDepreciationMainModel model = itr.next();
			Iterator<FixedAssetDepreciationModel> innerItr = model.getFixed_asset_depreciation_list().iterator();
			while(innerItr.hasNext()){
				FixedAssetDepreciationModel detModel = innerItr.next();
				FixedAssetLedgerBean bean = new FixedAssetLedgerBean();
				bean.setId(fixedAssetId);
				bean.setDate(model.getDate());
				bean.setGroup(detModel.getFixedAssetPurchaseDetailsId().getFixedAsset().getAssetTypeGroup().getName());
				bean.setFixedAsset(detModel.getFixedAssetPurchaseDetailsId().getFixedAsset().getName());
				bean.setParticulars("Depreciation ("+model.getDepreciationNo()+")");
				bean.setOpeningQty(0);
				bean.setOpeningBal(- detModel.getAmount());
				
				bean.setQty(detModel.getQuantity());
				bean.setUnit(detModel.getFixedAssetPurchaseDetailsId().getFixedAsset().getUnit().getSymbol());
				bean.setUnitPrice(detModel.getFixedAssetPurchaseDetailsId().getUnitPrice());
				bean.setAmount(detModel.getQuantity() * detModel.getFixedAssetPurchaseDetailsId().getUnitPrice());
				bean.setDepPercentage(detModel.getPercentage());
				bean.setDepValue(detModel.getAmount());
				bean.setCurrency(currency);
				
				mainList.add(bean);
			}			
		}		
		return mainList;
	}

	private double getOpeningBalance(long id, Date fromDate) throws Exception{
		double openingBalance = 0;
		
		openingBalance += (Double)getSession()
				.createQuery("SELECT  COALESCE(SUM(b.quantity * b.unitPrice), 0)" +
						" FROM FixedAssetPurchaseModel a JOIN a.fixed_asset_purchase_details_list b" +
						" WHERE b.fixedAsset.id = :id" +
						" AND a.date < :date")				
				.setParameter("id", id)
				.setParameter("date", fromDate).uniqueResult();
		
		openingBalance -= (Double)getSession()
				.createQuery("SELECT  COALESCE(SUM(b.salesQuantity * b.depreciationId.fixedAssetPurchaseDetailsId.unitPrice), 0)" +
						" FROM FixedAssetSalesModel a JOIN a.fixed_asset_sales_details_list b" +
						" WHERE b.depreciationId.fixedAssetPurchaseDetailsId.fixedAsset.id = :id" +
						" AND a.date < :date")				
				.setParameter("id", id)
				.setParameter("date", fromDate).uniqueResult();	
		
		openingBalance -= (Double)getSession()
				.createQuery("SELECT  COALESCE(SUM(amount), 0)" +
						" FROM FixedAssetDepreciationModel" +
						" WHERE fixedAssetPurchaseDetailsId.fixedAsset.id = :id" +
						" AND date < :fromDate" +
						" AND depreciationMode = :depreciationMode")			
				.setParameter("id", id)
				.setParameter("fromDate", fromDate)
				.setParameter("depreciationMode", SConstants.FixedAsset.NORMAL_DEPRECIATION).uniqueResult();	
		
		return openingBalance;
	}

	private double getQuantity(long id, Date fromDate, Date toDate) throws Exception{
		double qty = 0;
		
		qty += (Double)getSession()
				.createQuery("SELECT  COALESCE(SUM(b.quantity), 0)" +
						" FROM FixedAssetPurchaseModel a JOIN a.fixed_asset_purchase_details_list b" +
						" WHERE b.fixedAsset.id = :id" +
						" AND a.date BETWEEN :fromDate AND :toDate")				
				.setParameter("id", id)
				.setParameter("fromDate", fromDate)
				.setParameter("toDate", toDate).uniqueResult();
		
		qty += (Double)getSession()
				.createQuery("SELECT  COALESCE(SUM(b.salesQuantity), 0)" +
						" FROM FixedAssetSalesModel a JOIN a.fixed_asset_sales_details_list b" +
						" WHERE b.depreciationId.fixedAssetPurchaseDetailsId.fixedAsset.id = :id" +
						" AND a.date BETWEEN :fromDate AND :toDate")			
				.setParameter("id", id)
				.setParameter("fromDate", fromDate)
				.setParameter("toDate", toDate).uniqueResult();	
		
		qty += (Double)getSession()
				.createQuery("SELECT  COALESCE(SUM(quantity), 0)" +
						" FROM FixedAssetDepreciationModel" +
						" WHERE fixedAssetPurchaseDetailsId.fixedAsset.id = :id" +
						" AND date BETWEEN :fromDate AND :toDate" +
						" AND depreciationMode = :depreciationMode")			
				.setParameter("id", id)
				.setParameter("fromDate", fromDate)
				.setParameter("toDate", toDate)
				.setParameter("depreciationMode", SConstants.FixedAsset.NORMAL_DEPRECIATION).uniqueResult();	
		
		return qty;
	}

	private double getOpeningQty(long id, Date fromDate) throws Exception{
		double openingQty = 0;
		
		openingQty += (Double)getSession()
				.createQuery("SELECT  COALESCE(SUM(b.quantity), 0)" +
						" FROM FixedAssetPurchaseModel a JOIN a.fixed_asset_purchase_details_list b" +
						" WHERE b.fixedAsset.id = :id" +
						" AND a.date < :date")				
				.setParameter("id", id)
				.setParameter("date", fromDate).uniqueResult();
		
		openingQty -= (Double)getSession()
				.createQuery("SELECT  COALESCE(SUM(b.salesQuantity), 0)" +
						" FROM FixedAssetSalesModel a JOIN a.fixed_asset_sales_details_list b" +
						" WHERE b.depreciationId.fixedAssetPurchaseDetailsId.fixedAsset.id = :id" +
						" AND a.date < :date")				
				.setParameter("id", id)
				.setParameter("date", fromDate).uniqueResult();	
		
		return openingQty;
	}

	
}
