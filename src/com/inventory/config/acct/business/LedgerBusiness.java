package com.inventory.config.acct.business;

import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.LedgerModel;

/*
 * 
 * the business logic layer in between UI and DAO 
 */

public class LedgerBusiness {

	LedgerDao ledgerDao = new LedgerDao();

	public long save(List ledgerModels, Long officeId) throws Exception {

		LedgerModel ledgerModel = null;
		Iterator ofcList = ledgerModels.iterator();
		long saved = 0;
		while (ofcList.hasNext()) {
			boolean ledgerNamePresent = false;
			ledgerModel = (LedgerModel) ofcList.next();
			List<String> ledgerNames = ledgerDao.getAllLedgerNames(ledgerModel.getOffice().getId());
			for (String ledgerName : ledgerNames) {
				if (ledgerModel.getName().equalsIgnoreCase(ledgerName)) {
					ledgerNamePresent = true;
					break;
				}
			}

			if (!ledgerNamePresent) {
				saved = ledgerDao.save(ledgerModel);
			}

		}
		if (ledgerModel != null && saved != 0) {
			ledgerDao.updateGroupModel(ledgerModel.getGroup().getId());
		}
		return saved;
	}

}
