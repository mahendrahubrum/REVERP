package com.inventory.config.stock.ui;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

import org.vaadin.data.collectioncontainer.CollectionContainer;
import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.config.acct.dao.SupplierDao;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.config.stock.dao.BrandDao;
import com.inventory.config.stock.dao.ColourDao;
import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.dao.ItemGroupDao;
import com.inventory.config.stock.dao.ItemModelDao;
import com.inventory.config.stock.dao.ItemSubGroupDao;
import com.inventory.config.stock.dao.SizeDao;
import com.inventory.config.stock.dao.StyleDao;
import com.inventory.config.stock.model.BrandModel;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.config.tax.dao.TaxDao;
import com.inventory.config.tax.model.TaxModel;
import com.inventory.config.unit.dao.UnitDao;
import com.inventory.config.unit.model.ItemUnitMangementModel;
import com.inventory.config.unit.model.UnitModel;
import com.inventory.config.unit.ui.AddUnitUI;
import com.inventory.model.ItemSubGroupModel;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.FileResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
import com.webspark.Components.SButton;
import com.webspark.Components.SCheckBox;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SContainerPanel;
import com.webspark.Components.SCurrencyField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFileUpload;
import com.webspark.Components.SFileUploder;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SListSelect;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SRadioButton;
import com.webspark.Components.STextArea;
import com.webspark.Components.STextField;
import com.webspark.Components.STokenField;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.dao.LanguageDao;
import com.webspark.model.CurrencyModel;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.model.S_OfficeModel;

public class ItemPanel extends SContainerPanel {

	private static final long serialVersionUID = 3200006153726808092L;

	long id;

	SGridLayout hLayout;
	// SVerticalLayout vLayout;
	SFormLayout form;
	SFormLayout extraForm;
	HorizontalLayout buttonLayout;

	CollectionContainer bic;

	SComboField itemCombo;
	STextField itemCodeField;
	STextField supplierItemCodeField;
	STextField itemNameField;
	STextField quantityField;
	SCurrencyField purchaseRateField;
	SCurrencyField saleRateField;
	SCurrencyField discountRateField;
	SCurrencyField maxDiscountRateField;
	
	STextField minLevelField;
	STextField maxLevelField;
	
	SDateField openingStockDate;
	
	STextField reorderField;
	SComboField subgroupsCombo;
	SComboField groupsCombo;
	SComboField statusCombo;
	SNativeSelect saleTaxSelect;
	SNativeSelect purchaseTaxSelect;
	SNativeSelect unitSelect;

	SComboField sizeCombo;
	SComboField colourCombo;
	SComboField modelCombo;
	SComboField styleCombo;
	SComboField brandCombo;
	
	STextArea descriptionField;
	STextField quantityInStockField;
	STextArea specificationField;

	SButton saveButton;
	SButton deleteButton;
	SButton updateButton;
	// SButton createNew;
	
	STokenField supplierToken;

	@SuppressWarnings("rawtypes")
	List list;
	ItemDao objDao = new ItemDao();
	ItemSubGroupDao subGpDao = new ItemSubGroupDao();
	ItemGroupDao gpDao = new ItemGroupDao();
	TaxDao taxDao = new TaxDao();
	UnitDao unitDao = new UnitDao();

	boolean taxEnable = isTaxEnable();

	SButton newSaleButton;

	SFileUpload fileUpload;
	SFileUploder uploader;
	
	Image image;
	SFormLayout imageLayout;
	SCheckBox imageSelectBox;
	SButton removeImageButton;

	private SFormLayout imageMainLayout;

	private SRadioButton affectType;
	SButton newUnitButton;
	private SimpleDateFormat df;
	@SuppressWarnings("rawtypes")
	private List imageList;

	SButton newBrandButton;
	
	private WrappedSession session;
	private SettingsValuePojo settings;
	
	private SListSelect officeSel;
	
	OfficeDao ofcDao;

	private SNativeSelect languageNativeSelect;

	private STextField itemNameInAnotherLanguage;

	private SHorizontalLayout itemNameInAnotherLangLayout;

	private LanguageDao languageDao;

