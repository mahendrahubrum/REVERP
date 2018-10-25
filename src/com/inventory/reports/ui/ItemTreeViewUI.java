package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.dao.ItemGroupDao;
import com.inventory.config.stock.dao.ItemSubGroupDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.model.ItemGroupModel;
import com.inventory.model.ItemSubGroupModel;
import com.inventory.reports.bean.ItemTreeViewBean;
import com.inventory.reports.dao.ItemTreeViewDao;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.ReportReview;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SConfirmWithReview;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SNotification;
import com.webspark.Components.SOfficeComboField;
import com.webspark.Components.SPanel;
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.STable;
import com.webspark.Components.SparkLogic;
import com.webspark.core.Report;

/**
 * @author Anil. K P
 * 
 *         Jul 8, 2013
 */
public class ItemTreeViewUI extends SparkLogic {

	private static final long serialVersionUID = -5835327703018639924L;

	private SOfficeComboField officeComboField;
	private SComboField itemGroupComboField;
	private SComboField itemSubGroupComboField;
	private SComboField itemComboField;

	private SPanel mainPanel;
	private SHorizontalLayout buttonLayout;
	private SFormLayout formLayout;

	private SButton generateButton;

	private Report report;

	List groupList;
	List itemList;
	List subGroupList;
	List itemReportList;

	private ItemSubGroupDao itemSubGroupDao;
	private ItemDao itemDao;
	CommonMethodsDao comDao;
	ItemTreeViewDao dao;

	private SCollectionContainer subGroupContainer;
	private SCollectionContainer itemContainer;
	private SReportChoiceField reportChoiceField;

	private SNativeSelect showAllRadio;

	private WrappedSession session;
	private SettingsValuePojo settings;
	
	SConfirmWithReview confirmBox;
	ReportReview review;
	
	static String TBC_SN = "SN";
	static String TBC_ID = "ID";
	static String TBC_GROUP = "Item Group";
	static String TBC_SUBGROUP = "Item Subgroup";
	static String TBC_ITEM = "Item";
	static String TBC_UNIT = "Unit";
	static String TBC_BALANCE = "In Stock";
	
	
	SHorizontalLayout popupContainer,mainHorizontal;
	Object[] allColumns;
	Object[] visibleColumns;
	STable table;
	SButton showButton;

	@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
	@Override
	public SPanel getGUI() {

		allColumns = new Object[] { TBC_SN, TBC_ID, TBC_GROUP, TBC_SUBGROUP, TBC_ITEM, TBC_UNIT, TBC_BALANCE };
		visibleColumns = new Object[] { TBC_SN, TBC_GROUP, TBC_SUBGROUP, TBC_ITEM, TBC_UNIT, TBC_BALANCE };
		mainHorizontal=new SHorizontalLayout();
		popupContainer = new SHorizontalLayout();
		showButton=new SButton(getPropertyName("show"));
		table = new STable(null, 650, 250);
		table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
		table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,Align.CENTER);
		table.addContainerProperty(TBC_GROUP, String.class, null,getPropertyName("item_group"), null, Align.LEFT);
		table.addContainerProperty(TBC_SUBGROUP, String.class, null,getPropertyName("item_subgroup"), null, Align.LEFT);
		table.addContainerProperty(TBC_ITEM, String.class, null,getPropertyName("item"), null, Align.LEFT);
		table.addContainerProperty(TBC_UNIT, String.class, null,getPropertyName("unit"), null, Align.LEFT);
		table.addContainerProperty(TBC_BALANCE, Double.class, null,getPropertyName("stock_quantity"), null, Align.LEFT);

		table.setColumnExpandRatio(TBC_SN, (float) 0.3);
		table.setColumnExpandRatio(TBC_ITEM, 1);
		table.setColumnExpandRatio(TBC_GROUP, 1);
		table.setColumnExpandRatio(TBC_SUBGROUP, 1);
		table.setColumnExpandRatio(TBC_UNIT, 0.5f);
		table.setColumnExpandRatio(TBC_BALANCE, 0.75f);
		table.setSelectable(true);
		table.setMultiSelect(false);
		table.setVisibleColumns(visibleColumns);
		
