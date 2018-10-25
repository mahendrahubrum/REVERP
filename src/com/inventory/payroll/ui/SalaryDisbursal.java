package com.inventory.payroll.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.payroll.dao.PayrollEmployeeMapDao;
import com.inventory.payroll.dao.SalaryDisbursalDao;
import com.inventory.payroll.model.PayrollEmployeeMapModel;
import com.inventory.payroll.model.SalaryDisbursalDetailsModel;
import com.inventory.payroll.model.SalaryDisbursalModel;
import com.vaadin.annotations.Theme;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.Action;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SPanel;
import com.webspark.Components.STable;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.uac.dao.UserManagementDao;
import com.webspark.uac.model.UserModel;

/**
 * @Author Jinshad P.T.
 */

@Theme("testappstheme")
public class SalaryDisbursal extends SparkLogic {

	long id = 0;

	SCollectionContainer bic;

	WrappedSession session = getHttpSession();

	UserManagementDao objUserDao = new UserManagementDao();
	SalaryDisbursalDao objDao = new SalaryDisbursalDao();

	SVerticalLayout vertLayout;

	SFormLayout content;

	SComboField employList;

	SDateField startDate;
	SDateField endDate;

	STable table;

	SDateField dispursalDate;

	SGridLayout detailsGrid;

	String[] allHeaders, requiredHeaders;

	HorizontalLayout buttonLayout = null;

