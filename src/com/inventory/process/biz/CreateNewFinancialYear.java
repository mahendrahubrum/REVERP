package com.inventory.process.biz;

import java.sql.Date;
import java.util.Calendar;

import com.inventory.process.dao.EndProcessDao;
import com.inventory.process.model.FinancialYearsModel;
import com.vaadin.server.WrappedSession;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SessionUtil;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author Jinshad P.T.
 * 
 *         Sep 25, 2013
 */
public class CreateNewFinancialYear implements EndProcessInterface {

	@Override
	public void process() {
		// TODO Auto-generated method stub
		WrappedSession session =new SessionUtil().getHttpSession();
		try {
			long ofcId=(Long) session.getAttribute("dayend_office_id");
			S_OfficeModel ofc=new OfficeDao().getOffice(ofcId);
			
			Date OldFinEnd=ofc.getFin_end_date();
			Calendar cal=Calendar.getInstance();
			cal.setTime(new java.util.Date(OldFinEnd.getTime()));
			cal.add(Calendar.DAY_OF_MONTH, 1);
			
			FinancialYearsModel fin=new FinancialYearsModel();
			fin.setStart_date(new Date(cal.getTime().getTime()));
			
			cal.add(Calendar.YEAR, 1);
			cal.add(Calendar.DAY_OF_MONTH, -1);
			
			fin.setEnd_date(new Date(cal.getTime().getTime()));
			fin.setOffice_id(ofcId);
			fin.setStatus(1);
			fin.setName(CommonUtil.formatSQLDateToDDMMMYYYY(fin.getStart_date())+" - "+
					CommonUtil.formatSQLDateToDDMMMYYYY(fin.getEnd_date()));
			
			
			new EndProcessDao().createNewFinancialYearAndSetToOffice(fin);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