		setSize(1050, 350);
		
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
		dao = new ItemTreeViewDao();

		session = getHttpSession();
		if (session.getAttribute("settings") != null)
			settings = (SettingsValuePojo) session.getAttribute("settings");

		showAllRadio = new SNativeSelect(getPropertyName("show_type"), 200);
		showAllRadio.addItem("All");
		showAllRadio.addItem("Non-zero Stock");
		showAllRadio.addItem("Positive Stock");
		showAllRadio.addItem("Not Stocked");
		showAllRadio.addItem("Negetive Stock Only");

		showAllRadio.setValue("All");

		officeComboField = new SOfficeComboField(getPropertyName("office"), 200);

		groupList=new ArrayList();
		itemList=new ArrayList();
		subGroupList=new ArrayList();
		itemReportList=new ArrayList();
		
		try {
			groupList = new ItemGroupDao().getAllActiveItemGroupsNames(getOrganizationID());
		} catch (Exception e) {
			e.printStackTrace();
		}
		groupList.add(0, new ItemGroupModel(0, getPropertyName("all")));
		itemGroupComboField = new SComboField(getPropertyName("item_group"),200, groupList, "id", "name",false,getPropertyName("all"));
		itemGroupComboField.setValue((long)0);
		
		subGroupList.add(0, new ItemSubGroupModel(0, getPropertyName("all")));
		itemSubGroupComboField = new SComboField(getPropertyName("item_sub_group"), 200, subGroupList, "id", "name",false,getPropertyName("all"));

		itemList.add(0, new ItemModel(0, getPropertyName("all")));
		itemComboField = new SComboField(getPropertyName("item"), 200,itemList, "id", "name",false,getPropertyName("all"));
		
		reloadSubGroupCombo();
		reloadItemCombo();

		reportChoiceField = new SReportChoiceField(getPropertyName("export_to"));

		formLayout.addComponent(officeComboField);
		formLayout.addComponent(itemGroupComboField);
		formLayout.addComponent(itemSubGroupComboField);
		formLayout.addComponent(itemComboField);
		formLayout.addComponent(showAllRadio);

		formLayout.addComponent(reportChoiceField);

		generateButton = new SButton(getPropertyName("generate"));
		generateButton.setClickShortcut(KeyCode.ENTER);
		buttonLayout.addComponent(generateButton);
		buttonLayout.addComponent(showButton);
		formLayout.addComponent(buttonLayout);

