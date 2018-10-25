package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.List;

import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.dao.ItemGroupDao;
import com.inventory.config.stock.dao.ItemSubGroupDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.model.ItemGroupModel;
import com.inventory.model.ItemSubGroupModel;
import com.inventory.purchase.model.ItemStockModel;
import com.inventory.reports.bean.ItemReportBean;
import com.inventory.reports.dao.CommissionStockReportDao;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.ReportReview;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SConfirmWithReview;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SNotification;
import com.webspark.Components.SOfficeComboField;
import com.webspark.Components.SPanel;
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.core.Report;

/***
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Dec 5, 2014
 */
public class CommissionStockReportUI extends SparkLogic {

	private static final String PROMPT_ALL = "-------------------All-----------------";

	private static final long serialVersionUID = -5835327703018639924L;

	private SOfficeComboField officeComboField;
//	private SComboField itemGroupComboField;
//	private SComboField itemSubGroupComboField;
	private SComboField itemComboField;

	private SPanel mainPanel;
	private SHorizontalLayout buttonLayout;
	private SFormLayout formLayout;

	private SButton generateButton;

	private Report report;

	private List itemList;
	private List subGroupList;
	private List itemReportList;

	private ItemSubGroupDao itemSubGroupDao;
	private ItemDao itemDao;
	CommonMethodsDao comDao;
	SDateField toDate,fromDate;
	CommissionStockReportDao dao;

	private SCollectionContainer subGroupContainer;
	private SCollectionContainer itemContainer;
	private SReportChoiceField reportChoiceField;

	private WrappedSession session;
	private SettingsValuePojo settings;
	
	SConfirmWithReview confirmBox;
	ReportReview review;

	@Override
	public SPanel getGUI() {

		setSize(350, 380);
		
		review=new ReportReview();
		confirmBox=new SConfirmWithReview("Review", getOfficeID());

		mainPanel = new SPanel();
		mainPanel.setSizeFull();

		report = new Report(getLoginID());

		formLayout = new SFormLayout();
		// formLayout.setSizeFull();
		// formLayout.setSpacing(true);
		formLayout.setMargin(true);

		buttonLayout = new SHorizontalLayout();
		buttonLayout.setSpacing(true);

		itemSubGroupDao = new ItemSubGroupDao();
		itemDao = new ItemDao();
		comDao = new CommonMethodsDao();
		dao = new CommissionStockReportDao();

		session = getHttpSession();
		if (session.getAttribute("settings") != null)
			settings = (SettingsValuePojo) session.getAttribute("settings");

		toDate = new SDateField("To", 120, getDateFormat(),
				getWorkingDate());
		fromDate = new SDateField("From", 120, getDateFormat(),
				getFinStartDate());

		officeComboField = new SOfficeComboField(getPropertyName("office"), 200);

		List groupList;
		try {
			groupList = new ItemGroupDao()
					.getAllActiveItemGroupsNames(getOrganizationID());
		} catch (Exception e) {
			groupList = new ArrayList();
			e.printStackTrace();
		}
		ItemGroupModel itemGroupModel = new ItemGroupModel();
		itemGroupModel.setId(0);
		itemGroupModel.setName(PROMPT_ALL);
		groupList.add(0, itemGroupModel);
//		itemGroupComboField = new SComboField(getPropertyName("item_group"),
//				200, groupList, "id", "name");
//		itemGroupComboField.setInputPrompt(PROMPT_ALL);

		try {
			subGroupList = new ArrayList();
		} catch (Exception e) {
			subGroupList = new ArrayList();
			e.printStackTrace();
		}
		ItemSubGroupModel itemSubGroupModel = new ItemSubGroupModel();
		itemSubGroupModel.setId(0);
		itemSubGroupModel.setName(PROMPT_ALL);
		subGroupList.add(0, itemSubGroupModel);
//		itemSubGroupComboField = new SComboField(
//				getPropertyName("item_sub_group"), 200, null, "id", "name");
//		itemSubGroupComboField.setInputPrompt(PROMPT_ALL);

		itemList = new ArrayList();

		ItemModel itemModel = new ItemModel();
		itemModel.setId(0);
		itemModel.setName(PROMPT_ALL);
		itemList.add(0, itemModel);
		itemComboField = new SComboField(getPropertyName("item"), 200,
				itemList, "id", "name");
		itemComboField.setInputPrompt(PROMPT_ALL);
		reloadItemCombo();

		reportChoiceField = new SReportChoiceField(getPropertyName("export_to"));

		formLayout.addComponent(officeComboField);
//		formLayout.addComponent(itemGroupComboField);
//		formLayout.addComponent(itemSubGroupComboField);
		formLayout.addComponent(itemComboField);
		formLayout.addComponent(fromDate);
		formLayout.addComponent(toDate);
		formLayout.addComponent(reportChoiceField);

		generateButton = new SButton(getPropertyName("generate"));
		generateButton.setClickShortcut(KeyCode.ENTER);
		buttonLayout.addComponent(generateButton);
		formLayout.addComponent(buttonLayout);

		ClickListener confirmListener=new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				// TODO Auto-generated method stub
				if(event.getButton().getId().equals("1")) {
					try {
						saveReview(getOptionId(),confirmBox.getTitle(),confirmBox.getComments()	,getLoginID(),report.getReportFile());
						SNotification.show("Review Saved");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				confirmBox.close();
				confirmBox.setTitle("");
				confirmBox.setComments("");
			}
			
		};
		confirmBox.setClickListener(confirmListener);
		
		review.setClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(event.getButton().getId().equals(ReportReview.REVIEW)){
					if(generateReport())
						confirmBox.open();
				}
			}
		});
		
