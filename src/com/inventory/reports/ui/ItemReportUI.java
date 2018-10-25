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
import com.inventory.config.unit.dao.UnitDao;
import com.inventory.config.unit.model.UnitModel;
import com.inventory.dao.LocationDao;
import com.inventory.model.ItemGroupModel;
import com.inventory.model.ItemSubGroupModel;
import com.inventory.model.LocationModel;
import com.inventory.model.RackModel;
import com.inventory.reports.bean.ItemReportBean;
import com.inventory.reports.dao.ItemReportDao;
import com.vaadin.data.Item;
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
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHTMLLabel;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SNotification;
import com.webspark.Components.SOfficeComboField;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPopupView;
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.STable;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.core.Report;

/**
 * @author Anil. K P
 * 
 *         Jul 8, 2013
 */

@SuppressWarnings("serial")
public class ItemReportUI extends SparkLogic {

	private SOfficeComboField officeCombo;
	private SComboField itemGroupCombo;
	private SComboField itemSubGroupCombo;
	private SComboField itemComboField;
	private SComboField locationCombo;
	private SComboField showExtraUnitCombo;;

//	SCheckBox isRackWise;

	private SPanel mainPanel;
	private SHorizontalLayout buttonLayout;
	private SFormLayout formLayout;

	private SButton generateButton;

	private Report report;

	private ItemSubGroupDao itemSubGroupDao;
	private ItemDao itemDao;
	CommonMethodsDao comDao;
	SDateField date;
	ItemReportDao dao;

	private SReportChoiceField reportChoiceField;

	private SNativeSelect showTypeSelect;
	private SComboField rackSelect;

	private WrappedSession session;
	private SettingsValuePojo settings;
	
	SConfirmWithReview confirmBox;
	ReportReview review;
	
	static String TBC_SN = "SN";
	static String TBC_ID = "ID";
	static String TBC_ITEM = "Item";
	static String TBC_REAL = "Quantity";
	static String TBC_UNIT = "Unit";
	static String TBC_EXTRA = "Qty In";
	static String TBC_SUBGROUP = "Item Subgroup";
	
	SHorizontalLayout popupContainer,mainHorizontal;
	Object[] allColumns;
	Object[] visibleColumns;
	Object[] visibleColumnsWithExtra;
	STable table;
	SButton showButton;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public SPanel getGUI() {

		allColumns = new Object[] { TBC_SN, TBC_ID,TBC_ITEM,TBC_REAL, TBC_UNIT,TBC_EXTRA,TBC_SUBGROUP};
		visibleColumnsWithExtra = new Object[] { TBC_SN, TBC_ITEM,TBC_REAL, TBC_UNIT,TBC_EXTRA,TBC_SUBGROUP};
		visibleColumns = new Object[] { TBC_SN, TBC_ITEM,TBC_REAL, TBC_UNIT,TBC_SUBGROUP};
		mainHorizontal=new SHorizontalLayout();
		popupContainer = new SHorizontalLayout();
		showButton=new SButton(getPropertyName("show"));
		table = new STable(null, 650, 300);
		table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
		table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,Align.CENTER);
		table.addContainerProperty(TBC_ITEM, String.class, null,getPropertyName("item"), null, Align.LEFT);
		table.addContainerProperty(TBC_REAL, Double.class, null,getPropertyName("quantity"), null, Align.LEFT);
		table.addContainerProperty(TBC_UNIT, String.class, null,getPropertyName("unit"), null, Align.LEFT);
		table.addContainerProperty(TBC_EXTRA, Double.class, null,getPropertyName("qty in"), null, Align.LEFT);
		table.addContainerProperty(TBC_SUBGROUP, String.class, null,getPropertyName("item_subgroup"), null, Align.LEFT);

		table.setColumnExpandRatio(TBC_SN, (float) 0.3);
		table.setColumnExpandRatio(TBC_ITEM, 1);
		table.setColumnExpandRatio(TBC_SUBGROUP, 1);
		table.setColumnExpandRatio(TBC_REAL, 1);
		table.setSelectable(true);
		table.setMultiSelect(false);
		table.setVisibleColumns(visibleColumns);
		
		setSize(1050, 420);
		
		review=new ReportReview();
		confirmBox=new SConfirmWithReview("Review", getOfficeID());

//		isRackWise = new SCheckBox(getPropertyName("Rack wise Report"));

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
		dao = new ItemReportDao();