	@SuppressWarnings("deprecation")
	@Override
	public SPanel getGUI() {

		vertLayout = new SVerticalLayout();

		objUserDao = new UserManagementDao();

		objDao = new SalaryDisbursalDao();

		buttonLayout = new HorizontalLayout();

		session = getHttpSession();

		setSize(700, 550);

		final SButton pay = new SButton(getPropertyName("pay"));

		SPanel pan = new SPanel();
		pan.setSizeFull();

		try {

			allHeaders = new String[] { "SI No.", "Disb. ID", "Emp. Name",
					"From Date", "To Date", "Total Salary" };
			requiredHeaders = new String[] { "SI No.", "Disb. ID", "Emp. Name",
					"From Date", "To Date", "Total Salary" };

			detailsGrid = new SGridLayout();

			content = new SFormLayout();

			table = new STable(null, 600, 140);

			table.setSelectable(true);
			table.setMultiSelect(true);

			table.addContainerProperty("SI No.", Integer.class, null, "#",
					null, Align.CENTER);
			table.addContainerProperty("Disb. ID", Long.class, null,
					"Disb. ID", null, Align.CENTER);
			table.addContainerProperty("Emp. Name", String.class, null,
					"Emp. Name", null, Align.CENTER);
			table.addContainerProperty("From Date", Date.class, null,
					"From Date", null, Align.CENTER);
			table.addContainerProperty("To Date", Date.class, null, "To Date",
					null, Align.CENTER);
			table.addContainerProperty("Total Salary", Double.class, null,
					"Total Salary", null, Align.CENTER);

			table.setColumnExpandRatio("SI No.", (float) 0.4);
			table.setColumnExpandRatio("Disb. ID", 1);
			table.setColumnExpandRatio("Emp. Name", 2);
			table.setColumnExpandRatio("Total Salary", 2);

			table.setWidth("600");
			table.setHeight("140");

			employList = new SComboField(
					getPropertyName("employee"),
					200,
					objUserDao
							.getUsersWithFullNameAndCodeUnderOffice(getOfficeID()),
					"id", "first_name");
			employList
					.setInputPrompt("------------------- Create New -------------------");

			dispursalDate = new SDateField(getPropertyName("employee"), 100,
					getDateFormat(), new Date());

			startDate = new SDateField(getPropertyName("from_date"), 100,
					getDateFormat());
			endDate = new SDateField(getPropertyName("to_date"), 100,
					getDateFormat());

			Date toDay = new Date();
			Calendar cal = Calendar.getInstance();

			cal.setTime(toDay);

			cal.set(Calendar.DATE, 1);
			startDate.setValue(cal.getTime());

			cal.set(Calendar.DATE, cal.getMaximum(Calendar.DATE));
			cal.add(Calendar.DATE, -1);
			endDate.setValue(cal.getTime());

			content.setMargin(true);
			content.setWidth("280px");
			content.setHeight("200px");

			content.addComponent(employList);
			content.addComponent(dispursalDate);
			content.addComponent(startDate);
			content.addComponent(endDate);

			content.addComponent(buttonLayout);

			// content.addComponent(table);

			vertLayout.addComponent(content);
			vertLayout.addComponent(table);

			vertLayout.setMargin(true);

			vertLayout.addComponent(detailsGrid);

			vertLayout.setComponentAlignment(content, Alignment.MIDDLE_CENTER);
			vertLayout.setComponentAlignment(table, Alignment.MIDDLE_CENTER);
			vertLayout.setComponentAlignment(detailsGrid,
					Alignment.MIDDLE_CENTER);

			buttonLayout.addComponent(pay);

			pan.setContent(vertLayout);

//			pay.addClickListener(new Button.ClickListener() {
//				public void buttonClick(ClickEvent event) {
//					try {
//
//						if (employList.getValue() != null) {
//
//							if (isValid()) {
//
//								SalaryDisbursalModel objModel = new SalaryDisbursalModel();
//
//								objModel.setEmploy(new UserModel(
//										(Long) employList.getValue()));
//								objModel.setFrom_date(CommonUtil
//										.getSQLDateFromUtilDate(startDate
//												.getValue()));
//								objModel.setTo_date(CommonUtil
//										.getSQLDateFromUtilDate(endDate
//												.getValue()));
//								objModel.setDispursal_date(CommonUtil
//										.getSQLDateFromUtilDate(dispursalDate
//												.getValue()));
//
//								List<SalaryDisbursalDetailsModel> details = new ArrayList<SalaryDisbursalDetailsModel>();
//
//								SalaryDisbursalDetailsModel obj = null;
//
//								double total = 0;
//								List lst = new PayrollEmployeeMapDao()
//										.getPayRollMap((Long) employList
//												.getValue());
//
//								if (lst.size() > 0) {
//
//									PayrollEmployeeMapModel pem;
//									Iterator it1 = lst.iterator();
//									while (it1.hasNext()) {
//
//										pem = (PayrollEmployeeMapModel) it1
//												.next();
//
//										obj = new SalaryDisbursalDetailsModel();
//										obj.setComponent(pem.getComponent());
//
//										if (pem.getComponent().getAction() == SConstants.payroll.ADDITION) { // Addition
//
//											obj.setAmount(getAmount(pem
//													.getComponent().getId()));
//											total += obj.getAmount();
//
//										} else { // Deduction
//
//											obj.setAmount(getAmount(pem
//													.getComponent().getId()));
//											total -= obj.getAmount();
//
//										}
//
//										details.add(obj);
//
//									}
//
//									objModel.setTotal_salary(total);
//
//									objModel.setDetailsList(details);
//
//									try {
//										long id = objDao.save(objModel);
//
//										loadOptions(id);
//										Notification
//												.show(getPropertyName("save_success"),
//														Type.WARNING_MESSAGE);
//
//									} catch (Exception e) {
//										// TODO Auto-generated catch block
//										Notification.show(
//												getPropertyName("error"),
//												Type.ERROR_MESSAGE);
//										e.printStackTrace();
//									}
//
//								} else {
//									Notification
//											.show(getPropertyName("not_mapped"),
//													getPropertyName("no_components_mapped"),
//													Type.WARNING_MESSAGE);
//								}
//							}
//						}
//
//					} catch (NumberFormatException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//
//				}
//
//			});

//			employList.addListener(new Property.ValueChangeListener() {
//
//				public void valueChange(ValueChangeEvent event) {
//
//					try {
//
//						table.removeAllItems();
//						detailsGrid.removeAllComponents();
//
//						if (employList.getValue() != null
//								&& !employList.getValue().toString()
//										.equals("0")) {
//
//							List disbList = objDao
//									.getPayments((Long) employList.getValue());
//
//							table.setVisibleColumns(allHeaders);
//
//							SalaryDisbursalModel obj;
//							int ct = 0, id = 0;
//							Iterator it = disbList.iterator();
//							while (it.hasNext()) {
//								obj = (SalaryDisbursalModel) it.next();
//
//								id++;
//								ct++;
//
//								table.addItem(
//										new Object[] {
//												ct,
//												obj.getId(),
//												obj.getEmploy().getFirst_name(),
//												obj.getFrom_date(),
//												obj.getTo_date(),
//												obj.getTotal_salary() }, id);
//
//							}
//
//						} else {
//
//							Date toDay = new Date();
//							Calendar cal = Calendar.getInstance();
//
//							cal.setTime(toDay);
//
//							cal.set(Calendar.DATE, 1);
//							startDate.setValue(cal.getTime());
//
//							cal.set(Calendar.DATE,
//									cal.getMaximum(Calendar.DATE));
//							cal.add(Calendar.DATE, -1);
//							endDate.setValue(cal.getTime());
//							dispursalDate.setValue(toDay);
//
//						}
//
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//			});

//			table.addListener(new Property.ValueChangeListener() {
//
//				public void valueChange(ValueChangeEvent event) {
//
//					try {
//
//						Collection selectedItems = null;
//
//						if (table.getValue() != null) {
//							selectedItems = (Collection) table.getValue();
//						}
//
//						if (selectedItems != null && selectedItems.size() == 1) {
//
//							Item items = table.getItem(selectedItems.iterator()
//									.next());
//
//							SalaryDisbursalModel item = objDao
//									.getSalaryDesbursal((Long) items
//											.getItemProperty("Disb. ID")
//											.getValue());
//
//							detailsGrid.removeAllComponents();
//
//							detailsGrid.setColumns(3);
//							detailsGrid.setSpacing(true);
//							detailsGrid
//									.setRows(item.getDetailsList().size() + 1);
//
//							SalaryDisbursalDetailsModel det;
//							Iterator it1 = item.getDetailsList().iterator();
//							while (it1.hasNext()) {
//								det = (SalaryDisbursalDetailsModel) it1.next();
//
//								SLabel lab = new SLabel();
//								lab.setValue(det.getComponent().getName());
//
//								detailsGrid.addComponent(lab);
//
//								detailsGrid.addComponent(new SLabel(":"));
//
//								SLabel val = new SLabel();
//								val.setValue("" + det.getAmount());
//								detailsGrid.addComponent(val);
//
//							}
//
//							SLabel lab = new SLabel();
//							lab.setValue("Total Salary");
//
//							detailsGrid.addComponent(lab);
//
//							detailsGrid.addComponent(new SLabel(":"));
//
//							SLabel val = new SLabel();
//							val.setValue("" + item.getTotal_salary());
//							detailsGrid.addComponent(val);
//
//						} else {
//							detailsGrid.removeAllComponents();
//						}
//
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//
//				}
//
//			});

			addShortcutListener(new ShortcutListener("Add New",
					ShortcutAction.KeyCode.DELETE,
					new int[] { ShortcutAction.ModifierKey.SHIFT }) {
				@Override
				public void handleAction(Object sender, Object target) {
//					deleteDespursal();
				}
			});

			/*
			 * addShortcutListener(new ShortcutListener("Save",
			 * ShortcutAction.KeyCode.ENTER, null) {
			 * 
			 * @Override public void handleAction(Object sender, Object target)
			 * { if (save.isVisible()) save.click(); else update.click(); } });
			 */

			final Action actionDelete = new Action("Delete");

			table.addActionHandler(new Action.Handler() {
				@Override
				public Action[] getActions(final Object target,
						final Object sender) {
					// if(deleteItemButton.isVisible())
					// deleteItemButton.click();
					return new Action[] { actionDelete };
				}

				@Override
				public void handleAction(final Action action,
						final Object sender, final Object target) {
//					deleteDespursal();
				}

			});

		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}

		// TODO Auto-generated method stub
		return pan;
	}

