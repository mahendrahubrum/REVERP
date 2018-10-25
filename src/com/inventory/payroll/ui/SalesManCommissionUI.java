package com.inventory.payroll.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.inventory.payroll.dao.SalesManCommissionMapDao;
import com.inventory.payroll.model.SalesManCommissionMapModel;
import com.vaadin.annotations.Theme;
import com.vaadin.data.Item;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SButton;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SPanel;
import com.webspark.Components.STable;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.uac.dao.UserManagementDao;
import com.webspark.uac.model.UserModel;

/**
 * @author sangeeth
 * @date 25-Nov-2015
 * @Project REVERP
 */

@SuppressWarnings("serial")
@Theme("testappstheme")
public class SalesManCommissionUI extends SparkLogic {
	
	SPanel mainPanel;

	SVerticalLayout mainLayout;
	private STable table;
	SButton saveButton;
	SHorizontalLayout buttonLayout;

	private static final String TBL_NO = "#";
	private static final String TBL_ID = "ID";
	private static final String TBL_UID = "UID";
	private static final String TBL_USER = "Sales Man";
	private static final String TBL_COMMISSION = "Commission %";
	
	SalesManCommissionMapDao dao;
	private Object[] allHeaders;
	private Object[] visibleHeaders;
	
	@Override
	public SPanel getGUI() {
		mainPanel=new SPanel();
		mainPanel.setSizeFull();
		mainLayout = new SVerticalLayout();
		mainLayout.setSpacing(true);
		mainLayout.setMargin(true);
		setSize(730, 575);
		dao=new SalesManCommissionMapDao();
		allHeaders=new Object[]{TBL_NO, TBL_ID, TBL_UID, TBL_USER, TBL_COMMISSION};
		visibleHeaders=new Object[]{TBL_NO, TBL_USER, TBL_COMMISSION};
		try {
			table = new STable(null, 600, 350);
			table.setSelectable(false);
			table.addContainerProperty(TBL_NO, Integer.class, null, TBL_NO,null, Align.CENTER);
			table.addContainerProperty(TBL_ID, Long.class, null,TBL_ID, null, Align.CENTER);
			table.addContainerProperty(TBL_UID, Long.class, null,TBL_UID, null, Align.CENTER);
			table.addContainerProperty(TBL_USER, String.class, null,getPropertyName("sales_man"), null, Align.LEFT);
			table.addContainerProperty(TBL_COMMISSION, STextField.class, null,getPropertyName("commission_percentage"), null, Align.RIGHT);
			table.setColumnExpandRatio(TBL_NO, 0.5f);
			table.setColumnExpandRatio(TBL_USER, 2f);
			table.setColumnExpandRatio(TBL_COMMISSION, 2.5f);
			table.setVisibleColumns(visibleHeaders);
			
			buttonLayout = new SHorizontalLayout();
			saveButton = new SButton(getPropertyName("save"), 70);
			saveButton.setStyleName("savebtnStyle");
			saveButton.setIcon(new ThemeResource("icons/saveSideIcon.png"));
			
			mainLayout.addComponent(table);
			buttonLayout.addComponent(saveButton);
			mainLayout.addComponent(buttonLayout);
			mainLayout.setComponentAlignment(buttonLayout, Alignment.MIDDLE_CENTER);
			mainLayout.setSizeUndefined();
			mainPanel.setContent(mainLayout);
			
			loadTable();
			
			saveButton.addClickListener(new ClickListener() {
				
				@SuppressWarnings("rawtypes")
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						if(isValid()){
							List<SalesManCommissionMapModel> list=new ArrayList<SalesManCommissionMapModel>();
							Iterator itr=table.getItemIds().iterator();
							STextField field;
							SalesManCommissionMapModel mapMdl=null;
							while (itr.hasNext()) {
								Item item=table.getItem(itr.next());
								long id=(Long)item.getItemProperty(TBL_ID).getValue();
								double commission=0;
								field=(STextField)item.getItemProperty(TBL_COMMISSION).getValue();
								try {
									commission=toDouble(field.getValue().toString().trim());
								} catch (Exception e) {
									commission=0;
								}
								mapMdl=dao.getSalesManCommissionMapModel(id);
								if(mapMdl==null)
									mapMdl=new SalesManCommissionMapModel();
								
								mapMdl.setUserId((Long)item.getItemProperty(TBL_UID).getValue());
								mapMdl.setCommissionPercentage(roundNumber(commission));
								mapMdl.setOfficeId(getOfficeID());
								list.add(mapMdl);
							}
							if(list.size()>0){
								dao.update(list);
								Notification.show(getPropertyName("update_success"),Type.WARNING_MESSAGE);
								loadTable();
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						Notification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
					}
				}
			});
			
			addShortcutListener(new ShortcutListener("Save", ShortcutAction.KeyCode.ENTER, null) {
				@Override
				public void handleAction(Object sender, Object target) {

					if (saveButton.isVisible())
						saveButton.click();
				}
			});
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return mainPanel;
	}
	
	
	@SuppressWarnings("rawtypes")
	public void loadTable(){
		try{
			table.removeAllItems();
			table.setVisibleColumns(allHeaders);
			List list=new ArrayList();
			list=new UserManagementDao().getUsersFromOffice(getOfficeID());
			Iterator itr=list.iterator();
			while (itr.hasNext()) {
				UserModel user = (UserModel) itr.next();
				double commisison=0;
				long id=0;
				STextField field=new STextField(null, 265);
				field.setStyleName("textfield_align_right");
				
				SalesManCommissionMapModel mapMdl=null;
				try {
					mapMdl=dao.getSalesManCommissionMapModel(user.getId(), getOfficeID());
				} catch (Exception e) {
					e.printStackTrace();
				}
				if(mapMdl!=null){
					commisison=roundNumber(mapMdl.getCommissionPercentage());
					id=mapMdl.getId();
				}
				
				field.setValue(roundNumber(commisison)+"");
				table.addItem(new Object[]{ table.getItemIds().size()+1,
											id,
											user.getId(),
											user.getFirst_name()+" "+user.getMiddle_name()+" "+user.getLast_name(),
											field},table.getItemIds().size()+1);
			}
			table.setVisibleColumns(visibleHeaders);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	
	@Override
	public Boolean isValid() {
		boolean flag=true;
		
		return flag;
	}
	
	
	@Override
	public Boolean getHelp() {
		return null;
	}

}
