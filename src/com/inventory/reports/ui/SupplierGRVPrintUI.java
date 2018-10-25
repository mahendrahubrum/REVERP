package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.purchase.model.PurchaseReturnInventoryDetailsModel;
import com.inventory.reports.bean.DebitNoteBean;
import com.inventory.sales.dao.SalesReturnNewDao;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SPanel;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.core.Report;
import com.webspark.dao.AddressDao;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.OrganizationDao;

/**
 * @author Jinshad P.T.
 * 
 *         Feb 5, 2013
 */
public class SupplierGRVPrintUI extends SparkLogic {

	private static final long serialVersionUID = -5835327703018639924L;

	private SPanel mainPanel;
	private SHorizontalLayout buttonLayout;
	private SFormLayout formLayout;

	private SButton generateButton;

	private Report report;

	private SalesReturnNewDao daoObj;

	private SCollectionContainer subGroupContainer;
	private SCollectionContainer itemContainer;

	SDateField fromDate, toDate;

	private SComboField organizationSelect;
	private SComboField officeSelect;
	private SComboField ledgertSelect;

	private SNativeSelect reportType;

	SHorizontalLayout mainLay;

	OfficeDao ofcDao;
	LedgerDao ledDao;

	String[] allColumns;
	String[] visibleColumns;

	SHorizontalLayout popupContainer;

