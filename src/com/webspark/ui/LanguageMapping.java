package com.webspark.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.vaadin.annotations.Theme;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SButton;
import com.webspark.Components.SComboField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SPanel;
import com.webspark.Components.STable;
import com.webspark.Components.STextField;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.KeyValue;
import com.webspark.common.util.SessionUtil;
import com.webspark.dao.DBOperations;
import com.webspark.dao.LanguageDao;
import com.webspark.dao.LanguageMappingDao;
import com.webspark.dao.ModuleDao;
import com.webspark.dao.OptionGroupDao;
import com.webspark.model.S_LanguageMappingModel;
import com.webspark.model.S_LanguageModel;
import com.webspark.model.S_OptionGroupModel;
import com.webspark.model.S_OptionModel;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.model.S_ModuleModel;
import com.webspark.uac.model.S_OfficeModel;

/**
 * @Author Jinshad P.T.
 */

@SuppressWarnings("serial")
@Theme("testappstheme")
public class LanguageMapping extends SparkLogic {

	long id = 0;
	WrappedSession session=new SessionUtil().getHttpSession();
	final SFormLayout content;
	SComboField typeCombo;
	SComboField languageCombo;
	private STable table;
	final SButton update = new SButton(getPropertyName("update"));
	final SHorizontalLayout buttonLayout = new SHorizontalLayout();

	private static final String TBL_NO = "#";
	private static final String TBL_ID = "Id";
	private static final String TBL_NAME = "Default Name";
	private static final String TBL_NEW_NAME = "New Name";
	
	LanguageMappingDao dao=new LanguageMappingDao();
	private Object[] allHeaders;
	private Object[] visibleHeaders;
	SButton createNewButton;
	
