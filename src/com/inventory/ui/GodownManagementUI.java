package com.inventory.ui;

import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.webspark.Components.SPanel;
import com.webspark.Components.STabSheet;
import com.webspark.Components.SparkLogic;

public class GodownManagementUI extends SparkLogic {

	long id;

	SPanel pannel;

	SPanel rackPanel;
	SPanel roomPanel;
	SPanel buildingPanel;

	STabSheet tab;

	@SuppressWarnings("deprecation")
	@Override
	public SPanel getGUI() {

		setSize(486, 451);
		pannel = new SPanel();

		tab = new STabSheet(null, 482, 394);

		rackPanel = new RackPanel();
		roomPanel = new RoomPanel();
		buildingPanel = new BuildingPanel();

		tab.addTab(rackPanel, getPropertyName("rack"));
		tab.addTab(roomPanel, getPropertyName("room"));
		tab.addTab(buildingPanel, getPropertyName("building"));

		pannel.setContent(tab);

		tab.addSelectedTabChangeListener(new TabSheet.SelectedTabChangeListener() {
			@Override
			public void selectedTabChange(SelectedTabChangeEvent event) {
				if (tab.getSelectedTab().getId().equals("Rack")) {
					rackPanel.setContent(new RackPanel().getContent());
				} else if (tab.getSelectedTab().getId().equals("Room")) {

					((RoomPanel) roomPanel).reloadBuilding();

				} else {
					((BuildingPanel) buildingPanel).loadOptions(0);

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
		// TODO Auto-generated method stub
		return null;
	}

}
