package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.List;

import com.inventory.config.acct.dao.ContractorDao;
import com.inventory.config.acct.model.ContractorModel;
import com.inventory.reports.bean.ListOfCustomerBeans;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SButton;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SNotification;
import com.webspark.Components.SOfficeComboField;
import com.webspark.Components.SPanel;
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.SparkLogic;
import com.webspark.business.AddressBusiness;
import com.webspark.core.Report;
import com.webspark.dao.AddressDao;

/**
 * @author Jinshad P.T.
 * 
 *         Nov 6, 2013
 */
public class ListOfContractorsUI extends SparkLogic {

	private static final long serialVersionUID = 1505896192095198870L;

	private SOfficeComboField officeComboField;
	private SReportChoiceField reportChoiceField;
	private SButton generateButton;
	private Report report;

	@Override
	public SPanel getGUI() {

		setSize(300, 200);

		SPanel panel = new SPanel();
		panel.setSizeFull();

		report = new Report(getLoginID());

		SFormLayout mainFormLayout = new SFormLayout();
		mainFormLayout.setMargin(true);

		officeComboField = new SOfficeComboField(getPropertyName("office"), 200);
		reportChoiceField = new SReportChoiceField(getPropertyName("export_to"));

		generateButton = new SButton(getPropertyName("generate"));
		generateButton.setClickShortcut(KeyCode.ENTER);

		mainFormLayout.addComponent(officeComboField);
		mainFormLayout.addComponent(reportChoiceField);
		mainFormLayout.addComponent(generateButton);
		panel.setContent(mainFormLayout);

		generateButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				if (isValid()) {
					List reportList = new ArrayList();
					ListOfCustomerBeans beans = null;
					ContractorModel model = null;

					try {

						List list = new ContractorDao()
								.getAllContractors(toLong(officeComboField
										.getValue().toString()));
						if (list != null && list.size() > 0) {
							for (int i = 0; i < list.size(); i++) {
								model = (ContractorModel) list.get(i);

								beans = new ListOfCustomerBeans(
										model.getName(), model
												.getContractor_code(),
												new AddressBusiness().getAddressString(model.getAddress()
												.getId()), ""
												+ model.getLedger()
														.getCurrent_balance());
								reportList.add(beans);
							}
							if (reportList.size() > 0) {
								report.setJrxmlFileName("ContractorList");
								report.setReportFileName("ContractorList");
								report.setReportTitle("Contractor List");
								report.setReportType(toInt(reportChoiceField
										.getValue().toString()));
								report.setIncludeHeader(true);
								report.setOfficeName(officeComboField
										.getItemCaption(officeComboField
												.getValue()));
								report.createReport(reportList, null);

								reportList.clear();
								list.clear();

							} else {
								SNotification.show(
										getPropertyName("no_data_available"),
										Type.WARNING_MESSAGE);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}
		});
		return panel;
	}

	@Override
	public Boolean isValid() {
		return true;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

}