	/*
	 * public void getPaymentDetails() throws Exception{
	 * 
	 * try {
	 * 
	 * 
	 * SalaryDisbursalDetailsModel obj=null;
	 * 
	 * double total=0; List details=new ArrayList();
	 * 
	 * List lst=new PayrollEmployeeMapDao().getPayRollMap((Long)
	 * employList.getValue());
	 * 
	 * Iterator it1=lst.iterator(); while(it1.hasNext()){
	 * 
	 * PayrollEmployeeMapModel pem=(PayrollEmployeeMapModel) it1.next();
	 * 
	 * 
	 * obj=new SalaryDisbursalDetailsModel();
	 * obj.setComponent(pem.getComponent());
	 * 
	 * if(pem.getComponent().getAction()==SConstants.payroll.ADDITION) { //
	 * Addition
	 * 
	 * obj.setAmount(getAmount(pem.getComponent().getId()));
	 * total+=obj.getAmount();
	 * 
	 * } else { // Deduction
	 * 
	 * obj.setAmount(getAmount(pem.getComponent().getId()));
	 * total-=obj.getAmount();
	 * 
	 * }
	 * 
	 * details.add(obj);
	 * 
	 * 
	 * } } catch (Exception e) { throw e; // TODO: handle exception }
	 * 
	 * 
	 * }
	 */

//	public double getAmount(long id) throws Exception {
//
//		double amt = 0;
//		try {
//			PayrollEmployeeMapModel map = objDao.getPayRollMap(
//					(Long) employList.getValue(), id);
//
//			if (map.getComponent().getType() == SConstants.payroll.FIXED) {
//				amt = map.getValue();
//			} else {
//				double par_amt = getAmount(map.getComponent().getParent_id());
//				amt = par_amt * map.getValue() / 100;
//			}
//
//			return amt;
//		} catch (Exception e) {
//			throw e;
//			// TODO: handle exception
//		}
//
//	}

//	public void deleteDespursal() {

//		ConfirmDialog.show(getUI(), getPropertyName("are_you_sure"),
//				new ConfirmDialog.Listener() {
//					public void onClose(ConfirmDialog dialog) {
//						if (dialog.isConfirmed()) {
//
//							try {
//
//								Collection selectedItems = null;
//
//								if (table.getValue() != null) {
//									selectedItems = (Collection) table
//											.getValue();
//								}
//
//								if (selectedItems != null
//										&& selectedItems.size() == 1) {
//
//									Iterator it = selectedItems.iterator();
//
//									Item items;
//									while (it.hasNext()) {
//
//										items = table.getItem(it.next());
//
//										objDao.delete((Long) items
//												.getItemProperty("Disb. ID")
//												.getValue());
//
//									}
//								}
//
//								loadOptions(0);
//
//							} catch (Exception e) {
//								// TODO: handle exception
//								e.printStackTrace();
//							}
//						}
//					}
//				});

//	}

