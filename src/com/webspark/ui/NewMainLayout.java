package com.webspark.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import org.vaadin.sebastian.dock.events.DockClickEvent;
import org.vaadin.sebastian.dock.events.DockClickListener;

import com.inventory.common.dao.CommonMethodsDao;
import com.inventory.config.acct.dao.CustomerDao;
import com.inventory.config.acct.dao.PDCPaymentDao;
import com.inventory.config.acct.dao.SupplierDao;
import com.inventory.config.acct.model.CustomerModel;
import com.inventory.config.acct.model.SupplierModel;
import com.inventory.config.acct.ui.PDCPaymentUI;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.config.settings.biz.SettingsBiz;
import com.inventory.config.settings.ui.AccountSettingsUI;
import com.inventory.management.dao.TasksDao;
import com.inventory.management.model.TasksModel;
import com.inventory.management.ui.AddTasksUI;
import com.inventory.process.model.FinancialYearsModel;
import com.inventory.purchase.dao.PurchaseOrderDao;
import com.inventory.purchase.ui.PurchaseOrderUI;
import com.inventory.reports.ui.LoginAlertPopup;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.AbstractErrorMessage.ContentMode;
import com.vaadin.server.ErrorMessage.ErrorLevel;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SContainerLayout;
import com.webspark.Components.SCustomLayout;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHTMLLabel;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SImage;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNativeButton;
import com.webspark.Components.SNativeSelect;
import com.webspark.Components.SPopupView;
import com.webspark.Components.STextField;
import com.webspark.Components.SUserError;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SWindow;
import com.webspark.Components.SparkLogic;
import com.webspark.bean.SessionActivityBean;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.common.util.SessionUtil;
import com.webspark.core.SReflection;
import com.webspark.dao.DBOperations;
import com.webspark.dao.LanguageDao;
import com.webspark.dao.LanguageMappingDao;
import com.webspark.dao.LoginDao;
import com.webspark.dao.ModuleDao;
import com.webspark.dao.OptionGroupDao;
import com.webspark.dao.QuickMenuDao;
import com.webspark.mailclient.model.MyMailsModel;
import com.webspark.mailclient.ui.MyEmailsUI;
import com.webspark.model.ActivityLogModel;
import com.webspark.model.ReportIssueModel;
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
import com.webspark.uac.model.S_OrganizationModel;
import com.webspark.uac.model.UserModel;
import com.webspark.uac.ui.QuickMenuOptionMapUI;
import com.webspark.uac.ui.UserManagementUI;

/**
 * @author anil
 */

@Title("REVERP")
@Theme("testappstheme")
public class NewMainLayout extends SHorizontalLayout {
	
	private static final long serialVersionUID = -540949543149045721L;
	
	SCustomLayout leftPanel, topPanel;
	
	SVerticalLayout vertLay=new SVerticalLayout();
	SVerticalLayout optionsVertLay=new SVerticalLayout();
	
	SHorizontalLayout analyticsLay=new SHorizontalLayout();
	
	OptionGroupDao opGrpDao=new OptionGroupDao();
	ModuleDao moduleDao=new ModuleDao();
	DBOperations dbopDao=new DBOperations();
	OfficeDao ofcDao=new OfficeDao();
	OrganizationDao orgDao=new OrganizationDao();
	UserManagementDao userDao=new UserManagementDao();
	
	int notification_id=2;
	
	SPopupView settingsPop;
	
	SNativeButton alertsNotifBtn, msgsNotifBtn, notificationsBtn;
	SButton logoutBtn,settingsButton,searchButton,helpBtn,homeButton;
	STextField searchTxt;
	
	LayoutClickListener clickListener;
	LayoutClickListener menuClickListener;
	
	SWindow window;
	
	SHTMLLabel orgDetails,userDetails;
	SLabel nameLabel,emailLabel;
	
	WrappedSession session=new SessionUtil().getHttpSession();
	SettingsValuePojo settings;
	SReflection objSRefl = new SReflection();
	
	MenuBar quickMenu,savedSessionsMenu;
	MenuBar addNewMenu;
	MenuBar recentMenu;
	MenuBar settingsMenu;
	
	MenuItem settingsMenuItem;
	MenuItem quickItem;
	MenuItem addItem;
	MenuItem recentItem, savedSessionItem;

	long office_id;
	long login_id;
	long moduleId=0;
	
	SImage userImage; 
	SImage userImageBig; 
	SImage image; 
	
	SDateField workingDate;
	SNativeSelect organizationSelect, languageSelect,finYearSelect,officeSelect;
	
	SHTMLLabel officeName= new SHTMLLabel();
	SHTMLLabel organizationName=new SHTMLLabel();
	SHTMLLabel workingDateLabel=new SHTMLLabel();
	
	SWindow settingsWindow;
	SettingsBiz stbizObj=new SettingsBiz();
	
	SComboField chartOptionComboField=new SComboField();
	SDateField chartFromDate =new SDateField();
	SDateField chartToDate =new SDateField();
	SCustomLayout analyticsCustomLay = new SCustomLayout("analytics_panel");
	
	SButton refreshButton;
	
	ClickListener refreshListener;
	ClickListener closeIssueListener;
	
	SButton viewProfileButton=new SButton(getPropertyName("view_profile"));
	SFormLayout userDetailsLay=new SFormLayout();
	SPopupView userDetailsView=null;
	
	SVerticalLayout mailLay=new SVerticalLayout();
	SFormLayout taskLay=new SFormLayout();
	SPopupView mailPopupView=null;
	LayoutClickListener mailLayoutClickListener;
	LayoutClickListener pdcLayoutClickListener;
	LayoutClickListener poExpiryLayoutClickListener;
	
	DockClickListener docClickListener;
	SNativeButton closeBtn;
	SNativeButton closeUserBtn;
	SNativeButton closeMailBtn;
	SNativeButton closeIssueBtn;

	private int sortType=1;
	
	CustomerModel cust = null;
	SupplierModel sup = null;
	
	HomePageUI homePage;
	CloseListener closeList;
	S_OfficeModel office;
	
