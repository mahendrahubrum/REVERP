package com.webspark.uac.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.config.stock.dao.ItemDao;
import com.inventory.purchase.model.ItemStockModel;
import com.inventory.reports.bean.StickerPrintingBean;
import com.inventory.reports.bean.StickerSubReportBean;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SPanel;
import com.webspark.Components.STable;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.core.Report;
import com.webspark.uac.dao.StickerPrintingDao;
import com.webspark.uac.model.StickerPrintingDetailsModel;
import com.webspark.uac.model.StickerPrintingModel;

/**
 * @author anil
 * @date 19-Nov-2015
 * @Project REVERP
 */
public class StickerPrintingUI extends SparkLogic{

	private static final long serialVersionUID = -2788117533740689766L;

	private static final String TBC_ID = "ID";
	private static final String TBC_SPECIFICATION = "Specification";
	private static final String TBC_RATE = "Rate";

	SFormLayout contentFormLayout;

	SComboField stickersComboField;
	SComboField itemCombo;
	SComboField stockCombo;
	STextField stickerNameTextField;
	STextField noOfStickersTextField;
	RichTextArea stickerContentTextField;	
	RichTextArea stickerSubContentTextField;	

	 SButton saveButton;
	 SButton deleteButton;
	 SButton updateButton;
	 SButton printButton;
	 
	 STable table;

	StickerPrintingDao stickerPrintingDao;

	SButton createNewButton;

	private STextField specificationTextField;

	private STextField rateTextField;

	private SButton addItemButton;

	private SButton updateItemButton;

	private Object[] allColumns;

	private Object[] visibleColumns;

	private int tableindexId;

	private ArrayList<Long> deletedIds;

	private boolean isWindows;

	private boolean isLinux;

	private boolean isMac;
	
