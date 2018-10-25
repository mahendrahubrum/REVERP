package com.webspark.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.config.acct.dao.CustomerDao;
import com.inventory.config.acct.dao.SupplierDao;
import com.inventory.config.acct.model.CustomerModel;
import com.inventory.config.acct.model.SupplierModel;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.config.settings.biz.SettingsBiz;
import com.inventory.config.settings.ui.AccountSettingsUI;
import com.inventory.process.model.FinancialYearsModel;
import com.inventory.reports.ui.LoginAlertPopup;
import com.vaadin.annotations.Theme;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.AbstractErrorMessage.ContentMode;
import com.vaadin.server.ErrorMessage.ErrorLevel;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
import com.webspark.Components.SButton;
import com.webspark.Components.SButtonLink;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SCustomLayout;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHorizontalSplitPanel;
import com.webspark.Components.SImage;
import com.webspark.Components.SLabel;
import com.webspark.Components.SMultiLink;
import com.webspark.Components.SNativeButton;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SPanel;
import com.webspark.Components.STextField;
import com.webspark.Components.SUserError;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SVerticalSplitPanel;
import com.webspark.Components.SWindow;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.KeyValue;
import com.webspark.common.util.SConstants;
import com.webspark.common.util.SessionUtil;
import com.webspark.core.SReflection;
import com.webspark.dao.DBOperations;
import com.webspark.dao.LanguageDao;
import com.webspark.dao.LanguageMappingDao;
import com.webspark.dao.LoginDao;
import com.webspark.dao.LoginOptionMappingDao;
import com.webspark.dao.ModuleDao;
import com.webspark.dao.OptionGroupDao;
import com.webspark.model.ActivityLogModel;
import com.webspark.model.S_LanguageMappingModel;
import com.webspark.model.S_LanguageModel;
import com.webspark.model.S_LoginModel;
import com.webspark.model.S_OptionGroupModel;
import com.webspark.model.S_OptionModel;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.OrganizationDao;
import com.webspark.uac.dao.UserManagementDao;
import com.webspark.uac.model.S_ModuleModel;
import com.webspark.uac.model.S_OfficeModel;
import com.webspark.uac.model.UserModel;

/**
 * @Author Jinshad P.T.
 */

@Theme("testappstheme")
public class MainLayout extends SVerticalLayout {

	private static final long serialVersionUID = -540949543149045721L;
	
	SReflection objSRefl = new SReflection();
	SComboField modules;

	SNativeSelect finYearSelect;
	SDateField workingDate;

	SNativeSelect organizationSelect, languageSelect;
	SNativeSelect officeSelect;
	SWindow window;
	WrappedSession session=new SessionUtil().getHttpSession();

	SVerticalSplitPanel vSplit;
	SHorizontalSplitPanel hsplit;

	SCustomLayout dashBoard;
	
	SImage image;
	MenuBar menubar;
	Command mycommand;
	
	SNativeButton dashCloseButton;
	
	SCustomLayout topPanel;
	SButtonLink logout;
	DBOperations dbopDao;
	
	SGridLayout optionGroups;
	
	SLabel ofc;
	SLabel ofcLabel;

	SLabel org;
	SLabel orgLabel;
	SLabel userLabel;
	SLabel timeLabel;
	
	SLabel cnctLabel;
	SLabel dateLabel;	
	SettingsValuePojo settings;
	
	OfficeDao ofcDao;
	OptionGroupDao opgpDao;
	SettingsBiz stbizObj;
	LoginOptionMappingDao logOptMap;
	ModuleDao mdDao;
	
	SWindow settingsWindow;
	
	CloseListener closeListener;
	Tree tree;
	// Clock clock;
	
	float screen_width;
	float screen_height;
	
	SImage userImage; 
	long login_id;
	
	@SuppressWarnings("deprecation")
	public MainLayout() {
		super();
		
		tree = new Tree("");
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date(new Date().getTime()+600000));
		
		ofcDao=new OfficeDao();
		opgpDao=new OptionGroupDao();
		stbizObj=new SettingsBiz();
		logOptMap=new LoginOptionMappingDao();
		mdDao=new ModuleDao();
		
		if (session.getAttribute("settings") != null) 
			settings = (SettingsValuePojo) session.getAttribute("settings");
			
		screen_width = getUI().getCurrent().getPage().getBrowserWindowWidth();
		screen_height = getUI().getCurrent().getPage().getBrowserWindowHeight();

		login_id = (Long) session.getAttribute("login_id");

