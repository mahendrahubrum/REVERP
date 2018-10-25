package com.inventory.process.biz;

import java.util.Iterator;
import java.util.List;

import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.model.ItemModel;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPopupView;
import com.webspark.Components.STable;
import com.webspark.Components.SWindow;
import com.webspark.Components.SparkLogic;
import com.webspark.ui.MainLayout;

public class PopUpAlert extends SparkLogic {
	
	
		public PopUpAlert(long office_id) {
			// TODO Auto-generated method stub
			
				try {
				
					STable table = new STable("");

					/* Define the names and data types of columns.
					 * The "default value" parameter is meaningless here. */
					
					table.addContainerProperty("Item Name",  String.class, null, "Item Name", null,
							Align.LEFT);
					table.addContainerProperty("Credit Limit", Double.class, null, "Credit Limit", null,
							Align.CENTER);
					table.addContainerProperty("Current Balance",Double.class, null, "Current Balance", null,
							Align.RIGHT);
					
					table.setVisibleColumns(new String[]{"Item Name", "Credit Limit", "Current Balance"});
					
					List list = new ItemDao().getItemsUnderReorderLevel(office_id);
					
					
					Iterator it=list.iterator();
					int i=0;
					ItemModel objIn;
					while (it.hasNext()) {
						objIn=(ItemModel) it.next();
						i++;
						table.addItem(new Object[] {
								objIn.getName(),objIn.getReorder_level(), objIn.getCurrent_balalnce()},i);
					}

					table.setColumnExpandRatio("Current Balance", (float) 0.3);
					
					table.setWidth("300");
					table.setHeight("300");
					
					table.setSelectable(true);
					SWindow w=new SWindow();
					SPopupView pop=new SPopupView("", table);
					
					w.setWidth("500");
					w.setHeight("500");
					
					w.setContent(pop);
					
//					w.setContent(this);
					MainLayout lay=(MainLayout) getUI().getCurrent().getContent();
					lay.PopUpAlert(office_id);
					
					pop.setPopupVisible(true);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Notification.show("Error..!!",
							"Error Message :" + e.getCause(),
							Type.ERROR_MESSAGE);
				}
			/*}
			else {
				SPopupView pop=new SPopupView("", new SLabel("Select a Item and Unit"));
				popupHor.addComponent(pop);
				pop.setPopupVisible(true);
			}*/
		}

		@Override
		public SPanel getGUI() {
			// TODO Auto-generated method stub
			return null;
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