	@SuppressWarnings("serial")
	@Override
	public SPanel getGUI() {
		
		allColumns = new String[]{TBC_ID, TBC_SPECIFICATION, TBC_RATE}; 
		visibleColumns = new String[]{TBC_SPECIFICATION, TBC_RATE};
		setSize(800, 650);
		SPanel pan=new SPanel();
		pan.setSizeFull();
		try {
			deletedIds = new ArrayList<Long>();	
			
			final HorizontalLayout buttonLayout = new HorizontalLayout();
		contentFormLayout = new SFormLayout();	
		contentFormLayout.setMargin(true);
		
		stickerPrintingDao=new StickerPrintingDao();
		
		stickersComboField = new SComboField(null, 300, null, "id", "name", false,"Create New");
		itemCombo = new SComboField(getPropertyName("item"), 300, new ItemDao().getAllActiveItemsWithAppendingItemCode(getOfficeID()),
				   					"id", "name", true, getPropertyName("select"));
		stockCombo = new SComboField(getPropertyName("stock"), 300, null, "id", "name", true, getPropertyName("select"));

		createNewButton = new SButton();
		createNewButton.setStyleName("createNewBtnStyle");
		createNewButton.setDescription(getPropertyName("create_new"));
		
		stickerNameTextField = new STextField(getPropertyName("Sticker Name"),300);
		
		stickerContentTextField = new RichTextArea(getPropertyName("content"));
		stickerContentTextField.setResponsive(true);
		stickerContentTextField.setSizeFull();
		stickerContentTextField.setHeight("260px");
		
		stickerSubContentTextField = new RichTextArea();
		stickerSubContentTextField.setResponsive(true);
		stickerSubContentTextField.setHeight("200px");
		stickerSubContentTextField.setWidth("300px");
		
		table=new STable(null,350,200);
		table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,Align.LEFT);
		table.addContainerProperty(TBC_SPECIFICATION, String.class, null, getPropertyName("Specification"), null,Align.LEFT);
		table.addContainerProperty(TBC_RATE, String.class, null, getPropertyName("rate"), null,Align.LEFT);
		table.setSelectable(true);
	//	table.setEditable(false);
		
		table.setVisibleColumns(visibleColumns);
		
		specificationTextField = new STextField(getPropertyName("Specification"), 175, true);
		rateTextField = new STextField(getPropertyName("rate"), 100, true);
		
		addItemButton = new SButton(null, "Add");
		addItemButton.setStyleName("addItemBtnStyle");
		addItemButton.setClickShortcut(KeyCode.ENTER);

		updateItemButton = new SButton(null, "Update");
		updateItemButton.setStyleName("updateItemBtnStyle");
		updateItemButton.setVisible(false);
		updateItemButton.setClickShortcut(KeyCode.ENTER);	
		
		noOfStickersTextField = new STextField(getPropertyName("No. of Copies"),	300);
		
		saveButton = new SButton(getPropertyName("save"), 70);
		saveButton.setStyleName("savebtnStyle");
		saveButton.setIcon(new ThemeResource("icons/saveSideIcon.png"));

		updateButton = new SButton(getPropertyName("update"), 80);
		updateButton.setIcon(new ThemeResource("icons/updateSideIcon.png"));
		updateButton.setStyleName("updatebtnStyle");

		deleteButton = new SButton(getPropertyName("delete"), 78);
		deleteButton.setIcon(new ThemeResource("icons/deleteSideIcon.png"));
		deleteButton.setStyleName("deletebtnStyle");
		
		printButton = new SButton(getPropertyName("Print"));
		printButton.setIcon(new ThemeResource("icons/print.png"));
		printButton.setStyleName("deletebtnStyle");
		
		loadOptions(0);	
		
		SVerticalLayout tableMainLayout = new SVerticalLayout();
		
	//	SVerticalLayout tableEntryLayout = new SVerticalLayout();
		
		
		SHorizontalLayout tableBottomLayout = new SHorizontalLayout();
		tableBottomLayout.setSpacing(true);
		
		tableBottomLayout.addComponent(specificationTextField);
		tableBottomLayout.addComponent(rateTextField);
		//tableBottomLayout.addComponent(tableEntryLayout);
		tableBottomLayout.addComponent(addItemButton);
		tableBottomLayout.addComponent(updateItemButton);
		
		tableMainLayout.addComponent(table);
		tableMainLayout.addComponent(tableBottomLayout);
		
		tableBottomLayout.setComponentAlignment(addItemButton, Alignment.MIDDLE_CENTER);
		tableBottomLayout.setComponentAlignment(updateItemButton, Alignment.MIDDLE_CENTER);
		
		
		SHorizontalLayout subLay = new SHorizontalLayout();
		subLay.setSpacing(true);
		
		SVerticalLayout subLayout=new SVerticalLayout();
		subLayout.setSpacing(true);
		
		subLayout.addComponent(stickerSubContentTextField);
		subLayout.addComponent(stockCombo);
		subLay.addComponent(subLayout);
		subLay.addComponent(tableMainLayout);
		
		SHorizontalLayout salLisrLay = new SHorizontalLayout(
				getPropertyName("Sticker"));
		salLisrLay.addComponent(stickersComboField);
		salLisrLay.addComponent(createNewButton);
		contentFormLayout.addComponent(salLisrLay);
		contentFormLayout.addComponent(stickerNameTextField);
		contentFormLayout.addComponent(itemCombo);
		contentFormLayout.addComponent(stickerContentTextField);
		contentFormLayout.addComponent(subLay);
		contentFormLayout.addComponent(noOfStickersTextField);
		noOfStickersTextField.setVisible(false);
		
		buttonLayout.setSpacing(true);
		buttonLayout.addComponent(saveButton);
		buttonLayout.addComponent(updateButton);
		buttonLayout.addComponent(deleteButton);
		buttonLayout.addComponent(printButton);

		contentFormLayout.addComponent(buttonLayout);
		contentFormLayout.setComponentAlignment(buttonLayout, Alignment.BOTTOM_CENTER);

		deleteButton.setVisible(false);
		updateButton.setVisible(false);
		printButton.setVisible(false);
		contentFormLayout.setSpacing(true);

		pan.setContent(contentFormLayout);

		
		itemCombo.addValueChangeListener(new ValueChangeListener() {
			
			@SuppressWarnings("rawtypes")
			@Override
			public void valueChange(ValueChangeEvent event) {
				try {
					if(itemCombo.getValue()!=null){
						List list=new ArrayList();
						list=new ItemDao().getItemStockList((Long)itemCombo.getValue(), getOfficeID());
						SCollectionContainer bic=SCollectionContainer.setList(list, "id");
						stockCombo.setContainerDataSource(bic);
						stockCombo.setItemCaptionPropertyId("details");
						stockCombo.setValue(null);
					}
					else
						stockCombo.removeAllItems();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		
		/*addShortcutListener(new ShortcutListener("Add New",
				ShortcutAction.KeyCode.N,
				new int[] { ShortcutAction.ModifierKey.ALT }) {
			@Override
			public void handleAction(Object sender, Object target) {
				loadOptions(0);
			}
		});*/

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

		createNewButton.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				stickersComboField.setValue(null);
			}
		});

		saveButton.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				try {

					if (isValid()) {

						if (stickerNameTextField.getValue() != null
								&& !stickerNameTextField.getValue().equals("")) {
							
							StickerPrintingModel lm = new StickerPrintingModel();
							lm.setName(stickerNameTextField.getValue());
							lm.setContent(stickerContentTextField.getValue());
							lm.setItem_id((Long)itemCombo.getValue());
							lm.setOrganization_id(getOrganizationID());
							lm.setSub_content(stickerSubContentTextField.getValue());
							
							List<StickerPrintingDetailsModel> detList = new ArrayList<StickerPrintingDetailsModel>();
							Iterator<?> itr =  table.getItemIds().iterator();
							
							while(itr.hasNext()){
								Item item = table.getItem(itr.next());
								StickerPrintingDetailsModel detModel = new StickerPrintingDetailsModel();
								detModel.setSpecification(item.getItemProperty(TBC_SPECIFICATION).getValue()+"");
								detModel.setDetails(item.getItemProperty(TBC_RATE).getValue()+"");
								
								detList.add(detModel);
							}
							lm.setSticker_list(detList);

							try {
								 stickerPrintingDao.save(lm);
								loadOptions(lm.getId());
								Notification.show(
										getPropertyName("save_success"),
										Type.WARNING_MESSAGE);

							} catch (Exception e) {
								Notification.show(getPropertyName("Error"),
										Type.ERROR_MESSAGE);
								e.printStackTrace();
							}
						}
					}

				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		});

		stickersComboField.addValueChangeListener(new Property.ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				table.removeAllItems();
				deletedIds.clear();
				try {
					if (stickersComboField.getValue() != null
							&& !stickersComboField.getValue().toString().equals("0")) {

						saveButton.setVisible(false);
						deleteButton.setVisible(true);
						updateButton.setVisible(true);
						printButton.setVisible(true);
						noOfStickersTextField.setVisible(true);
						noOfStickersTextField.setValue("1");

						StickerPrintingModel lmd = stickerPrintingDao.getSticker((Long)stickersComboField.getValue());

						stickerNameTextField.setValue(lmd.getName());
						stickerContentTextField.setValue(lmd.getContent());
						stickerSubContentTextField.setValue(lmd.getSub_content());
						itemCombo.setValue(lmd.getItem_id());
						
						Iterator<StickerPrintingDetailsModel> list = lmd.getSticker_list().iterator();
						table.setVisibleColumns(allColumns);
						tableindexId = 0;
						while(list.hasNext()){
							StickerPrintingDetailsModel detmodel = list.next();
							table.addItem(new Object[]{detmodel.getId(),
									detmodel.getSpecification(),
									detmodel.getDetails()},++tableindexId);
						}
						table.setVisibleColumns(visibleColumns);

					} else {
						saveButton.setVisible(true);
						deleteButton.setVisible(false);
						updateButton.setVisible(false);
						printButton.setVisible(false);
						noOfStickersTextField.setVisible(false);

						itemCombo.setValue(null);
						stickerNameTextField.setValue("");
						stickerContentTextField.setValue("");
						stickerSubContentTextField.setValue("");
						table.removeAllItems();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		deleteButton.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				try {

					ConfirmDialog.show(getUI(),
							getPropertyName("are_you_sure"),
							new ConfirmDialog.Listener() {
								public void onClose(ConfirmDialog dialog) {
									if (dialog.isConfirmed()) {

										try {
											stickerPrintingDao.delete(Long.parseLong(stickersComboField
													.getValue().toString()));

											loadOptions(0);
											Notification
													.show(getPropertyName("deleted_success"),
															Type.WARNING_MESSAGE);

										} catch (Exception e) {
											Notification.show(
													getPropertyName("Error"),
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

					if (stickersComboField.getValue() != null) {

						if (isValid()) {

							StickerPrintingModel op = stickerPrintingDao.getSticker(Long
									.parseLong(stickersComboField.getValue()
											.toString()));
							op.setName(stickerNameTextField.getValue());
							op.setItem_id((Long)itemCombo.getValue());
							op.setContent(stickerContentTextField.getValue());
							op.setSub_content(stickerSubContentTextField.getValue());
							
							List<StickerPrintingDetailsModel> detList = op.getSticker_list();
							Iterator<?> itr =  table.getItemIds().iterator();
							long id;
							while(itr.hasNext()){
								Item item = table.getItem(itr.next());
								id = toLong(item.getItemProperty(TBC_ID).getValue().toString());
								if(id == 0){
									StickerPrintingDetailsModel detModel = new StickerPrintingDetailsModel();
									detModel.setSpecification(item.getItemProperty(TBC_SPECIFICATION).getValue()+"");
									detModel.setDetails(item.getItemProperty(TBC_RATE).getValue()+"");
									
									detList.add(detModel);
								} else {
									//==================
									Iterator<StickerPrintingDetailsModel> innerItr = detList.iterator();
									while(innerItr.hasNext()){
										StickerPrintingDetailsModel detModel = innerItr.next();
										if(detModel.getId() == id){
											detModel.setSpecification(item.getItemProperty(TBC_SPECIFICATION).getValue()+"");
											detModel.setDetails(item.getItemProperty(TBC_RATE).getValue()+"");
											break;
										}
									}
									///==============================
								}								
							}
							
							Iterator<Long> longItr = deletedIds.iterator();
							while(longItr.hasNext()){
								id = longItr.next();
								detList.remove(new StickerPrintingDetailsModel(id));
							}
							op.setSticker_list(detList);

							try {
								stickerPrintingDao.Update(op, deletedIds);
								loadOptions(op.getId());
								Notification.show(
										getPropertyName("update_success"),
										Type.WARNING_MESSAGE);
							} catch (Exception e) {
								Notification.show(getPropertyName("Error"),
										Type.ERROR_MESSAGE);
								e.printStackTrace();
							}

						}

					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		});
		
		printButton.addClickListener(new Button.ClickListener() {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			public void buttonClick(ClickEvent event) {
				try {
					if(isValidPrintData()){
						StickerPrintingModel mdl=stickerPrintingDao.getSticker((Long)stickersComboField.getValue());
							if (mdl != null) {
								Report report=new Report(getLoginID());
								List reportList=new ArrayList();
//							// list=new ArrayList();
////								for (int i = 0; i < toInt(noOfStickersTextField.getValue().toString()); i++) {
////									list.add(new ReportBean(mdl.getContent()));
////								}
								Iterator<?> itr =  table.getItemIds().iterator();
								List beanList = new ArrayList(); 
//							//	long id;
					//			boolean isFirst = true;
								while(itr.hasNext()){
									Item item = table.getItem(itr.next());
									StickerSubReportBean subBean = new StickerSubReportBean();
									
									subBean.setDescription(item.getItemProperty(TBC_RATE).getValue().toString());
									subBean.setSpecification(item.getItemProperty(TBC_SPECIFICATION).getValue().toString());	
									
									beanList.add(subBean);
								}
								
								if(beanList.size() > 0){
									StickerPrintingBean bean = new StickerPrintingBean();
									bean.setDetails(beanList);
									bean.setTitle(stickerSubContentTextField.getValue());
									for (int i = 0; i < toInt(noOfStickersTextField.getValue().toString()); i++) {
										reportList.add(bean);
									}
								//	reportList.add(bean);
								}
								
								String rootPath = VaadinServlet.getCurrent().getServletContext().getRealPath("/")+"Jasper/";
								String fileName="Sticker_subreport.jrxml";
								String newFileName="Sticker_subreport.jasper";
								
								HashMap<String, Object> params = new HashMap<String, Object>();
								JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(beanList, true);
								JasperDesign jasperDesign = JRXmlLoader.load(rootPath.trim()+fileName.trim());
								JasperCompileManager.compileReportToFile(jasperDesign, rootPath.trim()+newFileName.trim());
								JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
								JasperFillManager.fillReport(jasperReport, params,dataSource);
								
								HashMap<String, Object> map = new HashMap<String, Object>();
								
								map.put("report_title", stickerNameTextField.getValue());
								map.put("SUBREPORT_DIR", rootPath);
								map.put("CONTENT", stickerContentTextField.getValue());
								
								map.put("MANUFACTURING_DATE_LABEL", getPropertyName("manufacturing_date"));
								map.put("EXPIRY_DATE_LABEL", getPropertyName("expiry_date"));
								
								if(stockCombo.getValue()!=null){
									ItemStockModel stock=new ItemDao().getItemStockModel((Long)stockCombo.getValue()) ;
									map.put("MANUFACTURING_DATE", CommonUtil.formatDateToDDMMYYYY(stock.getManufacturing_date()));
									map.put("EXPIRY_DATE", CommonUtil.formatDateToDDMMYYYY(stock.getExpiry_date()));
								}
								
								report.setJrxmlFileName("Sticker");
								report.setReportFileName("Sticker");
								report.setReportTitle("");
								
								report.setIncludeHeader(false);
								report.setIncludeFooter(false);
								report.setReportType((Integer) 0);
						//		report.setOfficeName(getOfficeName());
								report.createReport(reportList, map);
								
								
//								String rootPath;
//								String subReportFile;
//								String masterReportFile;
//								String reportPath;
//								String subReportJasperFile;
//								detectOS();
//								if (isWindows) {
//									rootPath = VaadinServlet.getCurrent().getServletContext()
//											.getRealPath("/");
//									System.out.println("=== "+rootPath);
//									
//									subReportFile = rootPath+"Jasper\\Sticker_subreport.jrxml";
//									
//									subReportJasperFile = rootPath+"Jasper\\";
//									
//									masterReportFile = rootPath+"Jasper\\Sticker.jrxml";
//									
//									reportPath = rootPath + "Reports\\sticker" +getLoginID()+".pdf";
//
//								} else {
//									rootPath = VaadinServlet.getCurrent().getServletContext()
//											.getRealPath("/");
//									System.out.println("=== "+rootPath);
//									
//									subReportFile = rootPath+"Jasper/Sticker_subreport.jrxml";
//									subReportJasperFile = rootPath+"Jasper/";
//									
//									masterReportFile = rootPath+"Jasper/Sticker.jrxml";
//									
//									reportPath = rootPath + "Reports/sticker" +getLoginID()+".pdf";
//								}
//								
//								File oldFile = new File(reportPath);
//								if (oldFile.exists()) {
//									oldFile.delete();
//								}
//								
//								
//								
//								
//								
//								
//								HashMap map=new HashMap();
//								map.put("report_title", stickerNameTextField.getValue());
//								map.put("CONTENT", stickerContentTextField.getValue());
//								map.put("SUB_CONTENT", stickerSubContentTextField.getValue());
//							//	map.put("title", stickerNameTextField.getValue());
//								map.put("Sticker_subreport", JasperCompileManager.compileReport(subReportFile));
//								
//								;
//								JasperReport jasperReport = JasperCompileManager.compileReport(masterReportFile);
//								
//								
//								
//								JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map, new JRBeanCollectionDataSource(beanList));
//
//						        JasperExportManager.exportReportToPdfFile(jasperPrint, reportPath);
//						        
//						        File file = new File(reportPath);
//
//								FileResource resource = new FileResource(file);
//
//								if (file != null&&file.exists()) {
//
//									Page.getCurrent().open(resource, "sticker" +getLoginID()+".pdf", true);
//									
//									file.deleteOnExit();
//
//								} else {
//									SNotification.show("Report generation failed. Please try again..!",
//											Type.ERROR_MESSAGE);
//								}
							}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		});
		
		addItemButton.addClickListener(new ClickListener() {
			
			

			@Override
			public void buttonClick(ClickEvent event) {
				if(isValidTableData()){
					
					table.setVisibleColumns(allColumns);
					table.addItem(new Object[]{(long)0,
							specificationTextField.getValue(),
							rateTextField.getValue()},
							++tableindexId);
					table.setVisibleColumns(visibleColumns);
					
					clearTableFields();
				}
				
			}

			
		});
		table.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(table.getValue() == null){
					clearTableFields();
					addItemButton.setVisible(true);
					updateItemButton.setVisible(false);
				} else{
					Item item = table.getItem(table.getValue());
					specificationTextField.setValue(item.getItemProperty(TBC_SPECIFICATION).getValue().toString());
					rateTextField.setValue(item.getItemProperty(TBC_RATE).getValue().toString());	
					
					addItemButton.setVisible(false);
					updateItemButton.setVisible(true);
				}
				
			}
		});
		
		updateItemButton.addClickListener(new ClickListener() {
			
			@SuppressWarnings("unchecked")
			@Override
			public void buttonClick(ClickEvent event) {
				if(isValidTableData()){
					Item item = table.getItem(table.getValue());
					item.getItemProperty(TBC_SPECIFICATION).setValue(specificationTextField.getValue());
					item.getItemProperty(TBC_RATE).setValue(rateTextField.getValue());
									
					clearTableFields();
					addItemButton.setVisible(true);
					updateItemButton.setVisible(false);
					table.setValue(null);
				}
				
			}

			
		});
		
		final Action actionDelete = new Action("Delete");

		
		table.addActionHandler(new Action.Handler() {
			@Override
			public Action[] getActions(final Object target,
					final Object sender) {
				return new Action[] { actionDelete };
			}

			@Override
			public void handleAction(final Action action,
					final Object sender, final Object target) {
				deleteItem();
			}

		});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pan;
	}
	
	
	public void deleteItem() {
		try {
			if (table.getValue() != null) {
				long id = (Long)table.getItem(table.getValue()).getItemProperty(TBC_ID).getValue();
				if(id != 0){
					deletedIds.add(id);		
					
				}
				
				System.out.println("ID ++=  "+id);
				table.removeItem(table.getValue());							
			}		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void clearTableFields() {
		specificationTextField.setValue("");
		rateTextField.setValue("");	
	}
	
	private void detectOS() {
		isWindows = false;
		isLinux = false;
		isMac = false;
		String osName = System.getProperty("os.name").toLowerCase();
		if (osName.contains("win")) {
			isWindows = true;
		} else if (osName.contains("nix") || osName.contains("nux")) {
			isLinux = true;
		} else if (osName.contains("mac")) {
			isMac = true;
		}
	}

	protected boolean isValidTableData() {
		boolean valid=true;
		specificationTextField.setRequiredError(null);
		rateTextField.setRequiredError(null);
		if(specificationTextField.getValue()==null||specificationTextField.getValue().toString().trim().length()<=0){
			setRequiredError(specificationTextField, getPropertyName("invalid_data"), true);
			valid=false;
		}
		
		if(rateTextField.getValue()==null||rateTextField.getValue().toString().trim().length()<=0){
			setRequiredError(rateTextField, getPropertyName("invalid_data"), true);
			valid=false;
		}
		return valid;
	}
	
	protected boolean isValidPrintData() {
		boolean valid=true;
		stickerContentTextField.setRequiredError(null);
		noOfStickersTextField.setRequiredError(null);
		if(stickerContentTextField.getValue()==null||stickerContentTextField.getValue().toString().trim().length()<=0){
			setRequiredError(stickerContentTextField, getPropertyName("invalid_data"), true);
			valid=false;
		}
		
		if(!isValid())
			valid=false;
		
		if (stockCombo.getValue() == null || stockCombo.getValue().equals("")) {
			setRequiredError(stockCombo, getPropertyName("invalid_selection"), true);
			valid = false;
		} else
			setRequiredError(stockCombo, null, false);
		
		try {
			if(toDouble(noOfStickersTextField.getValue().toString())<=0){
				setRequiredError(noOfStickersTextField, getPropertyName("invalid_data"), true);
				valid=false;
			}
		} catch (Exception e) {
			setRequiredError(noOfStickersTextField, getPropertyName("invalid_data"), true);
			valid=false;
		}
		return valid;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void loadOptions(long id) {
		List testList;
		try {
			testList = stickerPrintingDao.getStickers(getOrganizationID());

			StickerPrintingModel sop = new StickerPrintingModel();
			sop.setId(0);
			sop.setName("------------------- Create New -------------------");

			if (testList == null)
				testList = new ArrayList();

			testList.add(0, sop);

			stickersComboField
					.setInputPrompt("------------------- Create New -------------------");

			SCollectionContainer	bic = SCollectionContainer.setList(testList, "id");
			stickersComboField.setContainerDataSource(bic);
			stickersComboField.setItemCaptionPropertyId("name");

			stickersComboField.setValue(id);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	@Override
	public Boolean isValid() {
		
		boolean valid=true;
		
		if (itemCombo.getValue() == null || itemCombo.getValue().equals("")) {
			setRequiredError(itemCombo, getPropertyName("invalid_selection"), true);
			valid = false;
		} else
			setRequiredError(itemCombo, null, false);
		
		if(stickerNameTextField.getValue()==null&&stickerNameTextField.getValue().toString().trim().length()<=0){
			setRequiredError(stickerNameTextField, getPropertyName("invalid_data"), true);
			 valid=false;
		} else
			setRequiredError(stickerNameTextField, null, false);
		
		if(stickerContentTextField.getValue()==null&&stickerContentTextField.getValue().toString().trim().length()<=0){
			setRequiredError(stickerContentTextField, getPropertyName("invalid_data"), true);
			valid=false;
		} else
			setRequiredError(stickerContentTextField, null, false);
		
		
		
		return valid;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

}