		session = getHttpSession();
		if (session.getAttribute("settings") != null)
			settings = (SettingsValuePojo) session.getAttribute("settings");

		date = new SDateField(getPropertyName("date"), 120, getDateFormat(),
				getWorkingDate());

		showTypeSelect = new SNativeSelect(getPropertyName("show_type"), 200);
		showTypeSelect.addItem(getPropertyName("all"));
		showTypeSelect.addItem("Non-zero Stock");
		showTypeSelect.addItem("Positive Stock");
		showTypeSelect.addItem("Not Stocked");
		showTypeSelect.addItem("Negetive Stock Only");

		showTypeSelect.setValue("All");

		rackSelect = new SComboField(getPropertyName("rack"), 200);

		officeCombo = new SOfficeComboField(getPropertyName("office"), 200);
		officeCombo.setValue(null);

		List groupList=new ArrayList();
		try {
			groupList.add(0, new ItemGroupModel(0, getPropertyName("all")));
			groupList.addAll(new ItemGroupDao().getAllActiveItemGroupsNames(getOrganizationID()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		itemGroupCombo = new SComboField(getPropertyName("item_group"),200, groupList, "id", "name",false,getPropertyName("all"));
		itemGroupCombo.setValue((long)0);
		itemSubGroupCombo = new SComboField(getPropertyName("item_sub_group"), 200, null, "id", "name",false,getPropertyName("all"));
		reloadSubGroupCombo();
		itemComboField = new SComboField(getPropertyName("item"), 200,null, "id", "name", false, getPropertyName("all"));
		locationCombo=new SComboField(getPropertyName("location"), 200, null, "id", "name", false, getPropertyName("all"));
		List unitList=new ArrayList();
		try {
			unitList.addAll(new UnitDao().getAllActiveUnits(getOrganizationID()));
		} catch (Exception e1) {
		}
		unitList.add(0,new UnitModel((long)0,"NONE"));
		showExtraUnitCombo=new SComboField(getPropertyName("Extra unit"), 200, unitList, "id", "symbol", false, getPropertyName("NONE"));
		showExtraUnitCombo.setValue((long)0);

		reportChoiceField = new SReportChoiceField(getPropertyName("export_to"));

		formLayout.addComponent(officeCombo);
		formLayout.addComponent(itemGroupCombo);
		formLayout.addComponent(itemSubGroupCombo);
		formLayout.addComponent(itemComboField);
		formLayout.addComponent(locationCombo);
		formLayout.addComponent(date);
		if (settings.isRACK_ENABLED()) {
//			formLayout.addComponent(isRackWise);
			formLayout.addComponent(rackSelect);
		} else
			formLayout.addComponent(showTypeSelect);

		formLayout.addComponent(showExtraUnitCombo);
		formLayout.addComponent(reportChoiceField);

		generateButton = new SButton(getPropertyName("generate"));
		generateButton.setClickShortcut(KeyCode.ENTER);
		buttonLayout.addComponent(generateButton);
		buttonLayout.addComponent(showButton);
		formLayout.addComponent(buttonLayout);

		ClickListener confirmListener=new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				// TODO Auto-generated method stub
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
		
		
		itemGroupCombo.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				try {
					reloadSubGroupCombo();
					reloadItemCombo();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		
		itemSubGroupCombo.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				try {
					reloadItemCombo();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		
		officeCombo.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				try {
					if (selected(officeCombo)){
						loadRacks((Long) officeCombo.getValue());
						reloadItemCombo();
						if (settings.isRACK_ENABLED())
							loadRacks((Long) officeCombo.getValue());
						List locationList=new ArrayList();
						locationList.add(0, new LocationModel(0, getPropertyName("all")));
						locationList.addAll(new LocationDao().getLocationModelList((Long)officeCombo.getValue()));
						locationCombo.setContainerDataSource(SCollectionContainer.setList(locationList, "id"));
						locationCombo.setItemCaptionPropertyId("name");
						locationCombo.setValue((long)0);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		officeCombo.setValue(getOfficeID());
		
		
		table.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				try{
					if (table.getValue() != null) {
						Item itm = table.getItem(table.getValue());
						SFormLayout form = new SFormLayout();
						form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("item_details")+"</u></h2>"));
						form.addComponent(new SLabel(getPropertyName("item"),itm.getItemProperty(TBC_ITEM).getValue().toString()));
						form.addComponent(new SLabel(getPropertyName("quantity"),itm.getItemProperty(TBC_REAL).getValue().toString()));
						form.addComponent(new SLabel(getPropertyName("unit"),itm.getItemProperty(TBC_UNIT).getValue().toString()));
						form.addComponent(new SLabel(getPropertyName("item_subgroup"),itm.getItemProperty(TBC_SUBGROUP).getValue().toString()));
						form.setStyleName("grid_max_limit");
						popupContainer.removeAllComponents();
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
		
		
		
		mainHorizontal.addComponent(formLayout);
		mainHorizontal.addComponent(table);
		mainHorizontal.addComponent(popupContainer);
		
		review.addComponent(mainHorizontal, "left: 0px; right: 0px; z-index:-1;");
		mainPanel.setContent(review);

		return mainPanel;
	}

	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void loadRacks(long officeID) {

		try {
			List list = new ArrayList();
			list.add(0, new RackModel(0,getPropertyName("all")));
			list.addAll(dao.getAllRacksUnderOffice(officeID));
			SCollectionContainer subGroupContainer = SCollectionContainer.setList(list, "id");
			rackSelect.setContainerDataSource(subGroupContainer);
			rackSelect.setItemCaptionPropertyId("rack_number");
			rackSelect.setValue(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@SuppressWarnings("rawtypes")
	protected boolean showReport() {
		boolean flag=false;
		try {
			List itemReportList=new ArrayList();
			table.removeAllItems();
			table.setVisibleColumns(allColumns);
			ArrayList<Object> reportList = new ArrayList<Object>();
			ItemReportBean bean = new ItemReportBean();

			long groupId = 0;
			long subgroupId = 0;
			long itemId = 0;
			long location = 0;
			if (selected(itemGroupCombo)) {
				groupId = toLong(itemGroupCombo.getValue().toString());
			}

			if (selected(itemSubGroupCombo)) {
				subgroupId = toLong(itemSubGroupCombo.getValue()
						.toString());
			}

			if (selected(itemComboField)) {
				itemId = toLong(itemComboField.getValue().toString());
			}
			
			if (selected(locationCombo)) {
				location = toLong(locationCombo.getValue().toString());
			}

			itemReportList = itemDao.getAllActiveItems((Long)officeCombo.getValue(),
														itemId,
														subgroupId,
														groupId,
														getOrganizationID());

			String show_type = showTypeSelect.getValue().toString();

			double real=0;
			double extraQty=0;
			double covQty=0;
			ItemModel allModel;

//			if (!isRackWise.getValue()) {

				for (int i = 0; i < itemReportList.size(); i++) {
				
					allModel = (ItemModel) itemReportList.get(i);
					covQty=comDao.getConvertionQty(allModel.getId(), (Long)showExtraUnitCombo.getValue(),0 );
					
					if (show_type.equals("All")) {
						bean = new ItemReportBean();
						real=comDao.getItemBalanceAtDateFromStock(allModel.getId(), location, CommonUtil.getSQLDateFromUtilDate(date.getValue()),(Long)officeCombo.getValue());
						extraQty=covQty*real;
						
						table.addItem(new Object[]{ table.getItemIds().size()+1,
													allModel.getId(),
													allModel.getName()+" [ "+allModel.getItem_code()+" ]",
													roundNumber(real),
													allModel.getUnit().getSymbol(),extraQty,
													allModel.getSub_group().getName()},table.getItemIds().size()+1);
						
					}
					else if (show_type.equals("Non-zero Stock") && allModel.getCurrent_balalnce() != 0) {
						bean = new ItemReportBean();
						real=comDao.getItemBalanceAtDateFromStock(allModel.getId(), location, CommonUtil.getSQLDateFromUtilDate(date.getValue()),(Long)officeCombo.getValue());
						extraQty=covQty*real;
						if (bean.getCurrent_quantity() != 0)
							table.addItem(new Object[]{ table.getItemIds().size()+1,
									allModel.getId(),
									allModel.getName()+" [ "+allModel.getItem_code()+" ]",
									roundNumber(real),
									allModel.getUnit().getSymbol(),extraQty,
									allModel.getSub_group().getName()},table.getItemIds().size()+1);
					} 
					else if (show_type.equals("Positive Stock") && allModel.getCurrent_balalnce() > 0) {
						bean = new ItemReportBean();
						real=comDao.getItemBalanceAtDateFromStock(allModel.getId(), location, CommonUtil.getSQLDateFromUtilDate(date.getValue()),(Long)officeCombo.getValue());
						extraQty=covQty*real;
						if (bean.getCurrent_quantity() > 0)
							table.addItem(new Object[]{ table.getItemIds().size()+1,
									allModel.getId(),
									allModel.getName()+" [ "+allModel.getItem_code()+" ]",
									roundNumber(real),
									allModel.getUnit().getSymbol(),extraQty,
									allModel.getSub_group().getName()},table.getItemIds().size()+1);
					}
					else if (show_type.equals("Not Stocked") && allModel.getCurrent_balalnce() <= 0) {
						bean = new ItemReportBean();
						real=comDao.getItemBalanceAtDateFromStock(allModel.getId(), location, CommonUtil.getSQLDateFromUtilDate(date.getValue()),(Long)officeCombo.getValue());
						extraQty=covQty*real;
						if (bean.getCurrent_quantity() <= 0)
							table.addItem(new Object[]{ table.getItemIds().size()+1,
									allModel.getId(),
									allModel.getName()+" [ "+allModel.getItem_code()+" ]",
									roundNumber(real),
									allModel.getUnit().getSymbol(),extraQty,
									allModel.getSub_group().getName()},table.getItemIds().size()+1);
						
					} 
					else if (show_type.equals("Negetive Stock Only") && allModel.getCurrent_balalnce() < 0) {
						bean = new ItemReportBean();
						real=comDao.getItemBalanceAtDateFromStock(allModel.getId(), location, CommonUtil.getSQLDateFromUtilDate(date.getValue()),(Long)officeCombo.getValue());
						extraQty=covQty*real;
						if (bean.getCurrent_quantity() < 0)
							table.addItem(new Object[]{ table.getItemIds().size()+1,
									allModel.getId(),
									allModel.getName()+" [ "+allModel.getItem_code()+" ]",
									roundNumber(real),
									allModel.getUnit().getSymbol(),extraQty,
									allModel.getSub_group().getName()},table.getItemIds().size()+1);
					}
				}
//			} 
			
			/*else {

				StockRackMappingModel map = null;
				List mapList = null;
				Iterator iter;
				long rackId = 0;

				if (rackSelect.getValue() != null&& !rackSelect.getValue().equals(""))
					rackId = (Long) rackSelect.getValue();

				for (int i = 0; i < itemReportList.size(); i++) {
					allModel = (ItemModel) itemReportList.get(i);
					mapList = dao.getStockMappingOfItems(allModel.getId(),rackId);

					iter = mapList.iterator();

					while (iter.hasNext()) {
						map = (StockRackMappingModel) iter.next();

						if (map.getBalance() > 0) {
							bean = new ItemReportBean();
							table.addItem(new Object[]{
									table.getItemIds().size()+1,
									allModel.getId(),
									allModel.getName()+"["+allModel.getItem_code()+"]",
									map.getBalance(),
									grvQty,
									map.getBalance()+grvQty,
									allModel.getUnit().getSymbol(),
									allModel.getSub_group().getName()},table.getItemIds().size()+1);
						}
					}
				}
			}*/
				if((Long)showExtraUnitCombo.getValue()!=0){
					table.setVisibleColumns(visibleColumnsWithExtra);
					table.setColumnHeader(TBC_EXTRA, "Qty In "+showExtraUnitCombo.getItemCaption(showExtraUnitCombo.getValue()));
				}else
					table.setVisibleColumns(visibleColumns);
				
			table.sort(new Object[]{TBC_ITEM}, new boolean[]{true});
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return flag;
	}
	

	@SuppressWarnings("rawtypes")
	protected boolean generateReport() {
		boolean flag=false;
		try {

			ArrayList<Object> reportList = new ArrayList<Object>();
			List itemReportList = new ArrayList();
			ItemReportBean bean = new ItemReportBean();

			long groupId = 0;
			long subgroupId = 0;
			long itemId = 0;
			long location = 0;
			if (selected(itemGroupCombo)) {
				groupId = toLong(itemGroupCombo.getValue().toString());
			}

			if (selected(itemSubGroupCombo)) {
				subgroupId = toLong(itemSubGroupCombo.getValue()
						.toString());
			}

			if (selected(itemComboField)) {
				itemId = toLong(itemComboField.getValue().toString());
			}
			
			if (selected(locationCombo)) {
				location = toLong(locationCombo.getValue().toString());
			}
			
			itemReportList = itemDao.getAllActiveItems((Long)officeCombo.getValue(), itemId, subgroupId, groupId, getOrganizationID());
			String show_type = showTypeSelect.getValue().toString();

			double grvQty = 0,covQty=0;
			ItemModel allModel;

//			if (!isRackWise.getValue()) {

				for (int i = 0; i < itemReportList.size(); i++) {
					allModel = (ItemModel) itemReportList.get(i);
					covQty=comDao.getConvertionQty(allModel.getId(), (Long)showExtraUnitCombo.getValue(),0 );
					if (show_type.equals("All")) {
						bean = new ItemReportBean();
						bean.setName(allModel.getName());
						bean.setCode(allModel.getItem_code());
						bean.setUnit(allModel.getUnit().getSymbol());
						bean.setSubgroup(allModel.getSub_group().getName());
						// bean.setCurrent_quantity(allModel.getCurrent_balalnce());
						bean.setCurrent_quantity(comDao.getItemBalanceAtDateFromStock(allModel.getId(), location, CommonUtil.getSQLDateFromUtilDate(date.getValue()),(Long)officeCombo.getValue()));
						bean.setQtyInExtraUnit(covQty*bean.getCurrent_quantity());
						reportList.add(bean);
					} else if (show_type.equals("Non-zero Stock")
							&& allModel.getCurrent_balalnce() != 0) {
						bean = new ItemReportBean();
						bean.setName(allModel.getName());
						bean.setCode(allModel.getItem_code());
						bean.setUnit(allModel.getUnit().getSymbol());
						bean.setSubgroup(allModel.getSub_group().getName());
						// bean.setCurrent_quantity(allModel.getCurrent_balalnce());
						bean.setCurrent_quantity(comDao.getItemBalanceAtDateFromStock(allModel.getId(), location, CommonUtil.getSQLDateFromUtilDate(date.getValue()),(Long)officeCombo.getValue()));
						bean.setQtyInExtraUnit(covQty*bean.getCurrent_quantity());
						if (bean.getCurrent_quantity() != 0)
							reportList.add(bean);
					} else if (show_type.equals("Positive Stock")
							&& allModel.getCurrent_balalnce() > 0) {
						bean = new ItemReportBean();
						bean.setName(allModel.getName());
						bean.setCode(allModel.getItem_code());
						bean.setUnit(allModel.getUnit().getSymbol());
						bean.setSubgroup(allModel.getSub_group().getName());
						// bean.setCurrent_quantity(allModel.getCurrent_balalnce());
						bean.setCurrent_quantity(comDao.getItemBalanceAtDateFromStock(allModel.getId(), location, CommonUtil.getSQLDateFromUtilDate(date.getValue()),(Long)officeCombo.getValue()));
						bean.setQtyInExtraUnit(covQty*bean.getCurrent_quantity());
						if (bean.getCurrent_quantity() > 0)
							reportList.add(bean);
					} else if (show_type.equals("Not Stocked") && allModel.getCurrent_balalnce() <= 0) {
						bean = new ItemReportBean();
						bean.setName(allModel.getName());
						bean.setCode(allModel.getItem_code());
						bean.setUnit(allModel.getUnit().getSymbol());
						bean.setSubgroup(allModel.getSub_group().getName());
						// bean.setCurrent_quantity(allModel.getCurrent_balalnce());
						bean.setCurrent_quantity(comDao.getItemBalanceAtDateFromStock(allModel.getId(), location, CommonUtil.getSQLDateFromUtilDate(date.getValue()),(Long)officeCombo.getValue()));
						bean.setQtyInExtraUnit(covQty*bean.getCurrent_quantity());
						if (bean.getCurrent_quantity() <= 0)
							reportList.add(bean);
					} else if (show_type.equals("Negetive Stock Only")
							&& allModel.getCurrent_balalnce() < 0) {
						bean = new ItemReportBean();
						bean.setName(allModel.getName());
						bean.setCode(allModel.getItem_code());
						bean.setUnit(allModel.getUnit().getSymbol());
						bean.setSubgroup(allModel.getSub_group().getName());
						// bean.setCurrent_quantity(allModel.getCurrent_balalnce());
						bean.setCurrent_quantity(comDao.getItemBalanceAtDateFromStock(allModel.getId(), location, CommonUtil.getSQLDateFromUtilDate(date.getValue()),(Long)officeCombo.getValue()));
						bean.setQtyInExtraUnit(covQty*bean.getCurrent_quantity());
						if (bean.getCurrent_quantity() < 0)
							reportList.add(bean);
					}
				}
				if((Long)showExtraUnitCombo.getValue()!=0)
					report.setJrxmlFileName("itemWithExtraUnit");
				else
					report.setJrxmlFileName("item");
				
				report.setReportFileName("item");

//			} 
			/*else {

				StockRackMappingModel map = null;
				List mapList = null;
				Iterator iter;
				long rackId = 0;

				if (rackSelect.getValue() != null
						&& !rackSelect.getValue().equals(""))
					rackId = (Long) rackSelect.getValue();

				for (int i = 0; i < itemReportList.size(); i++) {
					allModel = (ItemModel) itemReportList.get(i);
					mapList = dao.getStockMappingOfItems(allModel.getId(),
							rackId);

					iter = mapList.iterator();

					while (iter.hasNext()) {
						map = (StockRackMappingModel) iter.next();

						if (map.getBalance() > 0) {
							bean = new ItemReportBean();
							bean.setName(allModel.getName());
							bean.setCode(allModel.getItem_code());
							bean.setUnit(allModel.getUnit().getSymbol());
							bean.setSubgroup(allModel.getSub_group().getName());
							bean.setCurrent_quantity(map.getBalance());
							bean.setGrv_quantity(grvQty);
							bean.setRack(map.getRack().getRack_number());

							reportList.add(bean);
						}
					}
				}

				report.setJrxmlFileName("itemWithRack");
				report.setReportFileName("itemWithRack");
			}*/

			if (reportList.size() > 0) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				
				map.put("REPORT_TITLE_LABEL", getPropertyName("item_report"));
				map.put("SL_NO_LABEL", getPropertyName("sl_no"));
				map.put("NAME_LABEL", getPropertyName("name"));
				map.put("CODE_LABEL", getPropertyName("code"));
				map.put("REAL_STOCK_LABEL", getPropertyName("quantity"));
				map.put("GRV_STOCK_LABEL", getPropertyName("GRV_stock"));
				map.put("TOTAL_STOCK_LABEL", getPropertyName("total_stock"));
				map.put("UNIT_LABEL", getPropertyName("unit"));
				map.put("ITEM_SUBGROUP_LABEL", getPropertyName("item_subgroup"));
				map.put("RACK_LABEL", getPropertyName("rack"));
				map.put("QUANTITY_LABEL", getPropertyName("quantity"));
				map.put("QTY_IN_EXTRA_UNIT_LABEL", getPropertyName("Qty In "+showExtraUnitCombo.getItemCaption(showExtraUnitCombo.getValue())));
				
				
				String subTitle = "";
				if (selected(itemGroupCombo)) {
					subTitle += getPropertyName("item_group")+" : "
							+ itemGroupCombo
									.getItemCaption(itemGroupCombo
											.getValue());
				}
				if (selected(itemSubGroupCombo)) {
					subTitle += "\t "+getPropertyName("item_subgroup")+" : "
							+ itemSubGroupCombo
									.getItemCaption(itemSubGroupCombo
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
				report.setOfficeName(officeCombo
						.getItemCaption(officeCombo.getValue()));
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

	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void reloadSubGroupCombo() {
		try {
			List subGroupList=new ArrayList();
			subGroupList.add(0, new ItemSubGroupModel(0, getPropertyName("all")));
			if (selected(itemGroupCombo)) {
				subGroupList.addAll(itemSubGroupDao.getAllActiveItemSubGroups((Long)itemGroupCombo.getValue()));
			} else {
				subGroupList.addAll(itemSubGroupDao.getAllActiveItemSubGroupsNames(getOrganizationID()));
			}
			SCollectionContainer subGroupContainer = SCollectionContainer.setList(subGroupList, "id");
			itemSubGroupCombo.setContainerDataSource(subGroupContainer);
			itemSubGroupCombo.setItemCaptionPropertyId("name");
			itemSubGroupCombo.setValue((long)0);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void reloadItemCombo() {
		try {
			List itemList=new ArrayList();
			itemList.add(0, new ItemModel(0, getPropertyName("all")));
			itemList.addAll(itemDao.getAllActiveItemsWithAppendingItemCode(getValue(officeCombo),
																	getValue(itemSubGroupCombo),
																	getValue(itemGroupCombo)));
			SCollectionContainer itemContainer = SCollectionContainer.setList(itemList, "id");
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
