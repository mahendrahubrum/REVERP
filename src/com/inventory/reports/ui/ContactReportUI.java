package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.vaadin.data.collectioncontainer.CollectionContainer;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.management.dao.ContactDao;
import com.inventory.management.model.ContactCategoryModel;
import com.inventory.management.ui.AddContactUI;
import com.inventory.sales.dao.SalesDao;
import com.inventory.sales.model.SalesModel;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
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
import com.webspark.Components.SPanel;
import com.webspark.Components.SPopupView;
import com.webspark.Components.SRadioButton;
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.STable;
import com.webspark.Components.SparkLogic;
import com.webspark.bean.ReportBean;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.KeyValue;
import com.webspark.core.Report;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.OrganizationDao;
import com.webspark.uac.dao.UserManagementDao;
import com.webspark.uac.model.S_OfficeModel;
import com.webspark.uac.model.UserModel;

/**
 * @author Jinshad P.T. Inventory Jan 1, 2014
 */
public class ContactReportUI extends SparkLogic {

	private static final long serialVersionUID = -7528991603195156686L;

	private SComboField organizationComboField;
	private SComboField officeComboField;
	private SDateField fromDateField;
	private SDateField toDateField;
	private SComboField userComboField;
	private SComboField categoryComboField;
	private SReportChoiceField reportChoiceField;

	private SPanel mainPanel;

	static final long EXPENSE = 4;

	private SFormLayout mainFormLayout;
	private SHorizontalLayout dateHorizontalLayout;
	private SHorizontalLayout buttonHorizontalLayout;

	private SButton generateButton;

	private SCollectionContainer custContainer;

	private long customerId;

	private Report report;

	private SRadioButton modeRadio;

	LedgerDao ledDao;
	ContactDao contDao;

	static String TBC_SN = "SN";
	static String TBC_ID = "ID";
	static String TBC_DATE = "Date";
	static String TBC_COMPANY = "Company";
	static String TBC_CONTACT = "Contact Person";
	static String TBC_CATEGORY = "Category";
	static String TBC_ADDRESS = "Address";
	static String TBC_MOBILE = "Mobile";
	static String TBC_LOCATION = "Location";
	static String TBC_OFFICE = "Office";

	SHorizontalLayout popupContainer, mainHorizontal;
	Object[] allColumns;
	Object[] visibleColumns;
	STable table;
	SButton showButton;

	@SuppressWarnings("deprecation")
	@Override
	public SPanel getGUI() {
		allColumns = new Object[] { TBC_SN, TBC_ID, TBC_DATE, TBC_COMPANY,
				TBC_CONTACT, TBC_CATEGORY, TBC_ADDRESS, TBC_MOBILE,
				TBC_LOCATION, TBC_OFFICE };
		visibleColumns = new Object[] { TBC_SN, TBC_DATE, TBC_COMPANY,
				TBC_CONTACT, TBC_CATEGORY, TBC_MOBILE, TBC_LOCATION };
		mainHorizontal = new SHorizontalLayout();
		popupContainer = new SHorizontalLayout();
		showButton = new SButton(getPropertyName("show"));

		table = new STable(null, 650, 250);
		table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
		table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,Align.CENTER);
		table.addContainerProperty(TBC_DATE, String.class, null, getPropertyName("date"),null, Align.LEFT);
		table.addContainerProperty(TBC_COMPANY, String.class, null,getPropertyName("company"), null, Align.LEFT);
		table.addContainerProperty(TBC_CONTACT, String.class, null,getPropertyName("contact_person"), null, Align.LEFT);
		table.addContainerProperty(TBC_CATEGORY, String.class, null,getPropertyName("category"), null, Align.LEFT);
		table.addContainerProperty(TBC_ADDRESS, String.class, null,getPropertyName("address"), null, Align.LEFT);
		table.addContainerProperty(TBC_MOBILE, String.class, null, getPropertyName("mobile"),null, Align.LEFT);
		table.addContainerProperty(TBC_LOCATION, String.class, null,getPropertyName("location"), null, Align.LEFT);
		table.addContainerProperty(TBC_OFFICE, String.class, null, getPropertyName("office"),null, Align.LEFT);

