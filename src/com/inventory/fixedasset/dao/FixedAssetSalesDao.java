package com.inventory.fixedasset.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.inventory.fixedasset.model.FixedAssetDepreciationModel;
import com.inventory.fixedasset.model.FixedAssetPurchaseDetailsModel;
import com.inventory.fixedasset.model.FixedAssetPurchaseModel;
import com.inventory.fixedasset.model.FixedAssetSalesDetailsModel;
import com.inventory.fixedasset.model.FixedAssetSalesModel;
import com.inventory.transaction.model.TransactionDetailsModel;
import com.inventory.transaction.model.TransactionModel;
import com.webspark.dao.SHibernate;

@SuppressWarnings("serial")
public class FixedAssetSalesDao extends SHibernate implements Serializable {

	public long save(FixedAssetSalesModel assetModel, TransactionModel tranModel)
			throws Exception {
		try {
			begin();
			if (tranModel != null) {
				getSession().save(tranModel);

				assetModel.setTransactionId(tranModel.getTransaction_id());
				updateLedgerBalance(tranModel, true);
			}
			saveFixedAssetDepreciationModel(assetModel);

			updateFixedAssetPurchaseCurrentBalance(assetModel, false);

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

	private void saveFixedAssetDepreciationModel(FixedAssetSalesModel assetModel)
			throws Exception {
		Iterator<FixedAssetSalesDetailsModel> itr = assetModel
				.getFixed_asset_sales_details_list().iterator();
		while (itr.hasNext()) {
			FixedAssetSalesDetailsModel detModel = itr.next();
			if(detModel.getDepreciationId().getId() == 0){
				getSession().save(detModel.getDepreciationId());
			} else{
				getSession().update(detModel.getDepreciationId());
			}
			
		}
		flush();
	}

	public void update(FixedAssetSalesModel model, ArrayList<Long> deletedIds,
			TransactionModel tranModel) throws Exception {
		try {
			begin();
			if (tranModel == null && model.getTransactionId() != 0) {
				tranModel = (TransactionModel) getSession().get(
						TransactionModel.class, model.getTransactionId());
				getSession().delete(tranModel);
				deleteTransactionDetailsModel(tranModel);
				model.setTransactionId(0);
				updateLedgerBalance(tranModel, false);

			} else if (tranModel != null && model.getTransactionId() == 0) {
				getSession().save(tranModel);

				model.setTransactionId(tranModel.getTransaction_id());
				updateLedgerBalance(tranModel, true);
			} else if (tranModel != null) {
				TransactionModel tempModel = (TransactionModel) getSession()
						.get(TransactionModel.class, model.getTransactionId());
				updateLedgerBalance(tempModel, false);

				getSession().clear();
				tranModel.setTransaction_id(model.getTransactionId());
				getSession().update(tranModel);
				updateLedgerBalance(tranModel, true);
				deleteTransactionDetailsModel(tempModel);
			}
			// =============================================================
			FixedAssetSalesModel tempModel = (FixedAssetSalesModel) getSession()
					.get(FixedAssetSalesModel.class, model.getId());
			updateFixedAssetPurchaseCurrentBalance(tempModel, true);		
			
			getSession().clear();
			saveFixedAssetDepreciationModel(model);
			getSession().update(model);
			flush();
			
			updateFixedAssetPurchaseCurrentBalance(model, false);

			if (deletedIds != null) {
				for (Long id : deletedIds) {
					FixedAssetSalesDetailsModel childModel = (FixedAssetSalesDetailsModel) getSession()
							.get(FixedAssetSalesDetailsModel.class, id);
					getSession().delete(childModel);
					getSession().delete(childModel.getDepreciationId());
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

	private void updateFixedAssetPurchaseCurrentBalance(
			FixedAssetSalesModel model, boolean isAdd) throws Exception {
		Iterator<FixedAssetSalesDetailsModel> itr = model
				.getFixed_asset_sales_details_list().iterator();
		while (itr.hasNext()) {
			FixedAssetSalesDetailsModel detModel = itr.next();
			if (isAdd) {
				getSession()
				.createQuery(
						"UPDATE FixedAssetPurchaseDetailsModel"
								+ " SET currentBalance = currentBalance + :bal"
								+ " WHERE id = :id")
				.setDouble("bal", detModel.getSalesQuantity())
				.setLong(
						"id",
						detModel.getDepreciationId()
								.getFixedAssetPurchaseDetailsId()
								.getId()).executeUpdate();
			} else {
				getSession()
						.createQuery(
								"UPDATE FixedAssetPurchaseDetailsModel"
										+ " SET currentBalance = currentBalance - :bal"
										+ " WHERE id = :id")
						.setDouble("bal", detModel.getSalesQuantity())
						.setLong(
								"id",
								detModel.getDepreciationId()
										.getFixedAssetPurchaseDetailsId()
										.getId()).executeUpdate();
			}

			flush();
		}

	}

	private void deleteTransactionDetailsModel(TransactionModel tranModel)
			throws Exception {
		Iterator<TransactionDetailsModel> itr = tranModel
				.getTransaction_details_list().iterator();
		while (itr.hasNext()) {
			TransactionDetailsModel model = itr.next();
			getSession().delete(model);
			flush();
		}

	}

	private void updateLedgerBalance(TransactionModel tranModel, boolean newTran)
			throws Exception {
		Iterator<TransactionDetailsModel> aciter = tranModel
				.getTransaction_details_list().iterator();
		TransactionDetailsModel tdm;

		while (aciter.hasNext()) {
			tdm = aciter.next();
			if (newTran) {
				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tdm.getAmount())
						.setLong("id", tdm.getToAcct().getId()).executeUpdate();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tdm.getAmount())
						.setLong("id", tdm.getFromAcct().getId())
						.executeUpdate();
			} else {
				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance-:amt where id=:id")
						.setDouble("amt", tdm.getAmount())
						.setLong("id", tdm.getFromAcct().getId())
						.executeUpdate();

				getSession()
						.createQuery(
								"update LedgerModel set current_balance=current_balance+:amt where id=:id")
						.setDouble("amt", tdm.getAmount())
						.setLong("id", tdm.getToAcct().getId()).executeUpdate();
			}

			flush();

		}
	}

	public void delete(long id) throws Exception {

		try {
			begin();
			FixedAssetSalesModel model = (FixedAssetSalesModel) getSession()
					.get(FixedAssetSalesModel.class, id);
			getSession().delete(model);
			updateFixedAssetPurchaseCurrentBalance(model, true);
			Iterator<FixedAssetSalesDetailsModel> itr = model.getFixed_asset_sales_details_list().iterator();
			while(itr.hasNext()){
				FixedAssetDepreciationModel m = itr.next().getDepreciationId();
				getSession().delete(m);
			}
			
			

			if (model.getTransactionId() != 0) {

				TransactionModel tempModel = (TransactionModel) getSession()
						.get(TransactionModel.class, model.getTransactionId());
				updateLedgerBalance(tempModel, false);
				getSession().delete(tempModel);
			//	deleteTransactionDetailsModel(tempModel);
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

	public FixedAssetSalesModel getFixedAssetSalesModel(long id)
			throws Exception {
		FixedAssetSalesModel model = null;
		try {
			begin();
			model = (FixedAssetSalesModel) getSession().get(
					FixedAssetSalesModel.class, id);
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
	public List<FixedAssetSalesModel> getAllFixedAssetSalesList(long officeId)
			throws Exception {
		List list = null;
		try {
			begin();
			list = getSession()
					.createQuery(
							"SELECT new com.inventory.fixedasset.model.FixedAssetSalesModel(id, assetSalesNo)"
									+ " FROM FixedAssetSalesModel WHERE office.id = :officeid"
									+ " ORDER BY assetSalesNo")
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

	public FixedAssetSalesDetailsModel getFixedAssetSalesDetailsModel(
			long fixedAssetPurchaseId) throws Exception {
		FixedAssetSalesDetailsModel model = null;
		try {
			begin();
			model = (FixedAssetSalesDetailsModel) getSession().get(
					FixedAssetSalesDetailsModel.class, fixedAssetPurchaseId);
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
	public List<FixedAssetSalesModel> getAllFixedAssetSalesDetailList(
			long officeId, long fixedAssetId) throws Exception {
		List list = null;
		try {
			begin();
			list = getSession()
					.createQuery(
							"SELECT new com.inventory.fixedasset.model.FixedAssetPurchaseModel"
									+ "(b.id, CONCAT( b.fixedAsset.name, ' (No : ', a.assetNo, ' Date : ', a.date,')' ))"
									+ " FROM FixedAssetPurchaseModel a JOIN a.fixed_asset_purchase_details_list b"
									+ " WHERE a.office.id = :officeid"
									+ ((fixedAssetId != 0) ? " AND b.fixedAsset.id = "
											+ fixedAssetId
											: " ") + " ORDER BY a.assetNo")
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

	public double getFixedAssetTotalSalesQty(long officeId, long id)
			throws Exception {
		double quantity = 0;
		try {
			begin();
			quantity = (Double) getSession()
					.createQuery(
							"SELECT COALESCE(SUM(b.salesQuantity),0)"
									+ " FROM FixedAssetSalesModel a JOIN a.fixed_asset_sales_details_list b"
									+ " WHERE a.office.id = :officeId"
									+ ((id != 0) ? " AND a.id = " + id : " "))
					.setParameter("officeId", officeId).uniqueResult();
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
}
