package com.inventory.config.stock.ui;

import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.webspark.Components.SPanel;
import com.webspark.Components.STabSheet;
import com.webspark.Components.SparkLogic;

public class ItemManagementUI extends SparkLogic {

	private static final long serialVersionUID = 6694186579762258544L;

	long id;

	SPanel pannel;
	SPanel itemDepartmentPanel;
	SPanel itemGroupPanel;
	SPanel itemSubGroupPanel;
	SPanel itemPanel;

	STabSheet tab;

	@SuppressWarnings("deprecation")
	@Override
	public SPanel getGUI() {

		setSize(980, 640);
		pannel = new SPanel();

		tab = new STabSheet(null);

		itemPanel = new ItemPanel();
		itemSubGroupPanel = new ItemSubGroupPanel();
		itemGroupPanel = new ItemGroupPanel();
		itemDepartmentPanel = new ItemDepartmentPanel();

		tab.addTab(itemPanel, getPropertyName("item"));
		tab.addTab(itemSubGroupPanel, getPropertyName("item_sub_group"));
		tab.addTab(itemGroupPanel, getPropertyName("item_group"));
		tab.addTab(itemDepartmentPanel, getPropertyName("item_department"));

		pannel.setContent(tab);

		tab.addSelectedTabChangeListener(new TabSheet.SelectedTabChangeListener() {
			@Override
			public void selectedTabChange(SelectedTabChangeEvent event) {
				center();

				if (tab.getSelectedTab().getId().equals("Item Group")) {
					((ItemGroupPanel) itemGroupPanel).reloadDepartments();
					setWidth(itemGroupPanel.getWidth() + "");
					setHeight(itemGroupPanel.getHeight() + 100 + "");

				} else if (tab.getSelectedTab().getId().equals("Item")) {
					((ItemPanel) itemPanel).reloadGroup();
					setWidth(itemPanel.getWidth() + "");
					setHeight(itemPanel.getHeight() + 100 + "");
				} else if (tab.getSelectedTab().getId()
						.equals("Item Department")) {
					SPanel pan = new ItemDepartmentPanel();
					itemDepartmentPanel.setContent(pan.getContent());
					setWidth(itemDepartmentPanel.getWidth() + "");
					setHeight(itemDepartmentPanel.getHeight() + 100 + "");
				} else {
					((ItemSubGroupPanel) itemSubGroupPanel).reloadGroup();
					setWidth(itemSubGroupPanel.getWidth() + "");
					setHeight(itemSubGroupPanel.getHeight() + 100 + "");
				}
			}
		});

		return pannel;

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
