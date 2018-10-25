package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.budget.dao.BudgetDao;
import com.inventory.budget.dao.BudgetDefinitionDao;
import com.inventory.budget.dao.BudgetLVDao;
import com.inventory.budget.model.BudgetDefinitionModel;
import com.inventory.budget.model.BudgetLVMasterModel;
import com.inventory.budget.model.BudgetModel;
import com.inventory.budget.ui.BudgetUI;
import com.inventory.reports.bean.BudgetReportBean;
import com.inventory.reports.dao.BudgetReportDao;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
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
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.STable;
import com.webspark.Components.STextField;
import com.webspark.Components.SWindow;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.core.Report;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.OrganizationDao;
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author Aswathy
 * 
 *         WebSpark.
 * 
 *         Apr 28, 2014
 */
public class BudgetReportUI extends SparkLogic {

	private static final long serialVersionUID = -7431692047609723860L;
	private SComboField orgCombo;
	private SComboField offCombo;
	private SComboField budgetDefCombo;
	private SComboField budgetCombo;
	private SDateField fromDate;
	private SDateField toDate;
	private SReportChoiceField reportchoiceField;
	private SButton generate;
	private SButton show;
	private Report report;
	OfficeDao offDao;
	BudgetDao budgtDao;
	BudgetReportDao reportDao;
	BudgetReportBean reportBean;
	STable entryTable;
	STable childTable;
	SHorizontalLayout buttonLayout;
	SWindow popup;
	SDateField from_date;
	STextField personName;
	STextField reference_no;
	STextField amount;
	SFormLayout lay;
	BudgetLVDao LVDao;
	SHorizontalLayout popupContainer;

	@Override
	public SPanel getGUI() {
		popupContainer=new SHorizontalLayout();
		SPanel panel = new SPanel();
		panel.setSizeFull();
		SFormLayout layout = new SFormLayout();
		layout.setMargin(true);
		setWidth("1200");
		setHeight("500");
		layout.setSpacing(true);
		SHorizontalLayout horizontalLayout = new SHorizontalLayout();
		panel.setContent(horizontalLayout);
		lay = new SFormLayout();
		report = new Report(getLoginID());
		LVDao = new BudgetLVDao();
		fromDate = new SDateField(getPropertyName("from_date"), 200,getDateFormat());
		fromDate.setValue(getMonthStartDate());
		toDate = new SDateField(getPropertyName("to_date"), 200,getDateFormat());
		toDate.setValue(getWorkingDate());
		reportchoiceField = new SReportChoiceField(getPropertyName("export_to"));
		generate = new SButton(getPropertyName("generate"));
		show = new SButton(getPropertyName("show"));
		offDao = new OfficeDao();
		budgtDao = new BudgetDao();
		reportDao = new BudgetReportDao();
		reportBean = new BudgetReportBean();
		buttonLayout = new SHorizontalLayout();
		buttonLayout.setSpacing(true);
		entryTable = new STable();
		entryTable.setWidth("800");
		entryTable.addContainerProperty("Date", String.class, null, getPropertyName("date"), null,Align.CENTER);
		entryTable.addContainerProperty("Budget Definition",String.class, null,getPropertyName("budget_definition"), null,Align.CENTER);
		entryTable.addContainerProperty("Budget",String.class, null,getPropertyName("budget"), null,Align.CENTER);
		entryTable.addContainerProperty("Budget Id",Long.class, null,getPropertyName("budget"), null,Align.CENTER);
		entryTable.addContainerProperty("Budget Amount",Double.class, null,getPropertyName("budget_amount"), null,Align.CENTER);
		entryTable.addContainerProperty("Actual Amount",Double.class, null,getPropertyName("actual_amount"), null,Align.CENTER);
		entryTable.addContainerProperty("Variation Amount",Double.class, null,getPropertyName("variation_amount"), null,Align.CENTER);
		entryTable.setFooterVisible(true);
		// entryTable.setColumnFooter("Ref. No", "Total");
		entryTable.setSelectable(true);
		popup = new SWindow("Details");
		popup.setHeight("450");
		popup.setWidth("850");
		popup.setModal(true);
		popup.center();
		personName = new STextField(getPropertyName("person_name"), 200);
		from_date = new SDateField(getPropertyName("from_date"), 200);
		reference_no = new STextField(getPropertyName("ref_no"), 200);
		amount = new STextField(getPropertyName("amount"), 200);
		SPanel pan = new SPanel();
		pan.setSizeFull();
		popup.setContent(pan);
		pan.setContent(lay);
		childTable = new STable();
		childTable.setWidth("800");
		childTable
				.addContainerProperty("From Date", java.util.Date.class, null);
		childTable.addContainerProperty("Person's name", String.class, null);
		childTable.addContainerProperty("Ref. No", String.class, null);
		childTable.addContainerProperty("Amount", Double.class, null);

		try {
			List organizationList = new ArrayList();
			organizationList
					.addAll(new OrganizationDao().getAllOrganizations());

			orgCombo = new SComboField(getPropertyName("organization"), 200,
					organizationList, "id", "name");

			offCombo = new SComboField(getPropertyName("office"), 200);
			orgCombo.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					List officeList = new ArrayList();
					try {
						entryTable.removeAllItems();
						officeList.add(0, new S_OfficeModel(0,
								getPropertyName("all")));
						officeList.addAll(offDao
								.getAllOfficesUnderOrg((Long) orgCombo
										.getValue()));

						SCollectionContainer office = SCollectionContainer
								.setList(officeList, "id");
						offCombo.setContainerDataSource(office);
						offCombo.setInputPrompt(getPropertyName("all"));
						offCombo.setItemCaptionPropertyId("name");
						offCombo.setValue((long) 0);

					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			});

			offCombo.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					List officeList = new ArrayList();
					try {
						entryTable.removeAllItems();
						List budgetdefList = new ArrayList();
						if (offCombo.getValue() == null
								|| (Long) offCombo.getValue() == 0) {
							budgetdefList.addAll(new BudgetDefinitionDao()
									.getActiveBudgets());
						} else {
							budgetdefList.addAll(new BudgetDefinitionDao()
									.getAllActiveBudgets((Long) offCombo
											.getValue()));
						}

						budgetdefList.add(0, new BudgetDefinitionModel(0,
								getPropertyName("all")));

						SCollectionContainer office = SCollectionContainer
								.setList(budgetdefList, "id");
						budgetDefCombo.setContainerDataSource(office);
						budgetDefCombo
								.setInputPrompt(getPropertyName("all"));
						budgetDefCombo.setItemCaptionPropertyId("budgetname");
						budgetDefCombo.setValue((long) 0);

					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			});