	@SuppressWarnings({  "serial", "static-access" })
	public NewMainLayout() {
		super();
		try {
			orgDetails=new SHTMLLabel(null,"<h2><u style='margin-left: 40px;'>"+getPropertyName("organization_details"),300);
			userDetails=new SHTMLLabel(null,"<h2><u style='margin-left: 40px;'>"+getPropertyName("user_details"),170);
			nameLabel=new SLabel();
			emailLabel=new SLabel();
			
			office = ofcDao.getOffice((Long) session.getAttribute("office_id"));
			
		if(session.getAttribute("login_id")!=null)
			login_id=(Long) session.getAttribute("login_id");
		
		if(session.getAttribute("office_id")!=null)
			office_id=(Long) session.getAttribute("office_id");
		
		closeBtn = new SNativeButton("X");
		closeMailBtn = new SNativeButton("X");
		closeUserBtn = new SNativeButton("X");
		
		searchTxt=new STextField(null);
		searchTxt.setInputPrompt(getPropertyName("search"));
		searchTxt.setHeight("26");
		searchTxt.setStyleName("search_textfield");
		searchTxt.setDescription("ALT+S");
		
		searchTxt.addShortcutListener(new ShortcutListener(getPropertyName("submit_item"),
				KeyCode.ENTER, null) {
			@Override
			public void handleAction(Object sender, Object target) {
				try {
					if(target==(Object)searchTxt)
						searchButton.click();
				} catch (Exception e) {
				}
			}
		});
		
		addShortcutListener(new ShortcutListener(getPropertyName("search"),
				ShortcutAction.KeyCode.S,
				new int[] { ShortcutAction.ModifierKey.ALT }) {
				@Override
				public void handleAction(Object sender, Object target) {
					if(target!=(Object)searchTxt){
						searchTxt.focus();
						searchTxt.selectAll();
					}
				}
				});

		clickListener=new LayoutClickListener() {
			@Override
			public void layoutClick(LayoutClickEvent event) {
				if(event.getChildComponent()!=null){
					openOption(Long.parseLong(event.getChildComponent().getId()));
				}
			}
		};
		
		closeList=new CloseListener() {
			@Override
			public void windowClose(CloseEvent e) {
				homePage.loadRecentlyUsed(login_id, office.getId(), clickListener);
				createTaskNotification();
			}
		};
		
		menuClickListener=new LayoutClickListener() {
			@Override
				public void layoutClick(LayoutClickEvent event) {
				
				if(event.getChildComponent()!=null&&!event.getChildComponent().getId().equals("")){
					analyticsLay.removeAllComponents();
					optionsVertLay.removeAllComponents();
					
					moduleId=Long.parseLong(event.getChildComponent().getId());
					
					createOptions(Long.parseLong(event.getChildComponent()
							.getId()));
					createAnalytics(Long.parseLong(event.getChildComponent()
							.getId()));
					createUpdates(Long.parseLong(event.getChildComponent()
							.getId()));
					resetChartDates();
					
					optionsVertLay.addComponent(analyticsLay);
				}
				
				}
		};
		
		addNewMenu=new MenuBar();
		quickMenu=new MenuBar();
		recentMenu=new MenuBar();
		savedSessionsMenu=new MenuBar();
		
		quickItem = quickMenu.addItem("", null);
		quickItem.setStyleName("quick_menu_style");
		quickItem.setDescription(getPropertyName("quick_menu"));
		
		addItem = addNewMenu.addItem("", null);
		addItem.setStyleName("create_menu_style");
		addItem.setDescription(getPropertyName("create_new"));
		
		recentItem = recentMenu.addItem("", null);
		recentItem.setStyleName("recent_menu_style");
		recentItem.setDescription(getPropertyName("recent_activity"));
		
		savedSessionItem = savedSessionsMenu.addItem("", null);
		savedSessionItem.setStyleName("savedsessions_menu_style");
		savedSessionItem.setDescription(getPropertyName("saved_session"));
		
		
//		createNewPopGrid=createQuickMenu();
//		createNewPopGrid.addComponent(new SButton("Sale"));
//		createNewPopGrid.addComponent(new SButton("Purchase"));
//		createNewPopGrid.addComponent(new SButton("Payment"));
//		createNewPop=new SPopupView(null, createNewPopGrid);
//		createNewPop.setHideOnMouseOut(false);
		
//		createNewPopGrid.setPrimaryStyleName("addnew_popup_grid_style");

		if (session.getAttribute("settings") != null) 
			settings = (SettingsValuePojo) session.getAttribute("settings");
		
		setWidth("100%");
		
		alertsNotifBtn=new SNativeButton("99");
		alertsNotifBtn.setDescription(getPropertyName("alerts"));
		alertsNotifBtn.setPrimaryStyleName("box");
		
		msgsNotifBtn=new SNativeButton();
		msgsNotifBtn.setDescription(getPropertyName("mails"));
		msgsNotifBtn.setPrimaryStyleName("box2");
		
		notificationsBtn=new SNativeButton();
		notificationsBtn.setPrimaryStyleName("box3");
		notificationsBtn.setDescription(getPropertyName("issues"));
		
		mailLay.setWidth("250px");
		mailLay.setSpacing(true);
		mailPopupView=new SPopupView(null,mailLay);
		mailPopupView.setHideOnMouseOut(false);
		mailLay.setPrimaryStyleName("topMenu_popup_style");
		
		mailLayoutClickListener=new LayoutClickListener() {
			@Override
			public void layoutClick(LayoutClickEvent event) {
				if(notification_id==1) {
					if(event.getComponent()!=null&&event.getComponent().getId()!=null){
						AddTasksUI tasks=new AddTasksUI();
						tasks.setCaption(getPropertyName("task_management"));
						tasks.setOptionValue(Long.parseLong(event.getComponent().getId()));
						openOption(tasks);
						mailPopupView.setPopupVisible(false);
					}
				}
				else if(notification_id==2) {
					if(event.getComponent()!=null&&event.getComponent().getId()!=null){
						MyEmailsUI emails=new MyEmailsUI();
						emails.setCaption(getPropertyName("my_mails"));
						openOption(emails);
						emails.setMail(event.getComponent().getId());
						mailPopupView.setPopupVisible(false);
					}
				}
				else if(notification_id==3) {
					if(event.getComponent()!=null&&event.getComponent().getId()!=null){
						
						ReportIssueModel issues;
						try {
							issues = dbopDao.getReportedIssueModel(Long.parseLong(event.getComponent().getId()));
						
							SparkLogic spark=(SparkLogic) openOption(issues.getOption());
							spark.getBillNoFiled().setValue(issues.getBillId());
							mailPopupView.setPopupVisible(false);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		};
		
		
		pdcLayoutClickListener=new LayoutClickListener() {
			@Override
			public void layoutClick(LayoutClickEvent event) {
				
				try {
					PDCPaymentUI pdc=new PDCPaymentUI();
					pdc.setCaption("PDC Payment");
					openOption(pdc);
					pdc.loadData(0);
					mailPopupView.setPopupVisible(false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		
		
		poExpiryLayoutClickListener=new LayoutClickListener() {
			@Override
			public void layoutClick(LayoutClickEvent event) {
				
				try {
					PurchaseOrderUI pdc=new PurchaseOrderUI();
					pdc.setCaption("Purchase Order");
					openOption(pdc);
					pdc.loadOptions(0);
					mailPopupView.setPopupVisible(false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		
		msgsNotifBtn.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				notification_id=2;
				createMailNotification();
				mailPopupView.setPopupVisible(true);
			}
		});
		
		
		alertsNotifBtn.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				notification_id=1;
				createTaskNotification();
				mailPopupView.setPopupVisible(true);
			}
		});
	
		
		notificationsBtn.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				notification_id=3;
				createIssueNotification();
				mailPopupView.setPopupVisible(true);
			}
		});
		
		
		closeIssueListener=new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(event.getComponent().getId()!=null){
					
					try {
						dbopDao.updateReportedIssue(Long.parseLong(event.getComponent().getId()));
						createIssueNotification();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
		
		createMailNotification();
		createTaskNotification();
		createIssueNotification();
		
		logoutBtn=new SButton();
		logoutBtn.setDescription(getPropertyName("log_out"));
		logoutBtn.setPrimaryStyleName("logout_btn_style");
		
		
		settingsButton=new SButton();
		settingsButton.setPrimaryStyleName("main_settings_btn_style");
		searchButton=new SButton();
		searchButton.setPrimaryStyleName("main_search_btn_style");
		
		homeButton=new SButton();
		homeButton.setPrimaryStyleName("home_btn_style");
		homeButton.setDescription(getPropertyName("home"));
		
		helpBtn=new SButton();
		helpBtn.setPrimaryStyleName("main_help_btn_style");
		helpBtn.setDescription(getPropertyName("help"));
		
		 List orgList=null;
			if (((Long) session.getAttribute("role_id")) == SConstants.ROLE_SPECIAL_ADMIN) {
				orgList = ofcDao.getAllOrganizationNamesOfUserMaped(
						(Long) session.getAttribute("login_id"),
						(Long) session.getAttribute("user_office_id"));
			}
			else {
				orgList=new OrganizationDao().getAllOrganizations();
			}
			
		organizationSelect = new SNativeSelect(getPropertyName("organization")+" :", 180, orgList, "id", "name");
		organizationSelect.setImmediate(true);
//		organizationSelect.setDescription("Organization");
		
		if(Integer.parseInt(session.getAttribute("language_id")+"")!=0){
			languageSelect = new SNativeSelect(getPropertyName("language")+" :", 100,new LanguageDao().getAllLanguages(),"id","name");
			languageSelect.setComponentError(null);
			languageSelect.setDescription(getPropertyName("select_language"));
			languageSelect.setValue(Long.parseLong(session.getAttribute("language_id")+""));
		}
		else{
			languageSelect = new SNativeSelect(getPropertyName("language")+" :", 100,null,"id","name");
			languageSelect.setComponentError(null);
			setRequiredError(languageSelect, getPropertyName("set_language_office"), true);
			languageSelect.setValue(null);
		}
//		languageSelect.setDescription("Language Select");
//		languageSelect.addItem("English");
//		languageSelect.addItem("Arabic");
//		languageSelect.setValue("English");
		
		officeSelect = new SNativeSelect(getPropertyName("office")+" :", 180, null, "id", "name");
		officeSelect.setImmediate(true);
//		officeSelect.setDescription("Office");
		
		finYearSelect = new SNativeSelect(getPropertyName("financial_year")+" :", 180,dbopDao.getFinancialYears(office.getId()), "id", "name");
//		finYearSelect.setDescription("Financial Year");

		workingDate = new SDateField(getPropertyName("office_working_date")+" :");
		workingDate.setNewValue(new Date(((java.sql.Date) session.getAttribute("working_date")).getTime()));
		workingDate.setImmediate(true);
		workingDate.setReadOnly(true);
		
		String fil = "OrganizationLogos/"
				+ office.getOrganization().getId() + ".png";
		image = new SImage(null, new ThemeResource(fil));
	 	image.setHeight("120px");
	 	image.setWidth("120px");
	 	image.setStyleName("image_style_nm");
		
		SFormLayout settingsDetailLay=new SFormLayout();
		settingsDetailLay.setSpacing(true);
		
		settingsDetailLay.addComponent(image);
		settingsDetailLay.addComponent(organizationSelect);
		settingsDetailLay.addComponent(officeSelect);
		settingsDetailLay.addComponent(finYearSelect);
		settingsDetailLay.addComponent(workingDate);
		settingsDetailLay.addComponent(languageSelect);
		
		SVerticalLayout vLay=new SVerticalLayout(
				true,
				new SHorizontalLayout(orgDetails, closeBtn),settingsDetailLay);
		vLay.setStyleName("layout_bg_colored");
		settingsPop = new SPopupView("",vLay);
		settingsPop.setHideOnMouseOut(false);
		settingsPop.setPrimaryStyleName("addnew_popup_style");
		 
		
		if ((Boolean) session.getAttribute("isCustomer")) {
			cust = new CustomerDao().getCustomerFromLogin((Long) session
					.getAttribute("login_id"));
		} else if ((Boolean) session.getAttribute("isSupplier")) {
			sup = new SupplierDao().getSupplierFromLogin((Long) session
					.getAttribute("login_id"));
		}
		
		String empImageFile="EmployeePhotos/base_image.png";
		ThemeResource mainRes=new ThemeResource(empImageFile);
		File file=new File(VaadinServlet.getCurrent().getServletContext()
				.getRealPath("/")
				+ "VAADIN/themes/testappstheme/"+empImageFile);
		final UserModel usr;
		SLabel userName=new SLabel();
		userName.setWidth("80px");
		String email="";
			usr = userDao.getUserFromLogin(login_id);

			if (usr != null) {
				empImageFile = "EmployeePhotos/" + usr.getId() + ".png";
				 file=new File(VaadinServlet.getCurrent().getServletContext()
						.getRealPath("/")
						+ "VAADIN/themes/testappstheme/"+empImageFile);
				if (file != null && file.exists())
					mainRes = new ThemeResource(empImageFile);
				
				userName.setValue(usr.getFirst_name());
				if(usr.getAddress()!=null)
					email=usr.getAddress().getEmail();
				
				officeName.setValue("<font size='2px'><center>"+usr.getLoginId().getOffice().getName()+"</center></font>");
				
				organizationName.setValue("<font size='3px'><center>"+usr.getLoginId().getOffice()
						.getOrganization().getName()+"</center></font>");
				viewProfileButton.setVisible(true);
				
			}else if(cust!=null){
				
				userName.setValue(cust.getName());
				email=cust.getAddress().getEmail();
				officeName.setValue("<font size='2px'><center>"+cust.getLedger().getOffice().getName()+"</center></font>");
				organizationName.setValue("<font size='3px'><center>"+cust.getLedger().getOffice().getOrganization()
						.getName()+"</center></font>");
				
				
			}else if(sup!=null){
				
				userName.setValue(sup.getName());
				email=sup.getAddress().getEmail();
				officeName.setValue("<font size='2px'><center>"+sup.getLedger().getOffice().getName()+"</center></font>");
				organizationName.setValue("<font size='3px'><center>"+sup.getLedger().getOffice().getOrganization()
						.getName()+"</center></font>");
				
			}else {
				S_LoginModel loginModel = new LoginDao().getLoginModel(login_id);
				email="";
				userName.setValue(loginModel.getLogin_name());
				officeName.setValue("<font size='2px'><center>"+loginModel.getOffice().getName()+"</center></font>");
				organizationName.setValue("<font size='2px'><center>"+ loginModel.getOffice().getOrganization()
						.getName()+"</center></font>");
				viewProfileButton.setVisible(false);
			}
		 userImage=new SImage(null, mainRes);
		 userImage.setHeight("30px");
		 userImage.setWidth("30px");
		 userImage.setStyleName("user_image_round");
		 
		 userImageBig=new SImage(null, mainRes);
		 userImageBig.setHeight("70px");
		 userImageBig.setWidth("70px");
		 userImageBig.setStyleName("user_image_round");
		 
		 userDetailsLay.addComponent(userImageBig);
		 nameLabel.setCaption(getPropertyName("name")+" :");
		 nameLabel.setValue(userName.getValue());
		 emailLabel.setCaption(getPropertyName("email")+" :");
		 emailLabel.setValue(email);
		 userDetailsLay.addComponent(nameLabel);
		 userDetailsLay.addComponent(emailLabel);
		 userDetailsLay.addComponent(viewProfileButton);
		 userDetailsLay.setWidth("200px");
		 userDetailsLay.setHeight("200px");
		 userDetailsLay.setMargin(true);
		 userDetailsLay.setStyleName("user_image_round");
		 
		 userDetailsView= new SPopupView(
					"",
					new SVerticalLayout(
							true,
							new SHorizontalLayout(userDetails, closeUserBtn),userDetailsLay));
		 userDetailsView.setHideOnMouseOut(false);
		 
		 SHorizontalLayout userLay=new SHorizontalLayout();
		 userLay.addComponent(userImage);
		 userLay.addComponent(userName);
		 userLay.setSpacing(true);
		 userLay.setComponentAlignment(userName, Alignment.MIDDLE_CENTER);
		 userLay.setStyleName("user_lay_style");
		
//		 UI.getCurrent().getPushConfiguration().setPushMode(PushMode.AUTOMATIC);
		 
		 userLay.addLayoutClickListener(new LayoutClickListener() {
			
			@Override
			public void layoutClick(LayoutClickEvent event) {
				userDetailsView.setPopupVisible(true);
			}
		});
		 
		 
		helpBtn.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				// TODO Auto-generated method stub
				
			}
		});
		
		searchButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				// TODO Auto-generated method stub
				if(!searchTxt.getValue().equals("")) {
					createSearchButtons(searchTxt.getValue());
				}
			}
		});
		 
		viewProfileButton.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				UserManagementUI userMgt=new UserManagementUI();
				userMgt.getUserPanel().getBillNoFiled().setValue(Long.parseLong(session.getAttribute("user_id").toString()));
				userMgt.getBillNoFiled().setReadOnly(true);
				userMgt.setCaption("User Management");
				
				openOption(userMgt);
				
				userDetailsView.setPopupVisible(false);
			}
		});
		 
		topPanel = new SCustomLayout("top_panel");
		topPanel.addComponent(alertsNotifBtn, "alerts_notif");
		topPanel.addComponent(msgsNotifBtn, "msgs_notif");
		topPanel.addComponent(notificationsBtn, "issue_notif");
		topPanel.addComponent(logoutBtn, "logout_btn");
		topPanel.addComponent(recentMenu, "recent_menu");
		topPanel.addComponent(savedSessionsMenu, "saved_sessions_menu");
		topPanel.addComponent(addNewMenu, "create_menu");
		topPanel.addComponent(quickMenu , "quick_menu");
		topPanel.addComponent(userLay , "user_image");
		topPanel.addComponent(settingsButton , "main_settings");
		topPanel.addComponent(homeButton , "home_button");
		topPanel.addComponent(searchButton , "search_button");
		topPanel.addComponent(searchTxt, "search_box");
		topPanel.addComponent(helpBtn, "help_btn");
		topPanel.addComponent(userDetailsView, "user_details_popup");
		topPanel.addComponent(settingsPop, "settings_popup");
		topPanel.addComponent(mailPopupView, "email_popup");
		
		
		workingDateLabel.setValue("<font size='3px'><center>"+CommonUtil.formatDateToCommonFormat(workingDate.getValue())+"</center></font>");
	 
	 	leftPanel = new SCustomLayout("left_panel");
		leftPanel.addComponent(createModules(), "modules");
		leftPanel.addComponent(officeName, "office_name");
		leftPanel.addComponent(organizationName, "organization_name");
		leftPanel.addComponent(workingDateLabel, "working_date");
