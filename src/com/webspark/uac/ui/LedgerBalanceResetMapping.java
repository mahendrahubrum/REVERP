package com.webspark.uac.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.inventory.config.acct.dao.GroupDao;
import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.LedgerModel;
import com.vaadin.annotations.Theme;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SButton;
import com.webspark.Components.SCheckBox;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SOptionGroup;
import com.webspark.Components.SPanel;
import com.webspark.Components.SparkLogic;
import com.webspark.dao.LedgerBalanceResetMappingDao;
import com.webspark.dao.LoginCreationDao;
import com.webspark.dao.LoginOptionMappingDao;

/**
 * @Author Jinshad P.T.
 */

@SuppressWarnings("serial")
@Theme("testappstheme")
public class LedgerBalanceResetMapping extends SparkLogic {

	long id = 0;

	final SFormLayout content;
	SOptionGroup ledgerOptionGroup;
	SComboField groupCombo;

	SCheckBox checkAll;

	final SButton save = new SButton(getPropertyName("Save"));

	final SHorizontalLayout buttonLayout = new SHorizontalLayout();

	LoginCreationDao lcDao = new LoginCreationDao();
	LoginOptionMappingDao lomDao = new LoginOptionMappingDao();

	LedgerBalanceResetMappingDao dao=new LedgerBalanceResetMappingDao();
	
	
	@SuppressWarnings("rawtypes")
	public LedgerBalanceResetMapping() throws Exception {

		setCaption("Add User Role");
		setWidth("600px");
		setHeight("600px");
		content = new SFormLayout();

		List testList = new GroupDao().getAllGroupsNames(getOrganizationID());

		groupCombo = new SComboField(getPropertyName("ledger_group"), 300, testList, "id","name");
		groupCombo.setInputPrompt(getPropertyName("ledger_group"));

		checkAll = new SCheckBox(null, false);

		ledgerOptionGroup = new SOptionGroup(getPropertyName("ledger"), 300, null, "id", "name", true);
		ledgerOptionGroup.setImmediate(false);

		content.setMargin(true);
		content.setWidth("280px");
		content.setHeight("200px");
		content.addComponent(groupCombo);

		SHorizontalLayout hLay = new SHorizontalLayout();
		hLay.addComponent(new SLabel(getPropertyName("check_all")));
		hLay.addComponent(checkAll);
		hLay.setSpacing(true);
		content.addComponent(hLay);
		save.setClickShortcut(KeyCode.ENTER);
		buttonLayout.addComponent(save);

		content.addComponent(ledgerOptionGroup);
		content.addComponent(buttonLayout);

		content.setSizeUndefined();

		setContent(content);

		checkAll.addValueChangeListener(new Property.ValueChangeListener() {
			@SuppressWarnings({ "unchecked"})
			public void valueChange(ValueChangeEvent event) {
				try {

					if (checkAll.getValue()) {
						Set lst = new HashSet();
						for (Object optId : ledgerOptionGroup.getItemIds()) {
							lst.add(optId);
						}
						ledgerOptionGroup.setValue(lst);
					} 
					else {
						ledgerOptionGroup.setValue(null);
					}

				} catch (Exception e) {
					e.printStackTrace();
					Notification.show(getPropertyName("Error"),
							Type.ERROR_MESSAGE);
				}
			}
		});

		
		save.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				try {
					if (groupCombo.getValue() != null || !groupCombo.getValue().toString().equals("")) {
						long grpId=(Long)groupCombo.getValue();
						Iterator itr=ledgerOptionGroup.getItemIds().iterator();
						List<LedgerModel> ledgerList = new ArrayList<LedgerModel>();
						while (itr.hasNext()) {
							Long id=(Long)itr.next();
							LedgerModel ledger=new LedgerDao().getLedgeer(id);
							if(ledgerOptionGroup.isSelected(id)){
//								ledger.setBalance_reset(true);
							}
							else{
//								ledger.setBalance_reset(false);
							}
							ledgerList.add(ledger);
						}
						dao.updateLedger(ledgerList);
						Notification.show(getPropertyName("save_success"),Type.WARNING_MESSAGE);
						groupCombo.setValue(null);
						groupCombo.setValue(grpId);
					}
				} catch (Exception e) {
					e.printStackTrace();
					Notification.show(getPropertyName("Error"),Type.ERROR_MESSAGE);
				}
			}

		});

		
		groupCombo.addValueChangeListener(new Property.ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				try {
					checkAll.setValue(false);
					if (groupCombo.getValue() != null && !groupCombo.getValue().toString().equals("")) {
						ledgerOptionGroup.removeAllItems();
						List ledgerList = dao.selectLedgersofGroup((Long) groupCombo.getValue(),getOfficeID());
						SCollectionContainer bic=SCollectionContainer.setList(ledgerList, "id");
						ledgerOptionGroup.setContainerDataSource(bic);
						ledgerOptionGroup.setItemCaptionPropertyId("name");
						Iterator itr=ledgerOptionGroup.getItemIds().iterator();
						while (itr.hasNext()) {
							Long id=(Long)itr.next();
//							LedgerModel ledger=new LedgerDao().getLedgeer(id);
//							if(ledger.isBalance_reset())
//								ledgerOptionGroup.select(id);
						}
					}
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
	}

	
	@Override
	public SPanel getGUI() {
		return null;
	}

	
	@Override
	public Boolean isValid() {
		return null;

	}

	
	@Override
	public Boolean getHelp() {
		return null;
	}

}
