package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.dao.ItemGroupDao;
import com.inventory.config.stock.dao.ItemSubGroupDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.model.ItemGroupModel;
import com.inventory.model.ItemSubGroupModel;
import com.inventory.reports.bean.ReorderReportBean;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SNotification;
import com.webspark.Components.SOfficeComboField;
import com.webspark.Components.SPanel;
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.SparkLogic;
import com.webspark.core.Report;

/**
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Aug 27, 2013
 */
public class ReorderLevelReportUI extends SparkLogic {

	private static final long serialVersionUID = 7557911380966377544L;

//	private static final String PROMPT_ALL = "----------------ALL--------------";

	private SComboField itemGroupComboField;
	private SComboField itemSubGroupComboField;
	private SReportChoiceField choiceField;
	private SOfficeComboField officeComboField;
	private SButton generateButton;

	private ItemSubGroupDao subGroupDao;

	private Report report;

	@Override
	public SPanel getGUI() {

		setSize(380, 280);
		SPanel panel = new SPanel();
		panel.setSizeFull();

		SFormLayout mainFormLayout = new SFormLayout();
		mainFormLayout.setMargin(true);
		mainFormLayout.setSpacing(true);

		subGroupDao = new ItemSubGroupDao();
		report = new Report(getLoginID());

		officeComboField = new SOfficeComboField(getPropertyName("office"), 200);

		List groupList;
		try {
			groupList = new ItemGroupDao()
					.getAllItemGroupsNames(getOrganizationID());
		} catch (Exception e) {
			groupList = new ArrayList();
			e.printStackTrace();
		}
		ItemGroupModel itemGroupModel = new ItemGroupModel();
		itemGroupModel.setId(0);
		itemGroupModel.setName(getPropertyName("all"));
		groupList.add(0, itemGroupModel);
		itemGroupComboField = new SComboField(getPropertyName("item_group"),
				200, groupList, "id", "name");
		itemGroupComboField.setInputPrompt(getPropertyName("all"));

		List subGroupList = null;
		try {
			subGroupList = subGroupDao
					.getAllActiveItemSubGroupsNames(getOrganizationID());
		} catch (Exception e) {
			subGroupList = new ArrayList();
			e.printStackTrace();
		}
		ItemSubGroupModel itemSubGroupModel = new ItemSubGroupModel();
		itemSubGroupModel.setId(0);
		itemSubGroupModel.setName(getPropertyName("all"));
		subGroupList.add(0, itemSubGroupModel);
		itemSubGroupComboField = new SComboField(
				getPropertyName("item_sub_group"), 200, subGroupList, "id",
				"name");
		itemSubGroupComboField.setInputPrompt(getPropertyName("all"));

		generateButton = new SButton(getPropertyName("generate"));
		generateButton.setClickShortcut(KeyCode.ENTER);

		choiceField = new SReportChoiceField(getPropertyName("export_to"));

		mainFormLayout.addComponent(officeComboField);
		mainFormLayout.addComponent(itemGroupComboField);
		mainFormLayout.addComponent(itemSubGroupComboField);
		mainFormLayout.addComponent(choiceField);
		mainFormLayout.addComponent(generateButton);

		panel.setContent(mainFormLayout);

		itemGroupComboField.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				reloadSubGroupCombo();
			}
		});

		generateButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				if (isValid()) {
					List<Object> reportList = new ArrayList<Object>();
					ReorderReportBean bean = null;
					ItemModel model = null;
					long itemGroupId = 0;
					long itemSubGroupId = 0;

					if (itemGroupComboField.getValue() != null
							&& !itemGroupComboField.getValue().equals("")) {
						itemGroupId = toLong(itemGroupComboField.getValue()
								.toString());
					}

					if (itemSubGroupComboField.getValue() != null
							&& !itemSubGroupComboField.getValue().equals("")) {
						itemSubGroupId = toLong(itemSubGroupComboField
								.getValue().toString());
					}

					try {
						List<Object> itemList = new ItemDao()
								.getItemsUnderReorderLevel(
										toLong(officeComboField.getValue()
												.toString()), itemGroupId,
										itemSubGroupId);

						Iterator itr = itemList.iterator();
						while (itr.hasNext()) {
							model = (ItemModel) itr.next();

							bean = new ReorderReportBean(model.getName(), model
									.getItem_code(), model.getReorder_level(),
									model.getCurrent_balalnce());
							reportList.add(bean);
						}

						if (reportList.size() > 0) {
							HashMap<String, Object> map = new HashMap<String, Object>();
							report.setJrxmlFileName("Reorder_Report");
							report.setReportFileName("Reorder_Report");
							
							map.put("REPORT_TITLE_LABEL", getPropertyName("item_reorder_details_report"));
							map.put("SL_NO_LABEL", getPropertyName("sl_no"));
							map.put("ITEM_CODE_LABEL", getPropertyName("item_code"));
							map.put("ITEM_LABEL", getPropertyName("item"));
							map.put("REORDER_LEVEL_LABEL", getPropertyName("reorder_level"));
							map.put("CURRENT_BALANCE_LABEL", getPropertyName("current_balance"));
							
							String subTitle = "";
							if (itemGroupComboField.getValue() != null
									&& !itemGroupComboField.getValue().equals(
											"")
									&& !itemGroupComboField.getValue()
											.toString().equals("0")) {
								subTitle += getPropertyName("item_group")+" : "
										+ itemGroupComboField
												.getItemCaption(itemGroupComboField
														.getValue());
							}
							if (itemSubGroupComboField.getValue() != null
									&& !itemSubGroupComboField.getValue()
											.equals("")
									&& !itemSubGroupComboField.getValue()
											.toString().equals("0")) {
								subTitle += "\t "+getPropertyName("item_sub_group")+" : "
										+ itemSubGroupComboField
												.getItemCaption(itemSubGroupComboField
														.getValue());
							}
							report.setReportSubTitle(subTitle);
							report.setReportType(toInt(choiceField.getValue()
									.toString()));
							report.setIncludeHeader(true);
							report.setOfficeName(officeComboField
									.getItemCaption(officeComboField.getValue()));
							report.createReport(reportList, map);

							reportList.clear();
							itemList.clear();

						} else {
							SNotification.show(
									getPropertyName("no_data_available"),
									Type.WARNING_MESSAGE);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});

		return panel;
	}

	private void reloadSubGroupCombo() {
		try {
			List subGroupList;
			if (itemGroupComboField.getValue() != null
					&& !itemGroupComboField.getValue().toString().equals("0")
					&& !itemGroupComboField.getValue().equals("")) {
				subGroupList = subGroupDao.getAllActiveItemSubGroups(Long
						.parseLong(itemGroupComboField.getValue().toString()));
			} else {
				subGroupList = subGroupDao
						.getAllActiveItemSubGroupsNames(getOrganizationID());
			}

			ItemSubGroupModel itemSubGroupModel = new ItemSubGroupModel();
			itemSubGroupModel.setId(0);
			itemSubGroupModel.setName(getPropertyName("all"));
			if (subGroupList == null)
				subGroupList = new ArrayList();

			subGroupList.add(0, itemSubGroupModel);

			itemSubGroupComboField.setInputPrompt(getPropertyName("all"));

			SCollectionContainer con = SCollectionContainer.setList(
					subGroupList, "id");
			itemSubGroupComboField.setContainerDataSource(con);
			itemSubGroupComboField.setItemCaptionPropertyId("name");
			itemSubGroupComboField.setValue(0);

		} catch (Exception e) {
			e.printStackTrace();
		}
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