		ClickListener confirmListener=new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				if(event.getButton().getId().equals("1")) {
					try {
						saveReview(getOptionId(),confirmBox.getTitle(),confirmBox.getComments()	,getLoginID(),report.getReportFile());
						SNotification.show(getPropertyName("review_saved"));
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
		
		itemGroupComboField.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				reloadSubGroupCombo();
				reloadItemCombo();
			}
		});

		itemSubGroupComboField.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				reloadItemCombo();
			}
		});
		
		table.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				try{
					if (table.getValue() != null) {
//						Item itm = table.getItem(table.getValue());
//						SFormLayout form = new SFormLayout();
//						form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("item_details")+"</u></h2>"));
//						form.addComponent(new SLabel(getPropertyName("item"),itm.getItemProperty(TBC_ITEM).getValue().toString()));
//						form.addComponent(new SLabel(getPropertyName("real_stock"),itm.getItemProperty(TBC_REAL).getValue().toString()));
//						form.addComponent(new SLabel(getPropertyName("GRV_stock"),itm.getItemProperty(TBC_GRV).getValue().toString()));
//						form.addComponent(new SLabel(getPropertyName("total_stock"),itm.getItemProperty(TBC_TOTAL).getValue().toString()));
//						form.addComponent(new SLabel(getPropertyName("unit"),itm.getItemProperty(TBC_UNIT).getValue().toString()));
//						form.addComponent(new SLabel(getPropertyName("item_subgroup"),itm.getItemProperty(TBC_SUBGROUP).getValue().toString()));
//						form.setStyleName("grid_max_limit");
//						popupContainer.removeAllComponents();
//						SPopupView pop = new SPopupView("", form);
//						popupContainer.addComponent(pop);
//						pop.setPopupVisible(true);
//						pop.setHideOnMouseOut(false);
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
				if (isValid()) {
					showReport();
				}
			}
		});
		
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

		mainHorizontal.addComponent(formLayout);
		mainHorizontal.addComponent(table);
		mainHorizontal.addComponent(popupContainer);
		
		review.addComponent(mainHorizontal, "left: 0px; right: 0px; z-index:-1;");
		mainPanel.setContent(review);

		return mainPanel;
	}

	protected boolean showReport() {
		boolean flag=false;
		try {
			table.removeAllItems();
			table.setVisibleColumns(allColumns);

			long groupId = 0;
			long subgroupId = 0;
			long itemId = 0;
			if (selected(itemGroupComboField)) {
				groupId = toLong(itemGroupComboField.getValue().toString());
			}

			if (selected(itemSubGroupComboField)) {
				subgroupId = toLong(itemSubGroupComboField.getValue().toString());
			}

			if (selected(itemComboField)) {
				itemId = toLong(itemComboField.getValue().toString());
			}
			
			itemReportList = dao.getAllActiveItems(toLong(officeComboField.getValue().toString()),
														itemId, 
														subgroupId, 
														groupId,
														getOrganizationID());

			String show_type = showAllRadio.getValue().toString();

			ItemModel allModel;

				for (int i = 0; i < itemReportList.size(); i++) {
				
					allModel = (ItemModel) itemReportList.get(i);
					
					if (show_type.equals("All")) {
						table.addItem(new Object[]{
								table.getItemIds().size()+1,
								allModel.getId(),
								allModel.getSub_group().getGroup().getName(),
								allModel.getSub_group().getName(),
								allModel.getName(),
								allModel.getUnit().getSymbol(),
								roundNumber(allModel.getCurrent_balalnce())},table.getItemIds().size()+1);
						
					}
					else if (show_type.equals("Non-zero Stock") && allModel.getCurrent_balalnce() != 0) {
						if (allModel.getCurrent_balalnce() != 0)
							table.addItem(new Object[]{
									table.getItemIds().size()+1,
									allModel.getId(),
									allModel.getSub_group().getGroup().getName(),
									allModel.getSub_group().getName(),
									allModel.getName(),
									allModel.getUnit().getSymbol(),
									roundNumber(allModel.getCurrent_balalnce())},table.getItemIds().size()+1);
					} 
					else if (show_type.equals("Positive Stock") && allModel.getCurrent_balalnce() > 0) {
						if (allModel.getCurrent_balalnce() > 0)
							table.addItem(new Object[]{
									table.getItemIds().size()+1,
									allModel.getId(),
									allModel.getSub_group().getGroup().getName(),
									allModel.getSub_group().getName(),
									allModel.getName(),
									allModel.getUnit().getSymbol(),
									roundNumber(allModel.getCurrent_balalnce())},table.getItemIds().size()+1);
					}
					else if (show_type.equals("Not Stocked") && allModel.getCurrent_balalnce() <= 0) {
						if (allModel.getCurrent_balalnce() <= 0)
							table.addItem(new Object[]{
									table.getItemIds().size()+1,
									allModel.getId(),
									allModel.getSub_group().getGroup().getName(),
									allModel.getSub_group().getName(),
									allModel.getName(),
									allModel.getUnit().getSymbol(),
									roundNumber(allModel.getCurrent_balalnce())},table.getItemIds().size()+1);
						
					} 
					else if (show_type.equals("Negetive Stock Only") && allModel.getCurrent_balalnce() < 0) {
						if (allModel.getCurrent_balalnce() < 0)
							table.addItem(new Object[]{
									table.getItemIds().size()+1,
									allModel.getId(),
									allModel.getSub_group().getGroup().getName(),
									allModel.getSub_group().getName(),
									allModel.getName(),
									allModel.getUnit().getSymbol(),
									roundNumber(allModel.getCurrent_balalnce())},table.getItemIds().size()+1);
						
					}
				}
			table.setVisibleColumns(visibleColumns);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return flag;
	}

	protected boolean generateReport() {
		boolean flag=false;
		try {

			ArrayList<Object> reportList = new ArrayList<Object>();
			long groupId = 0;
			long subgroupId = 0;
			long itemId = 0;
			if (selected(itemGroupComboField)) {
				groupId = toLong(itemGroupComboField.getValue().toString());
			}

			if (selected(itemSubGroupComboField)) {
				subgroupId = toLong(itemSubGroupComboField.getValue()
						.toString());
			}

			if (selected(itemComboField)) {
				itemId = toLong(itemComboField.getValue().toString());
			}

			itemReportList = dao.getAllActiveItems(toLong(officeComboField.getValue().toString()),
													itemId, 
													subgroupId, 
													groupId,
													getOrganizationID());

			String show_type = showAllRadio.getValue().toString();

			ItemModel allModel;
				long prevGroupId=0;
				long prevSubGroupId=0;
				for (int i = 0; i < itemReportList.size(); i++) {
					allModel = (ItemModel) itemReportList.get(i);
					
					boolean newGroup=false, newSubGroup=false;
					
					if(prevGroupId!=allModel.getSub_group().getGroup().getId())
						newGroup=true;
					else
						newGroup=false;
					
					if(prevSubGroupId!=allModel.getSub_group().getId())
						newSubGroup=true;
					else
						newSubGroup=false;
					
					if (show_type.equals("All")) {
						ItemTreeViewBean bean=new ItemTreeViewBean(allModel.getSub_group().getGroup().getName(),
																	allModel.getSub_group().getName(), 
																	allModel.getName(), 
																	allModel.getUnit().getSymbol(),
																	roundNumber(allModel.getCurrent_balalnce()),
																	newGroup,
																	newSubGroup);
						reportList.add(bean);
					} else if (show_type.equals("Non-zero Stock") && allModel.getCurrent_balalnce() != 0) {
						ItemTreeViewBean bean=new ItemTreeViewBean(allModel.getSub_group().getGroup().getName(),
								allModel.getSub_group().getName(), 
								allModel.getName(), 
								allModel.getUnit().getSymbol(),
								roundNumber(allModel.getCurrent_balalnce()),
								newGroup,
								newSubGroup);

						if (allModel.getCurrent_balalnce() != 0)
							reportList.add(bean);
					} else if (show_type.equals("Positive Stock") && allModel.getCurrent_balalnce() > 0) {
						ItemTreeViewBean bean=new ItemTreeViewBean(allModel.getSub_group().getGroup().getName(),
								allModel.getSub_group().getName(), 
								allModel.getName(), 
								allModel.getUnit().getSymbol(),
								roundNumber(allModel.getCurrent_balalnce()),
								newGroup,
								newSubGroup);

						if (allModel.getCurrent_balalnce() > 0)
							reportList.add(bean);
					} else if (show_type.equals("Not Stocked") && allModel.getCurrent_balalnce() <= 0) {
						ItemTreeViewBean bean=new ItemTreeViewBean(allModel.getSub_group().getGroup().getName(),
								allModel.getSub_group().getName(), 
								allModel.getName(), 
								allModel.getUnit().getSymbol(),
								roundNumber(allModel.getCurrent_balalnce()),
								newGroup,
								newSubGroup);
						if (allModel.getCurrent_balalnce() <= 0)
							reportList.add(bean);
						
					} 
					else if (show_type.equals("Negetive Stock Only") && allModel.getCurrent_balalnce() < 0) {
						ItemTreeViewBean bean=new ItemTreeViewBean(allModel.getSub_group().getGroup().getName(),
								allModel.getSub_group().getName(), 
								allModel.getName(), 
								allModel.getUnit().getSymbol(),
								roundNumber(allModel.getCurrent_balalnce()),
								newGroup,
								newSubGroup);

						if (allModel.getCurrent_balalnce() < 0)
							reportList.add(bean);
						
					} 
					prevGroupId=allModel.getSub_group().getGroup().getId();
					prevSubGroupId=allModel.getSub_group().getId();
				}
				report.setJrxmlFileName("ItemTreeView");
				report.setReportFileName("ItemTreeView");
			if (reportList.size() > 0) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("REPORT_TITLE_LABEL", getPropertyName("item_tree_view"));
				map.put("SL_NO_LABEL", getPropertyName("sl_no"));
				map.put("ITEM_GROUP_LABEL", getPropertyName("item_group"));
				map.put("ITEM_SUBGROUP_LABEL", getPropertyName("item_subgroup"));
				map.put("ITEM_LABEL", getPropertyName("item"));
				map.put("UNIT_LABEL", getPropertyName("unit"));
				map.put("STOCK_QUANTITY_LABEL", getPropertyName("stock_quantity"));
				
				String subTitle = "";
				if (selected(itemGroupComboField)) {
					subTitle += getPropertyName("item_group")+" : "
							+ itemGroupComboField
									.getItemCaption(itemGroupComboField
											.getValue());
				}
				if (selected(itemSubGroupComboField)) {
					subTitle += "\t "+getPropertyName("item_subgroup")+" : "
							+ itemSubGroupComboField
									.getItemCaption(itemSubGroupComboField
											.getValue());
				}
				if (selected(itemComboField)) {
					subTitle += "\n "+getPropertyName("item")+" : "
							+ itemComboField.getItemCaption(itemComboField
									.getValue());
				}
				report.setReportSubTitle(subTitle);
				report.setReportType(toInt(reportChoiceField.getValue().toString()));
				report.setIncludeHeader(true);
				report.setIncludeFooter(false);
				report.setOfficeName(officeComboField
						.getItemCaption(officeComboField.getValue()));
				report.createReport(reportList, map);

				reportList.clear();
				itemReportList.clear();
				
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

	@SuppressWarnings("unchecked")
	private void reloadSubGroupCombo() {
		try {
			if (selected(itemGroupComboField)) {
				subGroupList = itemSubGroupDao.getAllActiveItemSubGroups(Long
						.parseLong(itemGroupComboField.getValue().toString()));
			} else {
				subGroupList = itemSubGroupDao
						.getAllActiveItemSubGroupsNames(getOrganizationID());
			}

			subGroupList.add(0, new ItemSubGroupModel(0, getPropertyName("all")));
			itemSubGroupComboField.setInputPrompt(getPropertyName("all"));
			subGroupContainer = SCollectionContainer.setList(subGroupList, "id");
			itemSubGroupComboField.setContainerDataSource(subGroupContainer);
			itemSubGroupComboField.setItemCaptionPropertyId("name");
			itemSubGroupComboField.setValue((long)0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private void reloadItemCombo() {
		try {

			itemList = itemDao.getAllActiveItemsWithAppendingItemCode(
					getValue(officeComboField),
					getValue(itemSubGroupComboField),
					getValue(itemGroupComboField));

			itemList.add(0, new ItemModel(0, getPropertyName("all")));
			itemComboField.setInputPrompt(getPropertyName("all"));
			itemContainer = SCollectionContainer.setList(itemList, "id");
			itemComboField.setContainerDataSource(itemContainer);
			itemComboField.setItemCaptionPropertyId("name");
			itemComboField.setValue((long)0);
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
