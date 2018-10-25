package com.inventory.fixedasset.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.inventory.fixedasset.model.FixedAssetDepreciationMainModel;
import com.inventory.fixedasset.model.FixedAssetDepreciationModel;
import com.inventory.fixedasset.model.FixedAssetPurchaseDetailsModel;
import com.inventory.fixedasset.model.FixedAssetPurchaseModel;
import com.inventory.transaction.model.TransactionDetailsModel;
import com.inventory.transaction.model.TransactionModel;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.dao.SHibernate;

public class FixedAssetDepreciationDao extends SHibernate implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private FixedAssetPurchaseDao fixedAssetPurchaseDao;
	private long sequenceNumber;
	
	public long getSequenceNumber() {
		return sequenceNumber;
	}


	public void setSequenceNumber(long sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}
	public FixedAssetDepreciationMainModel getFixedAssetDepreciationMainModel(long id)
			throws Exception {
		FixedAssetDepreciationMainModel model = null;
		try {
			begin();
			model = (FixedAssetDepreciationMainModel) getSession().get(
					FixedAssetDepreciationMainModel.class, id);
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
		return model;
	}

	public FixedAssetDepreciationModel getFixedAssetDepreciationModel(long id) throws Exception {
		FixedAssetDepreciationModel model = null;
		try {
			begin();
			model = (FixedAssetDepreciationModel) getSession().get(
					FixedAssetDepreciationModel.class, id);
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
		return model;
	}
	
	
	public Date getFixedAssetDepreciationMaxDate(long id,java.sql.Date fromDate, long sequenceNumber) throws Exception {
		Date date = null;
		try {
			begin();
			date = (Date) getSession()
					.createQuery(
							"SELECT MAX(date)"
									+ " FROM FixedAssetDepreciationModel" +
									" WHERE fixedAssetPurchaseDetailsId.id = :id" +
									" AND date <= :fromDate" +
									"  AND sequenceNo < "+sequenceNumber)									
							//		((depreciationId != 0 ) ? " AND id = "+depreciationId : "  AND sequenceNo < "+sequenceNumber))
					.setParameter("id", id)
					.setParameter("fromDate", fromDate).uniqueResult();
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
		return date;
	}
	
	public long getFixedAssetDepreciationMaxSequenceNumber(long purchaseDetailId,long depreciationId) throws Exception {
		long sequenceNo = 0;
		try {
			begin();
			sequenceNo = (Long) getSession()
					.createQuery(
							"SELECT COALESCE(MAX(sequenceNo),0)"
									+ " FROM FixedAssetDepreciationModel" +
									" WHERE fixedAssetPurchaseDetailsId.id = :id" +
									((depreciationId != 0 ) ? " AND id = "+depreciationId : ""))
					.setParameter("id", purchaseDetailId).uniqueResult();
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
		return sequenceNo;
	}
	public double getFixedAssetTotalDepreciationQty(long sequenceNo,int depreciationMode) throws Exception {
		double quantity = 0;
		try {
			begin();
			quantity = (Double) getSession()
					.createQuery(
							"SELECT COALESCE(SUM(quantity),0)"
									+ " FROM FixedAssetDepreciationModel" +
									" WHERE sequenceNo = :sequenceNo" +
									" AND depreciationMode = :depreciationMode")
					.setParameter("sequenceNo", sequenceNo)
					.setParameter("depreciationMode", depreciationMode).uniqueResult();
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
		return quantity;
	}
	
	public double getFixedAssetTotalDepreciation(long purchaseDetailId,java.sql.Date fromDate,long depreciationId) throws Exception {
		double amount = 0;
		try {
			begin();
			amount = (Double) getSession()
					.createQuery(
							"SELECT COALESCE(SUM(amount),0)"
									+ " FROM FixedAssetDepreciationModel" +
									" WHERE fixedAssetPurchaseDetailsId.id = :id" +
									" AND date <= :fromDate"+
									((depreciationId != 0 ) ? " AND id = "+depreciationId : ""))
					.setParameter("id", purchaseDetailId)
					.setParameter("fromDate", fromDate).uniqueResult();
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
		return amount;
	}
	
	public double getDepreciationValue(long fixedAssetPurchaseDetailsId, java.sql.Date currentDate, long depreciationId, long sequenceNumber) {
		double depreciationAmount = 0;
		if(fixedAssetPurchaseDao == null){
			fixedAssetPurchaseDao = new FixedAssetPurchaseDao();
		}
		try{
			if(sequenceNumber == 0){
				sequenceNumber = getSequenceNumber(fixedAssetPurchaseDetailsId);
			}
			setSequenceNumber(sequenceNumber);
			
			FixedAssetPurchaseModel purchaseDetModel = fixedAssetPurchaseDao
					.getFixedAssetPurchaseModelByDetailId(fixedAssetPurchaseDetailsId);
			long noOfDays = getNoOfDaysOfFixedAsset(fixedAssetPurchaseDetailsId,sequenceNumber, currentDate, purchaseDetModel.getDate());
			System.out.println("=====NO_OF_DAYS= "+noOfDays+" ========");
			
			FixedAssetPurchaseDetailsModel detModel = purchaseDetModel.getFixed_asset_purchase_details_list().get(0);
			double amount = detModel.getCurrentBalance() * detModel.getUnitPrice();
			double percentage = detModel.getFixedAsset().getPercentage();
			int depreciationType = detModel.getFixedAsset().getDepreciationType();
			if(depreciationType == SConstants.FixedAsset.FLAT){
				depreciationAmount = (amount * noOfDays * percentage) / 36500;
			} else if(depreciationType == SConstants.FixedAsset.WRITTEN_DOWN_VALUE){
				double totalDepreciation = getFixedAssetTotalDepreciation(fixedAssetPurchaseDetailsId, 
						currentDate, depreciationId);
						depreciationAmount = ((amount - totalDepreciation) * noOfDays * percentage) / 36500;
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		
		return CommonUtil.roundNumber(depreciationAmount);
	}

private long getSequenceNumber(long purchaseDetailId) {
	long sequenceNo = 0;
		
	if(sequenceNo == 0){
		try {
			sequenceNo = getFixedAssetDepreciationMaxSequenceNumber(purchaseDetailId, 0);						
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	if(sequenceNo == 0){
		sequenceNo++;
	} else {
		try{
			double totalDepreciationQty = getFixedAssetTotalDepreciationQty(sequenceNo, SConstants.FixedAsset.NORMAL_DEPRECIATION);
			double purchaseCurrentBal = fixedAssetPurchaseDao.getFixedAssetPurchaseCurrentBalance(purchaseDetailId);
			if(totalDepreciationQty == purchaseCurrentBal){
				sequenceNo++;
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		
	}
	System.out.println("============ SEQUENCE NO ==== "+sequenceNo);
	return sequenceNo;
}


/**
 * 
 * @param id
 * @param sequenceNumber
 * @param currentDate
 * @param purchaseDate
 * @return
 */
	private long getNoOfDaysOfFixedAsset(long id, long sequenceNumber, java.sql.Date currentDate, Date purchaseDate) {
		long noOfDays = 0;
		try {			
			Date depreciationMaxDate = getFixedAssetDepreciationMaxDate(id, currentDate,
							sequenceNumber);
			Date fromDate = null;
			if(depreciationMaxDate != null && purchaseDate.compareTo(depreciationMaxDate) < 0){
				fromDate = depreciationMaxDate;
			} else {
				fromDate = purchaseDate;
			}
			noOfDays = (int)( (currentDate.getTime() - fromDate.getTime()) / (1000 * 60 * 60 * 24));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return noOfDays;
	}


public long save(FixedAssetDepreciationMainModel depreciationModel,
		TransactionModel tranModel) throws Exception{
	try {
		begin();
		if(tranModel != null){
			getSession().save(tranModel);
			
			depreciationModel.setTransactionId(tranModel.getTransaction_id());
			updateLedgerBalance(tranModel, true);
		}
		
		
		getSession().save(depreciationModel);

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
	return depreciationModel.getId();
}


private void updateLedgerBalance(TransactionModel tranModel, boolean newTran)
		throws Exception {
	Iterator<TransactionDetailsModel> aciter = tranModel.getTransaction_details_list().iterator();
	TransactionDetailsModel tdm;
	
	while (aciter.hasNext()) {
		tdm = aciter.next();
		if(newTran){
			getSession().createQuery("update LedgerModel set current_balance=current_balance-:amt where id=:id")
			.setDouble("amt", tdm.getAmount()).setLong("id", tdm.getToAcct().getId()).executeUpdate();

			getSession().createQuery("update LedgerModel set current_balance=current_balance+:amt where id=:id")
			.setDouble("amt", tdm.getAmount()).setLong("id", tdm.getFromAcct().getId()).executeUpdate();
		} else{
			getSession().createQuery("update LedgerModel set current_balance=current_balance-:amt where id=:id")
			.setDouble("amt", tdm.getAmount()).setLong("id", tdm.getFromAcct().getId()).executeUpdate();

			getSession().createQuery("update LedgerModel set current_balance=current_balance+:amt where id=:id")
			.setDouble("amt", tdm.getAmount()).setLong("id", tdm.getToAcct().getId()).executeUpdate();
		}
		

		flush();
		
	}
}


public void update(FixedAssetDepreciationMainModel model,
		ArrayList<Long> deletedIds, TransactionModel tranModel) throws Exception{
	try {
		begin();
		if(tranModel ==  null && model.getTransactionId() != 0){
			tranModel = (TransactionModel) getSession()
					.get(TransactionModel.class, model.getTransactionId());				
			getSession().delete(tranModel);
			deleteTransactionDetailsModel(tranModel);
			model.setTransactionId(0);				
			updateLedgerBalance(tranModel , false);
			
		} else if(tranModel !=  null && model.getTransactionId() == 0){
			getSession().save(tranModel);
			
			model.setTransactionId(tranModel.getTransaction_id());
			updateLedgerBalance(tranModel , true);
		} else if(tranModel !=  null){
			TransactionModel tempModel = (TransactionModel) getSession()
					.get(TransactionModel.class, model.getTransactionId());	
			updateLedgerBalance(tempModel , false);
			
			getSession().clear();
			tranModel.setTransaction_id(model.getTransactionId());
			getSession().update(tranModel);				
			updateLedgerBalance(tranModel , true);
			deleteTransactionDetailsModel(tempModel);
		}
		
		getSession().update(model);
		flush();
		
		if (deletedIds != null) {
			for (Long id : deletedIds) {
				FixedAssetDepreciationModel childModel = (FixedAssetDepreciationModel) getSession()
						.get(FixedAssetDepreciationModel.class, id);
				getSession().delete(childModel);
			}
		}

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
	
}


private void deleteTransactionDetailsModel(TransactionModel tranModel) throws Exception{
	Iterator<TransactionDetailsModel> itr = tranModel.getTransaction_details_list().iterator();
	while(itr.hasNext()){
		TransactionDetailsModel model = itr.next();
		 getSession().delete(model);
		 flush();
	}	
	
}


@SuppressWarnings({ "rawtypes", "unchecked" })
public List<FixedAssetDepreciationMainModel> getFixedAssetDepreciationMainModelList(long officeId) throws Exception{
	List list = null;
	try {
		begin();
		list = getSession()
				.createQuery(
						"SELECT new com.inventory.fixedasset.model.FixedAssetDepreciationMainModel(id, depreciationNo)"
								+ " FROM FixedAssetDepreciationMainModel WHERE office.id = :officeid"
								+ " ORDER BY depreciationNo")
				.setParameter("officeid", officeId).list();
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
	return list;
}


public void delete(long id) throws Exception {

		try {
			begin();
			FixedAssetDepreciationMainModel model = (FixedAssetDepreciationMainModel) getSession()
					.get(FixedAssetDepreciationMainModel.class, id);
			getSession().delete(model);
			
			if(model.getTransactionId() != 0){

				TransactionModel tempModel = (TransactionModel) getSession()
						.get(TransactionModel.class, model.getTransactionId());	
				updateLedgerBalance(tempModel , false);
				getSession().delete(tempModel);
				//deleteTransactionDetailsModel(tempModel);
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

	}



}