		table.setColumnExpandRatio(TBC_SN, (float) 0.3);
		table.setColumnExpandRatio(TBC_COMPANY, (float) 1);
		table.setColumnExpandRatio(TBC_CONTACT, (float) 1);
		table.setColumnExpandRatio(TBC_CATEGORY, (float) 1);
		table.setColumnExpandRatio(TBC_MOBILE, (float) 1);
		table.setColumnExpandRatio(TBC_LOCATION, (float) 1);
		table.setColumnExpandRatio(TBC_OFFICE, (float) 1);
		table.setSelectable(true);
		table.setMultiSelect(false);
		table.setVisibleColumns(visibleColumns);

		ledDao = new LedgerDao();
		contDao = new ContactDao();

		customerId = 0;
		report = new Report(getLoginID());

		setSize(1050, 375);
		mainPanel = new SPanel();
		mainPanel.setSizeFull();

		mainFormLayout = new SFormLayout();
		mainFormLayout.setSpacing(true);

		dateHorizontalLayout = new SHorizontalLayout();
		dateHorizontalLayout.setSpacing(true);

		buttonHorizontalLayout = new SHorizontalLayout();
		buttonHorizontalLayout.setSpacing(true);

		fromDateField = new SDateField(getPropertyName("from_date"));
		fromDateField.setValue(getMonthStartDate());
		toDateField = new SDateField(getPropertyName("to_date"));
		toDateField.setValue(getWorkingDate());
		dateHorizontalLayout.addComponent(fromDateField);
		dateHorizontalLayout.addComponent(toDateField);
		// mainFormLayout.addComponent(officeComboField);

		modeRadio = new SRadioButton(null, 200, Arrays.asList(new KeyValue(
				(int) 0, getPropertyName("all")), new KeyValue((int) 1, getPropertyName("supplier")),
				new KeyValue((int) 2, getPropertyName("customer"))), "intKey", "value");
		modeRadio.setHorizontal(true);

