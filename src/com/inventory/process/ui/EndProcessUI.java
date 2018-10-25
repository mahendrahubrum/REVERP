package com.inventory.process.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.filefilter.AgeFileFilter;
import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.process.biz.EndProcessBiz;
import com.inventory.process.dao.EndProcessDao;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SPanel;
import com.webspark.Components.SparkLogic;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.OrganizationDao;
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author Jinshad P.T.
 * 
 *         Sep 26, 2013
 */
public class EndProcessUI extends SparkLogic {

	private static final long serialVersionUID = -5835327703018639924L;

	private SPanel mainPanel;
	private SHorizontalLayout buttonLayout;
	private SFormLayout formLayout;

	private SButton processButton;

	private SComboField organizationSelect;
	private SComboField officeSelect;

	OfficeDao ofcDao;
	EndProcessDao endPrcDao;

	@Override
	public SPanel getGUI() {

		setSize(340, 180);

		ofcDao = new OfficeDao();
		endPrcDao = new EndProcessDao();

		mainPanel = new SPanel();
		mainPanel.setSizeFull();

		formLayout = new SFormLayout();
		// formLayout.setSizeFull();
		// formLayout.setSpacing(true);
		formLayout.setMargin(true);

		buttonLayout = new SHorizontalLayout();
		buttonLayout.setSpacing(true);

		try {
			organizationSelect = new SComboField(
					getPropertyName("organization"), 200,
					new OrganizationDao().getAllOrganizations(), "id", "name");

			officeSelect = new SComboField(getPropertyName("office"), 200,
					null, "id", "name");

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		processButton = new SButton(getPropertyName("day_end_process"));
		buttonLayout.addComponent(processButton);

		formLayout.addComponent(organizationSelect);
		formLayout.addComponent(officeSelect);
		formLayout.addComponent(buttonLayout);

		organizationSelect.addValueChangeListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {

				SCollectionContainer bic = null;
				try {

					List lst = new ArrayList();
					lst.add(new S_OfficeModel(0, "ALL"));
					lst.addAll(ofcDao
							.getAllOfficeNamesUnderOrg((Long) organizationSelect
									.getValue()));

					bic = SCollectionContainer.setList(lst, "id");

				} catch (Exception e) {
					e.printStackTrace();
				}
				officeSelect.setContainerDataSource(bic);
				officeSelect.setItemCaptionPropertyId("name");

			}
		});