	public LanguageMapping() throws Exception {
		setSize(730, 575);
		allHeaders=new Object[]{TBL_NO,TBL_ID,TBL_NAME,TBL_NEW_NAME};
		visibleHeaders=new Object[]{TBL_NO,TBL_NAME,TBL_NEW_NAME};
		
		table = new STable(null, 600, 350);
		table.setSelectable(false);
		table.addContainerProperty(TBL_NO, Integer.class, null, TBL_NO,null, Align.CENTER);
		table.addContainerProperty(TBL_ID, Long.class, null,TBL_ID, null, Align.CENTER);
		table.addContainerProperty(TBL_NAME, String.class, null,getPropertyName("name"), null, Align.LEFT);
		table.addContainerProperty(TBL_NEW_NAME, STextField.class, null,getPropertyName("new_name"), null, Align.LEFT);
		table.setColumnExpandRatio(TBL_NO, (float)0.5);
		table.setColumnExpandRatio(TBL_NAME, (float)2);
		table.setColumnExpandRatio(TBL_NEW_NAME, (float)2.5);
		table.setVisibleColumns(visibleHeaders);
		
		content = new SFormLayout();
		createNewButton = new SButton();
		createNewButton.setStyleName("createNewBtnStyle");
		createNewButton.setDescription(getPropertyName("create_new"));
		
		List<KeyValue> typeList = Arrays.asList(new KeyValue((long) 1, "Module"), 
												new KeyValue((long) 2, "Option Group"),
												new KeyValue((long) 3, "Option"));
		typeCombo = new SComboField(null, 300,typeList,"key","value",true,getPropertyName("select"));
		languageCombo = new SComboField(getPropertyName("language"), 300,new LanguageDao().getAllLanguages(),"id","name",true,getPropertyName("select"));
		content.setMargin(true);
		SHorizontalLayout hrl=new SHorizontalLayout(getPropertyName("option_type"));
		hrl.addComponent(typeCombo);
		hrl.addComponent(createNewButton);
		content.addComponent(hrl);
		content.addComponent(languageCombo);
		content.addComponent(table);
		buttonLayout.addComponent(update);
		buttonLayout.setSpacing(true);
		content.addComponent(buttonLayout);
		content.setComponentAlignment(buttonLayout, Alignment.MIDDLE_CENTER);
		update.setVisible(true);
		content.setSizeUndefined();
		setContent(content);

		createNewButton.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				typeCombo.setValue(null);
				languageCombo.setValue(null);
				table.removeAllItems();
				update.setVisible(true);
			}
		});
		
		typeCombo.addValueChangeListener(new Property.ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				try {
					if(isValid()){
						int id=Integer.parseInt(((Long)typeCombo.getValue()+""));
						switch(id){
						case 1:	loadTable(1);
								break;
						case 2: loadTable(2);
								break;
						case 3: loadTable(3);
								break;
						}
					}
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		languageCombo.addValueChangeListener(new Property.ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				try {
					if(isValid())
						loadTable((Long)typeCombo.getValue());
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		update.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				try {
					if(isValid()){
						if(table.getItemIds().size()>0){
							Iterator itr=table.getItemIds().iterator();
							List<S_LanguageMappingModel> list=new ArrayList<S_LanguageMappingModel>();
							STextField field;
							S_LanguageMappingModel mapModel=null;
							while (itr.hasNext()) {
								Item item=table.getItem(itr.next());
								long optionId=toLong(item.getItemProperty(TBL_ID).getValue().toString());
								field = (STextField) item.getItemProperty(TBL_NEW_NAME).getValue();
								mapModel=dao.getLanguageMappingModel( (Long)typeCombo.getValue(), 
																						(Long)languageCombo.getValue(), 
																						optionId);
								if(mapModel!=null){
									mapModel.setType((Long)typeCombo.getValue());
									mapModel.setLanguage(new S_LanguageModel((Long)languageCombo.getValue()));
									mapModel.setOption(optionId);
									mapModel.setName(field.getValue());
								}
								else{
									mapModel=new S_LanguageMappingModel();
									mapModel.setType((Long)typeCombo.getValue());
									mapModel.setLanguage(new S_LanguageModel((Long)languageCombo.getValue()));
									mapModel.setOption(optionId);
									mapModel.setName(field.getValue());
								}
								list.add(mapModel);
							}
							dao.update(list);
							Notification.show(getPropertyName("update_success"),Type.WARNING_MESSAGE);
							loadTable((Long)typeCombo.getValue());
						}
					}
				} 
				catch (Exception e) {
					Notification.show(getPropertyName("error"),Type.ERROR_MESSAGE);
					e.printStackTrace();
				}

			}

		});
		
	}

	@SuppressWarnings("rawtypes")
	public void loadTable(long type){
		try{
			table.removeAllItems();
			table.setVisibleColumns(allHeaders);
			List list=null;
			int typ=Integer.parseInt(type+"");
			S_OfficeModel ofc=new OfficeDao().getOffice(getOfficeID());
			S_LanguageModel def=new LanguageDao().getLanguage(ofc.getLanguage());
			
			switch(typ){
			
				case 1:	list=new ModuleDao().getAllModules();
						if(list.size()>0){
							STextField field;
							for(int i=0;i<list.size();i++){
								S_ModuleModel module=(S_ModuleModel)list.get(i);
								field = new STextField(null,265);
								String name="";
								S_LanguageMappingModel defName=new LanguageMappingDao()
																.getLanguageMappingModel((long)1, 
																						 def.getId(), 
																						 module.getId());
								S_LanguageMappingModel newName=new LanguageMappingDao()
																.getLanguageMappingModel((long)1, 
																						 (Long)languageCombo.getValue(), 
																						 module.getId());
								if(defName!=null){
									name=defName.getName();
								}
								else{
									name="";
								}
								if(newName!=null){
									field.setValue(newName.getName());
								}
								else{
									field.setValue("");
								}
								table.addItem(new Object[]{ table.getItemIds().size()+1,
														    module.getId(),
														    name,
														   	field},table.getItemIds().size()+1);
							}
						}
						
						break;
						
				case 2: list=new OptionGroupDao().getAllOptionGroups();
						if(list.size()>0){
							STextField field;
							for(int i=0;i<list.size();i++){
								S_OptionGroupModel optGrp=(S_OptionGroupModel)list.get(i);
								field = new STextField(null,265);
								S_LanguageMappingModel defName=new LanguageMappingDao()
																.getLanguageMappingModel((long)2, 
																						 def.getId(), 
																						 optGrp.getId());
								S_LanguageMappingModel newName=new LanguageMappingDao()
																.getLanguageMappingModel((long)2, 
																						 (Long)languageCombo.getValue(), 
																						 optGrp.getId());
								if(newName!=null){
									field.setValue(newName.getName());
								}
								else{
									field.setValue("");
								}
								table.addItem(new Object[]{ table.getItemIds().size()+1,
															optGrp.getId(),
														    defName.getName(),
														   	field},table.getItemIds().size()+1);
							}
						}
						
						break;
				
				case 3: list=new DBOperations().getOptions();	 
						if(list.size()>0){
							STextField field;
							for(int i=0;i<list.size();i++){
								S_OptionModel option=(S_OptionModel)list.get(i);
								field = new STextField(null,265);
								S_LanguageMappingModel defName=new LanguageMappingDao()
																.getLanguageMappingModel((long)3, 
																						 def.getId(), 
																						 option.getOption_id());
								S_LanguageMappingModel newName=new LanguageMappingDao()
																.getLanguageMappingModel((long)3, 
																						 (Long)languageCombo.getValue(), 
																						 option.getOption_id());
								if(newName!=null){
									field.setValue(newName.getName());
								}
								else{
									field.setValue("");
								}
								table.addItem(new Object[]{ table.getItemIds().size()+1,
															option.getOption_id(),
														    defName.getName(),
														   	field},table.getItemIds().size()+1);
							}
						}
						
						break;
						
			}
			table.setVisibleColumns(visibleHeaders);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public SPanel getGUI() {
		return null;
	}

	@Override
	public Boolean isValid() {
		boolean flag=true;
		if(typeCombo.getValue()==null || typeCombo.getValue().equals("")){
			flag=false;
		}
		if(languageCombo.getValue()==null || languageCombo.getValue().equals("")){
			flag=false;
		}
		return flag;
	}
	
	@Override
	public Boolean getHelp() {
		return null;
	}

}
