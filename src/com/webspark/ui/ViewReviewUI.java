package com.webspark.ui;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import com.vaadin.annotations.Theme;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SLink;
import com.webspark.Components.SPanel;
import com.webspark.Components.STable;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.dao.ViewReviewDao;
import com.webspark.model.ReviewModel;
import com.webspark.uac.dao.UserManagementDao;

/**
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Nov 10, 2014
 */
@Theme("testappstheme")
public class ViewReviewUI extends SparkLogic {

	private static final long serialVersionUID = -3120514149988755876L;
	
	private static final String TBL_SN = "#";
	private static final String TBL_ID = "Id";
	private static final String TBL_DATE = "Date";
	private static final String TBL_TITLE = "Title";
	private static final String TBL_DETAILS = "Details";
	private static final String TBL_FILE = "File";

	SCollectionContainer bic;

	SVerticalLayout content;

	SComboField userComboField;
	SDateField fromDateField;
	SDateField toDateField;

	STable statusesTable;

	final SButton load = new SButton(getPropertyName("load"));
	final SButton delete = new SButton(getPropertyName("delete"));
	
	String[] allHeaders;
	String[] reqHeaders;

	ViewReviewDao dao = new ViewReviewDao();
	
	String rootPath ;
	
	SLabel dateLabel;
	SLabel titleLabel;
	SLabel detailsLabel;

	public ViewReviewUI() throws Exception {
		
		rootPath = VaadinServlet.getCurrent().getServletContext()
				.getRealPath("/")+"VAADIN/themes/testappstheme/Reviews/";

		setCaption(getPropertyName("status"));
		setWidth("950px");
		setHeight("580px");
		
		allHeaders = new String[] {TBL_SN,TBL_ID,TBL_DATE,TBL_TITLE,TBL_DETAILS,TBL_FILE};
		reqHeaders = new String[] {TBL_SN,TBL_DATE,TBL_TITLE,TBL_DETAILS,TBL_FILE};

		content = new SVerticalLayout();
		
		fromDateField=new SDateField(null,100,getDateFormat(),getMonthStartDate());
		toDateField=new SDateField(null,100,getDateFormat(),getWorkingDate());

		statusesTable = new STable(null);

		statusesTable.addContainerProperty(TBL_SN, Integer.class, null, "#", null,
				Align.CENTER);
		statusesTable.addContainerProperty(TBL_ID, Long.class, null, TBL_ID, null,
				Align.CENTER);
		statusesTable.addContainerProperty(TBL_DATE, String.class, null, getPropertyName("date"), null,
				Align.LEFT);
		statusesTable.addContainerProperty(TBL_TITLE, String.class, null, getPropertyName("title"), null,
				Align.LEFT);
		statusesTable.addContainerProperty(TBL_DETAILS, String.class, null, getPropertyName("details"), null,
				Align.LEFT);
		statusesTable.addContainerProperty(TBL_FILE, SLink.class, null, getPropertyName("file"), null,
				Align.LEFT);
		
		statusesTable.setSizeFull();
		statusesTable.setSelectable(true);
		statusesTable.setWidth("780");
		statusesTable.setHeight("350");
		statusesTable.setVisibleColumns(reqHeaders);
		
		statusesTable.setColumnExpandRatio(TBL_TITLE, 2);
		statusesTable.setColumnExpandRatio(TBL_DETAILS, 4);
		statusesTable.setColumnExpandRatio(TBL_FILE, 3);

		userComboField=new SComboField(null,200,new UserManagementDao().getAllUsersUnderOffice(getOfficeID()),"id","login_name");
		userComboField.setValue(getLoginID());

		SHorizontalLayout dateLay=new SHorizontalLayout();
		dateLay.setSpacing(true);
		dateLay.addComponent(new SLabel(getPropertyName("user")));
		dateLay.addComponent(userComboField);
		dateLay.addComponent(new SLabel(getPropertyName("from_date")));
		dateLay.addComponent(fromDateField);
		dateLay.addComponent(new SLabel(getPropertyName("to_date")));
		dateLay.addComponent(toDateField);
		dateLay.addComponent(load);
		
		dateLabel=new SLabel(null,100);
		titleLabel=new SLabel(null,200);
		detailsLabel=new SLabel(null,400);
		
		SHorizontalLayout detailsLay=new SHorizontalLayout();
		detailsLay.setSpacing(true);
		
		detailsLay.addComponent(dateLabel);
		detailsLay.addComponent(titleLabel);
		detailsLay.addComponent(detailsLabel);
		detailsLay.addComponent(delete);

		content.setMargin(true);
		content.setSpacing(true);
		content.setSizeFull();
		content.addComponent(dateLay);
		content.addComponent(statusesTable);
		content.addComponent(detailsLay);

		content.setSizeUndefined();
		delete.setEnabled(false);
		
		setContent(content);
		
		load.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				loadOptions();				
			}
		});

		statusesTable.addValueChangeListener(new Property.ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				try {

					if (statusesTable.getValue() != null) {
						
						Item item=statusesTable.getItem(statusesTable.getValue());
						dateLabel.setValue(item.getItemProperty(TBL_DATE).getValue().toString());
						titleLabel.setValue(item.getItemProperty(TBL_TITLE).getValue().toString());
						detailsLabel.setValue(item.getItemProperty(TBL_DETAILS).getValue().toString());
						
						delete.setEnabled(true);
					} else {
						
						dateLabel.setValue("");
						titleLabel.setValue("");
						detailsLabel.setValue("");
						
						delete.setEnabled(false);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

		delete.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				try {
					if(statusesTable.getValue()!=null){
						Item item=statusesTable.getItem(statusesTable.getValue());
						 dao.delete(Long.parseLong(item.getItemProperty(TBL_ID).getValue().toString()));

						Notification.show(getPropertyName("deleted_success"),
								Type.WARNING_MESSAGE);

						loadOptions();
					}

					} catch (Exception e) {
						Notification.show(getPropertyName("Error"),
								Type.ERROR_MESSAGE);
						e.printStackTrace();
					}

			}
		});


	}


	public void loadOptions() {
		statusesTable.removeAllItems();
		statusesTable.setVisibleColumns(allHeaders);
		try {
			List list = dao.getAllReviewsOfLogin(toLong(userComboField.getValue().toString()),
					getOfficeID(), CommonUtil
							.getSQLDateFromUtilDate(fromDateField
									.getValue()), CommonUtil
							.getSQLDateFromUtilDate(toDateField
									.getValue()));
			
			
			rootPath+=userComboField.getValue()+"/";
			SLink link;
			int index=1;
			ReviewModel reviewModel;
			Iterator it=list.iterator();
			while (it.hasNext()) {
				reviewModel = (ReviewModel) it.next();
				link = new SLink(reviewModel.getFileName(),
						new FileResource(new File(rootPath
								+ reviewModel.getFileName())));
				link.setTargetName("_blank");
				statusesTable.addItem(
						new Object[] {index,reviewModel.getId(),
								CommonUtil.formatDateToCommonFormat(reviewModel.getDate()),
								reviewModel.getTitle(),reviewModel.getDetails(),link}, index);
				index++;
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		statusesTable.setVisibleColumns(reqHeaders);

	}

	@Override
	public SPanel getGUI() {
		return null;
	}

	@Override
	public Boolean isValid() {
		return null;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

}