			budgetDefCombo = new SComboField(
					getPropertyName("budget_definition"), 200);
			budgetDefCombo.setInputPrompt(getPropertyName("all"));
			budgetCombo = new SComboField(getPropertyName("budget"), 200);
			budgetDefCombo.addValueChangeListener(new ValueChangeListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void valueChange(ValueChangeEvent event) {
					List budgetList = new ArrayList();
					try {
						if (offCombo.getValue() == null
								|| (Long) offCombo.getValue() == 0) {
							if (budgetDefCombo.getValue() == null
									|| (Long) budgetDefCombo.getValue() == 0) {

								budgetList.addAll(budgtDao
										.getActiveBudgetsUnderActiveBudgetDefinition());

							} else {
								budgetList.addAll(budgtDao
										.getAllbudgetUnderBudgtDef((Long) budgetDefCombo
												.getValue()));

							}
						} else {
							budgetList.addAll(budgtDao
									.getAllActiveBudgetsUnderActiveBudgetDefinition((Long) offCombo
											.getValue()));
						}
						budgetList.add(0, new BudgetModel(0,
								getPropertyName("all")));
						entryTable.removeAllItems();
						SCollectionContainer budget = SCollectionContainer
								.setList(budgetList, "id");
						budgetCombo.setContainerDataSource(budget);
						budgetCombo
								.setInputPrompt(getPropertyName("all"));
						budgetCombo.setItemCaptionPropertyId("jobName");
						budgetCombo.setValue((long) 0);
						System.out.println(budgetList.size());

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			budgetCombo.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					entryTable.removeAllItems();

				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}

		horizontalLayout.addComponent(layout);
		horizontalLayout.addComponent(entryTable);
		horizontalLayout.addComponent(popupContainer);
		layout.addComponent(orgCombo);
		layout.addComponent(offCombo);
		layout.addComponent(budgetDefCombo);
		layout.addComponent(budgetCombo);
		layout.addComponent(fromDate);
		layout.addComponent(toDate);
		layout.addComponent(reportchoiceField);
		layout.addComponent(buttonLayout);
		buttonLayout.addComponent(generate);
		buttonLayout.addComponent(show);
//		lay.addComponent(childTable);

		generate.addClickListener(new ClickListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void buttonClick(ClickEvent event) {

				List reportList = new ArrayList();
				List newReportList = new ArrayList();

				try {

					BudgetLVMasterModel mdl;
					reportList.addAll(reportDao.getBudgetReport((Long) orgCombo
							.getValue(), (Long) offCombo.getValue(),
							(Long) budgetDefCombo.getValue(),
							(Long) budgetCombo.getValue(),
							CommonUtil.getSQLDateFromUtilDate(fromDate
									.getValue()), CommonUtil
									.getSQLDateFromUtilDate(toDate.getValue())));
					Iterator itr = reportList.iterator();
					while (itr.hasNext()) {
						mdl = (BudgetLVMasterModel) itr.next();

						reportBean = new BudgetReportBean(mdl.getBudget_id()
								.getBudgetDef_id().getBudgetname(), mdl
								.getBudget_id().getJobName(), mdl
								.getBudget_id().getNotes(), mdl.getDate(), mdl
								.getBudget_id().getAmount(),
								mdl.getTotal_amt(), mdl.getBudget_id()
										.getAmount() - mdl.getTotal_amt());
						newReportList.add(reportBean);

					}
					if (newReportList != null && newReportList.size() > 0) {
						HashMap<String, Object> map = new HashMap<String, Object>();
						report.setJrxmlFileName("BudgetReport");
						report.setReportFileName("BudgetReport");
						
						map.put("REPORT_TITLE_LABEL", getPropertyName("budget_report"));
						map.put("SL_NO_LABEL", getPropertyName("sal_no"));
						map.put("DATE_LABEL", getPropertyName("date"));
						map.put("BUDGET_DEFINITION_LABEl", getPropertyName("budget_definition"));
						map.put("BUDGET_LABEL", getPropertyName("budget"));
						map.put("BUDGET_AMOUNT_LABEL", getPropertyName("budget_amount"));
						map.put("ACTUAL_AMOUNT_LABEL", getPropertyName("actual_amount"));
						map.put("VARIATION_AMOUNT_LABEL", getPropertyName("variation_amount"));
						
						
						String subHeader = "";
						if ((Long) budgetDefCombo.getValue() != 0) {
							subHeader += getPropertyName("budget_definition")+" : "
									+ budgetDefCombo
											.getItemCaption(budgetDefCombo
													.getValue()) + "\t";
						} else {
							subHeader += getPropertyName("budget_definition")+" : "+getPropertyName("all") + "\t";
						}
						if ((Long) budgetCombo.getValue() != 0) {
							subHeader += "\n "+getPropertyName("budget")+" : "
									+ budgetCombo.getItemCaption(budgetCombo
											.getValue());
						} else {
							subHeader += "\n "+getPropertyName("budget")+" : "+getPropertyName("all") + "\t";
						}

						subHeader += "\n "+getPropertyName("from")+" : "
								+ CommonUtil.formatDateToDDMMYYYY(fromDate
										.getValue())
								+ "\t "+getPropertyName("to")+" : "
								+ CommonUtil.formatDateToDDMMYYYY(toDate
										.getValue());

						report.setReportSubTitle(subHeader);
						report.setIncludeHeader(true);
						report.setIncludeFooter(false);
						report.setReportType((Integer) reportchoiceField
								.getValue());
						report.createReport(newReportList, map);

					} else {
						SNotification.show(
								getPropertyName("no_data_available"),
								Type.WARNING_MESSAGE);
					}

				} catch (Exception e) {

					e.printStackTrace();
				}

			}
		});

		final CloseListener closeListener = new CloseListener() {

			@Override
			public void windowClose(CloseEvent e) {
				show.click();
			}
		};

		final Action actionDelete = new Action("Edit");
		
		entryTable.addActionHandler(new Handler() {
			
			@Override
			public void handleAction(Action action, Object sender, Object target) {
				try{
					if (entryTable.getValue() != null) {
						Item item = entryTable.getItem(entryTable.getValue());
						BudgetUI sales = new BudgetUI();
						sales.setCaption("Budget");
						sales.getJobCombo().setValue((Long) item.getItemProperty("Budget Id").getValue());
						sales.center();
						getUI().getCurrent().addWindow(sales);
						sales.addCloseListener(closeListener);
					}
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
			
			@Override
			public Action[] getActions(Object target, Object sender) {
				return new Action[] { actionDelete };
			}
		});
		
		show.addClickListener(new ClickListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void buttonClick(ClickEvent event) {
				List reportList = new ArrayList();
				List newReportList = new ArrayList();

				entryTable.removeAllItems();

				try {
					entryTable.setVisibleColumns(new Object[] { "Date",
							"Budget Definition", "Budget", "Budget Id",
							"Budget Amount", "Actual Amount",
							"Variation Amount" });
					BudgetLVMasterModel mdl;
					reportList.addAll(reportDao.getBudgetReport((Long) orgCombo
							.getValue(), (Long) offCombo.getValue(),
							(Long) budgetDefCombo.getValue(),
							(Long) budgetCombo.getValue(),
							CommonUtil.getSQLDateFromUtilDate(fromDate
									.getValue()), CommonUtil
									.getSQLDateFromUtilDate(toDate.getValue())));
					Iterator itr = reportList.iterator();
					while (itr.hasNext()) {
						mdl = (BudgetLVMasterModel) itr.next();

						reportBean = new BudgetReportBean(mdl.getBudget_id()
								.getBudgetDef_id().getBudgetname(), mdl
								.getBudget_id().getJobName(), mdl
								.getBudget_id().getNotes(), mdl.getDate(), mdl
								.getBudget_id().getAmount(),
								mdl.getTotal_amt(), mdl.getBudget_id()
										.getAmount() - mdl.getTotal_amt());
						System.out.println(mdl.getDate());
						System.out.println("Variation : "
								+ (mdl.getBudget_id().getAmount() - mdl
										.getTotal_amt()));
						// newReportList.add(reportBean);
						//
						//
						// for(int m=0; m<newReportList.size(); m++){

						entryTable.addItem(
								new Object[] {
										reportBean.getDate().toString(),
										reportBean.getBudgetDefinition(),
										reportBean.getBudget(),
										mdl.getBudget_id().getId(),
										reportBean.getBudgetAmount(),
										reportBean.getActualAmount(),
										reportBean.getBudgetAmount()
												- reportBean.getActualAmount()

								}, entryTable.getItemIds().size() + 1);
						// }

					}

					entryTable.setVisibleColumns(new Object[] { "Date",
							"Budget Definition", "Budget", 
							"Budget Amount", "Actual Amount",
							"Variation Amount" });

				} catch (Exception e) {

					e.printStackTrace();
				}
			}
		});

		entryTable.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				if (entryTable.getValue() != null) {
					Item item = entryTable.getItem(entryTable.getValue());
					SFormLayout form = new SFormLayout();
					form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("budget")+"</u></h2>"));
					form.addComponent(new SLabel(getPropertyName("budget_definition"),item.getItemProperty("Budget Definition").getValue().toString()));
					form.addComponent(new SLabel(getPropertyName("budget"),item.getItemProperty("Budget").getValue().toString()));
					form.addComponent(new SLabel(getPropertyName("date"),item.getItemProperty("Date").getValue().toString()));
					form.addComponent(new SLabel(getPropertyName("budget_amount"),item.getItemProperty("Budget Amount").getValue().toString()));
					form.addComponent(new SLabel(getPropertyName("actual_amount"),item.getItemProperty("Actual Amount").getValue().toString()));
					form.addComponent(new SLabel(getPropertyName("variation_amount"),item.getItemProperty("Variation Amount").getValue().toString()));
					popupContainer.removeAllComponents();
					form.setStyleName("grid_max_limit");
					SPopupView pop = new SPopupView("", form);
					popupContainer.addComponent(pop);
					pop.setPopupVisible(true);
					pop.setHideOnMouseOut(false);
				}

			}
		});

		orgCombo.setValue(getOrganizationID());
		budgetDefCombo.setValue((long) 0);
		return panel;
	}

	@Override
	public Boolean isValid() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

}
