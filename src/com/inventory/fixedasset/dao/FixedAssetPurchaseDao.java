package com.inventory.fixedasset.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.inventory.fixedasset.model.FixedAssetPurchaseDetailsModel;
import com.inventory.fixedasset.model.FixedAssetPurchaseModel;
import com.inventory.transaction.model.TransactionDetailsModel;
import com.inventory.transaction.model.TransactionModel;
import com.webspark.dao.SHibernate;

@SuppressWarnings("serial")
public class FixedAssetPurchaseDao extends SHibernate implements Serializable {
	
	public long save(FixedAssetPurchaseModel assetModel, TransactionModel tranModel)
			throws Exception {
		try {
			begin();
			if(tranModel != null){
				getSession().save(tranModel);
				
				assetModel.setTransactionId(tranModel.getTransaction_id());
				updateLedgerBalance(tranModel, true);
			}
			
			
			getSession().save(assetModel);

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
		return assetModel.getId();
	}

//	private TransactionModel setTransactions(
//			FixedAssetPurchaseModel assetModel, long fromAccountId) throws Exception {
//		FinTransaction tran = new FinTransaction();
//		long toAccountId;
//		double amount;
//
//		Iterator<FixedAssetPurchaseDetailsModel> itr = assetModel
//				.getFixed_asset_purchase_details_list().iterator();
//		while (itr.hasNext()) {
//			FixedAssetPurchaseDetailsModel detModel = itr.next();
//			
//			amount = CommonUtil.roundNumber(detModel.getUnitPrice()
//					* detModel.getUnitPrice());
//			toAccountId = detModel.getFixedAsset().getAccount().getId();
//			tran.addTransaction(SConstants.DR, fromAccountId, toAccountId,
//					amount, "Fixed Asset Purchase",
//					assetModel.getCurrencyId(),
//					assetModel.getConversionRate());
//
//			getSession()
//					.createQuery(
//							"update LedgerModel set current_balance=current_balance-:amt where id=:id")
//					.setDouble("amt", amount).setLong("id", toAccountId)
//					.executeUpdate();
//
//			getSession()
//					.createQuery(
//							"update LedgerModel set current_balance=current_balance+:amt where id=:id")
//					.setDouble("amt", amount).setLong("id", fromAccountId)
//					.executeUpdate();
//
//			flush();
//		}
//
//		TransactionModel transaction = tran.getTransaction(
//				SConstants.FIXED_ASSET, assetModel.getDate());
//		getSession().save(transaction);
//	//	flush();
//		return transaction;
//	}

	public void update(FixedAssetPurchaseModel model, ArrayList<Long> deletedIds, TransactionModel tranModel)
			throws Exception {
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
					FixedAssetPurchaseDetailsModel childModel = (FixedAssetPurchaseDetailsModel) getSession()
							.get(FixedAssetPurchaseDetailsModel.class, id);
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

	private void deleteTransactionDetailsModel(TransactionModel tranModel) throws Exception {
		Iterator<TransactionDetailsModel> itr = tranModel.getTransaction_details_list().iterator();
		while(itr.hasNext()){
			TransactionDetailsModel model = itr.next();
			 getSession().delete(model);
			 flush();
		}	
		
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

	public void delete(long id) throws Exception {

		try {
			begin();
			FixedAssetPurchaseModel model = (FixedAssetPurchaseModel) getSession()
					.get(FixedAssetPurchaseModel.class, id);
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

	public FixedAssetPurchaseModel getFixedAssetPurchaseModel(long id)
			throws Exception {
		FixedAssetPurchaseModel model = null;
		try {
			begin();
			model = (FixedAssetPurchaseModel) getSession().get(
					FixedAssetPurchaseModel.class, id);
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<FixedAssetPurchaseModel> getAllFixedAssetPurchaseList(
			long officeId) throws Exception {
		List list = null;
		try {
			begin();
			list = getSession()
					.createQuery(
							"SELECT new com.inventory.fixedasset.model.FixedAssetPurchaseModel(id, assetNo)"
									+ " FROM FixedAssetPurchaseModel WHERE office.id = :officeid"
									+ " ORDER BY assetNo")
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
	
	
	public FixedAssetPurchaseDetailsModel getFixedAssetPurchaseDetailsModel(long fixedAssetPurchaseId) throws Exception {
		FixedAssetPurchaseDetailsModel model = null;
		try {
			begin();
			model = (FixedAssetPurchaseDetailsModel) getSession().get(
					FixedAssetPurchaseDetailsModel.class, fixedAssetPurchaseId);
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
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<FixedAssetPurchaseModel> getAllFixedAssetPurchaseDetailList(long officeId, long fixedAssetId) throws Exception {
		List list = null;
		try {
			begin();
			list = getSession()
					.createQuery(
							"SELECT new com.inventory.fixedasset.model.FixedAssetPurchaseModel" +
							"(b.id, CONCAT( b.fixedAsset.name, ' (No : ', a.assetNo, ' Date : ', a.date,')' ))"
									+ " FROM FixedAssetPurchaseModel a JOIN a.fixed_asset_purchase_details_list b" +
									" WHERE a.office.id = :officeid" +
										((fixedAssetId != 0) ? " AND b.fixedAsset.id = "+fixedAssetId : " ")+
										" ORDER BY a.assetNo")				
					.setParameter("officeid", officeId).list();
			commit();

		} catch (Exception e) {
			rollback();
			close();
			throw e;

		} finally {
			flush();
			close();
		}
		return list;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<FixedAssetPurchaseDetailsModel> getAllFixedAssetPurchaseDetailListByFixedAssetId(long officeId, long fixedAssetId) throws Exception {
		List list = null;
		try {
			begin();
			list = getSession()
					.createQuery(
							"SELECT b FROM FixedAssetPurchaseModel a JOIN a.fixed_asset_purchase_details_list b" +
									" WHERE a.office.id = :officeid" +
										((fixedAssetId != 0) ? " AND b.fixedAsset.id = "+fixedAssetId : " ")+
										" ORDER BY a.assetNo")				
					.setParameter("officeid", officeId).list();
			commit();

		} catch (Exception e) {
			rollback();
			close();
			throw e;

		} finally {
			flush();
			close();
		}
		return list;
	}


public FixedAssetPurchaseModel getFixedAssetPurchaseModelByDetailId(long det_id) throws Exception {
	FixedAssetPurchaseModel model = null;
	try {
		begin();
		model = (FixedAssetPurchaseModel) getSession()
				.createQuery(
						"SELECT a"
								+ " FROM FixedAssetPurchaseModel a JOIN a.fixed_asset_purchase_details_list b" +
								" WHERE b.id = :det_id")				
				
				.setParameter("det_id", det_id).uniqueResult();
		commit();

	} catch (Exception e) {
		rollback();
		close();
		throw e;

	} finally {
		flush();
		close();
	}
	return model;
}


public double getFixedAssetPurchaseCurrentBalance(long det_id) throws Exception {
	double currentBal = 0;
	try {
		begin();
		currentBal = (Double) getSession()
				.createQuery(
						"SELECT b.currentBalance"
								+ " FROM FixedAssetPurchaseModel a JOIN a.fixed_asset_purchase_details_list b" +
								" WHERE b.id = :det_id")				
				
				.setParameter("det_id", det_id).uniqueResult();
		commit();

	} catch (Exception e) {
		rollback();
		close();
		throw e;

	} finally {
		flush();
		close();
	}
	return currentBal;
}
}
