package com.inventory.config.stock.ui;

import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.webspark.Components.SPanel;
import com.webspark.Components.STabSheet;
import com.webspark.Components.SparkLogic;

public class StockTransferAndReceiveUI extends SparkLogic {

	long id;

	SPanel pannel;

	SPanel transferPanel;
	SPanel receivePanel;

	STabSheet tab;

	@SuppressWarnings("deprecation")
	@Override
	public SPanel getGUI() {

		setSize(1000, 600);
		pannel = new SPanel();

		tab = new STabSheet(null, 1000, 600);

		transferPanel = new StockTransferPanel();
		receivePanel = new StockRecievePanel();

		tab.addTab(transferPanel, getPropertyName("stock_transfer"));
	//	tab.addTab(receivePanel, getPropertyName("stock_receive"));

		pannel.setContent(tab);

		tab.addListener(new TabSheet.SelectedTabChangeListener() {
			@Override
			public void selectedTabChange(SelectedTabChangeEvent event) {
				if (tab.getSelectedTab().getId().equals("Receiver")) {

					((StockRecievePanel) receivePanel).reloadStockTransfer();

					// tab.setHeight("684");
					// setHeight("500");
					// setWidth("520");
				} else {

					// tab.setHeight("354");
					// setHeight("410");
					// setWidth("506");
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
