package com.inventory.config.stock.ui;

import java.util.List;
import java.util.Set;

import org.vaadin.data.collectioncontainer.CollectionContainer;

import com.inventory.config.stock.dao.StockMergeDao;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SButton;
import com.webspark.Components.SComboField;
import com.webspark.Components.SListSelect;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;

/**
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Apr 8, 2014
 */
public class StockMergeUI extends SparkLogic {

	private static final long serialVersionUID = -2907370435266653413L;

	private SComboField stockComboField;
	private SListSelect oldStockSelect;

	private SButton mergeButton;

	private StockMergeDao dao;

	@Override
	public SPanel getGUI() {

		setSize(500, 500);

		SPanel pan = new SPanel();
		pan.setSizeFull();

		SVerticalLayout mainLayout = new SVerticalLayout();
		mainLayout.setSpacing(true);
		mainLayout.setMargin(true);

		pan.setContent(mainLayout);

		try {

			dao = new StockMergeDao();

			stockComboField = new SComboField(getPropertyName("master_stock"),
					400, dao.getAllStocks(getOfficeID()), "id", "details",
					true, "Select");

			oldStockSelect = new SListSelect(
					getPropertyName("stocks_to_be_merged"));
			oldStockSelect.setWidth("400px");
			oldStockSelect.setHeight("300px");
			oldStockSelect.setImmediate(true);
			oldStockSelect.setMultiSelect(true);

			mergeButton = new SButton(getPropertyName("merge"));

			mainLayout.addComponent(stockComboField);
			mainLayout.addComponent(oldStockSelect);
			mainLayout.addComponent(mergeButton);

			stockComboField.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent arg0) {
					try {
						if (stockComboField.getValue() != null
								&& !stockComboField.getValue().equals("")) {
							List lst = dao.getAllStocksExceptSelected(
									getOfficeID(),
									(Long) stockComboField.getValue());
							CollectionContainer bic = CollectionContainer
									.fromBeans(lst, "id");
							oldStockSelect.setContainerDataSource(bic);
							oldStockSelect.setItemCaptionPropertyId("details");

						}

					} catch (Exception e) {
					}
				}
			});

			mergeButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent arg0) {

					if (isValid()) {
						try {
							dao.mergeStocks((Long) stockComboField.getValue(),
									(Set) oldStockSelect.getValue());
							SNotification.show(
									getPropertyName("merged_successfully"),
									Type.WARNING_MESSAGE);
							reloadStocks((Long) stockComboField.getValue());
						} catch (Exception e) {
							e.printStackTrace();
							SNotification.show(
									getPropertyName("unable_to_merge"),
									Type.ERROR_MESSAGE);
						}

					}
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}

		return pan;
	}

	protected void reloadStocks(Long stockId) {

		try {
			List lst = dao.getAllStocks(getOfficeID());
			CollectionContainer bic = CollectionContainer.fromBeans(lst, "id");
			stockComboField.setContainerDataSource(bic);
			stockComboField.setItemCaptionPropertyId("details");

			stockComboField.setValue(stockId);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Boolean isValid() {

		oldStockSelect.setComponentError(null);
		stockComboField.setComponentError(null);

		boolean valid = true;
		if (((Set) oldStockSelect.getValue()).size() <= 0) {
			valid = false;
			setRequiredError(oldStockSelect,
					getPropertyName("invalid_selection"), true);
		}
		if (stockComboField.getValue() == null
				|| stockComboField.getValue().equals("")) {
			valid = false;
			setRequiredError(stockComboField,
					getPropertyName("invalid_selection"), true);
		}
		return valid;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

}