	@SuppressWarnings("deprecation")
	@Override
	public SPanel getGUI() {

		ofcDao = new OfficeDao();
		ledDao = new LedgerDao();

		try {
			popupContainer = new SHorizontalLayout();
			mainLay = new SHorizontalLayout();

			setSize(380, 370);
			reportType = new SNativeSelect(getPropertyName("report_type"), 100,
					SConstants.reportTypes, "intKey", "value");

			mainPanel = new SPanel();
			mainPanel.setSizeFull();

			report = new Report(getLoginID());

			organizationSelect = new SComboField(
					getPropertyName("organization"), 200,
					new OrganizationDao().getAllOrganizations(), "id", "name");
			organizationSelect.setValue(getOrganizationID());
			officeSelect = new SComboField(getPropertyName("office"), 200,
					ofcDao.getAllOfficeNamesUnderOrg((Long) organizationSelect
							.getValue()), "id", "name");
			officeSelect.setValue(getOfficeID());
			ledgertSelect = new SComboField(getPropertyName("supplier"), 200,
					ledDao.getAllSuppliers((Long) officeSelect.getValue()),
					"id", "name");
			ledgertSelect.setInputPrompt(getPropertyName("select"));

			if (isSuperAdmin() || isSystemAdmin()) {
				organizationSelect.setEnabled(true);
				officeSelect.setEnabled(true);
			} else {
				organizationSelect.setEnabled(false);
				if (isOrganizationAdmin()) {
					officeSelect.setEnabled(true);
				} else
					officeSelect.setEnabled(false);
			}

			formLayout = new SFormLayout();
			// formLayout.setSizeFull();
			// formLayout.setSpacing(true);
			formLayout.setMargin(true);

			buttonLayout = new SHorizontalLayout();
			buttonLayout.setSpacing(true);

			daoObj = new SalesReturnNewDao();

			fromDate = new SDateField(getPropertyName("from_date"), 150,
					getDateFormat(), new Date(getFinStartDate().getTime()));
			toDate = new SDateField(getPropertyName("to_date"), 150,
					getDateFormat(), new Date(getFinEndDate().getTime()));

			formLayout.addComponent(organizationSelect);
			formLayout.addComponent(officeSelect);
			formLayout.addComponent(ledgertSelect);
			formLayout.addComponent(fromDate);
			formLayout.addComponent(toDate);
			formLayout.addComponent(reportType);

			reportType.setValue(0);

			generateButton = new SButton(getPropertyName("generate"));
			buttonLayout.addComponent(generateButton);
			formLayout.addComponent(buttonLayout);

			mainLay.addComponent(formLayout);
			mainLay.addComponent(popupContainer);

			mainLay.setMargin(true);

			generateButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					if (isValid()) {
						generateReport();
					}
				}
			});

			organizationSelect.addListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {

					SCollectionContainer bic = null;
					try {
						bic = SCollectionContainer.setList(
								ofcDao.getAllOfficeNamesUnderOrg((Long) organizationSelect
										.getValue()), "id");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					officeSelect.setContainerDataSource(bic);
					officeSelect.setItemCaptionPropertyId("name");

				}
			});

			officeSelect.addListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {

					SCollectionContainer bic = null;
					try {
						bic = SCollectionContainer.setList(
								ledDao.getAllSuppliers((Long) officeSelect
										.getValue()), "id");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					ledgertSelect.setContainerDataSource(bic);
					ledgertSelect.setItemCaptionPropertyId("name");

				}
			});

			mainPanel.setContent(mainLay);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return mainPanel;
	}

	protected void generateReport() {
		try {
			List debitNoteList;

			if (isValid()) {

				LedgerModel ledger = ledDao.getLedgeer((Long) ledgertSelect
						.getValue());

				Map<String, Object> map = new HashMap<String, Object>();
				debitNoteList = new ArrayList<Object>();

				DebitNoteBean debitNoteBean;

				Report report = new Report(getLoginID());

				List lst = daoObj.getAllDebitNotesOfSupplier(
						(Long) officeSelect.getValue(),
						CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
						CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
						(Long) ledgertSelect.getValue());

				if (lst != null && lst.size() > 0) {

					PurchaseReturnInventoryDetailsModel detMdl = null;

					String address = "";

//					if (ledger.getAddress() != null) {
//						address = new AddressDao().getAddressString(ledger
//								.getAddress().getId());
//					}

					Iterator iter = lst.iterator();
					double totalAmt = 0;
					while (iter.hasNext()) {

						detMdl = (PurchaseReturnInventoryDetailsModel) iter
								.next();

						debitNoteBean = new DebitNoteBean(detMdl.getItem()
								.getItem_code(), detMdl.getItem().getName(),
								detMdl.getUnit().getSymbol(),
								detMdl.getUnit_price(), detMdl.getQunatity(),
								detMdl.getUnit_price() * detMdl.getQunatity(),
								detMdl.getTaxAmount(), detMdl.getUnit_price()
										* detMdl.getQunatity()
										+ detMdl.getTaxAmount());
						debitNoteList.add(debitNoteBean);
						totalAmt += detMdl.getUnit_price()
								* detMdl.getQunatity() + detMdl.getTaxAmount();

					}

					map.put("SUPPLIER_NAME", ledger.getName());
					map.put("SUPPLIER_ADDRESS", address);
					map.put("DATE",
							CommonUtil.formatDateToDDMMYYYY(fromDate.getValue())
									+ " - "
									+ CommonUtil.formatDateToDDMMYYYY(toDate
											.getValue()));
					map.put("DEBIT_NOTE_NO", "");
					map.put("AMOUNT_IN_WORDS",
							getAmountInWords(roundNumber(totalAmt)));
					map.put("LOGO_PATH", VaadinServlet.getCurrent()
							.getServletContext().getRealPath("/")
							+ "VAADIN/themes/testappstheme/OrganizationLogos/"
							+ getOrganizationID() + ".png");

					report.setJrxmlFileName("SupplierGRV");
					report.setReportFileName("Supplier GRV");
					report.setReportType(Report.PDF);
					report.createReport(debitNoteList, map);

				} else {
					Notification.show(getPropertyName("no_data_available"),
							Type.WARNING_MESSAGE);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Boolean isValid() {

		boolean ret = true;

		if (ledgertSelect.getValue() == null
				|| ledgertSelect.getValue().equals("")) {
			setRequiredError(ledgertSelect,
					getPropertyName("invalid_selection"), true);
			ledgertSelect.focus();
			ret = false;
		} else
			setRequiredError(ledgertSelect, null, false);

		if (fromDate.getValue() == null || fromDate.getValue().equals("")) {
			setRequiredError(fromDate, getPropertyName("invalid_selection"),
					true);
			fromDate.focus();
			ret = false;
		} else
			setRequiredError(fromDate, null, false);

		if (toDate.getValue() == null || toDate.getValue().equals("")) {
			setRequiredError(toDate, getPropertyName("invalid_selection"), true);
			toDate.focus();
			ret = false;
		} else
			setRequiredError(toDate, null, false);

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