		S_OfficeModel office = null;
		try {
			office = ofcDao.getOffice((Long) session
					.getAttribute("office_id"));
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		long role_id = (Long) session.getAttribute("role_id");

		setSizeFull();
		setSizeUndefined();
		setWidth("" + (screen_width - 15));

		dashCloseButton = new SNativeButton("Hide");

		dbopDao = new DBOperations();

		// clock=new Clock(120, 120);
		// getUI().removeWindow(window);

		SLabel userName;
		SLabel dt = new SLabel(null, CommonUtil.formatDateToCommonDateTimeFormat(new Date()));

		SLabel contact;

		UserModel usr = null;
		CustomerModel cust = null;
		SupplierModel sup = null;
		
		S_LoginModel loginModel = null;
		try {
			
			closeListener=new CloseListener() {
				
				@Override
				public void windowClose(CloseEvent e) {
					// TODO Auto-generated method stub
					
//					settingsWindow=null;
					
				}
			};
			
			List orgList;
			
			if (((Long)session.getAttribute("role_id")) == SConstants.ROLE_SPECIAL_ADMIN) {
				
				orgList=ofcDao.getAllOrganizationNamesOfUserMaped((Long) session.getAttribute("login_id"),(Long) session.getAttribute("user_office_id"));
				
			}
			else {
				orgList=new OrganizationDao().getAllOrganizations();
			}
			
			organizationSelect = new SNativeSelect(null, 180, orgList, "id", "name");
			if(Integer.parseInt(session.getAttribute("language_id")+"")!=0){
				languageSelect = new SNativeSelect(null, 100,new LanguageDao().getAllLanguages(),"id","name");
				languageSelect.setComponentError(null);
				languageSelect.setDescription(getPropertyName("select_language"));
				languageSelect.setValue(Long.parseLong(session.getAttribute("language_id")+""));
			}
			else{
				languageSelect = new SNativeSelect(null, 100,null,"id","name");
				languageSelect.setComponentError(null);
				setRequiredError(languageSelect, getPropertyName("set_language_office"), true);
				languageSelect.setValue(null);
			}
//			languageSelect.addItem("English");
//			languageSelect.addItem("Arabic");
//			languageSelect.setValue("English");
			
			officeSelect = new SNativeSelect(null, 180, null, "id", "name");

			organizationSelect.setImmediate(true);
			officeSelect.setImmediate(true);
			
			organizationSelect.setDescription("Organization");
			officeSelect.setDescription("Office");
			
			orgLabel=new SLabel(null,getPropertyName("organization")+": ");
			userLabel=new SLabel(null,getPropertyName("welcome")+" ");
			timeLabel=new SLabel(null,getPropertyName("login_time")+": ");
			ofcLabel=new SLabel(null,getPropertyName("office")+": ");
			cnctLabel=new SLabel(null,getPropertyName("contact_no")+": ");
			dateLabel=new SLabel(null,getPropertyName("working_date")+" ");
			
			
			usr = new UserManagementDao().getUserFromLogin((Long) session.getAttribute("login_id"));
			
			if((Boolean)session.getAttribute("isCustomer")){
				cust=new CustomerDao().getCustomerFromLogin((Long) session
						.getAttribute("login_id"));
			}else if((Boolean)session.getAttribute("isSupplier")){
				sup=new SupplierDao().getSupplierFromLogin((Long) session
						.getAttribute("login_id"));
			}
			
			 String empImageFile="EmployeePhotos/base_image.png";
			 ThemeResource mainRes=new ThemeResource(empImageFile);

			if (usr != null) {
				userName = new SLabel(null, usr.getFirst_name());

				ofc = new SLabel(null, usr.getLoginId().getOffice().getName());

				org = new SLabel(null, usr.getLoginId().getOffice()
						.getOrganization().getName());
				contact = new SLabel(null, usr.getLoginId().getOffice()
						.getAddress().getPhone());
				empImageFile="EmployeePhotos/"+ usr.getId()+".png";
				File file=new File(VaadinServlet.getCurrent().getServletContext()
						.getRealPath("/")
						+ "VAADIN/themes/testappstheme/"+empImageFile);
				if(file!=null&&file.exists())
					 mainRes=new ThemeResource(empImageFile);
				
				
				
			}else if(cust!=null){
				
				userName = new SLabel(null, cust.getName());
				ofc = new SLabel(null, cust.getLedger().getOffice().getName());
				org = new SLabel(null,  cust.getLedger().getOffice().getOrganization()
						.getName());
				contact = new SLabel(null,  cust.getLedger().getOffice().getAddress()
						.getPhone());
				
			}else if(sup!=null){
				
				userName = new SLabel(null, sup.getName());
				ofc = new SLabel(null, sup.getLedger().getOffice().getName());
				org = new SLabel(null,  sup.getLedger().getOffice().getOrganization()
						.getName());
				contact = new SLabel(null,  sup.getLedger().getOffice().getAddress()
						.getPhone());
				
			}else {
				loginModel = new LoginDao().getLoginFromLoginName(session
						.getAttribute("login_name").toString());
				userName = new SLabel(null, loginModel.getLogin_name());
				ofc = new SLabel(null, loginModel.getOffice().getName());
				org = new SLabel(null, loginModel.getOffice().getOrganization()
						.getName());
				contact = new SLabel(null, loginModel.getOffice().getAddress()
						.getPhone());
			}
			
			 userImage=new SImage(null, mainRes);
			 userImage.setHeight("100px");
			 userImage.setWidth("100px");
			 userImage.setStyleName("user_image_round");
			 
			 String fil = "OrganizationLogos/"
						+ office.getOrganization().getId() + ".png";
			 image = new SImage(null, new ThemeResource(fil));
			 image.setWidth("80");
			
			List list = new ArrayList();
			S_ModuleModel mod = new S_ModuleModel(0);
			mod.setModule_name("All");
			list.add(mod);
			list.addAll(mdDao.getAllModules());

			modules = new SComboField(null, 240, list, "id", "module_name");

			dashBoard = new SCustomLayout("DashBoard");
			dashBoard.setSizeFull();
			dashBoard.addComponent(dashCloseButton, "closebtn");

			dashBoard.setSizeUndefined();
			dashBoard.setSizeFull();

			optionGroups = new SGridLayout();
			optionGroups.setColumns(5);

			// List lst=new DBOperations().getOptions();
			
			
			finYearSelect = new SNativeSelect(null, 180,
					dbopDao.getFinancialYears(office.getId()), "id", "name");
			finYearSelect.setDescription("Financial Year");
			
			workingDate = new SDateField();
			workingDate.setNewValue(new Date(((java.sql.Date)session.getAttribute("working_date")).getTime()));
			workingDate.setImmediate(true);

			/*
			 * if(role_id>2) { finYearSelect.setReadOnly(true);
			 * workingDate.setReadOnly(true); }
			 */
			
			ofc.setImmediate(true);
			org.setImmediate(true);
			
			organizationSelect.addValueChangeListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {

						SCollectionContainer bic=null;
						
						if (((Long)session.getAttribute("role_id")) == SConstants.ROLE_SPECIAL_ADMIN) {
							bic = SCollectionContainer.setList(ofcDao
											.getAllOfficeNamesOfUserMaped((Long) session.getAttribute("login_id"),(Long) session.getAttribute("user_office_id"), (Long) organizationSelect
													.getValue()), "id");
						}
						else {
							bic = SCollectionContainer.setList(
									ofcDao.getAllOfficeNamesUnderOrg((Long) organizationSelect
													.getValue()), "id");
						}
						
						
						officeSelect.setContainerDataSource(bic);
						officeSelect.setItemCaptionPropertyId("name");

						session.setAttribute("organization_id",
								(Long) organizationSelect.getValue());
						org.setValue(organizationSelect
								.getItemCaption(organizationSelect.getValue()));

						session.setAttribute("organization_name",
								organizationSelect
										.getItemCaption(organizationSelect
												.getValue()));
						
						
						if(((Long)session.getAttribute("user_organization_id"))!=((Long) organizationSelect
								.getValue())) {
							Iterator it = officeSelect.getItemIds().iterator();
							if (it.hasNext())
								officeSelect.setNewValue(it.next());
						}
						else {
							officeSelect.setNewValue((Long) session.getAttribute("user_office_id"));
							
							if(officeSelect.getValue()==null) {
								Iterator it = officeSelect.getItemIds().iterator();
								if (it.hasNext())
									officeSelect.setNewValue(it.next());
							}
						}
						
						
						String fil = "OrganizationLogos/"
								+ organizationSelect.getValue() + ".png";
						image.setSource(new ThemeResource(fil));
						image.markAsDirty();

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			
			officeSelect.addValueChangeListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						
						if(officeSelect.getValue()!=null) {
							
							S_OfficeModel ofcObj = ofcDao
									.getOffice((Long) officeSelect.getValue());
							
							session.setAttribute("project_type",ofcObj.getOrganization().getProject_type());
							
							session.setAttribute("office_id",(Long) officeSelect.getValue());
							
							System.out.println(session.getAttribute("office_id"));
	
							ofc.setValue(officeSelect.getItemCaption(officeSelect
									.getValue()));
	
							session.setAttribute("currency_id", ofcObj
									.getCurrency().getId());
							session.setAttribute("country_id", ofcObj.getCountry()
									.getId());
							session.setAttribute("no_of_precisions", ofcObj
									.getCurrency().getNo_of_precisions());
							
							session.setAttribute("office_name", officeSelect
									.getItemCaption(officeSelect.getValue()));
							
							session.setAttribute("time_zone", ofcObj.getTimezone());
	
							SettingsValuePojo setting=stbizObj.updateSettingsValue(
									(Long) organizationSelect.getValue(),
									(Long) officeSelect.getValue());
							
							if(settings.getDEFAULT_DATE_SELECTION()==SConstants.SYSTEM_DATE)
								session.setAttribute("working_date", new java.sql.Date(new Date().getTime()));
							else
								session.setAttribute("working_date", ofcObj.getWorkingDate());
							
							workingDate.setNewValue(new Date(((java.sql.Date)session.getAttribute("working_date")).getTime()));
							
							
							SCollectionContainer bic = SCollectionContainer.setList(dbopDao.getFinancialYears(ofcObj.getId()), "id");
							finYearSelect.setContainerDataSource(bic);
							finYearSelect.setItemCaptionPropertyId("name");
							
							finYearSelect.setNewValue(dbopDao.getCurrentFinYear(ofcObj.getId(),
									ofcObj.getFin_start_date(), ofcObj.getFin_end_date()));
							
							if(getUI()!=null) 
							if(getUI().getWindows()!=null){
								Iterator it= getUI().getWindows().iterator();
								while (it.hasNext()) {
									try {
										getUI().removeWindow((Window) it.next());
									} catch (Exception e) {
										// TODO: handle exception
									}
								}
							}
							
							checkSettings();
							
							if(officeSelect.getValue()!=null)
								loadOptions(login_id,(Long) officeSelect.getValue());
							
							
							
							
							
							List list =mdDao.getAllAssignedModules(login_id,ofcObj.getId());
							if(list==null)
								list=new ArrayList();
							S_ModuleModel mod = new S_ModuleModel(0);
							mod.setModule_name("All");
							list.add(0,mod);
							SCollectionContainer bic1 = SCollectionContainer.setList(list, "id");
							modules.setContainerDataSource(bic1);
							modules.setItemCaptionPropertyId("module_name");
							
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			
			languageSelect.addValueChangeListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						
						if(getUI()!=null) {
							long ofc=(Long)officeSelect.getValue();
							officeSelect.setValue(null);
							if(Long.parseLong(session.getAttribute("language_id")+"")!=0){
								if(languageSelect.getValue()!=null){
									S_LanguageModel language=new LanguageDao().getLanguage((Long)languageSelect.getValue());
									session.setAttribute("property_file", language.getProperty());
									session.setAttribute("language_id", (Long)languageSelect.getValue());
									languageSelect.setDescription(getPropertyName("select_language"));
									orgLabel.setValue(getPropertyName("organization")+": ");
									userLabel.setValue(getPropertyName("welcome")+": ");
									ofcLabel.setValue(getPropertyName("office")+": ");
									cnctLabel.setValue(getPropertyName("contact_no")+": ");
									dateLabel.setValue(getPropertyName("working_date"));
									timeLabel.setValue(getPropertyName("login_time"));
									logout.setCaption(getPropertyName("log_out"));
								}
							}
							else{
								session.setAttribute("property_file", "english");
								session.setAttribute("language_id", 0);
							}
							if(getUI().getWindows()!=null){
								Iterator it= getUI().getWindows().iterator();
								while (it.hasNext()) {
									try {
										getUI().removeWindow((Window) it.next());
									} catch (Exception e) {
										// TODO: handle exception
									}
								}
							}
							officeSelect.setValue(ofc);
						}
					} 
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			
			if (Long.parseLong(session.getAttribute("role_id").toString()) != SConstants.ROLE_SYSTEM_ADMIN
					&& Long.parseLong(session.getAttribute("role_id")
							.toString()) != SConstants.ROLE_SUPER_ADMIN) {
				organizationSelect.setReadOnly(true);
				if (!(Boolean) session.getAttribute("isOrganizationAdmin") && (Long.parseLong(session.getAttribute("role_id").toString()) != SConstants.SEMI_ADMIN) && (Long.parseLong(session.getAttribute("role_id").toString()) != SConstants.ROLE_SPECIAL_ADMIN)) {
					officeSelect.setReadOnly(true);
				}
			}

			workingDate.addValueChangeListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						session.setAttribute("working_date", CommonUtil
								.getSQLDateFromUtilDate(workingDate.getValue()));

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			finYearSelect.addValueChangeListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {
						
						if(finYearSelect.getValue()!=null) {
						
							FinancialYearsModel fy = dbopDao
									.getFinancialYear((Long) finYearSelect
											.getValue());
	
							session.setAttribute("fin_start", fy.getStart_date());
							session.setAttribute("fin_end", fy.getEnd_date());
	
							SettingsValuePojo settings = (SettingsValuePojo) session
									.getAttribute("settings");
							if (!settings.isFIN_YEAR_BACK_ENTRY_ENABLE()) {
								S_OfficeModel office = ofcDao
										.getOffice((Long) session
												.getAttribute("office_id"));
	
								if (!office.getFin_start_date().toString()
										.equals(fy.getStart_date().toString())) {
									session.setAttribute("fin_yr_back_entry", false);
								} else {
									session.setAttribute("fin_yr_back_entry", true);
								}
							} else {
								session.setAttribute("fin_yr_back_entry", true);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			organizationSelect.setNewValue((Long) session
					.getAttribute("organization_id"));
			officeSelect.setNewValue((Long) session.getAttribute("office_id"));

//			finYearSelect.setValue(dbopDao.getCurrentFinYear(office.getId(),
//					office.getFin_start_date(), office.getFin_end_date()));


			/*
			 * SCustomLayout cst=new SCustomLayout("DashButtons");
			 * cst.setId("1"); gr.addComponent(cst); cst=new
			 * SCustomLayout("DashButtons"); cst.setId("2");
			 * gr.addComponent(cst); cst=new SCustomLayout("DashButtons");
			 * cst.setId("3"); gr.addComponent(cst); cst=new
			 * SCustomLayout("DashButtons"); cst.setId("4");
			 * gr.addComponent(cst);
			 */

			dashBoard.addComponent(optionGroups, "btns");

			menubar = new MenuBar();
			mycommand = new Command() {

				@Override
				public void menuSelected(MenuItem selectedItem) {
					// TODO Auto-generated method stub

				}
			};

			/*
			 * gr.addListener(new LayoutClickListener() {
			 * 
			 * @Override public void layoutClick(LayoutClickEvent event) { //
			 * TODO Auto-generated method stub
			 * 
			 * try {
			 * 
			 * System.out.println(event.getChildComponent().getId());
			 * 
			 * if (event.getChildComponent().getId() != null) { S_OptionModel
			 * opt;
			 * 
			 * opt = new DBOperations().getOptionModel(Long
			 * .parseLong(event.getChildComponent() .getId()));
			 * 
			 * getUI().removeWindow(window);
			 * 
			 * window = (SparkLogic) objSRefl.getClassInstance(opt
			 * .getClass_name());
			 * 
			 * window.setCaption(opt.getOption_name());
			 * 
			 * window.center(); window.setCloseShortcut(KeyCode.X,
			 * ShortcutAction.ModifierKey.ALT);
			 * getUI().getCurrent().addWindow(window);
			 * 
			 * }
			 * 
			 * } catch (NumberFormatException e) { // TODO Auto-generated catch
			 * block e.printStackTrace(); } catch (Exception e) { // TODO
			 * Auto-generated catch block e.printStackTrace(); } } });
			 */

			/*MenuBar.MenuItem beverages = menubar.addItem("Beverages", null,
					null);
			MenuBar.MenuItem hot_beverages = beverages.addItem("Hot", null,
					null);
			hot_beverages.addItem("Tea", null, mycommand);
			hot_beverages.addItem("Coffee", null, mycommand);
			MenuBar.MenuItem cold_beverages = beverages.addItem("Cold", null,
					null);
			cold_beverages.addItem("Milk", null, mycommand);

			// Another top-level item
			MenuBar.MenuItem snacks = menubar.addItem("Snacks", null, null);
			snacks.addItem("Weisswurst", null, mycommand);
			snacks.addItem("Salami", null, mycommand);

			// Yet another top-level item
			MenuBar.MenuItem services = menubar.addItem("Services", null, null);
			services.addItem("Car Service", null, mycommand);*/

			logout = new SButtonLink(getPropertyName("log_out"), true);
			logout.setStyleName("logout_btn");
			
			// final SVerticalLayout layout = new SVerticalLayout();
			// layout.setMargin(true);

			SPanel mainPanel = new SPanel();
			topPanel = new SCustomLayout("home_head");
			SFormLayout sidePanel = new SFormLayout();
			SFormLayout workPanel = new SFormLayout();
			SFormLayout bottomPanel = new SFormLayout();

			topPanel.setStyleName("top_panel");

			topPanel.setWidth(screen_width - 15 + "");
			topPanel.setHeight("175px");

			// topPanel.addComponent(menubar);

			STextField search = new STextField("Search :");
			search.setWidth("400px");
			search.setStyleName("jptstyle");

			
			if (settings.isHIDE_ORGANIZATION_DETAILS()) {
				organizationSelect.setVisible(false);
				org.setValue(ofc.getValue());
			}

			topPanel.addComponent(userName, "username");
			topPanel.addComponent(userLabel, "userLabel");
			topPanel.addComponent(timeLabel, "timeLabel");
			topPanel.addComponent(ofc, "office");
			topPanel.addComponent(orgLabel, "organizationLabel");
			topPanel.addComponent(ofcLabel, "officeLabel");
			topPanel.addComponent(cnctLabel, "contactLabel");
			topPanel.addComponent(dateLabel, "dateLabel");
			topPanel.addComponent(org, "organization");
			topPanel.addComponent(dt, "date");
			topPanel.addComponent(contact, "contact");
			topPanel.addComponent(logout, "logout");
			topPanel.addComponent(finYearSelect, "finyear");
			topPanel.addComponent(workingDate, "workingdate");
			topPanel.addComponent(image, "org_logo");
			topPanel.addComponent(userImage, "user_image");

			topPanel.addComponent(organizationSelect, "organization_select");
			topPanel.addComponent(officeSelect, "office_select");
			topPanel.addComponent(languageSelect, "language_select");
			

			vSplit = new SVerticalSplitPanel();
			vSplit.setSplitPosition(100);
			vSplit.setHeight((.867 * screen_height) + "px");
			vSplit.setWidth(screen_width - 15 + "");

			hsplit = new SHorizontalSplitPanel();
			// hsplit.setSplitPosition(20);
			hsplit.setSplitPosition(0);
			hsplit.setFirstComponent(sidePanel);
			hsplit.setSecondComponent(dashBoard);

			vSplit.setStyleName("dash_vert_split");
			hsplit.setStyleName("dash_horizontal_split");

			hsplit.setLocked(true);

			hsplit.setHeight((.855 * screen_height) + "px");
			hsplit.setWidth(screen_width - 15 + "");

			SPanel menuPanel = new SPanel();

			modules.setStyleName("module_combo_style");

			logout.addClickListener(new SButton.ClickListener() {
				public void buttonClick(ClickEvent event) {

					session.invalidate();

					getUI().getSession().close();

					getUI().getPage().setLocation(
							VaadinService.getCurrentRequest().getContextPath()
									+ "/");
					
				}
			});
			
			dashCloseButton.addClickListener(new SButton.ClickListener() {
				public void buttonClick(ClickEvent event) {
					
////					Broadcaster.broadcast("Hai All");
//					MainGUI main=(MainGUI) getUI();
//					main.sendBroadCast("Server 'ii restart Now");
					
					
					// hsplit.removeComponent(dashBoard);

					// SCustomLayout newDash= new SCustomLayout("DashBoard");
					// newDash.setSizeFull();
					// newDash.addComponent(dashCloseButton, "closebtn");

					if (dashCloseButton.getCaption().equals("Hide")) {
						dashCloseButton.setCaption("Show");

						dashBoard.removeComponent(optionGroups);
						hsplit.setSplitPosition(20);
						hsplit.setLocked(false);

					} else {
						dashCloseButton.setCaption("Hide");

						dashBoard.addComponent(optionGroups, "btns");
						hsplit.setSplitPosition(0);
						hsplit.setLocked(true);
						// hsplit.addComponent(dashBoard);
					}

				}
			});

			tree.addItemClickListener(new ItemClickEvent.ItemClickListener() {
				public void itemClick(ItemClickEvent itemClickEvent) {
					try {

						KeyValue key = (KeyValue) itemClickEvent.getItemId();

						if (key.getKey() != 0) {

							S_OptionModel opt =dbopDao
									.getOptionForOpen(key.getKey(), (Long)session.getAttribute("project_type"));
							
							if(!settings.isKEEP_OTHER_WINDOWS()) {
								if(getUI()!=null) 
								if(getUI().getWindows()!=null){
									Iterator it= getUI().getWindows().iterator();
									while (it.hasNext()) {
										try {
											getUI().removeWindow((Window) it.next());
										} catch (Exception e) {
											// TODO: handle exception
										}
									}
								}
							}

							window = (SparkLogic) objSRefl.getClassInstance(opt
									.getClass_name());

							window.setCaption(opt.getOption_name());

							window.center();
							window.setCloseShortcut(KeyCode.X,
									ShortcutAction.ModifierKey.ALT);
							getUI().getCurrent().addWindow(window);
							session.setAttribute("option_id", opt.getOption_id());
							// hsplit.setSplitPosition(0);

						} else {

							if (tree.isExpanded(itemClickEvent.getItemId())) {
								tree.collapseItemsRecursively(itemClickEvent
										.getItemId());
							} else {
								tree.expandItem(itemClickEvent.getItemId());
							}

						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			});
			
			modules.addValueChangeListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					
					try {
						if(officeSelect.getValue()!=null)
							loadTree( (Long) officeSelect.getValue());
						
						/*tree.setItemCaptionPropertyId("value");
						
						final String CAPTION = "key"; // can be any string
						HierarchicalContainer hc = new HierarchicalContainer();
						hc.addContainerProperty(CAPTION, String.class, null);
						tree.setContainerDataSource(hc);
						tree.setItemCaptionPropertyId(CAPTION);
						
						S_ModuleModel mod = mdDao
								.getModule((Long) modules.getValue());
						
						List groups = null;
						if (mod == null) {
							groups = opgpDao.getAllOptionGroups();
						} else {
							groups = opgpDao
									.getOptionGroupsUnderModule(mod.getId());
						}
						int parent_size = 0;

						if (groups.size() <= 0) {
							// tree.setChildrenAllowed(root, false);
						} else {

							S_OptionGroupModel grp;
							List options;
							int child_size;
							S_OptionModel opt;
							long loginId;
							boolean avail;
							KeyValue child;
							for (int j = 0; j < groups.size(); j++) {

								grp = (S_OptionGroupModel) groups
										.get(j);

								KeyValue parent = new KeyValue(0, grp.getOption_group_name());

								hc.addItem(parent).getItemProperty(CAPTION)
										.setValue(parent.getValue());

								parent_size++;

								options = dbopDao
										.getOptionsUnderGroup(grp.getId());

								child_size = 0;

								if (options.size() <= 0) {
									// The planet has no moons so make it a
									// leaf.
									tree.setChildrenAllowed(parent, false);
								} else {
									// Add children (moons) under the planets.
									for (int k = 0; k < options.size(); k++) {

										opt = (S_OptionModel) options
												.get(k);

										if (session.getAttribute("login_id") != null) {
											loginId = (Long) session
													.getAttribute("login_id");

											avail = logOptMap
													.isOptionsAvailToUser(
															loginId,
															opt.getOption_id(), (Long)officeSelect.getValue());

											if (avail) {
												
												child = new KeyValue(
														opt.getOption_id(),opt.getOption_name());

												hc.addItem(child).getItemProperty(
																CAPTION)
														.setValue(child.getValue());

												// Set it to be a child.
												tree.setParent(child, parent);

												tree.setChildrenAllowed(child,
														false);

												child_size++;
												parent_size++;
											}

										}

									}

								}

								if (child_size == 0) {
									hc.removeItem(parent);
									parent_size--;
								}
							}
						}*/

					} catch (Exception e) {
						// TODO: handle exception
					}
					
				}
			});

			/*
			 * window.addListener(new Window.CloseListener() {
			 * 
			 * @Override public void windowClose(CloseEvent e) { // TODO
			 * Auto-generated method stub vSplit.setSplitPosition(92); } });
			 */

			/*
			 * window.addListener(new Window.CloseListener() {
			 * 
			 * @Override public void windowClose(CloseEvent e) { // TODO
			 * Auto-generated method stub hsplit.setSplitPosition(20); } });
			 */

			SFormLayout menuLayout = new SFormLayout();

			menuLayout.addComponent(modules);
			menuLayout.setComponentAlignment(modules, Alignment.TOP_CENTER);
			menuLayout.addComponent(tree);

			hsplit.setFirstComponent(menuLayout);
			// hsplit.setFirstComponent();

			vSplit.setFirstComponent(hsplit);

			addComponent(topPanel);
			addComponent(vSplit);

			setSpacing(false);

			// layout.addComponent(bottomPanel);
			vSplit.setSecondComponent(bottomPanel);

			bottomPanel.addComponent(new Label("Footer Descriptions"));
			// bottomPanel.setContent(new Label("Footer Details"));

			mainPanel.setContent(new Label("Main Panel"));
			
			if (((Long)session.getAttribute("role_id")) == SConstants.ROLE_SPECIAL_ADMIN) {
				organizationSelect.setReadOnly(false);
				officeSelect.setReadOnly(false);
			}
			
			
			

			// TODO Auto-generated constructor stub

			if(!(Boolean)session.getAttribute("isCustomer")&&!(Boolean)session.getAttribute("isSupplier")){
				if(!settings.isHIDE_ALERTS())
					PopUpAlert(office.getId());
			}else{
				dashCloseButton.setVisible(false);
				finYearSelect.setReadOnly(true);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		
		
		/*if (((Long)session.getAttribute("role_id")) == SConstants.ROLE_SUPER_ADMIN || 
				((Long)session.getAttribute("role_id")) == SConstants.SEMI_ADMIN || 
				((Long)session.getAttribute("role_id")) == SConstants.ROLE_SYSTEM_ADMIN) { 
			workingDate.setReadOnly(false);
		}
		else*/
		workingDate.setReadOnly(true);
		
//		UI.getCurrent().getPushConfiguration().setPushMode(PushMode.AUTOMATIC);
		
		checkSettings();
		
		if(officeSelect.getValue()!=null)
			loadOptions(login_id,(Long) officeSelect.getValue());
		
	}
	
	private void loadOptions(long login_id, long ofcId) {
		// TODO Auto-generated method stub
		try {
			
		
			optionGroups.removeAllComponents();
			
			List lst = opgpDao.getAllOptionGroups();
			
			optionGroups.setRows((int) (lst.size() / 10) + 1);
			optionGroups.setWidth("1250");
			
			S_OptionGroupModel optionGpObj;
			List optionList;
			SVerticalLayout module;
			Iterator iter2;
			int ct = 0;
			S_OptionModel option;
			boolean avail;
			SMultiLink link1;
			SLabel lb;
			Iterator optIt = lst.iterator();
			while (optIt.hasNext()) {
	
				optionGpObj = (S_OptionGroupModel) optIt.next();
				S_LanguageMappingModel mdl=new LanguageMappingDao()
													.getLanguageMappingModel((long)2, 
																			Long.parseLong(session.getAttribute("language_id")+""), 
																			optionGpObj.getId());
				optionList = dbopDao.getOptionsUnderGroup(optionGpObj.getId());
	
				if (optionList.size() > 0) {
					module = new SVerticalLayout();
					module.setStyleName("dash_board_blocks");
					module.setMargin(true);
					module.setSpacing(true);
					module.setWidth("200");
					
					lb = new SLabel();
					if(mdl!=null)
						lb.setValue(mdl.getName());
					else
						lb.setValue(optionGpObj.getOption_group_name());
					lb.setIcon(new ThemeResource("moduleicons/logo"+ optionGpObj.getId() + ".png"));
					lb.setStyleName("optiongp_name_icon");
					//
					module.addComponent(lb);
					module.setComponentAlignment(lb, Alignment.TOP_LEFT);
	
					iter2 = optionList.iterator();
					ct = 0;
					while (iter2.hasNext()) {
						option = (S_OptionModel) iter2.next();
						
						S_LanguageMappingModel optmdl=new LanguageMappingDao()
																.getLanguageMappingModel((long)3, 
																						Long.parseLong(session.getAttribute("language_id")+""), 
																						option.getOption_id());
						
						avail = logOptMap
								.isOptionsAvailToUser(login_id,
										option.getOption_id(),ofcId);
	
						if (avail) {
							if(optmdl!=null)
								link1 = new SMultiLink(optmdl.getName());
							else
								link1 = new SMultiLink(option.getOption_name());
							
							link1.setId(String.valueOf(option.getOption_id()));
							module.addComponent(link1);
							module.setComponentAlignment(link1,Alignment.MIDDLE_LEFT);
							module.setCaption(null);
	
							ct = 1;
						}
					}
	
					if (ct != 0)
						optionGroups.addComponent(module);
	
				}
	
			}
	
			optionGroups.setSpacing(true);
			

		} 
		catch (Exception e) {
			e.printStackTrace();
		}
			
		
		loadTree(ofcId);
		
		
	}

	private void loadTree(long ofcId) {
		// TODO Auto-generated method stub

		try {

			tree.setStyleName("menu_tree_style");

			tree.setItemCaptionPropertyId("value");
			// List modules = new ModuleDao().getAllModules();

			final String CAPTION = "key"; // can be any string
			HierarchicalContainer hc = new HierarchicalContainer();
			hc.addContainerProperty(CAPTION, String.class, null);
			tree.setContainerDataSource(hc);
			tree.setItemCaptionPropertyId(CAPTION);

			S_ModuleModel mod = null;
			
			if(modules.getValue()!=null && !modules.getValue().toString().equals("0"))
				 mod = mdDao.getModule((Long) modules.getValue());
			
			List groups = null;
			if (mod == null) {
				groups = opgpDao.getAllOptionGroups();
			} else {
				groups = opgpDao
						.getOptionGroupsUnderModule(mod.getId());
			}
			

			int parent_size = 0;

			if (groups.size() <= 0) {

			} else {

				S_OptionGroupModel grp;
				String group;
				KeyValue parent;
				List options;
				KeyValue child;
				long loginId;
				for (int j = 0; j < groups.size(); j++) {

					grp = (S_OptionGroupModel) groups.get(j);

					group = grp.getOption_group_name();

					parent = new KeyValue(0, group);

					hc.addItem(parent).getItemProperty(CAPTION)
							.setValue(parent.getValue());

					parent_size++;

					options = dbopDao.getOptionsUnderGroup(grp.getId());

					int child_size = 0;

					if (options.size() <= 0) {
						// The planet has no moons so make it a leaf.
						tree.setChildrenAllowed(parent, false);
					} else {
						// Add children (moons) under the planets.
						for (int k = 0; k < options.size(); k++) {

							S_OptionModel opt = (S_OptionModel) options.get(k);

							if (session.getAttribute("login_id") != null) {
								loginId = (Long) session
										.getAttribute("login_id");

								boolean avail = new LoginOptionMappingDao()
										.isOptionsAvailToUser(loginId,
												opt.getOption_id(), ofcId);

								if (avail) {
									child = new KeyValue(opt.getOption_id(),
											opt.getOption_name());

									hc.addItem(child).getItemProperty(CAPTION)
											.setValue(child.getValue());

									// Set it to be a child.
									tree.setParent(child, parent);

									tree.setChildrenAllowed(child, false);

									child_size++;
									parent_size++;
								}

							}

						}

					}

					if (child_size == 0) {
						hc.removeItem(parent);
						parent_size--;
					}
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		tree.setSelectable(true);
		
	}

	public void openOption(long option_id) {
		// TODO Auto-generated method stub
		
		S_OptionModel opt;
		
		try {
			long language=(Long)languageSelect.getValue();
			opt = dbopDao.getOptionForOpen(option_id, (Long)session.getAttribute("project_type"));

			if(!settings.isKEEP_OTHER_WINDOWS()) {
				if(getUI()!=null) 
				if(getUI().getWindows()!=null){
					Iterator it= getUI().getWindows().iterator();
					while (it.hasNext()) {
						try {
							getUI().removeWindow((Window) it.next());
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				}
			}
			// getUI().getCurrent().getWindows().removeAll(getUI().getCurrent().getWindows());
			
			
			S_LanguageMappingModel optmdl=new LanguageMappingDao().getLanguageMappingModel((long)3, 
																							Long.parseLong(session.getAttribute("language_id")+""), 
																							opt.getOption_id());

			window = (SparkLogic) objSRefl.getClassInstance(opt.getClass_name());
//			if(optmdl!=null)
//				window.setCaption(optmdl.getName());
//			else
				window.setCaption(opt.getOption_name());
			window.center();
			window.setCloseShortcut(KeyCode.X, ShortcutAction.ModifierKey.ALT);
			getUI().getCurrent().addWindow(window);
			
			saveActivity(opt.getOption_id(), "Accesed Option : "+opt.getOption_name(), (Long)session.getAttribute("login_id"), 
					(Long)session.getAttribute("office_id"));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void saveActivity(long optionId, String log, long login_id, long office_id){
		try {
			ActivityLogModel activityLogModel=new ActivityLogModel();
			activityLogModel.setDate(CommonUtil.getCurrentDateTime());
			activityLogModel.setLog(log);
			activityLogModel.setLogin(login_id);
			activityLogModel.setOffice_id(office_id);
			activityLogModel.setOption(optionId);
			activityLogModel.setBillId(0);
			new CommonMethodsDao().saveActivityLog(activityLogModel);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void checkSettings() {
		// TODO Auto-generated method stub
		
		S_OptionModel opt;
		
		try {
			
//			if(!settings.isKEEP_OTHER_WINDOWS()) {
//				Iterator it= getUI().getWindows().iterator();
//				while (it.hasNext()) {
//					try {
//						getUI().removeWindow((Window) it.next());
//					} catch (Exception e) {
//						// TODO: handle exception
//					}
//				}
//			}
			
			if (settingsWindow != null)
				settingsWindow.close();
			
			System.out.println(session
					.getAttribute("office_id"));
			
			if(session.getAttribute("settings_not_set")!=null) {
				
				settingsWindow = new AccountSettingsUI();
				settingsWindow.setClosable(true);
				settingsWindow.setCaption("Account Settings");
				settingsWindow.center();
				getUI().getCurrent().addWindow(settingsWindow);
				
				settingsWindow.addCloseListener(closeListener);
				
				settingsWindow.setModal(true);
				
				
				Notification.show("Settings Not Set", "Account Settings is Mandatory for the System. You can't continue without set the settings.",
						Type.ERROR_MESSAGE);
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void PopUpAlert(long office_id) {
		try {

			SparkLogic pop = new LoginAlertPopup();

			if (pop.getContent() != null) {
				pop.center();
				pop.setModal(true);
				pop.setCloseShortcut(ShortcutAction.KeyCode.ESCAPE, null);

				pop.setCaption("Alerts..!!");
				getUI().getCurrent().addWindow(pop);

				pop.focus();
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Notification.show("Error..!!", "Error Message :" + e.getCause(),
					Type.ERROR_MESSAGE);
		}
	}

	public String getPropertyName(String name) {
		try {
			ResourceBundle bundle = ResourceBundle.getBundle(new SessionUtil().getHttpSession().getAttribute("property_file").toString());
			if (bundle != null)
				name = bundle.getString(name);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return name;
	}
	
	public void setRequiredError(AbstractComponent component,
			String fieldNameToDisplay, boolean enable) {
		if (enable) {
			component.setComponentError(new SUserError(
					"<i style='font-size: 13px;'>" + fieldNameToDisplay,
					ContentMode.HTML, ErrorLevel.CRITICAL));
		} else
			component.setComponentError(null);
	}
	
}
