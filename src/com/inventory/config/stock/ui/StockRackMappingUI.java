package com.inventory.config.stock.ui;

import com.webspark.Components.SPanel;
import com.webspark.Components.SparkLogic;

/**
 * @author Jinshad P.T.
 *
 * Jul 11, 2013
 */
public class StockRackMappingUI extends SparkLogic {

	SPanel pannel=null;
	
	@Override
	public SPanel getGUI() {
		// TODO Auto-generated method stub
		
		pannel=new StockRackMappingPannel(0);
		
		return pannel;
	}

	@Override
	public Boolean isValid() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

}
