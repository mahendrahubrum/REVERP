package com.inventory.process.biz;

import com.inventory.process.dao.EndProcessDao;
import com.vaadin.server.WrappedSession;
import com.webspark.common.util.SessionUtil;

/**
 * @author Jinshad P.T.
 * 
 *         Sep 25, 2013
 */
public class ChangeOfficeWorkingDate implements EndProcessInterface {

	@Override
	public void process() {
		// TODO Auto-generated method stub
		WrappedSession session =new SessionUtil().getHttpSession();
		try {
			
			
			new EndProcessDao().changeOfficeWorkingDate((Long) session.getAttribute("dayend_office_id"));
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
