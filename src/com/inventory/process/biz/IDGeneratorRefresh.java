package com.inventory.process.biz;

import java.sql.Date;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import com.inventory.process.dao.EndProcessDao;
import com.vaadin.server.WrappedSession;
import com.webspark.common.util.SConstants;
import com.webspark.common.util.SessionUtil;
import com.webspark.dao.IDGeneratorSettingsDao;
import com.webspark.model.S_IDGeneratorSettingsModel;

/**
 * @author Jinshad P.T.
 * 
 *         Sep 26, 2013
 */
public class IDGeneratorRefresh implements EndProcessInterface {

	@Override
	public void process() {
		// TODO Auto-generated method stub
		WrappedSession session =new SessionUtil().getHttpSession();
		try {
			
			boolean isMonthEnd=false, isYearEnd=false;
			
			Date working_date=(Date) session.getAttribute("working_date");
			
			if(working_date.toString().equals(((Date)session.getAttribute("fin_end")).toString())) {
    			isYearEnd=true;
    		}
			
			Calendar cal=Calendar.getInstance();
			cal.setTime(new java.util.Date(working_date.getTime()));
			cal.add(Calendar.MONTH, 1);  
	        cal.set(Calendar.DAY_OF_MONTH, 1);  
	        cal.add(Calendar.DATE, -1);  
			
			Date monthEnd=new Date(cal.getTime().getTime());
			if(working_date.toString().equals(monthEnd.toString())) {
				isMonthEnd=true;
    		}
			
			EndProcessDao endPrcDao=new EndProcessDao();
			
			List idsList=new IDGeneratorSettingsDao().getAllIDGenerators();
			
			S_IDGeneratorSettingsModel idGen;
			Iterator it=idsList.iterator();
			while(it.hasNext()) {
				idGen=(S_IDGeneratorSettingsModel) it.next();
				
				if(idGen.getReset_mode()==SConstants.DAILY_REPEAT) {
					endPrcDao.refreshIDGenerator(idGen);
				}
				else if(idGen.getReset_mode()==SConstants.MONTHLY_REPEAT) {
					if(isMonthEnd)
						endPrcDao.refreshIDGenerator(idGen);
				}
				else if(idGen.getReset_mode()==SConstants.YEARLY_REPEAT) {
					if(isYearEnd)
						endPrcDao.refreshIDGenerator(idGen);
				}
				
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
