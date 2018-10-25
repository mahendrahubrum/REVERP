package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.management.dao.ContactDao;
import com.inventory.management.model.ContactCategoryModel;
import com.inventory.management.model.ContactModel;
import com.inventory.purchase.dao.PurchaseDao;
import com.inventory.purchase.model.PurchaseModel;
import com.inventory.reports.bean.SentMailReportBean;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHTMLLabel;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNotification;
import com.webspark.Components.SOfficeComboField;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPopupView;
import com.webspark.Components.SRadioButton;
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.STable;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.KeyValue;
import com.webspark.core.Report;

/**
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Jan 15, 2014
 */
public class SentMailReportUI extends SparkLogic {

	private static final long serialVersionUID = -8459855615568111760L;

	private SButton generateButton;
	private Report report;

	private SDateField fromDate, toDate;

	private SReportChoiceField reportType;

	private SComboField contactComboField;
	private SComboField categoryComboField;
	private SRadioButton criteriaRadio;

	private ContactDao dao;

	private SCollectionContainer bic;
	private SOfficeComboField officeSelect;

	static String TBC_SN = "SN";
	static String TBC_ID = "ID";
	static String TBC_DATE = "Date";
	static String TBC_CONTACT = "Contact";
	static String TBC_SUBJECT = "Subject";
	static String TBC_CONTENT = "Content";
	SHorizontalLayout popupContainer,mainHorizontal;
	Object[] allColumns;
	Object[] visibleColumns;
	STable table;
	SButton showButton;
	@Override
	public SPanel getGUI() {
		allColumns = new Object[] { TBC_SN, TBC_ID, TBC_DATE,TBC_CONTACT, TBC_SUBJECT,TBC_CONTENT};
		visibleColumns = new Object[]  { TBC_SN, TBC_DATE,TBC_CONTACT, TBC_SUBJECT,TBC_CONTENT};
		mainHorizontal=new SHorizontalLayout();
		popupContainer = new SHorizontalLayout();
		showButton=new SButton(getPropertyName("show"));
		table = new STable(null, 650, 250);
		table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
		table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,Align.CENTER);
		table.addContainerProperty(TBC_DATE, String.class, null,getPropertyName("date"), null, Align.LEFT);
		table.addContainerProperty(TBC_CONTACT, String.class, null,getPropertyName("contact"), null, Align.LEFT);
		table.addContainerProperty(TBC_SUBJECT, String.class, null,getPropertyName("subject"), null, Align.LEFT);
		table.addContainerProperty(TBC_CONTENT, String.class, null,getPropertyName("content"), null, Align.LEFT);
		
		table.setColumnExpandRatio(TBC_SN, (float) 0.3);
		table.setColumnExpandRatio(TBC_DATE, (float) 0.8);
		table.setColumnExpandRatio(TBC_CONTACT, 2);
		table.setColumnExpandRatio(TBC_SUBJECT, (float) 2);
		table.setColumnExpandRatio(TBC_CONTENT, (float) 1.5);
		table.setSelectable(true);
		table.setMultiSelect(false);
		table.setVisibleColumns(visibleColumns);
		
		
		setSize(1050, 350);

		SPanel mainPanel = new SPanel();
		mainPanel.setSizeFull();

		SFormLayout formLayout = new SFormLayout();
		formLayout.setSpacing(true);

		SHorizontalLayout dateLayout = new SHorizontalLayout();
		dateLayout.setSpacing(true);