//		leftPanel.addComponent(image, "organization_logo");
		
		leftPanel.setSizeFull();
		topPanel.setSizeFull();
		optionsVertLay.setSizeFull();
		optionsVertLay.setSpacing(true);
		
		vertLay.setSizeFull();
		vertLay.addComponent(topPanel);
		vertLay.addComponent(optionsVertLay);
		homePage=new HomePageUI(login_id, (Long) session.getAttribute("office_id"), clickListener);
		homePage.showAlert();
		optionsVertLay.addComponent(homePage);
		
		
		addComponent(leftPanel);
		addComponent(vertLay);
		setExpandRatio(leftPanel, 1);
		setExpandRatio(vertLay, 6);
		
		refreshListener=new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(event.getComponent().getId().equals("chart")){
					createChart();
				}else if(event.getComponent().getId().equals("updates")){
					createUpdates(moduleId);
				}else if(event.getComponent().getId().equals("feeds")){
					createUpdates(moduleId);
				}else{
					
					if(Long.parseLong(event.getComponent().getId())!=0){
						SCustomLayout	optionsLay = (SCustomLayout) event.getComponent().getParent();
						reloadOptions(optionsLay);
					}
				}
			}
		};
		
		logoutBtn.addClickListener(new SButton.ClickListener() {
			public void buttonClick(ClickEvent event) {

				session.invalidate();
				getUI().getSession().close();
				getUI().getPage().setLocation(
						VaadinService.getCurrentRequest().getContextPath()
								+ "/");
				
			}
		});
		
		settingsButton.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				settingsPop.setPopupVisible(true);
			}
		});
		
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

					organizationName.setValue("<font size='3px'><center>"+organizationSelect.getItemCaption(organizationSelect.getValue())+"</center></font>");

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
					
					S_OrganizationModel org=orgDao.getOrganization((Long)organizationSelect.getValue());
					String fil = "OrganizationLogos/"
							+ org.getLogoName();
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
						
						S_OfficeModel ofcObj = ofcDao.getOffice((Long) officeSelect.getValue());
						
						session.setAttribute("project_type",ofcObj.getOrganization().getProject_type());
						
						session.setAttribute("office_id",(Long) officeSelect.getValue());
						

						officeName.setValue("<font size='2px'><center>"+officeSelect.getItemCaption(officeSelect.getValue())+"</center></font>");

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
						
						workingDateLabel.setValue("<font size='3px'><center>"+CommonUtil.formatDateToCommonFormat(workingDate.getValue())+"</center></font>");
						
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
								}
							}
						}
						
						checkSettings();
						
						createAnalytics(moduleId);
						createUpdates(moduleId);
						
						if(moduleId==0)
							homeButton.click();
						
						
						leftPanel.addComponent(createModules(), "modules");
						
						optionsVertLay.removeAllComponents();
						if(cust==null&&sup==null) {
							homePage=new HomePageUI(login_id, (Long) session.getAttribute("office_id"), clickListener);
							optionsVertLay.addComponent(homePage);
						
						}
						moduleId=0;
						
						
						createAddMenu();
						createQuickMenu();
						createRecentMenu();
						createSavedSessionsMenu();
						createTaskNotification();
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
								logoutBtn.setDescription(getPropertyName("log_out"));
								homeButton.setDescription(getPropertyName("home"));
								helpBtn.setDescription(getPropertyName("help"));
								organizationSelect.setCaption(getPropertyName("organization")+" :");
								languageSelect.setCaption(getPropertyName("language")+" :");
								officeSelect.setCaption(getPropertyName("office")+" :");
								workingDate.setCaption(getPropertyName("office_working_date")+" :");
								finYearSelect.setCaption(getPropertyName("financial_year")+" :");
								viewProfileButton.setCaption(getPropertyName("view_profile"));
								quickItem.setDescription(getPropertyName("quick_menu"));
								addItem.setDescription(getPropertyName("create_new"));
								recentItem.setDescription(getPropertyName("recent_activity"));
								savedSessionItem.setDescription(getPropertyName("saved_session"));
								workingDateLabel.setValue("<font size='3px'><center>"+CommonUtil.formatDateToCommonFormat(workingDate.getValue())+"</center></font>");
								searchTxt.setInputPrompt(getPropertyName("search"));
								orgDetails.setValue("<h2><u style='margin-left: 40px;'>"+getPropertyName("organization_details"));
								userDetails.setValue("<h2><u style='margin-left: 40px;'>"+getPropertyName("user_details"));
								nameLabel.setCaption(getPropertyName("name")+" :");
								emailLabel.setCaption(getPropertyName("email")+" :");
								
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
					
				} catch (Exception e) {
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
					
					workingDateLabel.setValue("<font size='3px'><center>"+CommonUtil.formatDateToCommonFormat(workingDate.getValue())+"</center></font>");

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
		
		
		resetChartDates();
		
		
		if (((Long)session.getAttribute("role_id")) == SConstants.ROLE_SPECIAL_ADMIN) {
			organizationSelect.setReadOnly(false);
			officeSelect.setReadOnly(false);
		}
		
		if((Boolean)session.getAttribute("isCustomer")||(Boolean)session.getAttribute("isSupplier")){
			finYearSelect.setReadOnly(true);			
		}
		
		homeButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				optionsVertLay.removeAllComponents();
				if(cust==null&&sup==null) {
					homePage=new HomePageUI(login_id, (Long) session.getAttribute("office_id"), clickListener);
					optionsVertLay.addComponent(homePage);
					searchTxt.setValue("");
				}
				moduleId=0;
			}
		});
		