		officeSelect.addValueChangeListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				try {

					if ((Long) officeSelect.getValue() != 0) {
						S_OfficeModel ofc = ofcDao
								.getOffice((Long) officeSelect.getValue());

						if (ofc.getWorkingDate().toString()
								.equals(getFinEndDate().toString())) {
							processButton.setCaption("Year End Process");
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

		processButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				if (isValid()) {

					try {
						// final S_OfficeModel ofc=new
						// OfficeDao().getOffice((Long)
						// officeSelect.getValue());

						// String yearEndInfo="";
						// if(ofc.getWorkingDate().toString().equals(getFinEndDate().toString()))
						// {
						// yearEndInfo+="Year end Process Also work on this process.!  ";
						// }

						ConfirmDialog.show(getUI(), "Are you sure?",
								new ConfirmDialog.Listener() {
									public void onClose(ConfirmDialog dialog) {
										if (dialog.isConfirmed()) {
											
											EndProcessBiz biz=new EndProcessBiz();

											try {
												List ofcList = new ArrayList();

												if ((Long) officeSelect
														.getValue() != 0) {
													ofcList.add(ofcDao
															.getOffice((Long) officeSelect
																	.getValue()));
												} else {
													ofcList.addAll(ofcDao
															.getAllOfficesUnderOrg((Long) organizationSelect
																	.getValue()));
												}

												if (ofcList.size() > 0) {
													S_OfficeModel ofc;
													boolean isMonthEnd = false, isYearEnd = false;
//													Date OldFinEnd;
//													Calendar cal;
//													FinancialYearsModel fin;
//													Date working_date;
//													Date monthEnd;
//													List idsList;
//													Iterator itr;
//													S_IDGeneratorSettingsModel idGen;
													Iterator it = ofcList
															.iterator();
													while (it.hasNext()) {
														ofc = (S_OfficeModel) it
																.next();
														
//														endPrcDao
//																.changeOfficeWorkingDate(ofc
//																		.getId());

														isMonthEnd = false;
														isYearEnd = false;

														if (getWorkingDate()
																.equals(ofc
																		.getFin_end_date())) {
															isYearEnd = true;
															

//															OldFinEnd = ofc
//																	.getFin_end_date();
//															cal = Calendar
//																	.getInstance();
//															cal.setTime(new java.util.Date(
//																	OldFinEnd
//																			.getTime()));
//															cal.add(Calendar.DAY_OF_MONTH,
//																	1);
//
//															fin = new FinancialYearsModel();
//															fin.setStart_date(new Date(
//																	cal.getTime()
//																			.getTime()));
//
//															cal.add(Calendar.YEAR,
//																	1);
//															cal.add(Calendar.DAY_OF_MONTH,
//																	-1);
//
//															fin.setEnd_date(new Date(
//																	cal.getTime()
//																			.getTime()));
//															fin.setOffice_id(ofc
//																	.getId());
//															fin.setStatus(1);
//															fin.setName(CommonUtil
//																	.formatSQLDateToDDMMMYYYY(fin
//																			.getStart_date())
//																	+ " - "
//																	+ CommonUtil
//																			.formatSQLDateToDDMMMYYYY(fin
//																					.getEnd_date()));
//
//															endPrcDao
//																	.createNewFinancialYearAndSetToOffice(fin);

														}

//														working_date = ofc
//																.getWorkingDate();
//
//														cal = Calendar
//																.getInstance();
//														cal.setTime(new java.util.Date(
//																working_date
//																		.getTime()));
//														cal.add(Calendar.MONTH,
//																1);
//														cal.set(Calendar.DAY_OF_MONTH,
//																1);
//														cal.add(Calendar.DATE,
//																-1);
//
//														monthEnd = new Date(cal
//																.getTime()
//																.getTime());
//														if (working_date
//																.toString()
//																.equals(monthEnd
//																		.toString())) {
//															isMonthEnd = true;
//														}
														
														getHttpSession().setAttribute("dayend_office_id", ofc.getId());
														biz.doEndProcess(isYearEnd);
														
//														idsList = null;
//														idsList = new IDGeneratorSettingsDao()
//																.getAllIDGenerators();
//
//														itr = idsList
//																.iterator();
//														while (itr.hasNext()) {
//															idGen = (S_IDGeneratorSettingsModel) itr
//																	.next();
//
//															if (idGen
//																	.getReset_mode() == SConstants.DAILY_REPEAT) {
//																endPrcDao
//																		.refreshIDGenerator(idGen);
//															} else if (idGen
//																	.getReset_mode() == SConstants.MONTHLY_REPEAT) {
//																if (isMonthEnd)
//																	endPrcDao
//																			.refreshIDGenerator(idGen);
//															} else if(idGen
//																	.getReset_mode() == SConstants.YEARLY_REPEAT){
//																if (isYearEnd)
//																	endPrcDao
//																			.refreshIDGenerator(idGen);
//															}
//
//														}

													}
													
													/*try {
														deleteOldFiles(new File(VaadinServlet.getCurrent().getServletContext()
																.getRealPath("Reports/")), 2);
													} catch (Exception e) {
													}*/
													

													Notification
															.show(getPropertyName("success"),
																	Type.WARNING_MESSAGE);
													getHttpSession()
															.invalidate();
													getUI().getPage()
															.setLocation(
																	VaadinService
																			.getCurrentRequest()
																			.getContextPath()
																			+ "/");

												} else {
													Notification
															.show(getPropertyName("no_office_found"),
																	Type.WARNING_MESSAGE);
												}

											} catch (Exception e) {
												Notification
														.show(getPropertyName("error"),
																Type.ERROR_MESSAGE);
												e.printStackTrace();
											}
											
										}
									}
								});

					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		});

		mainPanel.setContent(formLayout);

		organizationSelect.setValue(getOrganizationID());
		officeSelect.setValue(getOfficeID());

		return mainPanel;
	}
	public void deleteOldFiles(File file,int olderThanDate) {
		
		Calendar cal=getCalendar();
		cal.add(cal.DAY_OF_MONTH, -olderThanDate);
		
	    Iterator<File> filesToDelete = org.apache.commons.io.FileUtils.iterateFiles(file, new AgeFileFilter(cal.getTime()), null);
	    
	    while (filesToDelete.hasNext()) {
			File deleteFile = (File) filesToDelete.next();
			deleteFile.delete();
	    }
	}

	@Override
	public Boolean isValid() {

		boolean ret = true;

		return ret;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

}
