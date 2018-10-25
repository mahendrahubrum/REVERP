package com.webspark.ui;

import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.config.settings.biz.SettingsBiz;
import com.inventory.dao.PrivilageSetupDao;
import com.vaadin.annotations.Title;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SButton;
import com.webspark.Components.SContainerPanel;
import com.webspark.Components.SCustomLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPasswordField;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.common.util.SConstants;
import com.webspark.common.util.SEncryption;
import com.webspark.common.util.SessionUtil;
import com.webspark.dao.LanguageDao;
import com.webspark.dao.LoginDao;
import com.webspark.model.S_LanguageModel;
import com.webspark.model.S_LoginModel;
import com.webspark.uac.model.UserModel;

/**
 * @Author Jinshad P.T.
 */

@Title("REVERP")
public class Login extends SContainerPanel {

WrappedSession session=new SessionUtil().getHttpSession();
	
	STextField userID;
	SPasswordField password;
	
	LoginDao lcdObj;
	SButton submit;
	
	SLabel errLabel;
	
	PrivilageSetupDao privDao;
	

	@SuppressWarnings("serial")
	public Login() {
		
	
	
		lcdObj=new LoginDao();
		
		
		privDao=new PrivilageSetupDao();
		
		lcdObj=new LoginDao();
		
		final SVerticalLayout layout = new SVerticalLayout();
		setContent(layout);
//		layout.setStyleName("marg_100");
	
		
	//	SWindow window=new SWindow();
		
		
		errLabel=new SLabel(null);
		userID=new STextField(null,250);
		password=new SPasswordField(null, 250);
		submit=new SButton("Login");
		submit.setPrimaryStyleName("login_submit_btn");
		userID.setPrimaryStyleName("login_password");
		password.setPrimaryStyleName("login_password");
		submit.setId("Test");
		errLabel.setVisible(false);
		
		userID.setInputPrompt("User name");
		password.setInputPrompt("Password");
		
		userID.focus();
		
	//	topPanel.addComponent(userID);
	//	topPanel.addComponent(password);
	//	topPanel.addComponent(submit);
	//	topPanel.setComponentAlignment(submit, Alignment.BOTTOM_CENTER);
		
	//	submit.setStyleName("marg_200");
		
		
		
		SCustomLayout custom=new SCustomLayout("LoginNew");
		
		
		 final STextField username = new STextField();
		 custom.addComponent(userID, "username");
	
	    custom.addComponent(password, "password");
	    
	    custom.addComponent(errLabel, "err");
	
	    custom.addComponent(submit, "okbutton");
	    custom.setHeight("100%");
		
	    
	    custom.addShortcutListener(new ShortcutListener("Shortcut Name", ShortcutAction.KeyCode.ENTER, null) {
	        @Override
	        public void handleAction(Object sender, Object target) {
	             submit.click();
	        }
	    });
    
    
    
	
	submit.addClickListener(new SButton.ClickListener(){
    	public void buttonClick(ClickEvent event){
    		
    		try {
    			errLabel.setValue("");
    			if(isValid()){
    				
//    				System.out.println(getSession().getService().get);
    				
    				S_LoginModel loginModel=lcdObj.getLoginFromLoginName(userID.getValue().toString());
    				
    				if(lcdObj.isExist(userID.getValue().toString()) && loginModel.getStatus()==0){
    					
    					if(!lcdObj.isProductExpire(loginModel.getOffice().getOrganization().getId())||loginModel.getUserType().getId()==1) {
						
							UserModel usr=lcdObj.getUserFromLoginId(loginModel.getId());
							if(SEncryption.encrypt(password.getValue().toString()).equals(loginModel.getPassword())){
								
								session.setAttribute("login_id", loginModel.getId());
								S_LanguageModel language=new LanguageDao().getLanguage(loginModel.getOffice().getLanguage());
								
								if(usr!=null)
									session.setAttribute("user_id", usr.getId());
								
								session.setAttribute("role_id", loginModel.getUserType().getId());
								
								session.setAttribute("login_name", loginModel.getLogin_name());
								
								if(language!=null){
									session.setAttribute("property_file", language.getProperty());
									session.setAttribute("language_id", language.getId());
								}
								else{
									session.setAttribute("property_file", "english");
									session.setAttribute("language_id", 0);
								}
								
	//							session.setAttribute("user_type", usr.getLoginId().getUserType());
								
								session.setAttribute("office_id", loginModel.getOffice().getId());
								session.setAttribute("office_name", loginModel.getOffice().getName());
								session.setAttribute("user_office_id", loginModel.getOffice().getId());
								session.setAttribute("user_organization_id", loginModel.getOffice().getOrganization().getId());
								
								session.setAttribute("currency_id",loginModel.getOffice().getCurrency().getId());
								
								session.setAttribute("organization_id",loginModel.getOffice().getOrganization().getId());
								session.setAttribute("organization_name",loginModel.getOffice().getOrganization().getName());
								
								session.setAttribute("project_type",loginModel.getOffice().getOrganization().getProject_type());
								
								session.setAttribute("country_id", loginModel.getOffice().getCountry().getId());
								
								session.setAttribute("no_of_precisions", loginModel.getOffice().getCurrency().getNo_of_precisions());
								
								session.setAttribute("time_zone", loginModel.getOffice().getTimezone());
								
								
								long id=loginModel.getId();
								
								if(loginModel.getUserType().getId()==SConstants.ROLE_SUPER_ADMIN){
									session.setAttribute("isSuperAdmin",true);
								}
								else{
									session.setAttribute("isSuperAdmin",false);
								}
								
								if(loginModel.getUserType().getId()==SConstants.ROLE_SYSTEM_ADMIN){
									session.setAttribute("isSystemAdmin",true);
								}
								else{
									session.setAttribute("isSystemAdmin",false);
								}
								
								if(loginModel.getUserType().getId()==SConstants.ROLE_CUSTOMER){
									session.setAttribute("isCustomer",true);
								}
								else{
									session.setAttribute("isCustomer",false);
								}
								
								if(loginModel.getUserType().getId()==SConstants.ROLE_SUPPLIER){
									session.setAttribute("isSupplier",true);
								}
								else{
									session.setAttribute("isSupplier",false);
								}
	
								
								if(loginModel.getOffice().getAdmin_user_id()==id) {
									session.setAttribute("isOfficeAdmin",true);
								}
								else{
									session.setAttribute("isOfficeAdmin",false);
								}
								
								if(loginModel.getOffice().getOrganization().getAdmin_user_id()==id) {
									session.setAttribute("isOrganizationAdmin",true);
								}
								else{
									session.setAttribute("isOrganizationAdmin",false);
								}
								
								if(usr!=null) {
									if(usr.getDepartment().getAdmin_user_id()==id) {
										session.setAttribute("isDepartmentAdmin",true);
									}
									else{
										session.setAttribute("isDepartmentAdmin",false);
									}
								}
								
								
								boolean ediSaleavail=true;
								boolean taskAvail=true;
								boolean quotAvail=true;
								boolean printSaleAvail=true;
								if(loginModel.getUserType().getId()!=SConstants.ROLE_SUPER_ADMIN && 
										loginModel.getUserType().getId()!=SConstants.ROLE_SYSTEM_ADMIN){
									ediSaleavail=privDao.isOptionsAvailToUser(loginModel.getOffice().getId(), SConstants.privilegeTypes.EDIT_SALES, loginModel.getId());
									taskAvail=privDao.isOptionsAvailToUser(loginModel.getOffice().getId(), SConstants.privilegeTypes.ADD_TASK, loginModel.getId());
									quotAvail=privDao.isOptionsAvailToUser(loginModel.getOffice().getId(), SConstants.privilegeTypes.DAILY_QUOTATION_REPORT, loginModel.getId());
									printSaleAvail=privDao.isOptionsAvailToUser(loginModel.getOffice().getId(), SConstants.privilegeTypes.PRINT_SALE, loginModel.getId());
								}
								else{
									ediSaleavail=true;
									taskAvail=true;
									quotAvail=true;
									printSaleAvail=true;
								
								}
								session.setAttribute("sale_editable", ediSaleavail);
								session.setAttribute("task_add_enabled", taskAvail);
								session.setAttribute("daily_quotation_office_change_allowed", quotAvail);
								session.setAttribute("sale_printable", printSaleAvail);
								
								try {
									session.setAttribute("login_history_id", lcdObj.doRecordUserLogin(loginModel.getId()));
								} catch (Exception e) {
									e.printStackTrace();
								}
								
								// The Settings Session Variables are adding inside this method
								SettingsValuePojo settings=new SettingsBiz().updateSettingsValue(loginModel.getOffice().getOrganization().getId(),
										loginModel.getOffice().getId());
								
								if(settings.getDEFAULT_DATE_SELECTION()==SConstants.SYSTEM_DATE)
									session.setAttribute("working_date", getFormattedTime(new Date()));
								else
									session.setAttribute("working_date", loginModel.getOffice().getWorkingDate());
								
								if(settings.getEXPENDETURE_SHOW_ACCOUNTS()==1) 
									session.setAttribute("expendeture_acct_cash_only",true);
								else
									session.setAttribute("expendeture_acct_cash_only",false);
								
								
								StringBuilder decFormat=new StringBuilder("#.");
								for(int i=0;i<loginModel.getOffice().getCurrency().getNo_of_precisions();i++){
									decFormat.append("#");
								}
								session.setAttribute("no_of_precisions_hash", decFormat.toString());
								
								
								if(settings.getTHEME()==2) {
									
//									NewMainLayout mainLay=new NewMainLayout();
//									DocLayout doc=new DocLayout(mainLay.getDocListener());
//									doc.addComponent(mainLay, "left: 0px; top: 0px; z-index:1; right: 0;");
//									doc.addClickListener(mainLay.getDocListener());
									getUI().getUI().setContent(new NewMainLayout());
								}
								else
									getUI().getUI().setContent(new MainLayout());
								
//								try {
//									deleteOldFiles(new File(VaadinServlet.getCurrent().getServletContext()
//											.getRealPath("Reports/")), 2);
//								} catch (Exception e) {
//								}
								
							}
							else{
								errLabel.setVisible(true);
								errLabel.setValue("Wrong password..! ");
								password.focus();
							}
						
						
    				}
					else {
						SNotification.show("ERROR 101-Unable to connect to database", "Please contact Sparknova support desk. Phone : 0471-4010020", Type.ERROR_MESSAGE);
					}
    				}
					else{
						errLabel.setVisible(true);
						errLabel.setValue("This user id doesn't exist..! ");
						userID.focus();
					}
					
    			}
					
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    	}
    });
	
	setContent(custom);
}
	
//	public void deleteOldFiles(File file,int olderThanDate) {
//		
//		Calendar cal=Calendar.getInstance(TimeZone.getTimeZone(session.getAttribute("time_zone").toString()));
//		cal.add(cal.DAY_OF_MONTH, -olderThanDate);
//		
//	    Iterator<File> filesToDelete = org.apache.commons.io.FileUtils.iterateFiles(file, new AgeFileFilter(cal.getTime()), null);
//	    
//	    while (filesToDelete.hasNext()) {
//			File deleteFile = (File) filesToDelete.next();
//			deleteFile.delete();
//	    }
//	}

	public java.sql.Date getFormattedTime(java.util.Date date){
		DateFormat format = DateFormat.getDateInstance();
		if (getHttpSession().getAttribute("time_zone") != null) 
			format.setTimeZone(TimeZone.getTimeZone(getHttpSession().getAttribute("time_zone").toString()));
		System.out.println(format.format(date));
		return new java.sql.Date(new java.util.Date(format.format(date)).getTime());
	}
public Boolean isValid() {
	// TODO Auto-generated method stub
	boolean ret=true;
	if(userID.getValue()==null || userID.getValue().equals("")){
		errLabel.setValue("Enter User ID..! ");
		errLabel.setVisible(true);
		userID.focus();
		ret=false;
		return ret;
	}
	else {
		errLabel.setValue("");
		errLabel.setVisible(false);
	}
	
	if(password.getValue()==null || password.getValue().equals("")){
		errLabel.setValue("Enter Password..! ");
		errLabel.setVisible(true);
		password.focus();
		ret=false;
	}
	else {
		errLabel.setValue("");
		errLabel.setVisible(false);
	}
	return ret;
}

public Boolean getHelp() {
	// TODO Auto-generated method stub
	return null;
}
	
}
