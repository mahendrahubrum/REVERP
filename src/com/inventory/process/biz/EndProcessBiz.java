package com.inventory.process.biz;

import java.util.Iterator;
import java.util.List;

import com.inventory.process.dao.EndProcessDao;
import com.inventory.process.model.EndProcessModel;
import com.webspark.common.util.SConstants;
import com.webspark.core.SReflection;

/**
 * @author Jinshad P.T.
 * 
 *         Sep 25, 2013
 */
public class EndProcessBiz {
	
	
	EndProcessDao objDao=new EndProcessDao();
	
	
	public void doEndProcess(boolean isYearEnd) {
		
		try {
			SReflection reflection;
			
			List processList=objDao.getProcesses(SConstants.DAY_END);
			
			EndProcessModel process;
			EndProcessInterface end;
			Iterator it=processList.iterator();
			while(it.hasNext()) {
				
				process=(EndProcessModel) it.next();
				
				reflection=new SReflection();
				
				end=(EndProcessInterface) reflection.getClassInstance(process.getClass_name());
				
				end.process();
				
			}
			
			
			processList=null;
			if(isYearEnd) {
				
				processList=objDao.getProcesses(SConstants.YEAR_END);
				
				Iterator it1=processList.iterator();
				while(it1.hasNext()) {
					
					process=(EndProcessModel) it1.next();
					
					reflection=new SReflection();
					
					end=(EndProcessInterface) reflection.getClassInstance(process.getClass_name());
					
					end.process();
					
				}
			}
			
			
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
		
	}
	
	
	

}