	public void setReadOnlyAll() {
		dispursalDate.setReadOnly(true);
		endDate.setReadOnly(true);
		startDate.setReadOnly(true);
		// country.setReadOnly(true);
		// address.setReadOnly(true);
		dispursalDate.setReadOnly(true);
		// phone.setReadOnly(true);

		dispursalDate.focus();
	}

	public void setWritableAll() {
		dispursalDate.setReadOnly(false);
		dispursalDate.setReadOnly(false);
		// country.setReadOnly(false);
		endDate.setReadOnly(false);
		startDate.setReadOnly(false);
		endDate.setReadOnly(false);
		// address.setReadOnly(false);
		// phone.setReadOnly(false);

	}

	public void loadOptions(long id) {
		List testList;
		try {

			Object obj = employList.getValue();
			employList.setValue(null);
			employList.setValue(obj);

			table.setValue(id);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public Boolean isValid() {
		// TODO Auto-generated method stub

		try {

			if (dispursalDate.getValue() == null
					|| dispursalDate.getValue().equals("")) {
				Notification.show(getPropertyName("invalid_data"),
						getPropertyName("invalid_data"), Type.ERROR_MESSAGE);
				return false;
			}

			if (startDate.getValue() == null || startDate.getValue().equals("")) {
				Notification.show(getPropertyName("invalid_data"),
						getPropertyName("invalid_selection"),
						Type.ERROR_MESSAGE);
				return false;
			}

			if (endDate.getValue() == null || endDate.getValue().equals("")) {
				Notification.show(getPropertyName("invalid_data"),
						getPropertyName("invalid_selection"),
						Type.ERROR_MESSAGE);
				return false;
			}

			if (dispursalDate.getValue() == null
					|| dispursalDate.getValue().equals("")) {
				Notification.show(getPropertyName("invalid_data"),
						getPropertyName("invalid_selection"),
						Type.ERROR_MESSAGE);
				return false;
			}

			return true;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

	}

	@Override
	public Boolean getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

}
