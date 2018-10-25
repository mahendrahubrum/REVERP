package com.inventory.config.stock.ui;

import java.util.List;

import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.config.tax.dao.StockRackMappingDao;
import com.inventory.dao.RackDao;
import com.inventory.purchase.model.StockRackMappingModel;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SPanel;
import com.webspark.Components.STextField;
import com.webspark.Components.SparkLogic;

/**
 * @author Jinshad P.T.
 * 
 *         Jun 26, 2014
 */
public class RackToRackTransferUI extends SparkLogic {

	private static final long serialVersionUID = -5835327703018639924L;

	private SComboField fromRackComboField;
	private SComboField toRackComboField;
	private SComboField stocksComboField;
	private STextField qtyTextField;

	private SPanel mainPanel;
	private SHorizontalLayout buttonLayout;
	private SFormLayout formLayout;

	private SButton mergeButton;

	private WrappedSession session;
	private SettingsValuePojo settings;

	double balance_qty = 0;

	@Override
	public SPanel getGUI() {

		setSize(350, 350);

		mainPanel = new SPanel();
		mainPanel.setSizeFull();

		try {

			formLayout = new SFormLayout();
			formLayout.setMargin(true);

			buttonLayout = new SHorizontalLayout();
			buttonLayout.setSpacing(true);

			session = getHttpSession();
			if (session.getAttribute("settings") != null)
				settings = (SettingsValuePojo) session.getAttribute("settings");

			List racks = new RackDao().getAllRacksUnderOffice(getOfficeID());

			fromRackComboField = new SComboField(getPropertyName("from_rack"),
					200, racks, "id", "rack_number");

			stocksComboField = new SComboField(
					getPropertyName("stock_rack_maps"), 200);

			toRackComboField = new SComboField(getPropertyName("to_rack"), 200,
					racks, "id", "rack_number");

			qtyTextField = new STextField(getPropertyName("quantity"), 200);

			formLayout.addComponent(fromRackComboField);
			formLayout.addComponent(stocksComboField);
			formLayout.addComponent(toRackComboField);
			formLayout.addComponent(qtyTextField);

			mergeButton = new SButton("Transfer");
			mergeButton.setClickShortcut(KeyCode.ENTER);
			buttonLayout.addComponent(mergeButton);
			formLayout.addComponent(buttonLayout);

			fromRackComboField
					.addValueChangeListener(new ValueChangeListener() {
						@Override
						public void valueChange(ValueChangeEvent event) {
							loadStocks();
						}
					});

			stocksComboField.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						StockRackMappingModel obj = new StockRackMappingDao()
								.getStockRackMap((Long) stocksComboField
										.getValue());
						qtyTextField.setValue(asString(obj.getBalance()));
						balance_qty = obj.getBalance();
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			});

			mergeButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					if (isValid()) {
						try {

							new StockRackMappingDao().transferStocks(
									(Long) stocksComboField.getValue(),
									(Long) toRackComboField.getValue(),
									balance_qty,
									toDouble(qtyTextField.getValue()));

							Notification.show(getPropertyName("save_success"),
									Type.WARNING_MESSAGE);

							Object obj = fromRackComboField.getValue();
							fromRackComboField.setValue(null);
							fromRackComboField.setValue(obj);
							qtyTextField.setValue("0");

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Notification.show(getPropertyName("error"),
									Type.ERROR_MESSAGE);
						}
					}
				}
			});

		} catch (Exception e) {
			// TODO: handle exception
		}

		mainPanel.setContent(formLayout);

		return mainPanel;
	}

	private void loadStocks() {

		try {

			if (fromRackComboField.getValue() != null) {

				List list = new StockRackMappingDao()
						.getStockRackMaps((Long) fromRackComboField.getValue());
				SCollectionContainer subGroupContainer = SCollectionContainer
						.setList(list, "id");
				stocksComboField.setContainerDataSource(subGroupContainer);
				stocksComboField.setItemCaptionPropertyId("rack_number");
			}
		} catch (Exception e) {
		}

	}

	@Override
	public Boolean isValid() {

		boolean ret = true;

		if (fromRackComboField.getValue() == null
				|| fromRackComboField.getValue().equals("")) {
			setRequiredError(fromRackComboField,
					getPropertyName("invalid_selection"), true);
			fromRackComboField.focus();
			ret = false;
		} else
			setRequiredError(fromRackComboField, null, false);

		if (stocksComboField.getValue() == null
				|| stocksComboField.getValue().equals("")) {
			setRequiredError(stocksComboField,
					getPropertyName("invalid_selection"), true);
			stocksComboField.focus();
			ret = false;
		} else
			setRequiredError(stocksComboField, null, false);

		if (toRackComboField.getValue() == null
				|| toRackComboField.getValue().equals("")) {
			setRequiredError(toRackComboField,
					getPropertyName("invalid_selection"), true);
			toRackComboField.focus();
			ret = false;
		} else
			setRequiredError(toRackComboField, null, false);

		if (ret) {
			if (toRackComboField.getValue().toString()
					.equals(fromRackComboField.getValue().toString())) {
				setRequiredError(toRackComboField,
						"From and To Racks are Same", true);
				toRackComboField.focus();
				ret = false;
			}
		}

		if (qtyTextField.getValue() == null
				|| qtyTextField.getValue().equals("")) {
			setRequiredError(qtyTextField, "Enter a Quantity", true);
			qtyTextField.focus();
			qtyTextField.selectAll();
			ret = false;
		} else {
			try {
				if (toDouble(qtyTextField.getValue()) <= 0) {
					setRequiredError(qtyTextField,
							getPropertyName("quantity_greater_zero"), true);
					qtyTextField.focus();
					qtyTextField.selectAll();
					ret = false;
				} else
					setRequiredError(qtyTextField, null, false);
			} catch (Exception e) {
				setRequiredError(qtyTextField,
						getPropertyName("enter_valid_quantity"), true);
				qtyTextField.focus();
				qtyTextField.selectAll();
				ret = false;
				// TODO: handle exception
			}
		}

		if (ret) {
			if (toDouble(qtyTextField.getValue()) > balance_qty) {
				setRequiredError(qtyTextField,
						"There is no balance in Rack. Avail Bal : "
								+ balance_qty, true);
				qtyTextField.focus();
				qtyTextField.selectAll();
				ret = false;
			} else
				setRequiredError(qtyTextField, null, false);

		}

		return ret;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

	private boolean selected(SComboField comboField) {
		return (comboField.getValue() != null
				&& !comboField.getValue().toString().equals("0") && !comboField
				.getValue().equals(""));
	}

	private long getValue(SComboField comboField) {
		if (selected(comboField)) {
			return toLong(comboField.getValue().toString());
		}
		return 0;

	}

}
