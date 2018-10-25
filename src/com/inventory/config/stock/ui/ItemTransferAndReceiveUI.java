package com.inventory.config.stock.ui;

import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import com.webspark.Components.SPanel;
import com.webspark.Components.STabSheet;
import com.webspark.Components.SparkLogic;

public class ItemTransferAndReceiveUI extends SparkLogic {

	private static final long serialVersionUID = -2988522936812131457L;

	long id;

	SPanel pannel;

	SPanel receivePanel;
	SPanel itemTranPanel;

	STabSheet tab;

	@SuppressWarnings("deprecation")
	@Override
	public SPanel getGUI() {

		setSize(820, 580);
		pannel = new SPanel();

		tab = new STabSheet(null);

		itemTranPanel = new ItemTransferPanel();
		receivePanel = new ItemRecievePanel();

		tab.addTab(itemTranPanel, getPropertyName("item_transfer"));
		tab.addTab(receivePanel, getPropertyName("item_receive"));
		

		pannel.setContent(tab);

		tab.addSelectedTabChangeListener(new SelectedTabChangeListener() {
			@Override
			public void selectedTabChange(SelectedTabChangeEvent event) {
				if (tab.getSelectedTab().getId().equals("Receiver")) {

					((ItemRecievePanel) receivePanel).reloadStockTransfer();

					// tab.setHeight("684");
					setHeight("500");
					// setWidth("520");
				} else if (tab.getSelectedTab().getId().equals("Transfer")) {

					((ItemTransferPanel) itemTranPanel).reloadStock();
					// tab.setHeight("354");
					setHeight("580");
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