		try {

			organizationComboField = new SComboField(
					getPropertyName("organization"), 200,
					new OrganizationDao().getAllOrganizations(), "id", "name");
			officeComboField = new SComboField(getPropertyName("office"), 200);

			mainFormLayout.addComponent(organizationComboField);
			mainFormLayout.addComponent(officeComboField);

			mainFormLayout.addComponent(dateHorizontalLayout);

			mainFormLayout.addComponent(modeRadio);

			categoryComboField = new SComboField(getPropertyName("category"),
					200, null, "id", "name", false, getPropertyName("all"));

			userComboField = new SComboField(getPropertyName("created_by"),
					200, null, "id", "name", false,getPropertyName("all"));

			mainFormLayout.addComponent(categoryComboField);
			mainFormLayout.addComponent(userComboField);

			reportChoiceField = new SReportChoiceField(
					getPropertyName("export_to"));
			mainFormLayout.addComponent(reportChoiceField);

			generateButton = new SButton(getPropertyName("generate"));
			generateButton.setClickShortcut(KeyCode.ENTER);

			buttonHorizontalLayout.addComponent(generateButton);
			buttonHorizontalLayout.setComponentAlignment(generateButton,
					Alignment.MIDDLE_CENTER);
			buttonHorizontalLayout.addComponent(showButton);
			buttonHorizontalLayout.setComponentAlignment(showButton,
					Alignment.MIDDLE_CENTER);

			mainFormLayout.addComponent(buttonHorizontalLayout);
			mainHorizontal.addComponent(mainFormLayout);
			mainHorizontal.addComponent(table);
			mainHorizontal.addComponent(popupContainer);
			mainHorizontal.setMargin(true);

			mainPanel.setContent(mainHorizontal);

			modeRadio.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						if (modeRadio.getValue() != null) {

							List lst = new ArrayList();
							lst.add(new ContactCategoryModel(0, getPropertyName("all")));
							lst.addAll(contDao.getCategories(
									(Integer) modeRadio.getValue(),
									getOrganizationID()));

							SCollectionContainer bic = SCollectionContainer
									.setList(lst, "id");
							categoryComboField.setContainerDataSource(bic);
							categoryComboField.setItemCaptionPropertyId("name");

							categoryComboField.setValue((long) 0);
						}

					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			organizationComboField
					.addListener(new Property.ValueChangeListener() {
						@SuppressWarnings("unchecked")
						public void valueChange(ValueChangeEvent event) {
							try {
								List lst = new ArrayList();
								lst.add(new S_OfficeModel(0, getPropertyName("all")));
								lst.addAll(new OfficeDao()
										.getAllOfficeNamesUnderOrg((Long) organizationComboField
												.getValue()));

								SCollectionContainer bic = SCollectionContainer
										.setList(lst, "id");
								officeComboField.setContainerDataSource(bic);
								officeComboField
										.setItemCaptionPropertyId("name");

								Iterator it = officeComboField.getItemIds()
										.iterator();
								if (it.hasNext())
									officeComboField.setValue(it.next());
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});

			officeComboField.addListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {

						String criteria = "";
						if (((Long) officeComboField.getValue()) != 0) {
							criteria += "and ledger.office.id="
									+ officeComboField.getValue();
						} else {
							criteria += "and ledger.office.organization.id="
									+ organizationComboField.getValue();
						}

						List<Object> userList = new ArrayList<Object>();
						userList.add(new UserModel(0, getPropertyName("all")));
						userList.addAll(new UserManagementDao()
								.getUsersByCriteria(criteria));

						CollectionContainer bic = CollectionContainer
								.fromBeans(userList, "id");
						userComboField.setContainerDataSource(bic);
						userComboField.setItemCaptionPropertyId("first_name");
						userComboField
								.setInputPrompt(getPropertyName("select"));

						userComboField.setValue((long) 0);

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});

			if (isSystemAdmin() || isSuperAdmin()) {
				organizationComboField.setEnabled(true);
				officeComboField.setEnabled(true);
			} else {
				organizationComboField.setEnabled(false);
				if (isOrganizationAdmin()) {
					officeComboField.setEnabled(true);
				} else {
					officeComboField.setEnabled(false);
				}
			}

			organizationComboField.setValue(getOrganizationID());
			officeComboField.setValue(getOfficeID());
			modeRadio.setValue(0);

			final CloseListener closeListener = new CloseListener() {

				@Override
				public void windowClose(CloseEvent e) {
					showButton.click();
				}
			};

			final Action action = new Action("Edit");

			table.addActionHandler(new Handler() {

				@Override
				public void handleAction(Action action, Object sender,
						Object target) {
					try {
						Item item = null;
						if (table.getValue() != null) {
							item = table.getItem(table.getValue());
							AddContactUI option = new AddContactUI();
							option.setCaption(getPropertyName("contact"));
							option.getContactComboField().setValue(
									(Long) item.getItemProperty(TBC_ID)
											.getValue());
							option.center();
							getUI().getCurrent().addWindow(option);
							option.addCloseListener(closeListener);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				@Override
				public Action[] getActions(Object target, Object sender) {
					return new Action[] { action };
				}
			});

			table.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						if (table.getValue() != null) {
							Item item = table.getItem(table.getValue());
							long id = (Long) item.getItemProperty(TBC_ID)
									.getValue();
							SalesModel mdl = new SalesDao().getSale(id);
							SFormLayout form = new SFormLayout();
							form.addComponent(new SHTMLLabel(null,
									"<h2><u>"+getPropertyName("contact")+"</u></h2>"));
							form.addComponent(new SLabel(getPropertyName("company"), item
									.getItemProperty(TBC_COMPANY).getValue()
									.toString()));
							form.addComponent(new SLabel(getPropertyName("contact_person"), item
									.getItemProperty(TBC_CONTACT).getValue()
									.toString()));
							form.addComponent(new SLabel(getPropertyName("category"), item
									.getItemProperty(TBC_CATEGORY).getValue()
									.toString()));
							form.addComponent(new SLabel(getPropertyName("mobile"), item
									.getItemProperty(TBC_MOBILE).getValue()
									.toString()));
							form.addComponent(new SLabel(getPropertyName("location"), item
									.getItemProperty(TBC_LOCATION).getValue()
									.toString()));
							form.addComponent(new SLabel(getPropertyName("office"), item
									.getItemProperty(TBC_OFFICE).getValue()
									.toString()));
							form.addComponent(new SLabel(getPropertyName("address"), item
									.getItemProperty(TBC_ADDRESS).getValue()
									.toString()));
							popupContainer.removeAllComponents();
							form.setStyleName("grid_max_limit");
							SPopupView pop = new SPopupView("", form);
							popupContainer.addComponent(pop);
							pop.setPopupVisible(true);
							pop.setHideOnMouseOut(false);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			showButton.addClickListener(new ClickListener() {
				@SuppressWarnings("unchecked")
				@Override
				public void buttonClick(ClickEvent event) {

					try {

						if (officeComboField.getValue() != null) {

							List<Object> reportList;
							long categId = 0, user = 0;
							table.removeAllItems();
							table.setVisibleColumns(allColumns);
							if (categoryComboField.getValue() != null
									&& !categoryComboField.getValue()
											.equals("")
									&& !categoryComboField.getValue()
											.toString().equals("0")) {
								categId = (Long) categoryComboField.getValue();
							}
							if (userComboField.getValue() != null
									&& !userComboField.getValue().equals("")) {
								user = (Long) userComboField.getValue();
							}

							reportList = contDao.getContactReport(
									categId,
									CommonUtil
											.getSQLDateFromUtilDate(fromDateField
													.getValue()), CommonUtil
											.getSQLDateFromUtilDate(toDateField
													.getValue()),
									(Long) officeComboField.getValue(),
									(Long) organizationComboField.getValue(),
									user, (Integer) modeRadio.getValue(),getLoginID());

							if (reportList.size() > 0) {
								ReportBean bean = null;
								Iterator itr = reportList.iterator();
								while (itr.hasNext()) {
									bean = (ReportBean) itr.next();
									table.addItem(
											new Object[] {
													table.getItemIds().size() + 1,
													bean.getId(),
													bean.getDt().toString(),
													bean.getName(),
													bean.getContact_person(),
													bean.getCategory(),
													bean.getAddress(),
													bean.getMobile(),
													bean.getLocation(),
													bean.getOfficeName() },
											table.getItemIds().size() + 1);
								}
							} else {
								SNotification.show(getPropertyName("no_data_available"),
										Type.WARNING_MESSAGE);
							}

							table.setVisibleColumns(visibleColumns);

							setRequiredError(officeComboField, null, false);
						} 
						else {
							setRequiredError(officeComboField, null,true);
						}
					} 
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			generateButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					try {

						if (officeComboField.getValue() != null) {

							List<Object> reportList;
							long categId = 0, user = 0;

							if (categoryComboField.getValue() != null
									&& !categoryComboField.getValue()
											.equals("")
									&& !categoryComboField.getValue()
											.toString().equals("0")) {
								categId = (Long) categoryComboField.getValue();
							}
							if (userComboField.getValue() != null
									&& !userComboField.getValue().equals("")) {
								user = (Long) userComboField.getValue();
							}

							reportList = contDao.getContactReport(
									categId,
									CommonUtil
											.getSQLDateFromUtilDate(fromDateField
													.getValue()), CommonUtil
											.getSQLDateFromUtilDate(toDateField
													.getValue()),
									(Long) officeComboField.getValue(),
									(Long) organizationComboField.getValue(),
									user, (Integer) modeRadio.getValue(),getLoginID());

							if (reportList.size() > 0) {
								HashMap<String, Object> map = new HashMap<String, Object>();
								report.setJrxmlFileName("Contact_Report");
								report.setReportFileName("Contact Report");
								
								
								map.put("REPORT_TITLE_LABEL", getPropertyName("contact_report"));
								map.put("SL_NO_LABEL", getPropertyName("sl_no"));
								map.put("DATE_LABEL", getPropertyName("date"));
								map.put("COMPANY_LABEL", getPropertyName("company"));
								map.put("CONTACT_PERSON_LABEL", getPropertyName("contact_person"));
								map.put("CATEGORY_LABEL", getPropertyName("category"));
								map.put("ADDRESS_LABEL", getPropertyName("address"));
								map.put("MOBILE_LABEL", getPropertyName("mobile"));
								map.put("LOCATION_LABEL", getPropertyName("location"));
								map.put("CREATED_BY_LABEL", getPropertyName("created_by"));
								map.put("OFFICE_LABEL", getPropertyName("office"));

								String subHeader = "";

								subHeader += "\n "+getPropertyName("from")+" : "
										+ CommonUtil
												.formatDateToDDMMYYYY(fromDateField
														.getValue())
										+ "\t "+getPropertyName("to")+" : "
										+ CommonUtil
												.formatDateToDDMMYYYY(toDateField
														.getValue());

								report.setReportSubTitle(subHeader);

								report.setIncludeHeader(true);
								report.setIncludeFooter(false);
								report.setReportType(toInt(reportChoiceField
										.getValue().toString()));
								report.setOfficeName(officeComboField
										.getItemCaption(officeComboField
												.getValue()));
								report.createReport(reportList, map);

								reportList.clear();

							} else {
								SNotification.show(
										getPropertyName("no_data_available"),
										Type.WARNING_MESSAGE);
							}

							setRequiredError(officeComboField, null, false);
						} 
						else {
							setRequiredError(officeComboField, null,
									true);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
		return mainPanel;
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
