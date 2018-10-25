package com.webspark.uac.ui;

import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.webspark.Components.SContainerPanel;
import com.webspark.Components.SPanel;
import com.webspark.Components.STabSheet;
import com.webspark.Components.SparkLogic;

public class UserManagementUI extends SparkLogic {

	private static final long serialVersionUID = 6694186579762258544L;

	long id;
	SPanel panel;
	SContainerPanel userPanel;
	SContainerPanel userDocumentPanel;
	SContainerPanel userQualificationPanel;
	SContainerPanel userContactPanel;
	SContainerPanel userAddressPanel;
	SContainerPanel previousEmployerPanel;
	STabSheet tabSheet;

	@SuppressWarnings({ "serial" })
	@Override
	public SPanel getGUI() {

		setSize(1100, 650);
		panel = new SPanel();
		panel.setSizeFull();
		tabSheet = new STabSheet("");
		
		try {
			userPanel = new SContainerPanel();
			userPanel.setId("User Management");
			userDocumentPanel = new SContainerPanel();
			userDocumentPanel.setId("Document Management");
			userQualificationPanel= new SContainerPanel();
			userQualificationPanel.setId("Qualification");
			userContactPanel= new SContainerPanel();
			userContactPanel.setId("Contact");
			userAddressPanel= new SContainerPanel();
			userAddressPanel.setId("Address");
			previousEmployerPanel= new SContainerPanel();
			previousEmployerPanel.setId("Previous Employer");
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		try {
			
			tabSheet.addSelectedTabChangeListener(new TabSheet.SelectedTabChangeListener() {
				@Override
				public void selectedTabChange(SelectedTabChangeEvent event) {
					center();

					if (tabSheet.getSelectedTab().getId().equals("User Management")) {
						UserManagementPanel documentPanel=new UserManagementPanel();
						userPanel.setContent(documentPanel);
						setWidth((userPanel.getContent().getWidth()+30)+"");
						setHeight((userPanel.getContent().getHeight()+95)+"");

					} else if (tabSheet.getSelectedTab().getId().equals("Document Management")) {
						UserDocumentUploadPanel documentPanel=new UserDocumentUploadPanel();
						userDocumentPanel.setContent(documentPanel);
						setWidth((userDocumentPanel.getContent().getWidth()+30)+"");
						setHeight((userDocumentPanel.getContent().getHeight()+95)+"");
					}
					else if (tabSheet.getSelectedTab().getId().equals("Qualification")) {
						UserQualificationPanel documentPanel=new UserQualificationPanel();
						userQualificationPanel.setContent(documentPanel);
						setWidth((userQualificationPanel.getContent().getWidth()+30)+"");
						setHeight((userQualificationPanel.getContent().getHeight()+95)+"");
					}
					else if (tabSheet.getSelectedTab().getId().equals("Contact")) {
						UserContactPanel documentPanel=new UserContactPanel();
						userContactPanel.setContent(documentPanel);
						setWidth((userContactPanel.getContent().getWidth()+30)+"");
						setHeight((userContactPanel.getContent().getHeight()+95)+"");
					}
					else if (tabSheet.getSelectedTab().getId().equals("Address")) {
						UserAddressPanel documentPanel=new UserAddressPanel();
						userAddressPanel.setContent(documentPanel);
						setWidth((userAddressPanel.getContent().getWidth()+30)+"");
						setHeight((userAddressPanel.getContent().getHeight()+95)+"");
					}
					else if (tabSheet.getSelectedTab().getId().equals("Previous Employer")) {
						UserPreviousEmployerPanel documentPanel=new UserPreviousEmployerPanel();
						previousEmployerPanel.setContent(documentPanel);
						setWidth((previousEmployerPanel.getContent().getWidth()+30)+"");
						setHeight((previousEmployerPanel.getContent().getHeight()+95)+"");
					}
				}
			});
			tabSheet.addTab(userPanel, "User Management");
			tabSheet.addTab(userDocumentPanel, "Document Management");
			tabSheet.addTab(userQualificationPanel, "Qualification");
			tabSheet.addTab(userContactPanel, "Contact");
			tabSheet.addTab(userAddressPanel, "Address");
			tabSheet.addTab(previousEmployerPanel, "Previous Employer");
			tabSheet.setSelectedTab(0);
			
			panel.setContent(tabSheet);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return panel;
	}

	@Override
	public Boolean isValid() {
		return null;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

	public SContainerPanel getUserPanel() {
		return userPanel;
	}

	public void setUserPanel(SContainerPanel userPanel) {
		this.userPanel = userPanel;
	}

}