//		itemGroupComboField.addValueChangeListener(new ValueChangeListener() {
//
//			@Override
//			public void valueChange(ValueChangeEvent event) {
//				reloadSubGroupCombo();
//				reloadItemCombo();
//
//			}
//		});
//
//		itemSubGroupComboField
//				.addValueChangeListener(new ValueChangeListener() {
//
//					@Override
//					public void valueChange(ValueChangeEvent event) {
//						reloadItemCombo();
//					}
//				});

		generateButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				if (isValid()) {
					generateReport();
				}
			}
		});
		officeComboField.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				reloadItemCombo();
			}
		});

		SVerticalLayout hLayout=new SVerticalLayout();
		hLayout.addComponent(formLayout);
		
		review.addComponent(hLayout, "left: 0px; right: 0px; z-index:-1;");
		mainPanel.setContent(review);
//		itemGroupComboField.setValue((long)0);
//		itemSubGroupComboField.setValue((long)0);
		itemComboField.setValue((long)0);
		return mainPanel;
	}

	@SuppressWarnings("unchecked")
	protected boolean generateReport() {
		boolean flag=false;
		try {

			List reportList = new ArrayList();
			ItemReportBean bean = new ItemReportBean();

//			long groupId = 0;
//			long subgroupId = 0;
			long itemId = 0;

			if (selected(itemComboField)) {
				itemId = toLong(itemComboField.getValue().toString());
			}
			reportList=dao.getCommissionStockReport(itemId,
													CommonUtil.getSQLDateFromUtilDate(fromDate.getValue()),
													CommonUtil.getSQLDateFromUtilDate(toDate.getValue()),
													(Long)officeComboField.getValue());

			double grvQty = 0;
			ItemModel allModel;
			ItemStockModel stkMdl;

			if (reportList.size() > 0) {
				report.setJrxmlFileName("CommissionStock");
				report.setReportFileName("Commission Stock");
				report.setReportTitle("Commission Stock Report");
				String subTitle = "";
				if (selected(itemComboField)) {
					subTitle += "\n Item : "
							+ itemComboField.getItemCaption(itemComboField
									.getValue());
				}
				report.setReportSubTitle(subTitle);
				report.setReportType(toInt(reportChoiceField.getValue()
						.toString()));
				report.setIncludeHeader(true);
				report.setIncludeFooter(false);
				report.setOfficeName(officeComboField
						.getItemCaption(officeComboField.getValue()));
				report.createReport(reportList, null);

				reportList.clear();
//				itemReportList.clear();
				
				flag=true;

			} else {
				SNotification.show(getPropertyName("no_data_available"),
						Type.WARNING_MESSAGE);
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return flag;
	}

	@Override
	public Boolean isValid() {

		boolean valid = true;

		return valid;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

	/*private void reloadSubGroupCombo() {
		try {

			if (selected(itemGroupComboField)) {
				subGroupList = itemSubGroupDao.getAllActiveItemSubGroups(Long
						.parseLong(itemGroupComboField.getValue().toString()));
			} else {
				subGroupList = itemSubGroupDao
						.getAllActiveItemSubGroupsNames(getOrganizationID());
			}

			ItemSubGroupModel itemSubGroupModel = new ItemSubGroupModel();
			itemSubGroupModel.setId(0);
			itemSubGroupModel.setName(PROMPT_ALL);
			if (subGroupList == null)
				subGroupList = new ArrayList();

			subGroupList.add(0, itemSubGroupModel);

			itemSubGroupComboField.setInputPrompt(PROMPT_ALL);

			subGroupContainer = SCollectionContainer
					.setList(subGroupList, "id");
			itemSubGroupComboField.setContainerDataSource(subGroupContainer);
			itemSubGroupComboField.setItemCaptionPropertyId("name");
			itemSubGroupComboField.setValue(0);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/

	private void reloadItemCombo() {
		try {

			itemList = itemDao.getAllActiveItemsWithAppendingItemCode(
					getValue(officeComboField),0,0);

			ItemModel itemModel = new ItemModel();
			itemModel.setId(0);
			itemModel.setName(PROMPT_ALL);
			if (itemList == null)
				itemList = new ArrayList();

			itemList.add(0, itemModel);

			itemComboField.setInputPrompt(PROMPT_ALL);

			itemContainer = SCollectionContainer.setList(itemList, "id");
			itemComboField.setContainerDataSource(itemContainer);
			itemComboField.setItemCaptionPropertyId("name");
			itemComboField.setValue(0);

		} catch (Exception e) {
			e.printStackTrace();
		}
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
