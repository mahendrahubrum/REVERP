package com.inventory.ui;

import java.util.Iterator;
import java.util.List;

import com.inventory.dao.RackDao;
import com.inventory.model.RackModel;
import com.inventory.purchase.model.StockRackMappingModel;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPopupView;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;

/**
 * 
 * @author anil
 * 
 */
public class RackArrangementUI extends SparkLogic {

	private static final long serialVersionUID = -7181810591940980172L;
	private RackDao rackDao;
	private SPopupView popupView;
	private SFormLayout popLay;

	@Override
	public SPanel getGUI() {

		setSize(1000, 600);

		SPanel panel = new SPanel();

		rackDao = new RackDao();

		popLay = new SFormLayout();
		popupView = new SPopupView(null, popLay);

		SHorizontalLayout horizontalLayout = new SHorizontalLayout();
		panel.setContent(horizontalLayout);
		horizontalLayout.setMargin(true);

		try {

			LayoutClickListener clickListener = new LayoutClickListener() {

				@Override
				public void layoutClick(LayoutClickEvent event) {

					if (event.getChildComponent() != null) {
						loadDetails(event.getChildComponent().getId());
						popupView.setPopupVisible(true);
					}
				}
			};

			List list = rackDao.getAllRacksUnderOffice(getOfficeID());
			RackModel rack;
			SGridLayout grid = new SGridLayout(7, 50);
			grid.setSpacing(true);
			SVerticalLayout vlaLayout;
			SLabel label;

			double qty = 0;

			Iterator iter = list.iterator();
			while (iter.hasNext()) {
				qty = 0;
				rack = (RackModel) iter.next();

				qty = rackDao.getTotalQuantityFromRack(rack.getId());

				label = new SLabel(getPropertyName("total") + qty);

				vlaLayout = new SVerticalLayout();
				vlaLayout.setCaption("Rack : " + rack.getRack_number());
				if (qty == 0) {
					vlaLayout.setStyleName("grid_bordered_green");
				} else {
					vlaLayout.setStyleName("grid_bordered_red");
				}

				vlaLayout.addComponent(label);
				vlaLayout.setId("" + rack.getId());
				grid.addLayoutClickListener(clickListener);
				grid.addComponent(vlaLayout);

			}
			horizontalLayout.addComponent(popupView);
			horizontalLayout.addComponent(grid);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return panel;
	}

	protected void loadDetails(String rackId) {
		try {
			long id = toLong(rackId);
			popLay.removeAllComponents();

			List itemList = null;
			Iterator itemIter = null;

			StockRackMappingModel stkMdl;

			itemList = rackDao.getItemsInRack(id);
			itemIter = itemList.iterator();
			SLabel label;

			while (itemIter.hasNext()) {
				label = new SLabel();
				stkMdl = (StockRackMappingModel) itemIter.next();

				label.setValue("Item : "
						+ stkMdl.getStock().getItem().getName() + " Qty : "
						+ stkMdl.getQuantity());
				popLay.addComponent(label);
			}

		} catch (Exception e) {

		}

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