		try {
			report = new Report(getLoginID());
			dao = new ContactDao();

			officeSelect = new SOfficeComboField(getPropertyName("office"), 100);

			fromDate = new SDateField(getPropertyName("from_date"), 100,
					getDateFormat(), getMonthStartDate());
			toDate = new SDateField(getPropertyName("to_date"), 100,
					getDateFormat(), getWorkingDate());

			reportType = new SReportChoiceField(getPropertyName("export_to"));

			criteriaRadio = new SRadioButton(null, 200, Arrays.asList(
					new KeyValue((int) 0, getPropertyName("all")), new KeyValue((int) 1,
							getPropertyName("supplier")), new KeyValue((int) 2, getPropertyName("customer"))),
					"intKey", "value");
			criteriaRadio.setHorizontal(true);
			criteriaRadio.setValue(0);

			contactComboField = new SComboField(getPropertyName("contact"), 200);
			loadContacts(0);

			categoryComboField = new SComboField(getPropertyName("category"), 200);
			loadCategory((Integer) criteriaRadio.getValue());
			SHorizontalLayout hrl=new SHorizontalLayout();
			generateButton = new SButton(getPropertyName("generate"));
			generateButton.setClickShortcut(KeyCode.ENTER);
			hrl.addComponent(generateButton);
			hrl.addComponent(showButton);
			dateLayout.addComponent(fromDate);
			dateLayout.addComponent(toDate);

			formLayout.addComponent(dateLayout);
			formLayout.addComponent(criteriaRadio);
			formLayout.addComponent(contactComboField);
			formLayout.addComponent(categoryComboField);
			formLayout.addComponent(reportType);
			formLayout.addComponent(hrl);
			mainHorizontal.addComponent(formLayout);
			mainHorizontal.addComponent(table);
			mainHorizontal.addComponent(popupContainer);
			mainHorizontal.setMargin(true);

			criteriaRadio.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					loadCategory((Integer) criteriaRadio.getValue());
					loadContacts((Integer) criteriaRadio.getValue());
				}
			});

			table.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					try{
						if (table.getValue() != null) {
							Item item = table.getItem(table.getValue());
							SFormLayout form = new SFormLayout();
							form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("mail")+"</u></h2>"));
							form.addComponent(new SLabel(getPropertyName("contact"),item.getItemProperty(TBC_CONTACT).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("date"),item.getItemProperty(TBC_DATE).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("subject"),item.getItemProperty(TBC_SUBJECT).getValue().toString()));
							form.addComponent(new SLabel(getPropertyName("content"),item.getItemProperty(TBC_CONTENT).getValue().toString()));
							popupContainer.removeAllComponents();
							form.setStyleName("grid_max_limit");
							SPopupView pop = new SPopupView("", form);
							popupContainer.addComponent(pop);
							pop.setPopupVisible(true);
							pop.setHideOnMouseOut(false);
						}
						
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
			});
			
			showButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					table.removeAllItems();
					table.setVisibleColumns(allColumns);
					List list;
					List reportList = new ArrayList();
					SentMailReportBean mailmdl = null;
					try {
						list = dao.getSentMailReport((Integer) criteriaRadio
								.getValue(), (Long) contactComboField
								.getValue(), (Long) categoryComboField
								.getValue(), CommonUtil
								.getSQLDateFromUtilDate(fromDate.getValue()),
								CommonUtil.getSQLDateFromUtilDate(toDate
										.getValue()));

						if (list != null && list.size() > 0) {

							Iterator itr = list.iterator();
							while (itr.hasNext()) {
								mailmdl = (SentMailReportBean) itr.next();
								table.addItem(new Object[]{
										table.getItemIds().size()+1,
										(long)0,
										mailmdl.getDate(),
										mailmdl.getContact(),
										mailmdl.getSubject(),
										CommonUtil.removeHtml(mailmdl.getContent())
								},table.getItemIds().size()+1);
							}
						} 
						else {
							SNotification.show(getPropertyName("no_data_available"),
									Type.WARNING_MESSAGE);
						}
						table.setVisibleColumns(visibleColumns);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			generateButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					List list;
					List reportList = new ArrayList();
					SentMailReportBean mailmdl = null;
					try {
						list = dao.getSentMailReport((Integer) criteriaRadio
								.getValue(), (Long) contactComboField
								.getValue(), (Long) categoryComboField
								.getValue(), CommonUtil
								.getSQLDateFromUtilDate(fromDate.getValue()),
								CommonUtil.getSQLDateFromUtilDate(toDate
										.getValue()));

						if (list != null && list.size() > 0) {

							Iterator itr = list.iterator();
							while (itr.hasNext()) {
								mailmdl = (SentMailReportBean) itr.next();
								mailmdl.setContent(CommonUtil
										.removeHtml(mailmdl.getContent()));
								reportList.add(mailmdl);
							}
							HashMap<String, Object> map = new HashMap<String, Object>();
							report.setJrxmlFileName("SentMailReport");
							report.setReportFileName("SentMailReport");

							map.put("REPORT_TITLE_LABEL", getPropertyName("sent_mail_report"));
							map.put("SL_NO_LABEL", getPropertyName("sl_no"));
							map.put("DATE_LABEL", getPropertyName("date"));
							map.put("CONTACT_LABEL", getPropertyName("contact"));
							map.put("SUBJECT_LABEL", getPropertyName("subject"));
							map.put("CONTENT_LABEL", getPropertyName("content"));
								

							report.setIncludeHeader(true);
							report.setReportSubTitle(getPropertyName("from")+" : "
									+ CommonUtil.formatDateToDDMMMYYYY(fromDate
											.getValue())
									+ getPropertyName("to")+" : "
									+ CommonUtil.formatDateToDDMMMYYYY(toDate
											.getValue()));
							report.setReportType((Integer) reportType
									.getValue());
							report.setOfficeName(officeSelect
									.getItemCaption(officeSelect.getValue()));
							report.createReport(reportList, map);

							reportList.clear();

						} else {
							SNotification.show(getPropertyName("no_data_available"),
									Type.WARNING_MESSAGE);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}

		mainPanel.setContent(mainHorizontal);

		return mainPanel;
	}

	private void loadCategory(Integer type) {
		List testList;
		try {
			testList = dao.getCategories(type, getOrganizationID());
			ContactCategoryModel model = new ContactCategoryModel();
			model.setId(0);
			model.setName(getPropertyName("all"));

			if (testList == null)
				testList = new ArrayList();

			testList.add(0, model);

			categoryComboField
					.setInputPrompt(getPropertyName("all"));

			bic = SCollectionContainer.setList(testList, "id");
			categoryComboField.setContainerDataSource(bic);
			categoryComboField.setItemCaptionPropertyId("name");
			categoryComboField.setValue((long) 0);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void loadContacts(int criteriaId) {
		List testList;
		try {

			testList = dao.getAllContacts(getLoginID(), criteriaId);
			ContactModel model = new ContactModel();
			model.setId(0);
			model.setName(getPropertyName("all"));

			if (testList == null)
				testList = new ArrayList();

			testList.add(0, model);

			contactComboField
					.setInputPrompt(getPropertyName("all"));

			bic = SCollectionContainer.setList(testList, "id");
			contactComboField.setContainerDataSource(bic);
			contactComboField.setItemCaptionPropertyId("name");
			contactComboField.setValue((long) 0);

		} catch (Exception e) {
			e.printStackTrace();
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
