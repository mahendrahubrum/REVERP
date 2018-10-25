package com.inventory.process.biz;

import java.sql.Date;

import com.inventory.process.dao.EndProcessDao;
import com.vaadin.server.WrappedSession;
import com.webspark.common.util.SessionUtil;

/**
 * @author Jinshad P.T.
 * 
 *         Sep 26, 2013
 */
public class AddClosingStock implements EndProcessInterface {

	@Override
	public void process() {
		// TODO Auto-generated method stub
		WrappedSession session =new SessionUtil().getHttpSession();
		try {
			
			
			new EndProcessDao().updateClosingStock((Long) session.getAttribute("office_id"), 
					(Date)session.getAttribute("working_date"));
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