	@SuppressWarnings({ "serial", "unchecked", "rawtypes", "deprecation" })
	public ItemPanel() {
		newUnitButton = new SButton();
		newUnitButton.setStyleName("smallAddNewBtnStyle");
		newUnitButton.setDescription(getPropertyName("add_new_unit"));
		taxEnable = isTaxEnable();

		newSaleButton = new SButton();
		newSaleButton.setStyleName("createNewBtnStyle");
		newSaleButton.setDescription("create_new");
		supplierToken = new STokenField (getPropertyName("preferred_vendors"));
		setId("Item");
		setSize(1000, 570);

		objDao = new ItemDao();

		df = new SimpleDateFormat("ddMMyyyyHHmmssSSS");

		session = getHttpSession();
		if (session.getAttribute("settings") != null)
			settings = (SettingsValuePojo) session.getAttribute("settings");

		try {
			ofcDao=new OfficeDao();
			hLayout = new SGridLayout(4,1);
			hLayout.setColumnExpandRatio(0, 2.3f);
			hLayout.setColumnExpandRatio(1, 2f);
			hLayout.setColumnExpandRatio(2, 2f);
			hLayout.setSizeFull();
			form = new SFormLayout();
			extraForm = new SFormLayout();
			buttonLayout = new HorizontalLayout();

			SGridLayout buttonGridLayout = new SGridLayout(8, 1);
			buttonGridLayout.setSpacing(true);

			form.setSizeFull();

			saveButton = new SButton(getPropertyName("Save"));
			saveButton.setStyleName("savebtnStyle");
			saveButton.setIcon(new ThemeResource("icons/saveSideIcon.png"));
			deleteButton = new SButton(getPropertyName("Delete"));
			deleteButton.setIcon(new ThemeResource("icons/deleteSideIcon.png"));
			deleteButton.setStyleName("deletebtnStyle");
			updateButton = new SButton(getPropertyName("Update"));
			updateButton.setIcon(new ThemeResource("icons/updateSideIcon.png"));
			updateButton.setStyleName("updatebtnStyle");

			buttonLayout.addComponent(saveButton);
			buttonLayout.addComponent(updateButton);
			buttonLayout.addComponent(deleteButton);

			buttonLayout.setSpacing(true);
			buttonGridLayout.addComponent(buttonLayout, 4, 0);

			deleteButton.setVisible(false);
			updateButton.setVisible(false);
			
			List supplierList=new SupplierDao().getAllActiveSupplierNamesWithLedgerID(getOfficeID());
			SCollectionContainer sbic=SCollectionContainer.setList(supplierList, "id");
			supplierToken.setContainerDataSource(sbic);
			supplierToken.setWidth("200");
			supplierToken.setTokenCaptionPropertyId("name");
			supplierToken.setStyleName(STokenField.STYLE_TOKENFIELD);
			supplierToken.setNewTokensAllowed(false);
			supplierToken.setFilteringMode(SComboField.FILTERINGMODE_CONTAINS);

			// list = objDao.getAllActiveItems(getOfficeID());
			ItemModel og = new ItemModel();
			og.setId(0);
			og.setName("----------- "+getPropertyName("create_new")+" ----------");
			/*
			 * if (list == null) list = new ArrayList(); list.add(0, og);
			 */

			itemCombo = new SComboField(null, 200, null, "id", "name");

			itemCombo.setInputPrompt(getPropertyName("create_new"));

			statusCombo = new SComboField(getPropertyName("status"), 200,
					SConstants.statuses.status,
					"key", "value");
			statusCombo
					.setInputPrompt(getPropertyName("select"));
			statusCombo.setValue((long)1);

			itemCodeField = new STextField(getPropertyName("item_code"),200);
			itemCodeField.setInputPrompt(getPropertyName("item_code"));
			supplierItemCodeField = new STextField(getPropertyName("supplier_item_code"),200);
			supplierItemCodeField.setInputPrompt(getPropertyName("supplier_item_code"));
			
			
			itemNameField = new STextField(getPropertyName("item_name"),200);
			itemNameField.setInputPrompt(getPropertyName("item_name"));
			itemNameField.setMaxLength(200);
			
			languageDao = new LanguageDao();
			
			itemNameInAnotherLanguage = new STextField(null, 150);
			languageNativeSelect =new SNativeSelect(null, 70, languageDao.getAllLanguages(), "id", "name");
			languageNativeSelect.setValue(ofcDao.getOffice(getOfficeID()).getLanguage());
			//itemNameField.setInputPrompt(getPropertyName("item_name"));
			
			itemNameInAnotherLangLayout = new SHorizontalLayout(getPropertyName("item_name_in_"));
			
			itemNameInAnotherLangLayout.addComponent(languageNativeSelect);
			itemNameInAnotherLangLayout.addComponent(itemNameInAnotherLanguage);

			quantityField = new STextField(getPropertyName("opening_quantity"),200, "0.0");
			
			purchaseRateField=new SCurrencyField(getPropertyName("purchase_rate"), 100, getWorkingDate());
			saleRateField=new SCurrencyField(getPropertyName("sale_rate"), 100, getWorkingDate());
			discountRateField=new SCurrencyField(getPropertyName("discount"), 100, getWorkingDate());
			maxDiscountRateField=new SCurrencyField(getPropertyName("max_discount"), 100, getWorkingDate());
			discountRateField.currencySelect.setReadOnly(true);
			maxDiscountRateField.currencySelect.setReadOnly(true);
			
			minLevelField = new STextField(getPropertyName("minimum_level"), 200, "0.0");
			maxLevelField = new STextField(getPropertyName("maximum_level"), 200, "0.0");
			openingStockDate=new SDateField(getPropertyName("opening_stock_date"), 100);
			openingStockDate.setValue(getWorkingDate());
			
			reorderField = new STextField(getPropertyName("reorder_level"), 200, "0.00");

			subgroupsCombo = new SComboField(getPropertyName("item_sub_group"), 200,
					subGpDao.getAllActiveItemSubGroupsNames(getOrganizationID()), "id", "name",true,getPropertyName("select"));

			groupsCombo = new SComboField(getPropertyName("item_group"), 200,
					gpDao.getAllActiveItemGroupsNames(getOrganizationID()),
					"id", "name");
			groupsCombo
					.setInputPrompt(getPropertyName("select"));

			saleTaxSelect = new SNativeSelect(getPropertyName("sales_tax"),
					200, taxDao.getAllActiveTaxesFromType(getOfficeID(), 1),
					"id", "name");
			purchaseTaxSelect = new SNativeSelect(
					getPropertyName("purchase_tax"), 200,
					taxDao.getAllActiveTaxesFromType(getOfficeID(), 2), "id",
					"name");
			SHorizontalLayout unitLayout=new SHorizontalLayout(getPropertyName("unit"));
			
			unitSelect = new SNativeSelect(null, 200,
					unitDao.getAllActiveUnits(getOrganizationID()), "id",
					"symbol");
			if(unitSelect.getItemIds().iterator().hasNext())
				unitSelect.setValue(unitSelect.getItemIds().iterator().next());
			unitLayout.addComponent(unitSelect);
			unitLayout.addComponent(newUnitButton);
			if (taxEnable) {
				saleTaxSelect.setVisible(true);
				purchaseTaxSelect.setVisible(true);
			} else {
				saleTaxSelect.setVisible(false);
				purchaseTaxSelect.setVisible(false);
			}

			affectType = new SRadioButton(getPropertyName("affect_type"), 200,
					SConstants.affect_type.affect_type, "intKey", "value");
			affectType.setHorizontal(true);
			affectType.setValue(SConstants.affect_type.AFFECT_ALL);

			BrandModel brandModel = new BrandModel(0, "General");
			
			List brandLis = new ArrayList();
			brandLis.add(0, brandModel);
			brandLis.addAll(new BrandDao().getAllBrands(getOrganizationID()));
			
			brandCombo = new SComboField(null, 200,brandLis, "id", "name");
			SHorizontalLayout brandLayout=new SHorizontalLayout(getPropertyName("brand_name"));
			brandLayout.addComponent(brandCombo);
			brandCombo.setValue((long) 0);
			
			sizeCombo = new SComboField(getPropertyName("size"), 200,new SizeDao().getAllActiveSizeModel(getOfficeID()), "id", "name",true,getPropertyName("select"));
			
			colourCombo = new SComboField(getPropertyName("colour"), 200,new ColourDao().getAllActiveColourModel(getOfficeID()), "id", "name",true,getPropertyName("select"));
			
			modelCombo = new SComboField(getPropertyName("model"), 200,new ItemModelDao().getAllActiveItemModelModel(getOfficeID()), "id", "name",true,getPropertyName("select"));
			
			styleCombo = new SComboField(getPropertyName("style"), 200,new StyleDao().getAllActiveStyleModel(getOfficeID()), "id", "name",true,getPropertyName("select"));
			
			newBrandButton = new SButton();
			newBrandButton.setStyleName("smallAddNewBtnStyle");
			newBrandButton.setDescription(getPropertyName("add_new_brand"));
			
			brandLayout.addComponent(newBrandButton);
			
			descriptionField = new STextArea(getPropertyName("description"),200, 40);
			descriptionField.setInputPrompt(getPropertyName("description"));
			descriptionField.setMaxLength(250);
			specificationField = new STextArea(getPropertyName("specification"), 200, 40);
			specificationField.setInputPrompt(getPropertyName("specification"));
			
			quantityInStockField = new STextField(getPropertyName("quantity_in_stock"), 200);
			quantityInStockField.setValue("0");
			quantityInStockField.setReadOnly(true);
			
			officeSel=new SListSelect(getPropertyName("create_in_offices"),200, ofcDao.getAllOfficeNamesUnderOrg(getOrganizationID()),"id","name");
			officeSel.setHeight("100px");
			officeSel.setMultiSelect(true);
			officeSel.setNullSelectionAllowed(true);
			HashSet set=new HashSet(officeSel.getItemIds());
			officeSel.setValue(set);

			SHorizontalLayout salLisrLay = new SHorizontalLayout(
					getPropertyName("items"));
			salLisrLay.addComponent(itemCombo);
			salLisrLay.addComponent(newSaleButton);

			form.addComponent(salLisrLay);
			form.addComponent(itemCodeField);
			form.addComponent(supplierItemCodeField);
			form.addComponent(itemNameField);
			if(settings.isITEMS_IN_MULTIPLE_LANGUAGE()){
				form.addComponent(itemNameInAnotherLangLayout);
			}
			form.addComponent(groupsCombo);
			form.addComponent(subgroupsCombo);
			form.addComponent(quantityField);
			form.addComponent(reorderField);
			form.addComponent(minLevelField);
			form.addComponent(maxLevelField);
			form.addComponent(saleTaxSelect);
			form.addComponent(purchaseTaxSelect);
			form.addComponent(unitLayout);
			form.addComponent(statusCombo);
			form.addComponent(quantityInStockField);

			form.addComponent(buttonGridLayout);

			hLayout.addComponent(form);

			
			extraForm.addComponent(affectType);
			extraForm.addComponent(openingStockDate);
			extraForm.addComponent(purchaseRateField);
			extraForm.addComponent(saleRateField);
			extraForm.addComponent(discountRateField);
			extraForm.addComponent(maxDiscountRateField);
			
			if (settings.isSHOW_ITEM_ATTRIBUTES()) {
				
				extraForm.addComponent(modelCombo);
				extraForm.addComponent(colourCombo);
				extraForm.addComponent(sizeCombo);
				extraForm.addComponent(styleCombo);
				extraForm.addComponent(brandLayout);
			}
			extraForm.addComponent(supplierToken);
			extraForm.addComponent(specificationField);
			extraForm.addComponent(descriptionField);
			extraForm.addComponent(officeSel);

			hLayout.addComponent(extraForm);

			uploader = new SFileUploder();
			fileUpload = new SFileUpload(null, uploader);
			fileUpload.setButtonCaption(getPropertyName("upload_img"));
			fileUpload.setImmediate(true);
			removeImageButton = new SButton(getPropertyName("remove"));

			imageList = new ArrayList();
			imageLayout = new SFormLayout();
			imageMainLayout = new SFormLayout();

			// imageLayout.addComponent(image);
			imageLayout.setStyleName("inout_layout");
			SHorizontalLayout uploaderLay = new SHorizontalLayout();
			uploaderLay.setSpacing(true);

			uploaderLay.addComponent(fileUpload);
			uploaderLay.addComponent(removeImageButton);

			imageMainLayout.addComponent(imageLayout);
			imageMainLayout.addComponent(uploaderLay);

			hLayout.addComponent(imageMainLayout);

			hLayout.setSpacing(true);
			hLayout.setSizeFull();

			loadOptions(0);

			setContent(hLayout);
			languageNativeSelect.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					itemNameInAnotherLangLayout.setCaption(getPropertyName("item_name_in_")+" "
							+languageNativeSelect.getItemCaption(languageNativeSelect.getValue()));				
				}
			});
			addShortcutListener(new ShortcutListener("Add New",
					ShortcutAction.KeyCode.N,
					new int[] { ShortcutAction.ModifierKey.ALT }) {
				@Override
				public void handleAction(Object sender, Object target) {
					loadOptions(0);
				}
			});
			
			addShortcutListener(new ShortcutListener("Save",
					ShortcutAction.KeyCode.ENTER, null) {
				@Override
				public void handleAction(Object sender, Object target) {
					if (saveButton.isVisible())
						saveButton.click();
					else
						updateButton.click();
				}
			});
			
			
			saleRateField.currencySelect.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					if(saleRateField.currencySelect.getValue()!=null){
						discountRateField.setNewCurrency(saleRateField.getCurrency());
						maxDiscountRateField.setNewCurrency(saleRateField.getCurrency());
						discountRateField.setNewCurrency(saleRateField.getCurrency());
						discountRateField.rateButton.setVisible(false);
						maxDiscountRateField.rateButton.setVisible(false);
					}
				}
			});
			saleRateField.setValue(getCurrencyID(), 0.0);
			purchaseRateField.setValue(getCurrencyID(), 0.0);
			discountRateField.setNewValue(0.0);
			maxDiscountRateField.setNewValue( 0.0);

			
			final CloseListener unitCloseListener = new CloseListener() {

				@Override
				public void windowClose(CloseEvent e) {
					try {
						List unitList=unitDao.getAllActiveUnits(getOrganizationID());
						SCollectionContainer unitbic=SCollectionContainer.setList(unitList, "id");
						unitSelect.setContainerDataSource(unitbic);
						unitSelect.setItemCaptionPropertyId("symbol");
						unitSelect.setValue(null);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			};
			
			newUnitButton.addClickListener(new ClickListener() {
				
				@SuppressWarnings("static-access")
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						AddUnitUI unitUi=new AddUnitUI();
						unitUi.setCaption(getPropertyName("add_new_unit"));
						unitUi.center();
						getUI().getCurrent().addWindow(unitUi);
						unitUi.addCloseListener(unitCloseListener);
					}catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			// Brand
			final CloseListener brandCloseListener = new CloseListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void windowClose(CloseEvent e) {
					try {
						List brandLis = new ArrayList();
						brandLis.add(0, new BrandModel(0, "General"));
						brandLis.addAll(new BrandDao().getAllBrands(getOrganizationID()));
						brandCombo = new SComboField(null, 200,brandLis, "id", "name");
						SCollectionContainer unitbic=SCollectionContainer.setList(brandLis, "id");
						brandCombo.setContainerDataSource(unitbic);
						brandCombo.setItemCaptionPropertyId("name");
						brandCombo.setValue((long)0);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			};
			
			newBrandButton.addClickListener(new ClickListener() {
				
				@SuppressWarnings("static-access")
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						AddBrandUI unitUi=new AddBrandUI();
						unitUi.setCaption(getPropertyName("add_new_brand"));
						unitUi.center();
						getUI().getCurrent().addWindow(unitUi);
						unitUi.addCloseListener(brandCloseListener);
					}catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			fileUpload.addSucceededListener(new SucceededListener() {

				@Override
				public void uploadSucceeded(SucceededEvent event) {
					if (uploader.getFile() != null) {

						SHorizontalLayout imgLay = new SHorizontalLayout();

						image = new Image(null, new FileResource(uploader
								.getFile()));
						image.setStyleName("user_photo");
						image.setWidth("120");
						image.setHeight("110");
						image.markAsDirty();
						imageSelectBox = new SCheckBox();
						imgLay.addComponent(imageSelectBox);
						imgLay.addComponent(image);
						imageLayout.addComponent(imgLay);
						imageList.add(uploader.getFile());

					}
				}
			});

			removeImageButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					if (uploader.getFile() != null) {
						uploader.deleteFile();
					}
					// if(itemCombo.getValue()!=null&&!itemCombo.getValue().equals("")){
					// deleteImage((Long)itemCombo.getValue());
					// }
					SHorizontalLayout hor;
					SCheckBox check = null;
					List remList = new ArrayList();
					Iterator iter = imageLayout.iterator();
					while (iter.hasNext()) {
						hor = (SHorizontalLayout) iter.next();
						check = (SCheckBox) hor.getComponent(0);
						if (check.getValue()) {
							remList.add(hor);
						}
					}
					Image imag;
					iter = remList.iterator();
					while (iter.hasNext()) {
						hor = ((SHorizontalLayout) iter.next());
						imageLayout.removeComponent(hor);
						imag = (Image) hor.getComponent(1);
						imageList.remove(((FileResource) imag.getSource())
								.getSourceFile());
					}
				}
			});

			newSaleButton.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					itemCombo.setValue((long)0);
					itemCombo.setValue(null);
				}
			});

			saveButton.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {

						if (itemCombo.getValue() == null || itemCombo.getValue().toString().equals("0")) {

							if (isValid()) {
								
								String fileName = "";
								if (imageList.size() > 0) {
									fileName = getFileName();
								}
								
								String supplier="";
								if (supplierToken.getValue() != null) {
									Iterator it = ((Set<Long>) supplierToken.getValue()).iterator();
									while (it.hasNext()) {
										supplier+=(Long) it.next()+",";
									}
								}
								
								S_OfficeModel ofcMdl;
								List mainList=new ArrayList();
								Set set=new HashSet();
								set.addAll((Set) officeSel.getValue());
								List idList=new ArrayList();
								idList.addAll(set);
//								idList.add(getOfficeID());;
//								List idList=ofcDao.getAllOfficesUnderOrg(getOrganizationID());
								Iterator iter=idList.iterator();
								while (iter.hasNext()) {
									ofcMdl = (S_OfficeModel)ofcDao.getOffice((Long) iter.next());
								
									ItemModel objModel = new ItemModel();
									objModel.setItem_code(itemCodeField.getValue());
									objModel.setSupplier_code(supplierItemCodeField.getValue());
									objModel.setName(itemNameField.getValue());
									if(settings.isITEMS_IN_MULTIPLE_LANGUAGE()){
										objModel.setSecondName(itemNameInAnotherLanguage.getValue());
									} else {
										objModel.setSecondName(itemNameField.getValue());
									}
									objModel.setLanguageId((Long)languageNativeSelect.getValue());
									objModel.setSub_group(new ItemSubGroupModel((Long) subgroupsCombo.getValue()));
									
									if(ofcMdl.getId()==getOfficeID()){
										objModel.setOpening_balance(roundNumber(toDouble(quantityField.getValue().toString())));
										objModel.setCurrent_balalnce(roundNumber(toDouble(quantityField.getValue().toString())));
									}else{
										objModel.setOpening_balance(0);
										objModel.setCurrent_balalnce(0);
									}
								
									objModel.setReorder_level(roundNumber(toDouble(reorderField.getValue())));
									objModel.setMinimum_level(roundNumber(toDouble(minLevelField.getValue().toString())));
									objModel.setMaximum_level(roundNumber(toDouble(maxLevelField.getValue().toString())));
									if (taxEnable) {
										objModel.setSalesTax(new TaxModel((Long) saleTaxSelect.getValue()));
										objModel.setPurchaseTax(new TaxModel((Long) purchaseTaxSelect.getValue()));
									} else {
										objModel.setSalesTax(new TaxModel(1));
										objModel.setPurchaseTax(new TaxModel(1));
									}
									objModel.setUnit(new UnitModel((Long) unitSelect.getValue()));
									objModel.setStatus((Long) statusCombo.getValue());
									objModel.setOpening_stock_date(CommonUtil.getSQLDateFromUtilDate(openingStockDate.getValue()));
									objModel.setAffect_type((Integer) affectType.getValue());
									
									objModel.setPurchaseCurrency(new CurrencyModel(purchaseRateField.getCurrency()));
									objModel.setRate(roundNumber(purchaseRateField.getValue()));
									objModel.setSaleCurrency(new CurrencyModel(saleRateField.getCurrency()));
									objModel.setSale_rate(roundNumber(saleRateField.getValue()));
									objModel.setDiscount(roundNumber(discountRateField.getValue()));
									objModel.setMax_discount(roundNumber(maxDiscountRateField.getValue()));
									objModel.setPurchase_convertion_rate(roundNumber(purchaseRateField.getConversionRate()));
									objModel.setSale_convertion_rate(roundNumber(saleRateField.getConversionRate()));
									
									if(modelCombo.getValue()!=null)
										objModel.setItem_model((Long)modelCombo.getValue());
									else
										objModel.setItem_model((long)0);
									if(colourCombo.getValue()!=null)
										objModel.setColour((Long)colourCombo.getValue());
									else
										objModel.setColour((long)0);
									if(sizeCombo.getValue()!=null)
										objModel.setSize((Long)sizeCombo.getValue());
									else
										objModel.setSize((long)0);
									if(styleCombo.getValue()!=null)
										objModel.setStyle((Long)styleCombo.getValue());
									else
										objModel.setStyle((long)0);
									if(brandCombo.getValue()!=null)
										objModel.setBrand((Long)brandCombo.getValue());
									else
										objModel.setBrand((long)0);
									
									if(ofcMdl.getId()==getOfficeID())
										objModel.setPreferred_vendor(supplier);
									else
										objModel.setPreferred_vendor("");
									
									objModel.setSpecification(specificationField.getValue());
									objModel.setDesciption(descriptionField.getValue());
									objModel.setIcon(fileName);
									objModel.setOffice(ofcMdl);
									objModel.setCess_enabled('N');
									objModel.setReservedQuantity(0);
									objModel.setParentId(objDao.getParentUnderGroup((Long) subgroupsCombo.getValue()));
	
									ItemUnitMangementModel objMdl = new ItemUnitMangementModel();
									objMdl.setAlternateUnit(objModel.getUnit().getId());
									objMdl.setBasicUnit(objModel.getUnit().getId());
									objMdl.setConvertion_rate(1);
									objMdl.setSales_type(0);
									objMdl.setItem_price(roundNumber(purchaseRateField.getValue()));
									objMdl.setPurchaseCurrency(new CurrencyModel(purchaseRateField.getCurrency()));
									objMdl.setPurchase_convertion_rate(roundNumber(purchaseRateField.getConversionRate()));
									objMdl.setStatus(2);
									objMdl.setItem(objModel);
									
									mainList.add(objMdl);
								}
								
								long id = objDao.save(mainList,getOfficeID());
								
								try {
									if (imageLayout.getComponentCount() > 0) {
										saveImageAsPNG(fileName);
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
								
								loadOptions(id);
								Notification.show(getPropertyName("save_success"),Type.WARNING_MESSAGE);
							}
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			});

			itemCombo.addValueChangeListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						if (uploader.getFile() != null)
							uploader.deleteFile();

						imageList.clear();
						imageLayout.removeAllComponents();
						purchaseRateField.setNewValue(getCurrencyID(), 0.0);
						saleRateField.setNewValue(getCurrencyID(), 0.0);
						discountRateField.setNewValue(getCurrencyID(), 0.0);
						maxDiscountRateField.setNewValue(getCurrencyID(), 0.0);
						if (itemCombo.getValue() != null && !itemCombo.getValue().toString().equals("0")) {
							saveButton.setVisible(false);
							deleteButton.setVisible(true);
							updateButton.setVisible(true);
							officeSel.setVisible(false);
							ItemModel objModel = objDao.getItem((Long) itemCombo.getValue());
							itemCodeField.setValue(objModel.getItem_code());
							supplierItemCodeField.setValue(objModel.getSupplier_code());
							itemNameField.setValue(objModel.getName());
							itemNameInAnotherLanguage.setValue(objModel.getSecondName());
							languageNativeSelect.setValue(objModel.getLanguageId());
							groupsCombo.setValue(objModel.getSub_group().getGroup().getId());
							subgroupsCombo.setValue(objModel.getSub_group().getId());
							quantityField.setNewValue(""+ roundNumber(objModel.getOpening_balance()));
							quantityField.setReadOnly(true);
							reorderField.setValue(roundNumber(objModel.getReorder_level()) + "");
							minLevelField.setValue(roundNumber(objModel.getMinimum_level())+"");
							maxLevelField.setValue(roundNumber(objModel.getMaximum_level())+"");
							if (taxEnable) {
								saleTaxSelect.setValue(objModel.getSalesTax().getId());
								purchaseTaxSelect.setValue(objModel.getPurchaseTax().getId());
							}
							else{
								saleTaxSelect.setValue(null);
								purchaseTaxSelect.setValue(null);
							}
							unitSelect.setValue(objModel.getUnit().getId());
							statusCombo.setValue(objModel.getStatus());
							quantityInStockField.setNewValue(asString(objModel.getCurrent_balalnce()));
							openingStockDate.setValue(objModel.getOpening_stock_date());
							affectType.setValue(objModel.getAffect_type());
							
							purchaseRateField.setNewValue(roundNumber(objModel.getRate()));
							if(objModel.getPurchaseCurrency()!=null)
								purchaseRateField.setNewCurrency(objModel.getPurchaseCurrency().getId());
							purchaseRateField.conversionField.setValue(""+roundNumber(objModel.getPurchase_convertion_rate()));
							
							saleRateField.setNewValue(roundNumber(objModel.getSale_rate()));
							if(objModel.getSaleCurrency()!=null)
								saleRateField.setNewCurrency(objModel.getSaleCurrency().getId());
							saleRateField.conversionField.setValue(""+roundNumber(objModel.getSale_convertion_rate()));
							discountRateField.setNewValue(roundNumber(objModel.getDiscount()));
							maxDiscountRateField.setNewValue(roundNumber(objModel.getMax_discount()));
							
							
							if(objModel.getSize()!=0)
								sizeCombo.setValue(objModel.getSize());
							else
								sizeCombo.setValue(null);
							if(objModel.getColour()!=0)
								colourCombo.setValue(objModel.getColour());
							else
								colourCombo.setValue(null);
							if(objModel.getStyle()!=0)
								styleCombo.setValue(objModel.getStyle());
							else
								styleCombo.setValue(null);
							if(objModel.getItem_model()!=0)
								modelCombo.setValue(objModel.getItem_model());
							else
								modelCombo.setValue(null);
							brandCombo.setValue(objModel.getBrand());
							Set<Long> descSet = new HashSet<Long>();
							if(objModel.getPreferred_vendor().length()>0){
								String[]  supplierArray=objModel.getPreferred_vendor().split(",");
								for(int i=0;i<supplierArray.length;i++){
									descSet.add(toLong(supplierArray[i]));
								}
								supplierToken.setValue(descSet);
							}
							else
								supplierToken.setValue(null);
							descriptionField.setValue(objModel.getDesciption());
							specificationField.setValue(objModel.getSpecification());
							
							String file = objDao.getIconName((Long) itemCombo.getValue());
							if (file != null && file.trim().length() > 0) {
								String[] fileArray = file.split(",");
								String dir = VaadinServlet.getCurrent().getServletContext().getRealPath("/")
										+ "VAADIN/themes/testappstheme/ItemImages/";
								File imgFile = null;
								for (int i = 0; i < fileArray.length; i++) {
									SHorizontalLayout imgLay = new SHorizontalLayout();
									imgFile = new File(dir+ fileArray[i].replace(',',' ').trim());
									image = new Image(null,new FileResource(imgFile));
									image.setStyleName("user_photo");
									image.setWidth("140");
									image.setHeight("110");
									image.markAsDirty();
									imageSelectBox = new SCheckBox();
									imgLay.addComponent(imageSelectBox);
									imgLay.addComponent(image);
									imageLayout.addComponent(imgLay);
									imageList.add(imgFile);
								}
							}
							
						} else {
							saveButton.setVisible(true);
							deleteButton.setVisible(false);
							updateButton.setVisible(false);
							officeSel.setVisible(true);
							HashSet set=new HashSet(officeSel.getItemIds());
							officeSel.setValue(set);
							itemCodeField.setValue("");
							itemNameInAnotherLanguage.setValue("");
							languageNativeSelect.setValue(ofcDao.getOffice(getOfficeID()).getLanguage());
							supplierItemCodeField.setValue("");
							itemNameField.setValue("");
							groupsCombo.setValue(null);
							subgroupsCombo.setValue(null);
							quantityField.setReadOnly(false);
							quantityField.setValue("0.0");
							reorderField.setValue("0.0");
							minLevelField.setValue("0.0");
							maxLevelField.setValue("0.0");
							saleTaxSelect.setValue(null);
							purchaseTaxSelect.setValue(null);
							unitSelect.setValue(null);
							statusCombo.setValue((long)1);
							quantityInStockField.setNewValue("0");
							openingStockDate.setValue(getWorkingDate());
							affectType.setValue(SConstants.affect_type.AFFECT_ALL);
							purchaseRateField.setNewValue(getCurrencyID(), 0.0);
							saleRateField.setNewValue(getCurrencyID(), 0.0);
							discountRateField.setNewValue(getCurrencyID(), 0.0);
							maxDiscountRateField.setNewValue(getCurrencyID(), 0.0);
							modelCombo.setValue(null);
							colourCombo.setValue(null);
							sizeCombo.setValue(null);
							styleCombo.setValue(null);
							brandCombo.setValue((long) 0);
							supplierToken.setValue(null);
							specificationField.setValue("");
							descriptionField.setValue("");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			groupsCombo.addValueChangeListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						if (groupsCombo.getValue() != null) {
							if (subgroupsCombo.isReadOnly()) {
								Object temp = subgroupsCombo.getValue();
								subgroupsCombo.setReadOnly(false);
								list = subGpDao.getAllActiveItemSubGroups((Long) groupsCombo.getValue());
								bic = CollectionContainer.fromBeans(list, "id");
								subgroupsCombo.setContainerDataSource(bic);
								subgroupsCombo.setItemCaptionPropertyId("name");
								subgroupsCombo.setValue(temp);
								subgroupsCombo.setReadOnly(true);
							} else {
								list = subGpDao.getAllActiveItemSubGroups((Long) groupsCombo.getValue());
								bic = CollectionContainer.fromBeans(list, "id");
								subgroupsCombo.setContainerDataSource(bic);
								subgroupsCombo.setItemCaptionPropertyId("name");
							}
						}

					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			groupsCombo.setValue(null);

			deleteButton.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {

						ConfirmDialog.show(getUI(),
								getPropertyName("are_you_sure"),
								new ConfirmDialog.Listener() {
									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {

											try {
												id = (Long) itemCombo
														.getValue();
												objDao.delete(id);

												deleteImage(id);

												Notification
														.show(getPropertyName("deleted_success"),
																Type.WARNING_MESSAGE);

												loadOptions(0);

											} catch (Exception e) {
												Notification
														.show(getPropertyName("Error"),
																Type.ERROR_MESSAGE);
												e.printStackTrace();
											}
										}
									}
								});

					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			updateButton.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					try {

						if (itemCombo.getValue() != null) {

							if (isValid()) {

								ItemModel oldMdl = objDao.getItem((Long) itemCombo.getValue());

								String fileName = "";
								if (imageList.size() > 0) {
									fileName = getFileName();
								}
								updateImage(oldMdl.getId());
								String supplier="";
								if (supplierToken.getValue() != null) {
									Iterator it = ((Set<Long>) supplierToken.getValue()).iterator();
									while (it.hasNext()) {
										supplier+=(Long) it.next()+",";
									}
								}
								
								ItemModel objModel;
								List mainList=new ArrayList();
								List idList=objDao.getAllItemsUnderParentFromItem(oldMdl);
								Iterator iter=idList.iterator();
							
							while (iter.hasNext()) {
								objModel = (ItemModel) iter.next();
								objModel.setItem_code(itemCodeField.getValue());
								objModel.setSupplier_code(supplierItemCodeField.getValue());
								objModel.setName(itemNameField.getValue());
								if(settings.isITEMS_IN_MULTIPLE_LANGUAGE()){
									objModel.setSecondName(itemNameInAnotherLanguage.getValue());
								} else {
									objModel.setSecondName(itemNameField.getValue());
								}
								objModel.setLanguageId((Long)languageNativeSelect.getValue());
								objModel.setSub_group(new ItemSubGroupModel((Long) subgroupsCombo.getValue()));
								
									
//								objModel.setCurrent_balalnce(roundNumber(toDouble(quantityField.getValue().toString())));
								objModel.setReorder_level(roundNumber(toDouble(reorderField.getValue())));
								objModel.setMinimum_level(roundNumber(toDouble(minLevelField.getValue().toString())));
								objModel.setMaximum_level(roundNumber(toDouble(maxLevelField.getValue().toString())));
								if (taxEnable) {
									objModel.setSalesTax(new TaxModel((Long) saleTaxSelect.getValue()));
									objModel.setPurchaseTax(new TaxModel((Long) purchaseTaxSelect.getValue()));
								} else {
									objModel.setSalesTax(new TaxModel(1));
									objModel.setPurchaseTax(new TaxModel(1));
								}
								objModel.setUnit(new UnitModel((Long) unitSelect.getValue()));
								objModel.setStatus((Long) statusCombo.getValue());
								objModel.setAffect_type((Integer) affectType.getValue());
								
								if(getOfficeID()==objModel.getOffice().getId()){
									objModel.setOpening_stock_date(CommonUtil.getSQLDateFromUtilDate(openingStockDate.getValue()));
									objModel.setOpening_balance(roundNumber(toDouble(quantityField.getValue().toString())));
									objModel.setPurchaseCurrency(new CurrencyModel(purchaseRateField.getCurrency()));
									objModel.setRate(roundNumber(purchaseRateField.getValue()));
									objModel.setSaleCurrency(new CurrencyModel(saleRateField.getCurrency()));
									objModel.setSale_rate(roundNumber(saleRateField.getValue()));
									objModel.setDiscount(roundNumber(discountRateField.getValue()));
									objModel.setMax_discount(roundNumber(maxDiscountRateField.getValue()));
									objModel.setPurchase_convertion_rate(roundNumber(purchaseRateField.getConversionRate()));
									objModel.setSale_convertion_rate(roundNumber(saleRateField.getConversionRate()));
								}
								
								if(modelCombo.getValue()!=null)
									objModel.setItem_model((Long)modelCombo.getValue());
								else
									objModel.setItem_model((long)0);
								if(colourCombo.getValue()!=null)
									objModel.setColour((Long)colourCombo.getValue());
								else
									objModel.setColour((long)0);
								if(sizeCombo.getValue()!=null)
									objModel.setSize((Long)sizeCombo.getValue());
								else
									objModel.setSize((long)0);
								if(styleCombo.getValue()!=null)
									objModel.setStyle((Long)styleCombo.getValue());
								else
									objModel.setStyle((long)0);
								if(brandCombo.getValue()!=null)
									objModel.setBrand((Long)brandCombo.getValue());
								else
									objModel.setBrand((long)0);
								
								if(objModel.getOffice().getId()==getOfficeID())
									objModel.setPreferred_vendor(supplier);
								else
									objModel.setPreferred_vendor("");
								
								objModel.setSpecification(specificationField.getValue());
								objModel.setDesciption(descriptionField.getValue());
								objModel.setIcon(fileName);
								objModel.setOffice(objModel.getOffice());
								objModel.setCess_enabled('N');
								mainList.add(objModel);
								
								}
								
								long id=objDao.update(mainList,getOfficeID());
								try {
									if (imageLayout.getComponentCount() > 0) {
										saveImageAsPNG(fileName);
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
								Notification.show(getPropertyName("updated_success"),Type.WARNING_MESSAGE);
								loadOptions(id);
							}
						}
					} catch (Exception e) {
						Notification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
						e.printStackTrace();
					}
				}
			});
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public void loadOptions(long id) {
		List testList;
		try {

			list = objDao.getAllItems(getOfficeID());

			ItemModel sop = new ItemModel();
			sop.setId(0);
			sop.setName("------------------- Create New -------------------");
			if (list == null)
				list = new ArrayList();
			list.add(0, sop);

			bic = CollectionContainer.fromBeans(list, "id");
			itemCombo.setContainerDataSource(bic);
			itemCombo.setItemCaptionPropertyId("name");

			if (id != 0)
				itemCombo.setValue(id);
			else
				itemCombo.setValue(null);

			itemCombo
					.setInputPrompt("------------------- Create New -------------------");
			itemCodeField.focus();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public void removeErrorMsgs() {
		statusCombo.setComponentError(null);
		unitSelect.setComponentError(null);
		subgroupsCombo.setComponentError(null);
		groupsCombo.setComponentError(null);
		purchaseTaxSelect.setComponentError(null);
		quantityField.setComponentError(null);
		purchaseRateField.setComponentError(null);
		itemCodeField.setComponentError(null);
		itemNameField.setComponentError(null);
		reorderField.setComponentError(null);
	}

	
	public Boolean isValid() {

		boolean ret = true;

		if (statusCombo.getValue() == null || statusCombo.getValue().equals("")) {
			setRequiredError(statusCombo, getPropertyName("invalid_selection"), true);
			ret = false;
		} else
			setRequiredError(statusCombo, null, false);
		
		if (openingStockDate.getValue() == null) {
			setRequiredError(openingStockDate, getPropertyName("invalid_selection"), true);
			ret = false;
		} else
			setRequiredError(openingStockDate, null, false);

		if (unitSelect.getValue() == null || unitSelect.getValue().equals("")) {
			setRequiredError(unitSelect, getPropertyName("invalid_selection"), true);
			ret = false;
		} else
			setRequiredError(unitSelect, null, false);

		if (taxEnable) {
			if (purchaseTaxSelect.getValue() == null || purchaseTaxSelect.getValue().equals("")) {
				setRequiredError(purchaseTaxSelect, getPropertyName("invalid_selection"), true);
				ret = false;
			} else
				setRequiredError(purchaseTaxSelect, null, false);

			if (saleTaxSelect.getValue() == null || saleTaxSelect.getValue().equals("")) {
				setRequiredError(saleTaxSelect,getPropertyName("invalid_selection"), true);
				ret = false;
			} else
				setRequiredError(saleTaxSelect, null, false);
		}

		if (subgroupsCombo.getValue() == null || subgroupsCombo.getValue().equals("")) {
			setRequiredError(subgroupsCombo,getPropertyName("invalid_selection"), true);
			ret = false;
		} else
			setRequiredError(subgroupsCombo, null, false);
		
		if (itemNameField.getValue() == null || itemNameField.getValue().equals("")) {
			setRequiredError(itemNameField,getPropertyName("invalid_data"), true);
			ret = false;
		} else
			setRequiredError(itemNameField, null, false);

		if (itemCodeField.getValue() == null || itemCodeField.getValue().equals("")) {
			setRequiredError(itemCodeField, getPropertyName("invalid_data"), true);
			ret = false;
		} else
			setRequiredError(itemCodeField, null, false);
		
		if (supplierItemCodeField.getValue() == null || supplierItemCodeField.getValue().equals("")) {
			setRequiredError(supplierItemCodeField,getPropertyName("invalid_data"), true);
			ret = false;
		} else
			setRequiredError(supplierItemCodeField, null, false);

		try{
			if (toDouble(quantityField.getValue().toString()) < 0) {
				setRequiredError(quantityField,getPropertyName("invalid_data"), true);
				ret = false;
			} else
				setRequiredError(quantityField, null, false);
		}
		catch(Exception e){
			setRequiredError(quantityField, getPropertyName("invalid_data"),true);
			ret = false;
		}
		
		try{
			if (toDouble(reorderField.getValue().toString()) < 0) {
				setRequiredError(reorderField,getPropertyName("invalid_data"), true);
				ret = false;
			} else
				setRequiredError(reorderField, null, false);
		}
		catch(Exception e){
			setRequiredError(reorderField, getPropertyName("invalid_data"),true);
			ret = false;
		}
		
		if(!saleRateField.isFieldValid(getWorkingDate())) {
			setRequiredError(saleRateField, getPropertyName("invalid_data"),true);
			ret = false;
		}
		else
			setRequiredError(saleRateField, null, false);
		
		if(!purchaseRateField.isFieldValid(getWorkingDate())) {
			setRequiredError(purchaseRateField, getPropertyName("invalid_data"),true);
			ret = false;
		}
		else
			setRequiredError(purchaseRateField, null, false);
		
		if(!discountRateField.isFieldValid(getWorkingDate())) {
			setRequiredError(discountRateField, getPropertyName("invalid_data"),true);
			ret = false;
		}
		else
			setRequiredError(discountRateField, null, false);
		
		if(!maxDiscountRateField.isFieldValid(getWorkingDate())) {
			setRequiredError(maxDiscountRateField, getPropertyName("invalid_data"),true);
			ret = false;
		}
		else
			setRequiredError(maxDiscountRateField, null, false);
		
		if(discountRateField.isFieldValid(getWorkingDate()) && maxDiscountRateField.isFieldValid(getWorkingDate())){
			if(discountRateField.getValue()>maxDiscountRateField.getValue()){
				setRequiredError(discountRateField, getPropertyName("invalid_data"),true);
				ret = false;
			}
			else
				setRequiredError(discountRateField, null, false);
		}
		
		try{
			if (toDouble(minLevelField.getValue().toString()) < 0) {
				setRequiredError(minLevelField,getPropertyName("invalid_data"), true);
				ret = false;
			} else
				setRequiredError(minLevelField, null, false);
		}
		catch(Exception e){
			setRequiredError(minLevelField, getPropertyName("invalid_data"),true);
			ret = false;
		}
		
		try{
			if (toDouble(maxLevelField.getValue().toString()) < 0) {
				setRequiredError(maxLevelField,getPropertyName("invalid_data"), true);
				ret = false;
			} else
				setRequiredError(maxLevelField, null, false);
		}
		catch(Exception e){
			setRequiredError(maxLevelField, getPropertyName("invalid_data"),true);
			ret = false;
		}

		if (settings.isSHOW_ITEM_ATTRIBUTES()) {
			
			if (modelCombo.getValue() == null || modelCombo.getValue().equals("")) {
				setRequiredError(modelCombo, getPropertyName("invalid_selection"),
						true);
				ret = false;
			} else
				setRequiredError(modelCombo, null, false);
			
			if (colourCombo.getValue() == null || colourCombo.getValue().equals("")) {
				setRequiredError(colourCombo, getPropertyName("invalid_selection"),
						true);
				ret = false;
			} else
				setRequiredError(colourCombo, null, false);
			
			if (sizeCombo.getValue() == null || sizeCombo.getValue().equals("")) {
				setRequiredError(sizeCombo, getPropertyName("invalid_selection"),
						true);
				ret = false;
			} else
				setRequiredError(sizeCombo, null, false);
			
			if (styleCombo.getValue() == null || styleCombo.getValue().equals("")) {
				setRequiredError(styleCombo, getPropertyName("invalid_selection"), true);
				ret = false;
			} else
				setRequiredError(styleCombo, null, false);
		}
		return ret;
	}

	
	public void reloadGroup() {
		try {
			if (groupsCombo.isReadOnly()) {
				Object obj = groupsCombo.getValue();
				groupsCombo.setReadOnly(false);
				list = gpDao.getAllItemGroupsNames(getOrganizationID());
				bic = CollectionContainer.fromBeans(list, "id");
				groupsCombo.setContainerDataSource(bic);
				groupsCombo.setItemCaptionPropertyId("name");
				groupsCombo.setValue(obj);
				groupsCombo.setReadOnly(true);
			} else {
				list = gpDao.getAllItemGroupsNames(getOrganizationID());
				bic = CollectionContainer.fromBeans(list, "id");
				groupsCombo.setContainerDataSource(bic);
				groupsCombo.setItemCaptionPropertyId("name");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public Boolean getHelp() {
		return null;
	}

	
	public void saveImageAsPNG(String fileName) {

		File file = null;
		String singleFile = "";
		String[] fileArray = fileName.split(",");
		BufferedImage bufferedImage;

		String dir = VaadinServlet.getCurrent().getServletContext()
				.getRealPath("/")
				+ "VAADIN/themes/testappstheme/ItemImages/";

		try {
			int i = 0;
			Iterator iter = imageList.iterator();
			while (iter.hasNext()) {
				singleFile = dir + fileArray[i].replace(',', ' ').trim();

				if (!new File(singleFile).isDirectory()
						&& fileArray[i].trim().length() > 0) {
					file = (File) iter.next();
					bufferedImage = ImageIO.read(file);
					float width = bufferedImage.getWidth(), height = bufferedImage
							.getHeight();
					if (bufferedImage.getWidth() > 500) {
						float div = width / 500;
						width = 500;
						if (div > 1)
							height = height / div;
					}

					BufferedImage newBufferedImage = new BufferedImage(
							(int) width, (int) height,
							BufferedImage.TYPE_INT_RGB);
					newBufferedImage.createGraphics().drawImage(bufferedImage,
							0, 0, (int) width, (int) height, Color.WHITE, null);

					ImageIO.write(newBufferedImage, "png", new File(singleFile));
					i++;
				}
			}

		} catch (IOException e) {

			e.printStackTrace();

		}

	}

	
	private void deleteImage(long itmId) {

		try {

			String fileName = objDao.getIconName(itmId);
			if(fileName!=null){
				String[] fileArray = fileName.split(",");

				for (int i = 0; i < fileArray.length; i++) {
					String file = VaadinServlet.getCurrent().getServletContext()
							.getRealPath("/")
							+ "VAADIN/themes/testappstheme/ItemImages/"
							+ fileArray[i];
					File f = new File(file);
					if (f.exists()) {
						f.delete();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	private void updateImage(long itmId) {

		try {

			String fileName = objDao.getIconName(itmId);
			if (fileName != null && fileName.contains(",")) {
				String[] fileArray = fileName.split(",");

				for (int i = 0; i < fileArray.length; i++) {
					String file = VaadinServlet.getCurrent()
							.getServletContext().getRealPath("/")
							+ "VAADIN/themes/testappstheme/ItemImages/"
							+ fileArray[i];

					File f = new File(file);

					if (!imageList.contains(f) && !f.isDirectory()) {

						if (f.exists()) {
							f.delete();
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	protected String getFileName() {

		Calendar cal = Calendar.getInstance();

		String fileName = "";

		for (int i = 0; i < imageList.size(); i++) {
			fileName += i + String.valueOf(df.format(cal.getTime())).trim()
					+ ".png ,";
		}
		return fileName;
	}

	
	public SRadioButton getAffectType() {
		return affectType;
	}

	
	public void setAffectType(SRadioButton affectType) {
		this.affectType = affectType;
	}

}
