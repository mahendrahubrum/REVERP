package com.webspark.uac.ui;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SButton;
import com.webspark.Components.SComboField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SPanel;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.SConstants;
import com.webspark.model.BillModel;
import com.webspark.model.BillNameModel;
import com.webspark.uac.dao.BillDao;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Nov 13, 2013
 */
public class OfficeBillMappingUI extends SparkLogic {

	private static final long serialVersionUID = 8073163533771876714L;

	private SComboField officeField;
	private SComboField billComboField;
	private SNativeSelect typeNativeSelect;

	private BillDao dao;

	private SButton saveButton;

	@Override
	public SPanel getGUI() {

		setSize(320, 220);

		SFormLayout layout = new SFormLayout();
		layout.setSpacing(true);
		layout.setMargin(true);

		SPanel panel = new SPanel();
		panel.setSizeFull();

		panel.setContent(layout);

		dao = new BillDao();

		try {
			officeField = new SComboField(getPropertyName("office"), 200,
					new OfficeDao()
							.getAllOfficeNamesUnderOrg(getOrganizationID()),
					"id", "name");
			officeField
					.setInputPrompt("-----------------Select-------------------");
			officeField.setValue(getOfficeID());

			typeNativeSelect = new SNativeSelect(getPropertyName("type"), 200,
					SConstants.bills.billTypes, "intKey", "value");

			billComboField = new SComboField(getPropertyName("bill_name"), 200,
					dao.getAllBills(), "id", "bill_name");
			billComboField
					.setInputPrompt("-----------------Select-------------------");

			saveButton = new SButton(getPropertyName("Save"));

			layout.addComponent(officeField);
			layout.addComponent(typeNativeSelect);
			layout.addComponent(billComboField);
			layout.addComponent(saveButton);

			saveButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					if (isValid()) {

						BillModel model = new BillModel();
						model.setBill_name(new BillNameModel(
								(Long) billComboField.getValue()));
						model.setOffice(new S_OfficeModel((Long) officeField
								.getValue()));
						model.setType((Integer) typeNativeSelect.getValue());

						try {
							dao.save(model);
							Notification.show(getPropertyName("save_success"),
									Type.WARNING_MESSAGE);
						} catch (Exception e) {
							Notification.show(getPropertyName("Error"),
									Type.ERROR_MESSAGE);
							e.printStackTrace();
						}
					}
				}
			});

			officeField.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					resetAll();
					try {
						if (officeField.getValue() != null) {
							long id = dao.loadBill(
									(Long) officeField.getValue(),
									(Integer) typeNativeSelect.getValue());
							billComboField.setValue(id);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			typeNativeSelect.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						if (officeField.getValue() != null) {
							long id = dao.loadBill(
									(Long) officeField.getValue(),
									(Integer) typeNativeSelect.getValue());
							billComboField.setValue(id);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			typeNativeSelect.setValue(1);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return panel;
	}

	protected void resetAll() {
		officeField.setComponentError(null);
		billComboField.setComponentError(null);

		typeNativeSelect.setValue(1);
		billComboField.setValue(null);
	}

	@Override
	public Boolean isValid() {

		officeField.setComponentError(null);
		billComboField.setComponentError(null);

		boolean flag = true;

		if (officeField.getValue() == null || officeField.getValue().equals("")) {
			flag = false;
			setRequiredError(officeField, getPropertyName("invalid_selection"),
					true);
		}
		if (billComboField.getValue() == null
				|| billComboField.getValue().equals("")) {
			flag = false;
			setRequiredError(billComboField,
					getPropertyName("invalid_selection"), true);
		}
		return flag;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

}