//		createQuickMenu();
//		createAddMenu();
//		createRecentMenu();
//		createSavedSessionsMenu();
		
		chartOptionComboField.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				createChart();
			}
		});
	
		
		chartFromDate.addListener(new Listener() {
			@Override
			public void componentEvent(Event event) {
				createChart();
			}
		});
		
		
		chartToDate.addListener(new Listener() {
			@Override
			public void componentEvent(Event event) {
				createChart();
			}
		});
		
		
		docClickListener=new DockClickListener() {
			@Override
			public void dockItemClicked(DockClickEvent event) {
				// TODO Auto-generated method stub
				if(event.getItem().getDescription().equals("Home")) {
					homeButton.click();
				}
				else if(event.getItem().getDescription().equals("Logout")) {
					logoutBtn.click();
				}
				else if(event.getItem().getDescription().equals("Help")) {
					helpBtn.click();
				}
			}
		};
		
		
		closeBtn.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				settingsPop.setPopupVisible(false);
			}
		});
		
		
		closeUserBtn.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				userDetailsView.setPopupVisible(false);
			}
		});
		
		
		closeMailBtn.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				mailPopupView.setPopupVisible(false);
			}
		});
		
		
		searchTxt.focus();
		
		if(cust!=null||sup!=null){
			disableTopPanel();
			optionsVertLay.removeAllComponents();
		}
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}
	
	
	private void disableTopPanel() {
		viewProfileButton.setVisible(false);
		
		recentItem.removeChildren();
		addItem.removeChildren();;
		quickItem.removeChildren();
		savedSessionItem.removeChildren();
		
		settingsButton.setEnabled(false);
		msgsNotifBtn.setEnabled(false);
		notificationsBtn.setEnabled(false);
		homeButton.setEnabled(false);
		alertsNotifBtn.setEnabled(false);
	}

	
	@SuppressWarnings("rawtypes")
	protected void reloadOptions(SCustomLayout optionsLay) {

		try {
				SGridLayout optionsGrid;
				
				S_OptionModel optionModel;
				
				SHorizontalLayout menu=null;
				SHTMLLabel label=null;
				optionsGrid=new SGridLayout();
				optionsGrid.setSizeFull();
				optionsGrid.setColumns(7);
//				optionsGrid.setHeight("170px");
				optionsGrid.setStyleName("options_scroll");
				
				S_OptionGroupModel optionGrpModel=opGrpDao.getOptionGroup(Long.parseLong(optionsLay.getId()));
				
				optionsLay.removeAllComponents();
				
				List optionList = dbopDao.getOptionsUnderGroupAssignedToUser(optionGrpModel.getId(),login_id,sortType, (Long) officeSelect.getValue());
				Iterator it=optionList.iterator();
				while (it.hasNext()) {
					optionModel = (S_OptionModel) it.next();
					
					S_LanguageMappingModel optmdl=new LanguageMappingDao().getLanguageMappingModel((long)3, 
																									Long.parseLong(session.getAttribute("language_id")+""), 
																									optionModel.getOption_id());
					
					menu = new SHorizontalLayout();
					if(optmdl!=null)
						label = new SHTMLLabel(null,"<center><b class='label_option_name'>"+optmdl.getName()+"</b><center>");
					else
						label = new SHTMLLabel(null,"<center><b class='label_option_name'>"+optionModel.getOption_name()+"</b><center>");
					label.setWidth("100%");
					menu.setId(""+optionModel.getOption_id());
					menu.addComponent(label);
					menu.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
					menu.setStyleName("button outline-inward");
					menu.setWidth("90%");
					menu.setHeight("40px");
					optionsGrid.addComponent(menu);
					optionsGrid.setComponentAlignment(menu, Alignment.TOP_CENTER);
				}
				
				S_LanguageMappingModel mapmdl=new LanguageMappingDao()
													.getLanguageMappingModel((long)2, 
															Long.parseLong(session.getAttribute("language_id")+""), 
																			optionGrpModel.getId());
				
				optionsGrid.addLayoutClickListener(clickListener);
				if(mapmdl!=null)
					optionsLay.addComponent(new SHTMLLabel(null, "<b class='label_name'>"+ mapmdl.getName()+ "</b>"), "option_grp_head");
				else
					optionsLay.addComponent(new SHTMLLabel(null, "<b class='label_name'>"+ optionGrpModel.getOption_group_name()+ "</b>"), "option_grp_head");				
				refreshButton=new SButton();
				refreshButton.setPrimaryStyleName("refresh_btn");
				refreshButton.setId(optionGrpModel.getId()+"");
				refreshButton.addClickListener(refreshListener);
				
				settingsMenu=new MenuBar();
				settingsMenuItem = settingsMenu.addItem("", null);
				settingsMenuItem.setDescription(optionGrpModel.getId()+"");
				settingsMenuItem.setStyleName("settings_menu_style");
				createSettingMenu();
				
				optionsLay.addComponent(optionsGrid, "options_grid");
				optionsLay.addComponent(refreshButton, "refresh_btn");
				optionsLay.addComponent(settingsMenu, "settings");
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	@SuppressWarnings("rawtypes")
	protected void sortOptions(long optionGrpId) {

		try {
				SGridLayout optionsGrid;
				
				S_OptionModel optionModel;
				
				SHorizontalLayout menu=null;
				SHTMLLabel label=null;
				optionsGrid=new SGridLayout();
				optionsGrid.setSizeFull();
				optionsGrid.setColumns(7);
//				optionsGrid.setHeight("170px");
				optionsGrid.setStyleName("options_scroll");
				
				Iterator iter=optionsVertLay.getComponentIterator();
				SCustomLayout optionsLay=null;
				while (iter.hasNext()) {
					
					Component comp=(Component) iter.next();
						
						if(comp.getId()!=null){
					if(comp.getId().equals(optionGrpId+"")){
						optionsLay = (SCustomLayout)comp ;
					}
						}
				}
				
				if(optionsLay!=null){
				S_OptionGroupModel optionGrpModel=opGrpDao.getOptionGroup(Long.parseLong(optionsLay.getId()));
				
				optionsLay.removeAllComponents();
				
				List optionList = dbopDao.getOptionsUnderGroupAssignedToUser(optionGrpModel.getId(),login_id,sortType,(Long) officeSelect.getValue());
				Iterator it=optionList.iterator();
				while (it.hasNext()) {
					optionModel = (S_OptionModel) it.next();
					
					S_LanguageMappingModel optmdl=new LanguageMappingDao()
														.getLanguageMappingModel((long)3, 
																Long.parseLong(session.getAttribute("language_id")+""), 
																				optionModel.getOption_id());
					
					menu = new SHorizontalLayout();
					if(optmdl!=null)
						label = new SHTMLLabel(null,"<center><b class='label_option_name'>"+optmdl.getName()+"</b><center>");
					else
						label = new SHTMLLabel(null,"<center><b class='label_option_name'>"+optionModel.getOption_name()+"</b><center>");
					label.setWidth("100%");
					menu.setId(""+optionModel.getOption_id());
					menu.addComponent(label);
					menu.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
					menu.setStyleName("button outline-inward");
					menu.setWidth("90%");
					menu.setHeight("40px");
					optionsGrid.addComponent(menu);
					optionsGrid.setComponentAlignment(menu, Alignment.TOP_CENTER);
				}
				
				S_LanguageMappingModel mapmdl=new LanguageMappingDao()
													.getLanguageMappingModel((long)2, 
															Long.parseLong(session.getAttribute("language_id")+""), 
																			optionGrpModel.getId());
				
				optionsGrid.addLayoutClickListener(clickListener);
				if(mapmdl!=null)
					optionsLay.addComponent(new SHTMLLabel(null, "<b class='label_name'>"+ mapmdl.getName()+ "</b>"), "option_grp_head");
				else
					optionsLay.addComponent(new SHTMLLabel(null, "<b class='label_name'>"+ optionGrpModel.getOption_group_name()+ "</b>"), "option_grp_head");
				
				refreshButton=new SButton();
				refreshButton.setPrimaryStyleName("refresh_btn");
				refreshButton.setId(optionGrpModel.getId()+"");
				refreshButton.addClickListener(refreshListener);
				
				settingsMenu=new MenuBar();
				settingsMenuItem = settingsMenu.addItem("", null);
				settingsMenuItem.setDescription(optionGrpModel.getId()+"");
				settingsMenuItem.setStyleName("settings_menu_style");
				createSettingMenu();
				
				optionsLay.addComponent(optionsGrid, "options_grid");
				optionsLay.addComponent(refreshButton, "refresh_btn");
				optionsLay.addComponent(settingsMenu, "settings");
				
				}
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	
	@SuppressWarnings("serial")
	private void createSettingMenu() {
		MenuItem sortItem=settingsMenuItem.addItem("Sort",null);
		sortItem.addItem("By Name",new Command() {
			
			@Override
			public void menuSelected(MenuItem selectedItem) {
				sortType=2;
				sortOptions(Long.parseLong(selectedItem.getParent().getParent().getDescription()));
				
			}
		});
		sortItem.addItem("By Priority",new Command() {
			
			@Override
			public void menuSelected(MenuItem selectedItem) {
				sortType=1;
				reloadOptions((SCustomLayout)settingsMenu.getParent());
			}
		});
	}

	
	public DockClickListener getDocListener() {
		return docClickListener;
	}
	
	
	private void createMailNotification() {
		try {
			MyMailsModel myMailsModel=null;
			SVerticalLayout mailForm;
			SHorizontalLayout closeLayout=new SHorizontalLayout();
			closeLayout.addComponent(new SHTMLLabel(
							null,
							"<h2><u style='margin-left: 40px;'>"+getPropertyName("unread_mails"),220));
			closeLayout.addComponent(closeMailBtn);
			mailLay.removeAllComponents();
			mailLay.addComponent(closeLayout);
			List list=dbopDao.getUnreadMailOfUser(login_id);
			if (list != null && list.size() > 0) {
				Iterator iter = list.iterator();
				while (iter.hasNext()) {
					myMailsModel = (MyMailsModel) iter.next();
					mailForm=new SVerticalLayout();
					mailForm.setId(""+myMailsModel.getId());
					mailForm.addComponent(new SLabel(myMailsModel.getSubject(),myMailsModel.getEmails()));
					mailForm.addComponent(new SLabel(myMailsModel.getDetails()));
					mailForm.setStyleName("layout_bordered_mail");
					mailForm.addLayoutClickListener(mailLayoutClickListener);
					
					mailLay.addComponent(mailForm);
				}
				msgsNotifBtn.setCaption("" + list.size());
			}else
				msgsNotifBtn.setCaption("0");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	@SuppressWarnings("rawtypes")
	public void createTaskNotification() {
		try {
			TasksModel myMailsModel=null;
			SVerticalLayout mailForm;
			SHorizontalLayout closeLayout=new SHorizontalLayout();
			closeLayout.addComponent(new SHTMLLabel(null,"<h2><u style='margin-left: 40px;'>"+getPropertyName("alert"),220));
			closeLayout.addComponent(closeMailBtn);
			mailLay.removeAllComponents();
			mailLay.addComponent(closeLayout);
			List mailList=new ArrayList();
			List pdcList=new ArrayList();
			pdcList=new PDCPaymentDao().getPdcCount(CommonUtil.getSQLDateFromUtilDate((Date)session.getAttribute("working_date")), office_id);
			mailList=new TasksDao().getAllNewTasksOfUser(login_id);
			List poExpiryList=new ArrayList();
			poExpiryList=new PurchaseOrderDao().getPurchaseOrderExpiry(CommonUtil.getSQLDateFromUtilDate((Date)session.getAttribute("working_date")),
																		CommonUtil.getSQLDateFromUtilDate(getAfterDate(5)),
																		office_id);
			int size=0;
			if (mailList != null && mailList.size() > 0) {
				Iterator iter = mailList.iterator();
				size+=mailList.size();
				while (iter.hasNext()) {
					myMailsModel = (TasksModel) iter.next();
					mailForm=new SVerticalLayout();
					mailForm.setId(""+myMailsModel.getId());
					mailForm.addComponent(new SLabel(myMailsModel.getTitle()));
					mailForm.setStyleName("layout_bordered_mail");
					mailForm.addLayoutClickListener(mailLayoutClickListener);
					mailLay.addComponent(mailForm);
				}
			}
			if(pdcList.size()>0){
				size+=1;
				mailForm=new SVerticalLayout();
//				mailForm.setId(""+myMailsModel.getId());
				mailForm.addComponent(new SHTMLLabel(null,"<h3><u style='margin-left: 40px;'>PDC Payment Pending",200));
				mailForm.setStyleName("layout_bordered_mail");
				mailForm.addLayoutClickListener(pdcLayoutClickListener);
				mailLay.addComponent(mailForm);
			}
			if(settings.isPURCHSE_ORDER_EXPIRY_ENABLED()){
				if(poExpiryList.size()>0){
					size+=1;
					mailForm=new SVerticalLayout();
//					mailForm.setId(""+myMailsModel.getId());
					mailForm.addComponent(new SHTMLLabel(null,"<h3><u style='margin-left: 40px;'>Purchase Order Expiry",200));
					mailForm.setStyleName("layout_bordered_mail");
					mailForm.addLayoutClickListener(poExpiryLayoutClickListener);
					mailLay.addComponent(mailForm);
				}
			}
			alertsNotifBtn.setCaption(""+size);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void createIssueNotification() {
		try {
			ReportIssueModel myMailsModel=null;
			S_OptionModel optionModel;
			UserModel user=null;
			SHorizontalLayout reporLay;
			SVerticalLayout mailForm;
			SHorizontalLayout closeLayout=new SHorizontalLayout();
			closeLayout.addComponent(new SHTMLLabel(
					null,
					"<h2><u style='margin-left: 40px;'>"+getPropertyName("reported_issues"),220));
			closeLayout.addComponent(closeMailBtn);
			mailLay.removeAllComponents();
			mailLay.addComponent(closeLayout);
			List list=dbopDao.getReportedIssues(login_id);;
			if (list != null && list.size() > 0) {
				Iterator iter = list.iterator();
				while (iter.hasNext()) {
					reporLay=new SHorizontalLayout();
					myMailsModel = (ReportIssueModel) iter.next();
					mailForm=new SVerticalLayout();
					mailForm.setId(""+myMailsModel.getId());
					optionModel=dbopDao.getOptionModel(myMailsModel.getOption());
					user=userDao.getUserFromLogin(myMailsModel.getLogin());
					
					closeIssueBtn = new SNativeButton("X");
					closeIssueBtn.addClickListener(closeIssueListener);
					closeIssueBtn.setId(myMailsModel.getId()+"");
					closeIssueBtn.setStyleName("close_btn_small");
					
					mailForm.addComponent(new SHTMLLabel(null,optionModel.getOption_name()+"."
							+myMailsModel.getIssue()+" - "+user.getFirst_name()+" "+user.getLast_name(),200));
					mailForm.setStyleName("layout_bordered_mail");
					mailForm.addLayoutClickListener(mailLayoutClickListener);
					
					reporLay.addComponent(mailForm);
					reporLay.addComponent(closeIssueBtn);
					
					mailLay.addComponent(reporLay);
				}
				notificationsBtn.setCaption("" + list.size());
			}else
				notificationsBtn.setCaption("0");
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private void resetChartDates() {
		java.util.Calendar cal = java.util.Calendar.getInstance();
		cal.setTime(new Date(((java.sql.Date) session.getAttribute("working_date")).getTime()));
		cal.set(cal.DAY_OF_MONTH, 1);
		chartFromDate.setValue(cal.getTime());
		cal.set(cal.DAY_OF_MONTH,cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH));
		chartToDate.setValue(cal.getTime());
		chartOptionComboField.setImmediate(true);
		chartFromDate.setImmediate(true);
		chartToDate.setImmediate(true);
		
	}

	
	protected void createUpdates(long moduleId) {
		
		analyticsLay.removeAllComponents();
		analyticsLay.addComponent(analyticsCustomLay);
		
		SVerticalLayout updatesMainLay=new SVerticalLayout();
		
		SVerticalLayout updatesContent=new SVerticalLayout();
		updatesContent.setWidth("280px");
		updatesContent.setHeight("90%");
		SHTMLLabel label=null;
		try {
			ActivityLogModel log=null;
			List list=dbopDao.getUpdatesOfUser(login_id);
			Iterator  iter=list.iterator();
			while (iter.hasNext()) {
				log= (ActivityLogModel) iter.next();
				label=new SHTMLLabel("* "+ dbopDao.getUserNameFromId(log.getLogin()),"<b>"+log.getLog()+"</b>");
				updatesContent.addComponent(label);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		SCustomLayout updatesLay = new SCustomLayout("updates_panel");
		updatesLay.addComponent(updatesContent,"updates");
		refreshButton=new SButton();
		refreshButton.setPrimaryStyleName("refresh_btn");
		refreshButton.setId("updates");
		refreshButton.addClickListener(refreshListener);
		updatesLay.addComponent(refreshButton,"refresh_btn");
		updatesMainLay.addComponent(updatesLay);
		
		SCustomLayout  feedsLay = new SCustomLayout("feeds_panel");
		feedsLay.addComponent(new SFormLayout(),"feeds");
		refreshButton=new SButton();
		refreshButton.setPrimaryStyleName("refresh_btn");
		refreshButton.setId("feeds");
		refreshButton.addClickListener(refreshListener);
		feedsLay.addComponent(refreshButton,"refresh_btn");
		updatesMainLay.addComponent(feedsLay);
		
		analyticsLay.addComponent(updatesMainLay);
		
	}

	
	protected void createAnalytics(long moduleId) {

		try {
			List optList = dbopDao.getOptionsWithAnalytics(moduleId, login_id);
			SCollectionContainer container = SCollectionContainer.setList(optList, "option_id");
			chartOptionComboField.setContainerDataSource(container);
			chartOptionComboField.setItemCaptionPropertyId("option_name");
			chartOptionComboField.setInputPrompt(getPropertyName("select"));
			if (optList != null && optList.size() > 0)
				chartOptionComboField.setValue(((S_OptionModel) optList.iterator().next()).getOption_id());
			else
				chartOptionComboField.setValue(null);

			analyticsCustomLay.removeAllComponents();
			SHorizontalLayout chartLay = new SHorizontalLayout();
			chartLay.setMargin(true);
			chartLay.setSpacing(true);
			chartLay.addComponent(new SLabel(getPropertyName("option")));
			chartLay.addComponent(chartOptionComboField);
			chartLay.addComponent(new SLabel(getPropertyName("from")));
			chartLay.addComponent(chartFromDate);
			chartLay.addComponent(new SLabel(getPropertyName("to")));
			chartLay.addComponent(chartToDate);
			analyticsCustomLay.addComponent(chartLay, "analytics_combo");

			refreshButton = new SButton();
			refreshButton.setPrimaryStyleName("refresh_btn");
			refreshButton.setId("chart");
			refreshButton.addClickListener(refreshListener);
			analyticsCustomLay.addComponent(refreshButton, "refresh_btn");

			analyticsLay.addComponent(analyticsCustomLay);

			refreshButton.click();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	private void createOptions(long modluleId) {
		
		SHorizontalLayout menu=null;
		SHTMLLabel label=null;
		
		S_OptionModel optionModel;
		S_OptionGroupModel optionGrpModel;
		SGridLayout optionsGrid;
		SCustomLayout optionsLay;
		
		try {
			
			List groupList=dbopDao.getOptionGroupsUnderModuleAssignedToUser(modluleId,login_id,(Long) officeSelect.getValue());
			Iterator iter=groupList.iterator();
			while (iter.hasNext()) {
				
				optionsGrid=new SGridLayout();
				optionsGrid.setSizeFull();
				optionsGrid.setColumns(7);
//				optionsGrid.setHeight("250px");
				optionsGrid.setStyleName("options_scroll");
				
				optionsLay = new SCustomLayout("options_panel");
				
				optionGrpModel=(S_OptionGroupModel) iter.next();
				optionsLay.setId(optionGrpModel.getId()+"");
				
				List optionList = dbopDao.getOptionsUnderGroupAssignedToUser(optionGrpModel.getId(),login_id, (Long) officeSelect.getValue());
				Iterator it=optionList.iterator();
				while (it.hasNext()) {
					optionModel = (S_OptionModel) it.next();
					
					S_LanguageMappingModel optmdl=new LanguageMappingDao()
														.getLanguageMappingModel((long)3, 
																Long.parseLong(session.getAttribute("language_id")+""), 
																				optionModel.getOption_id());
					
					menu = new SHorizontalLayout();
					if(optmdl!=null)
						label = new SHTMLLabel(null,"<center><b class='label_option_name'>"+optmdl.getName()+"</b><center>");
					else
						label = new SHTMLLabel(null,"<center><b class='label_option_name'>"+optionModel.getOption_name()+"</b><center>");
					label.setWidth("100%");
					menu.setId(""+optionModel.getOption_id());
					menu.addComponent(label);
					menu.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
					menu.setStyleName("button outline-inward");
					menu.setWidth("90%");
					menu.setHeight("40px");
					optionsGrid.addComponent(menu);
					optionsGrid.setComponentAlignment(menu, Alignment.TOP_CENTER);
				}
				
				S_LanguageMappingModel mapmdl=new LanguageMappingDao()
														.getLanguageMappingModel((long)2, 
																Long.parseLong(session.getAttribute("language_id")+""), 
																				optionGrpModel.getId());
				
				optionsGrid.addLayoutClickListener(clickListener);
				if(mapmdl!=null)
					optionsLay.addComponent(new SHTMLLabel(null, "<b class='group_label_name'>"+ mapmdl.getName()+ "</b>"), "option_grp_head");
				else
					optionsLay.addComponent(new SHTMLLabel(null, "<b class='group_label_name'>"+ optionGrpModel.getOption_group_name()+ "</b>"), "option_grp_head");
				
				
				refreshButton=new SButton();
				refreshButton.setPrimaryStyleName("refresh_btn");
				refreshButton.setId(optionGrpModel.getId()+"");
				refreshButton.addClickListener(refreshListener);
				
				settingsMenu=new MenuBar();
				settingsMenuItem = settingsMenu.addItem("", null);
				settingsMenuItem.setDescription(optionGrpModel.getId()+"");
				settingsMenuItem.setStyleName("settings_menu_style");
				createSettingMenu();
				
				optionsLay.addComponent(optionsGrid, "options_grid");
				optionsLay.addComponent(refreshButton, "refresh_btn");
				optionsLay.addComponent(settingsMenu, "settings");
				optionsVertLay.addComponent(optionsLay);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private void createSearchButtons(String text) {
		
		SHorizontalLayout menu=null;
		SHTMLLabel label=null;
		
		S_OptionModel optionModel;
		SGridLayout optionsGrid;
		SCustomLayout optionsLay;
		
		optionsVertLay.removeAllComponents();
		
		try {
			
//			List groupList=dbopDao.getOptionsLikeString(text,login_id);
//			Iterator iter=groupList.iterator();
//			while (iter.hasNext()) {
				
				optionsGrid=new SGridLayout();
				optionsGrid.setSizeFull();
				optionsGrid.setColumns(7);
//				optionsGrid.setHeight("170px");
				optionsGrid.setStyleName("options_scroll");
				
				optionsLay = new SCustomLayout("options_panel");
				
//				optionGrpModel=(S_OptionGroupModel) iter.next();
//				optionsLay.setId(optionGrpModel.getId()+"");
				
				List optionList = dbopDao.getOptionsLikeString(text,login_id,(Long) officeSelect.getValue());
				Iterator it=optionList.iterator();
				while (it.hasNext()) {
					optionModel = (S_OptionModel) it.next();
					
					S_LanguageMappingModel optmdl=new LanguageMappingDao()
																.getLanguageMappingModel((long)3, 
																		Long.parseLong(session.getAttribute("language_id")+""), 
																						optionModel.getOption_id());
					
					menu = new SHorizontalLayout();
					if(optmdl!=null)
						label = new SHTMLLabel(null,"<center><b class='label_option_name'>"+optmdl.getName()+"</b><center>");
					else
						label = new SHTMLLabel(null,"<center><b class='label_option_name'>"+optionModel.getOption_name()+"</b><center>");
					label.setWidth("100%");
					menu.setId(""+optionModel.getOption_id());
					menu.addComponent(label);
					menu.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
					menu.setStyleName("button outline-inward");
					menu.setWidth("90%");
					menu.setHeight("40px");
					optionsGrid.addComponent(menu);
					optionsGrid.setComponentAlignment(menu, Alignment.TOP_CENTER);
				}
				
				optionsGrid.addLayoutClickListener(clickListener);
				optionsLay.addComponent(
						new SHTMLLabel(null, "<b class='group_label_name'>"+getPropertyName("options_result")+"</b>"), "option_grp_head");
				
				
				refreshButton=new SButton();
				refreshButton.setPrimaryStyleName("refresh_btn");
				refreshButton.setId("0");
//				refreshButton.addClickListener(refreshListener);
				
//				settingsMenu=new MenuBar();
//				settingsMenuItem = settingsMenu.addItem("", null);
//				settingsMenuItem.setDescription(optionGrpModel.getId()+"");
//				settingsMenuItem.setStyleName("settings_menu_style");
//				createSettingMenu();
				
				optionsLay.addComponent(optionsGrid, "options_grid");
//				optionsLay.addComponent(refreshButton, "refresh_btn");
//				optionsLay.addComponent(settingsMenu, "settings");
				
				optionsLay.setStyleName("search_option_lay");
				optionsVertLay.addComponent(optionsLay);
//			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	private GridLayout createModules() {
		
		GridLayout menuLayout=new GridLayout();
		menuLayout.setImmediate(true);
		menuLayout.setWidth("100%");
		menuLayout.setStyleName("menu_style_new");
		menuLayout.addLayoutClickListener(menuClickListener);
		
		SHorizontalLayout menu = null;
		SHTMLLabel label = null;
		
		try {
			
			if(officeSelect.getValue()!=null) {
			
			List moduleList = moduleDao.getAllAssignedModules(login_id, (Long) officeSelect.getValue());
			
			Iterator iter=moduleList.iterator();
			S_ModuleModel modlue;
			
			while (iter.hasNext()) {
				modlue = (S_ModuleModel) iter.next();
				S_LanguageMappingModel mdl=new LanguageMappingDao()
													.getLanguageMappingModel((long)1, 
															Long.parseLong(session.getAttribute("language_id")+""), 
																				modlue.getId());

				menu = new SHorizontalLayout();
				if(mdl!=null)
					label = new SHTMLLabel(null, "<b class='label_name'>"+mdl.getName()+"</b>");
				else
					label = new SHTMLLabel(null, "<b class='label_name'>"+modlue.getModule_name()+"</b>");
				menu.setId(""+modlue.getId());
				menu.addComponent(label);
				menu.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
				menu.setStyleName("menu_style");
				menu.setWidth("100%");
				menu.setHeight("40px");
				menuLayout.addComponent(menu);

			}
			
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return menuLayout;
	}
	
	
	public void createQuickMenu() {

		SGridLayout menuLayout = new SGridLayout();
		menuLayout.setImmediate(true);
		menuLayout.setWidth("200");
		menuLayout.setStyleName("menu_style_new");
		menuLayout.addLayoutClickListener(menuClickListener);

		SHorizontalLayout menu = null;
		SHTMLLabel label = null;
		quickItem.removeChildren();
		
		try {
			
			quickItem.addItem("Add Quick Menu", new Command() {
				@Override
				public void menuSelected(MenuItem selectedItem) {
					try {
						openOption(new QuickMenuOptionMapUI());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			List quickList = new QuickMenuDao().getQuickMenuOfUser(login_id,(Long) officeSelect.getValue());
			
			if(quickList!=null&&quickList.size()>0){

			menuLayout.setColumns(1);
			menuLayout.setRows(quickList.size() + 2);
			
			Iterator iter = quickList.iterator();

			S_OptionModel optionModel;
			while (iter.hasNext()) {
				optionModel = (S_OptionModel) iter.next();
				
				S_LanguageMappingModel mdl=new LanguageMappingDao()
												.getLanguageMappingModel((long)3, 
														Long.parseLong(session.getAttribute("language_id")+""), 
																			optionModel.getOption_id());
				
				MenuItem itm=quickItem.addItem(optionModel.getOption_name(), new Command() {
					@Override
					public void menuSelected(MenuItem selectedItem) {
						openOption(Long.parseLong(selectedItem.getDescription()));
					}
				});
				itm.setDescription(""+optionModel.getOption_id());
				itm.setStyleName("menu_tooltip");

				menu = new SHorizontalLayout();
				if(mdl!=null)
					label = new SHTMLLabel(null, "<b style='font-size:11px;'>"+ mdl.getName() + "</b>");
				else
					label = new SHTMLLabel(null, "<b style='font-size:11px;'>"+ optionModel.getOption_name() + "</b>");
				menu.setId("" + optionModel.getOption_id());
				menu.addComponent(label);
				menu.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
				menu.setStyleName("menu_style");
				menu.setWidth("100%");
				menu.setHeight("30px");
				menuLayout.addComponent(menu);
				

			}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private void createAddMenu() {


		try {

			List quickList = dbopDao.getCreateMenuOfUser(login_id, (Long) officeSelect.getValue());
			
			if(quickList!=null&&quickList.size()>0){

			Iterator iter = quickList.iterator();

			S_OptionModel optionModel;
			
			while (iter.hasNext()) {
				optionModel = (S_OptionModel) iter.next();
				S_LanguageMappingModel optmdl=new LanguageMappingDao()
														.getLanguageMappingModel((long)3, 
																Long.parseLong(session.getAttribute("language_id")+""),  
																				optionModel.getOption_id());
				String name="";
				if(optmdl!=null)
					name=optmdl.getName();
				else	
					name=optionModel.getOption_name();
				
				MenuItem itm=addItem.addItem(name, new Command() {
					@Override
					public void menuSelected(MenuItem selectedItem) {
						openOption(Long.parseLong(selectedItem.getDescription()));
					}
				});
				itm.setDescription(""+optionModel.getOption_id());

			}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void createRecentMenu() {

		try {

			List quickList = dbopDao.getRecentMenuOfUser(login_id,(Long) officeSelect.getValue());
			
			recentItem.removeChildren();
			
			if(quickList!=null&&quickList.size()>0){

			Iterator iter = quickList.iterator();

			ActivityLogModel activityLogModel;
			
			
			
			while (iter.hasNext()) {
				activityLogModel = (ActivityLogModel) iter.next();
				
				MenuItem itm=recentItem.addItem(activityLogModel.getLog(), new Command() {
					@Override
					public void menuSelected(MenuItem selectedItem) {
						String[] ids=selectedItem.getDescription().split(":");
						SparkLogic sparkLogic=(SparkLogic) openOption(Long.parseLong(ids[0]));
						sparkLogic.getBillNoFiled().setValue(Long.parseLong(ids[1]));
//						S_ProjectOptionMapModel projMdl=null;
//						try {
//							projMdl = dbopDao.getProjectOptionMapping(Long.parseLong(session.getAttribute("project_type").toString()),Long.parseLong(selectedItem.getDescription()));
//						} catch (NumberFormatException e) {
//							e.printStackTrace();
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//						
//						if(projMdl!=null){
//							sparkLogic=(SparkLogic) openOption(projMdl.getClass_name(), projMdl.getOption().getOption_name());
//							sparkLogic.getBillNoFiled().setValue(activityLogModel.getBillId());
//						}
					}
				});
				itm.setDescription(activityLogModel.getOption()+":"+activityLogModel.getBillId());
				itm.setStyleName("menu_tooltip");

			}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	SessionActivityBean sesSavModel;
	
	
	public void createSavedSessionsMenu() {
		
		try {
			savedSessionItem.removeChildren();
			List savSesList = new CommonMethodsDao().getSavedSessionActivities(login_id, (Long) officeSelect.getValue());
			
			if(savSesList!=null&&savSesList.size()>0){
				
				Iterator iter = savSesList.iterator();
				
				while (iter.hasNext()) {
					sesSavModel = (SessionActivityBean) iter.next();
					if(sesSavModel.getDetails()==null)
						sesSavModel.setDetails("");
					
					MenuItem itm=savedSessionItem.addItem(sesSavModel.getDetails(), new Command() {
						@Override
						public void menuSelected(MenuItem selectedItem) {
							
							String[] ids=selectedItem.getDescription().split(":");
							
							try {
								new CommonMethodsDao().deleteSavedSessionActivities(login_id,Long.parseLong(ids[0]),Long.parseLong(ids[1]) );
								createSavedSessionsMenu();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							SparkLogic sparkLogic=(SparkLogic) openOption(Long.parseLong(ids[0]));
							sparkLogic.getBillNoFiled().setValue(Long.parseLong(ids[1]));
							
						}
					});
					itm.setDescription(sesSavModel.getOption().getOption_id()+":"+sesSavModel.getBillId());
					itm.setStyleName("menu_tooltip");
	
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public SWindow openOption(long option_id) {
		
		S_OptionModel opt;
		
		try {
			
			opt = dbopDao.getOptionForOpen(option_id, (Long)session.getAttribute("project_type"));

			if(!settings.isKEEP_OTHER_WINDOWS()) {
				if(getUI()!=null) 
				if(getUI().getWindows()!=null){
					Iterator it= getUI().getWindows().iterator();
					while (it.hasNext()) {
						try {
							getUI().removeWindow((SWindow) it.next());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}

			window = (SparkLogic) objSRefl.getClassInstance(opt.getClass_name());
			S_LanguageMappingModel optmdl=new LanguageMappingDao()
													.getLanguageMappingModel((long)3, 
															Long.parseLong(session.getAttribute("language_id")+""), 
																			opt.getOption_id());
			if(optmdl!=null)
				window.setCaption(optmdl.getName());
			else
				window.setCaption(opt.getOption_name());
			
			window.center();
			window.setCloseShortcut(KeyCode.X, ShortcutAction.ModifierKey.ALT);
			getUI().getCurrent().addWindow(window);
			session.setAttribute("option_id", opt.getOption_id());
			
			saveActivity(opt.getOption_id(), "Accesed Option : "+opt.getOption_name(), (Long)session.getAttribute("login_id"), 
					(Long)session.getAttribute("office_id"));
			
			window.addCloseListener(closeList);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return window;
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
	
	
	protected void openOption(SparkLogic widnow) {
		if(!settings.isKEEP_OTHER_WINDOWS()) {
			if(getUI()!=null) 
			if(getUI().getWindows()!=null){
				Iterator it= getUI().getWindows().iterator();
				while (it.hasNext()) {
					try {
						getUI().removeWindow((SWindow) it.next());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		
		widnow.center();
		widnow.setCloseShortcut(KeyCode.X, ShortcutAction.ModifierKey.ALT);
		getUI().getCurrent().addWindow(widnow);
		
		widnow.addCloseListener(closeList);
		
	}
	
//	public SWindow openOption(String className,String title) {
//		
//		try {
//
//			if(!settings.isKEEP_OTHER_WINDOWS()) {
//				if(getUI()!=null) 
//					Iterator it= getUI().getWindows().iterator();
//					while (it.hasNext()) {
//						try {
//							getUI().removeWindow((SWindow) it.next());
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//					}
//				}
//			}
//
//			window = (SparkLogic) objSRefl
//					.getClassInstance(className);
//
//			window.setCaption(title);
//
//			window.center();
//			window.setCloseShortcut(KeyCode.X, ShortcutAction.ModifierKey.ALT);
//			getUI().getCurrent().addWindow(window);
//			session.setAttribute("option_id",((SparkLogic) window).getOptionId());
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//
//	}	
	
	
	public void checkSettings() {
		
		
		try {
			
			if (settingsWindow != null)
				settingsWindow.close();
			
			
			if(session.getAttribute("settings_not_set")!=null) {
				
				settingsWindow = new AccountSettingsUI();
				settingsWindow.setClosable(true);
				settingsWindow.setCaption("Account Settings");
				settingsWindow.center();
				getUI().getCurrent().addWindow(settingsWindow);
				
				settingsWindow.setModal(true);
				
				
				Notification.show("Settings Not Set", "Account Settings is Mandatory for the System. You can't continue without set the settings.",
						Type.ERROR_MESSAGE);
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	
	private void createChart() {
		if (chartOptionComboField.getValue() != null
				&& !chartOptionComboField.getValue().equals("")) {

			S_OptionModel optionModel;
			try {
				optionModel = dbopDao.getOptionModel((Long) chartOptionComboField.getValue());
				if (optionModel != null) {
					SContainerLayout lay = (SContainerLayout) objSRefl
							.getClassInstance(optionModel
									.getAnalyticsClassName());
					if (lay != null) {
						lay.getChart(chartFromDate.getValue(),
								chartToDate.getValue());
						analyticsCustomLay.addComponent(lay, "chart");
					} else {

						String empImageFile = "Images/no_chart.png";
						ThemeResource mainRes = new ThemeResource(empImageFile);
						userImageBig = new SImage(null, mainRes);
						userImageBig.setHeight("430px");
						userImageBig.setWidth("500px");
						analyticsCustomLay.addComponent(userImageBig, "chart");
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			String empImageFile = "Images/no_chart.png";
			ThemeResource mainRes = new ThemeResource(empImageFile);
			userImageBig = new SImage(null, mainRes);
			userImageBig.setHeight("430px");
			userImageBig.setWidth("500px");
			analyticsCustomLay.addComponent(userImageBig, "chart");
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
	
	public java.util.Date getAfterDate(int days){
		java.util.Calendar cal=java.util.Calendar.getInstance();
		cal.add(Calendar.DATE, days);
		return (java.util.Date) cal.getTime();
	}
	
	
}
